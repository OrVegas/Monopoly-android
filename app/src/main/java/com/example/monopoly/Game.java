package com.example.monopoly;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Game extends AppCompatActivity implements View.OnClickListener {
    protected final ActivityResultHelper<Intent, ActivityResult>activityLauncher = ActivityResultHelper.registerActivityForResult(this);
    AppCompatButton rolldice;
    ImageView vacation, start, surprise1, surprise2, jail, gotojail, tax1, tax2, bengurion, haneda,rothschild, greencarnaby, greenbond, greenoxford,
            blueavenue, bluebroadway, bluecrosby, redjumeirah, redmudon, redwarsan, center;
    ImageView dice1, dice2;
    private int diceValueSum;
    MediaPlayer mprollingdice, mbuysound;
    Boolean sound=false;
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    TextView username;
    ImageView userpic;
    ImageView usertool, bot1tool, bot2tool;
    ArrayList<TextView>playerCoins = new ArrayList<TextView>();
    TextView usercoins, bot1coins,bot2coins;
    ArrayList<Cell>cells=new ArrayList<>();
    private int currentPlayerTurn = 0;
    int[]xCoordsUser = {1200, 1025, 845, 660, 480, 300, 10, 130, 130, 130, 130, 300, 480, 660, 840, 1020, 1200, 1200, 1200, 1200, 100};
    int[]yCoordsUser = {795, 795, 795, 795, 795, 795, 755, 630, 450, 270, 80, 80, 80, 70, 80, 80, 80, 260, 420, 620, 755};
    int[] xCoordsBot1 = {1120, 950, 760, 585, 400, 220, 30, 40, 40, 40, 40, 220, 400, 580, 760, 950, 1120, 1120, 1120, 1120, 100};
    int[] yCoordsBot1 = {800, 800, 800, 800, 800, 800, 800, 630, 450, 270, 88, 88, 88, 73, 88, 88, 88, 270, 430, 630, 800};
    int[] xCoordsBot2 = {1160, 990, 815, 630, 450, 260, 25, 90, 90, 90, 90, 260, 450, 630, 810, 990, 1170, 1170, 1170, 1170, 100};
    int[] yCoordsBot2 = {830, 830, 830, 830, 830, 830, 850, 650, 470, 290, 115, 115, 115, 105, 115, 115, 115, 300, 460, 650, 850};
    ArrayList<Player>players = new ArrayList<>();
    Player user0=new Player(1, xCoordsUser, yCoordsUser);
    Player user1=new Player(2, xCoordsBot1, yCoordsBot1);
    Player user2=new Player(3, xCoordsBot2, yCoordsBot2);
    Dialog d, lose, win, dialog;
    TextView name, price, pay, tv1, tv2;
    Button finishGameLose, finishGameWin;
    AppCompatButton cancel, buy;
    Boolean offerPause =true, checkDouble=false;//offerPause is for user that land on property to wait for him to chose if he wants to buy or not
    ProgressBar progressBar;
    ImageButton userbag, bot1bag, bot2bag;
    public static final String ACTION_STOP_BACKGROUND_MUSIC = "com.example.monopoly.STOP_BACKGROUND_MUSIC";
    Animation animFirst, animSec,animSpin,animThir,animFour,animFive,animBigger;
    ImageView crown;
    //todo: Consider adding functionality to persist game data (such as player positions, coins etc..)
    // using SharedPreferences SQLite or a local database This way if the app is closed or crashes,
    // the game state can be restored.
    //todo:Loading large images or media files can impact performance. Consider using libraries like Glide or Picasso for
    // optimized image loading and caching. Also, make sure your media files are compressed to reduce memory usage.
    //todo:The dice roll animation logic could benefit from using Handler or Animator frameworks to
    // provide smoother animations. This would ensure that your animations are more frame-consistent.
    //todo: add more const to magic numbers
    //todo:if needed separate the backend and the frontend to two classes for business module looks(if needed)
    //todo: maybe add extra logics like hotels, 3-doubles-jail,all colored-double payment and more surprise card types
    //todo: maybe add play against each others instead of just again bots using real-time database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        progressBar= findViewById(R.id.progressbar);
        readImageNameData();
        readUserData();
        readSoundApproval();
        players.add(user0);
        players.add(user1);
        players.add(user2);
        vacation=(ImageView)findViewById(R.id.cell_1_1);
        redjumeirah=(ImageView)findViewById(R.id.cell_1_2);
        surprise1=(ImageView)findViewById(R.id.cell_1_3);
        bengurion=(ImageView)findViewById(R.id.cell_1_4);
        redmudon=(ImageView)findViewById(R.id.cell_1_5);
        redwarsan=(ImageView)findViewById(R.id.cell_1_6);
        gotojail=(ImageView)findViewById(R.id.cell_1_7);
        bluecrosby=(ImageView)findViewById(R.id.cell_2_1);
        bluebroadway=(ImageView)findViewById(R.id.cell_3_1);
        blueavenue=(ImageView)findViewById(R.id.cell_4_1);
        jail=(ImageView)findViewById(R.id.cell_5_1);
        greenoxford=(ImageView)findViewById(R.id.cell_5_2);
        tax1=(ImageView)findViewById(R.id.cell_5_3);
        surprise2=(ImageView)findViewById(R.id.cell_5_4);
        greenbond=(ImageView)findViewById(R.id.cell_5_5);
        greencarnaby=(ImageView)findViewById(R.id.cell_5_6);
        start=(ImageView)findViewById(R.id.cell_5_7);
        tax2=(ImageView)findViewById(R.id.cell_2_7);
        haneda=(ImageView)findViewById(R.id.cell_3_7);
        rothschild=(ImageView)findViewById(R.id.cell_4_7);
        center=(ImageView)findViewById(R.id.cell_center);
        rolldice=(AppCompatButton) findViewById(R.id.rollDiceButton);
        dice1 = (ImageView)findViewById(R.id.dice1);
        dice2 = (ImageView)findViewById(R.id.dice2);
        mprollingdice = MediaPlayer.create(this, R.raw.rollingdice);
        mbuysound = MediaPlayer.create(this, R.raw.buysound);
        username=(TextView) findViewById(R.id.usernickname);
        userpic=(ImageView)findViewById(R.id.userpic);
        rolldice.setOnClickListener(this);
        usertool=(ImageView) findViewById(R.id.usertool);
        bot1tool=(ImageView) findViewById(R.id.bot1tool);
        bot2tool=(ImageView) findViewById(R.id.bot2tool);
        usercoins=(TextView) findViewById(R.id.usercoins);
        bot1coins=(TextView) findViewById(R.id.bot1coins);
        bot2coins=(TextView) findViewById(R.id.bot2coins);
        playerCoins.add(usercoins);
        playerCoins.add(bot1coins);
        playerCoins.add(bot2coins);
        userbag=(ImageButton)findViewById(R.id.userbag);
        bot1bag=(ImageButton)findViewById(R.id.bot1bag);
        bot2bag=(ImageButton)findViewById(R.id.bot2bag);
        userbag.setOnClickListener(this);
        bot1bag.setOnClickListener(this);
        bot2bag.setOnClickListener(this);
        addAllCells();
        animFirst = AnimationUtils.loadAnimation(this, R.anim.first_move);
        animSec = AnimationUtils.loadAnimation(this, R.anim.sec_move);
        animSpin = AnimationUtils.loadAnimation(this, R.anim.rotate_spin);
        animThir = AnimationUtils.loadAnimation(this, R.anim.thirth_move);
        animFour = AnimationUtils.loadAnimation(this, R.anim.fourth_move);
        animFive = AnimationUtils.loadAnimation(this, R.anim.five_move);
        animBigger = AnimationUtils.loadAnimation(this, R.anim.get_bigger);

    }
    private void startMovingAnimation() {
        crown.startAnimation(animSpin);

        animSpin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                crown.startAnimation(animFirst);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animFirst.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                crown.startAnimation(animSec);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animSec.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                crown.startAnimation(animThir);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animThir.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                crown.startAnimation(animFour);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animFour.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                crown.startAnimation(animFive);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animFive.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                crown.startAnimation(animSpin);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void addAllCells() {
        cells.add(new Cell("Start", 0,350,0,""));
        cells.add(new Cell("Carnaby", 360, 34,108,"green"));
        cells.add(new Cell("Bond", 340, 32,102,"green"));
        cells.add(new Cell("Suprise", 0, 0,0,""));
        cells.add(new Cell("Tax", 0, 200,0,""));
        cells.add(new Cell("Oxford", 385, 36,115,"green"));
        cells.add(new Cell("Jail pass", 0, 0,0,""));
        cells.add(new Cell("Avenue", 185, 17,55,"blue"));
        cells.add(new Cell("Broadway", 250, 24,75,"blue"));
        cells.add(new Cell("Crosby", 220, 20,66,"blue"));
        cells.add(new Cell("Vacation", 0, 0,0,""));
        cells.add(new Cell("Jumeirah", 520, 50,156,"red"));
        cells.add(new Cell("Surprise", 0, 0,0,""));
        cells.add(new Cell("Ben Gurion", 500, 60,150,"white"));
        cells.add(new Cell("Mudon", 420, 40,126,"red"));
        cells.add(new Cell("Warsan", 475, 43,142,"red"));
        cells.add(new Cell("Gotojail", 0, 0,0,""));
        cells.add(new Cell("Tax", 0, 200,0,""));
        cells.add(new Cell("Haneda", 500, 60,150,"white"));
        cells.add(new Cell("Rothschild", 730, 70,220,"yellow"));
        cells.add(new Cell("Jail", 0, 0,0,""));
    }

    @Override
    public void onClick(View v) {
        if(v==rolldice)
        {
            rolldice.setEnabled(false);
            rollDice();
        }
        else if(v==cancel){
            d.dismiss();
            switchToNextPlayerTurn();
        }
        else if(v==buy){
            if (mbuysound != null && sound){ mbuysound.start();}
            buyProperty(user0.getId());
        }
        else if(v== finishGameLose){
            Intent stopIntent = new Intent(ACTION_STOP_BACKGROUND_MUSIC);
            sendBroadcast(stopIntent);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            lose.dismiss();
        }
        else if(v==finishGameWin){
            Intent stopIntent = new Intent(ACTION_STOP_BACKGROUND_MUSIC);
            sendBroadcast(stopIntent);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            win.dismiss();
        }
        else if (v == userbag) {
            showUserPropertiesDialog(user0.getId());
        }
        else if (v==bot1bag) {
            showUserPropertiesDialog(user1.getId());
        }
        else if (v==bot2bag) {
            showUserPropertiesDialog(user2.getId());
        }
    }

    private void rollDice() {
        int[] diceImages= new int[]{R.drawable.d1, R.drawable.d2, R.drawable.d3, R.drawable.d4, R.drawable.d5, R.drawable.d6};
        Random random=new Random();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int diceValue1 = 0;
                int diceValue2 = 0;
                for (int i = 0; i < 30; i++) {
                    diceValue1 = random.nextInt(6) + 1;
                    diceValue2 = random.nextInt(6) + 1;
                    int dValue1=diceValue1;
                    int dValue2=diceValue2;
                    runOnUiThread(new Runnable() {// the background thread called to ui thread to change background imageview
                        @Override
                        public void run() {
                            dice1.setImageResource(diceImages[dValue1 - 1]);
                            dice2.setImageResource(diceImages[dValue2 - 1]);
                        }
                    });
                    try {
                        Thread.sleep(70);// for better animation
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(diceValue1==diceValue2){
                    checkDouble=true;
                }
                diceValueSum = (diceValue1 + diceValue2);
            }
        };
        Thread thread = new Thread(runnable);
        // Start the thread. This will cause the run() method to be called
        thread.start();
        if (mprollingdice != null && sound){ mprollingdice.start();}
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleDiceResult(diceValueSum);
            }
        },2900);

    }
    private void handleDiceResult(int diceValueSum){
        Player currentPlayer = players.get(currentPlayerTurn);
        movePlayer(currentPlayer, diceValueSum);
        handleCurrentCellAction(currentPlayer);
        botSellPropertySafety(currentPlayer);
        if(offerPause)switchToNextPlayerTurn();
        else offerPause =true;//to wait for user response from offer
    }
    private void movePlayer(Player currentPlayer, int diceValueSum) {
        currentPlayer.setPlayerCellIndex(currentPlayer.getPlayerCellIndex() + diceValueSum);
        repositionPlayerOnBoard(currentPlayer.getId());
    }
    private void handleCurrentCellAction(Player currentPlayer){
        if(isPropertyCell(currentPlayer.getPlayerCellIndex())){
            handlePropertyCell(currentPlayer);
        }//property
        else if(currentPlayer.getPlayerCellIndex()==4||currentPlayer.getPlayerCellIndex()==17) {
            handleTaxCell(currentPlayer);
        }//tax
        else if(currentPlayer.getPlayerCellIndex()==3||currentPlayer.getPlayerCellIndex()==12){
            handleSurpriseCell((int)((Math.random()*4)+1),currentPlayer.getId());
        }//surprise
        else if(currentPlayer.getPlayerCellIndex()==0) {
            handleStartCell(currentPlayer);
        }//start
        else if(currentPlayer.getPlayerCellIndex()==10){
            handleVacationCell(currentPlayer);
        }//vacation
        else if(currentPlayer.getPlayerCellIndex()==16){
            handleJailCell(currentPlayer);
        }//jail
    }
    private void handlePropertyCell(Player currentPlayer){
        Cell cell = cells.get(currentPlayer.getPlayerCellIndex());
        if(cell.isOwned()){
            if(cell.getOwnedById()!=currentPlayer.getId()){//is property owned by himself
                if(currentPlayer.getPlayerCoins()>=cell.getPayment()){
                    Toast.makeText(this, currentPlayer+" paid "+cell.getPayment()+"✧ for rent", Toast.LENGTH_SHORT).show();
                    updateCoins(currentPlayer, currentPlayer.getPlayerCoins() - cell.getPayment());
                    payTo(cell.getOwnedById(), cell.getPayment());
                    updatePlayerCoinsDisplay(cell.getOwnedById());
                }
                else kickPlayer(currentPlayer.getId());
            }
        }
        else {
            propertyOfferHandle(currentPlayer);
        }
    }
    private void handleTaxCell(Player currentPlayer){
        if(currentPlayer.getPlayerCoins()>=200){
            currentPlayer.setPlayerCoins(currentPlayer.getPlayerCoins()-200);
            updatePlayerCoinsDisplay(currentPlayer.getId());
            Toast.makeText(this, currentPlayer+" paid 200✧ for tax", Toast.LENGTH_SHORT).show();
        }
        else{
            kickPlayer(currentPlayer.getId());
        }
    }
    private void handleStartCell(Player currentPlayer){
        int amount =currentPlayer.getPlayerCoins()+cells.get(currentPlayer.getPlayerCellIndex()).getPayment();
        updateCoins(currentPlayer, amount);
    }
    private void handleVacationCell(Player currentPlayer){
        Toast.makeText(this, currentPlayer+" on vacation until the next turn", Toast.LENGTH_SHORT).show();
        currentPlayer.setTurnSkip(1);
    }
    private void handleJailCell(Player currentPlayer){
        Toast.makeText(this, currentPlayer+" are in jail for the next 2 turns", Toast.LENGTH_SHORT).show();
        currentPlayer.setPlayerCellIndexJail();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                repositionPlayerOnBoard(currentPlayer.getId());
            }
        }, 700);
        currentPlayer.setTurnSkip(2);
    }
    private void handleSurpriseCell(int random, int num){// check if need to kick someone
        Player currentPlayer = players.get(num-1);
        if(random==1){
            Toast.makeText(this, "Surprise-"+currentPlayer+" earn 50✧", Toast.LENGTH_SHORT).show();
            updateCoins(currentPlayer,currentPlayer.getPlayerCoins()+50);
        }
        else if(random==2){
            Toast.makeText(this, "Surprise-"+currentPlayer+" lost 30✧", Toast.LENGTH_SHORT).show();
            if(currentPlayer.getPlayerCoins()>=30){
                updateCoins(currentPlayer,currentPlayer.getPlayerCoins()-30);
            }
            else kickPlayer(currentPlayer.getId());
        }
        else if(random==3) {
            String surpriseDisplayText = "Surprise-"+currentPlayer+" go to Oxford but can't buy it+don't pay rent";
            Toast.makeText(this, surpriseDisplayText, Toast.LENGTH_SHORT).show();
            currentPlayer.setPlayerCellIndex(5);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    repositionPlayerOnBoard(num);
                }
            }, 400);
        }
        else if(random==4){
            Toast.makeText(this, "Surprise-"+currentPlayer+" earn 70✧", Toast.LENGTH_SHORT).show();
            updateCoins(currentPlayer,currentPlayer.getPlayerCoins()+70);
        }
    }
    private void botSellPropertySafety(Player currentPlayer){
        if(currentPlayer.getId() == user1.getId()&&currentPlayer.getPlayerCoins()<150&&!(currentPlayer.getOwn().isEmpty())){
            sellBotProperty(currentPlayer.getId());
        }
        else if(currentPlayer.getId() ==user2.getId()&&currentPlayer.getPlayerCoins()<100&&!(currentPlayer.getOwn().isEmpty())){
            sellBotProperty(currentPlayer.getId());
        }
    }
    private void switchToNextPlayerTurn() {
        currentPlayerTurn++;
        if (currentPlayerTurn > 2) {
            currentPlayerTurn = 0; // Reset to the first player (user)
        }
        if(checkDouble){
            checkDouble=false;
            if(currentPlayerTurn==0)
                currentPlayerTurn=2;
            else currentPlayerTurn--;

        }
        if(currentPlayerTurn==1&&!user1.getUserInGame())switchToNextPlayerTurn();
        else if(currentPlayerTurn==2&&!user2.getUserInGame()) switchToNextPlayerTurn();
        else if(currentPlayerTurn==0 && user0.getTurnSkip()!=0) {//check if user in jail/vacation
            user0.setTurnSkip(user0.getTurnSkip()-1);
            switchToNextPlayerTurn();
        }
        else if(currentPlayerTurn==1 && user1.getTurnSkip()!=0) {
            user1.setTurnSkip(user1.getTurnSkip()-1);
            switchToNextPlayerTurn();
        }
        else if(currentPlayerTurn==2 && user2.getTurnSkip()!=0) {
            user2.setTurnSkip(user2.getTurnSkip()-1);
            switchToNextPlayerTurn();
        }
        else if(currentPlayerTurn!=0) rollDice();//if bot
        else rolldice.setEnabled(true);
    }
    private void repositionPlayerOnBoard(int playerId){
        ImageView playerTool = getPlayerTool(playerId);
        Player currentPlayer = players.get(playerId-1);
        playerTool.setX(currentPlayer.getXCoords(currentPlayer.getPlayerCellIndex()));
        playerTool.setY(currentPlayer.getYCoords(currentPlayer.getPlayerCellIndex()));
        if(currentPlayer.getPlayerCellIndex()==20){
            currentPlayer.setPlayerCellIndex(6);
        }
    }
    private ImageView getPlayerTool(int playerId){
        if(playerId == user0.getId()){
            return usertool;
        }
        else if(playerId == user1.getId()){
            return bot1tool;
        }
        else if(playerId == user2.getId()){
            return bot2tool;
        }
        else{
            Log.d("or","invalid getPlayertool function");
            return usertool;
        }
    }
    private Boolean isPropertyCell(int num){
        return !Cell.NON_PROPERTY_CELLS.contains(num);
    }
    private void payTo(int num, int pay){
        players.get(num-1).setPlayerCoins(players.get(num-1).getPlayerCoins()+pay);
    }
    private void updatePlayerCoinsDisplay(int num){
        playerCoins.get(num-1).setText("coins:"+players.get(num-1).getPlayerCoins()+"✧");
    }
    private void updateCoins(Player currentPlayer, int amount){
        currentPlayer.setPlayerCoins(amount);
        updatePlayerCoinsDisplay(currentPlayer.getId());
    }
    private void showPropertyOfferDialog(){
        d= new Dialog(this);
        d.setContentView(R.layout.propertycell);
        d.setTitle("Property");
        d.setCancelable(false);
        name=(TextView)d.findViewById(R.id.propertyname);
        price=(TextView)d.findViewById(R.id.price);
        pay=(TextView)d.findViewById(R.id.pay);
        buy=(AppCompatButton)d.findViewById(R.id.buyProperty);
        cancel=(AppCompatButton)d.findViewById(R.id.cancel);
        name.setText(""+cells.get(user0.getPlayerCellIndex()).getName());
        price.setText("purchase price: "+cells.get(user0.getPlayerCellIndex()).getPrice());
        pay.setText("Rent payment profit: "+cells.get(user0.getPlayerCellIndex()).getPayment());
        setOfferPropertyColor();
        buy.setOnClickListener(this);
        cancel.setOnClickListener(this);
        d.show();
    }
    private void setOfferPropertyColor(){
        switch (cells.get(user0.getPlayerCellIndex()).getColor()) {
            case "blue":
                name.setBackgroundResource(R.color.blue);
                break;
            case "red":
                name.setBackgroundResource(R.color.red);
                break;
            case "yellow":
                name.setBackgroundResource(R.color.yellow);
                break;
            case "green":
                name.setBackgroundResource(R.color.green);
                break;
            case "white":
                name.setBackgroundResource(R.color.grey);
                break;
            default:
                name.setBackgroundResource(R.color.grey); // Fallback color
        }
    }
    private void propertyOfferHandle(Player currentPlayer){
        if (currentPlayer.getId()==user0.getId()){
            if (currentPlayer.getPlayerCoins()>=cells.get(currentPlayer.getPlayerCellIndex()).getPrice()){
                offerPause =false;
                showPropertyOfferDialog();
            }
        }
        else if (currentPlayer.getId() == user1.getId()){
            if(currentPlayer.getPlayerCoins()>200&& currentPlayer.getPlayerCoins()*0.7>cells.get(currentPlayer.getPlayerCellIndex()).getPrice())
                buyProperty(currentPlayer.getId());
        }
        else if (currentPlayer.getId() == user2.getId()){
            if(currentPlayer.getPlayerCoins()>250&& currentPlayer.getPlayerCoins()*0.8>cells.get(currentPlayer.getPlayerCellIndex()).getPrice())
                buyProperty(currentPlayer.getId());
        }
    }
    private void buyProperty(int num){
        if(num==user0.getId()){
            user0.setPlayerCoins(user0.getPlayerCoins()-cells.get(user0.getPlayerCellIndex()).getPrice());
            updatePlayerCoinsDisplay(user0.getId());
            cells.get(user0.getPlayerCellIndex()).setOwned(true);
            cells.get(user0.getPlayerCellIndex()).setOwnedBy(user0.getId());
            user0.addOwn(cells.get(user0.getPlayerCellIndex()));
            d.dismiss();
            Toast.makeText(this, "you bought "+cells.get(user0.getPlayerCellIndex()).getName(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchToNextPlayerTurn();
                }
            },900);
        }
        else if(num==user1.getId()|| num==user2.getId()){
            Player currentPlayer = players.get(num-1);
            currentPlayer.setPlayerCoins(currentPlayer.getPlayerCoins()-cells.get(currentPlayer.getPlayerCellIndex()).getPrice());
            updatePlayerCoinsDisplay(currentPlayer.getId());
            cells.get(currentPlayer.getPlayerCellIndex()).setOwned(true);
            cells.get(currentPlayer.getPlayerCellIndex()).setOwnedBy(currentPlayer.getId());
            currentPlayer.addOwn(cells.get(currentPlayer.getPlayerCellIndex()));
            Toast.makeText(this, currentPlayer+" bought "+cells.get(currentPlayer.getPlayerCellIndex()).getName(), Toast.LENGTH_SHORT).show();
        }
    }
    private void sellAllProperty(int num){
        Player currentPlayer = players.get(num-1);
        ArrayList<Cell>ownedCells =currentPlayer.getOwn();
        for (Cell cell: ownedCells){
            cell.setOwned(false);
            cell.setOwnedBy(0);
        }
        currentPlayer.clearPropertyOwnership();
    }//sell all property after player lost
    private void sellBotProperty(int num){
        Player currentPlayer = players.get(num-1);
        for(int i=0; i<cells.size();i++){
            if(currentPlayer.getOwn().get(0).getName().equals(cells.get(i).getName())){
                cells.get(i).setOwned(false);
                cells.get(i).setOwnedBy(0);
                break;
            }
        }
        Toast.makeText(this, currentPlayer+" sold "+currentPlayer.getOwn().get(0).getName(), Toast.LENGTH_SHORT).show();
        currentPlayer.setPlayerCoins(currentPlayer.getPlayerCoins()+currentPlayer.getOwn().get(0).getSellPrice());
        updatePlayerCoinsDisplay(currentPlayer.getId());
        currentPlayer.getOwn().remove(0);
    }
    private void kickPlayer(int num){
        if(num==user0.getId()){
            lose= new Dialog(this);
            lose.setContentView(R.layout.losemessage);
            lose.setTitle("You Lost");
            lose.setCancelable(false);
            tv1=(TextView)lose.findViewById(R.id.tv1);
            tv2=(TextView)lose.findViewById(R.id.tv2);
            finishGameLose =(Button)lose.findViewById(R.id.finishGameLose);
            finishGameLose.setOnClickListener(this);
            lose.show();
            currentPlayerTurn = 2;
        }
        else if(num==user1.getId()){
            user1.setUserIn(false);
            Toast.makeText(this, "bot1 lose and left the game", Toast.LENGTH_SHORT).show();
            user1.setPlayerCoins(0);
            updatePlayerCoinsDisplay(user1.getId());
            bot1tool.setVisibility(View.INVISIBLE);
            sellAllProperty(user1.getId());
        }
        else if(num==user2.getId()){
            user2.setUserIn(false);
            Toast.makeText(this, "bot2 lose and left the game", Toast.LENGTH_SHORT).show();
            user2.setPlayerCoins(0);
            updatePlayerCoinsDisplay(user2.getId());
            bot2tool.setVisibility(View.INVISIBLE);
            sellAllProperty(user2.getId());
        }
        if(!user1.getUserInGame()&&!user2.getUserInGame()) userWin();
    }
    private void userWin(){
        win= new Dialog(this);
        win.setContentView(R.layout.winmessage);
        win.setTitle("You Win");
        win.setCancelable(false);
        tv1=(TextView)win.findViewById(R.id.tv10);
        tv2=(TextView)win.findViewById(R.id.tv20);
        finishGameWin=(Button)win.findViewById(R.id.finishGameWin);
        crown=(ImageView)win.findViewById(R.id.crown) ;
        finishGameWin.setOnClickListener(this);
        win.show();
        startMovingAnimation();
    }
    private void showUserPropertiesDialog(int num){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.propertylist);
        dialog.setTitle("Properties display");
        dialog.setCancelable(true);
        ListView propertyListView = dialog.findViewById(R.id.propertyListView);
        ArrayList<Cell> userProperties = players.get(num-1).getOwn();
        if (userProperties.isEmpty()) {
            // Display default message when user has no properties
            showNoPropertiesMessage();
        }
        if(num==user0.getId()){
            if (!userProperties.isEmpty()){
                PropertyListAdapter adapter = new PropertyListAdapter(this, userProperties);
                propertyListView.setAdapter(adapter);
                propertyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        // Remove the property from the user's list
                        Cell property = adapter.getItem(position);
                        userProperties.remove(property);
                        // Update the ListView
                        adapter.remove(property);
                        adapter.notifyDataSetChanged();
                        user0.setOwn(userProperties);
                        user0.setPlayerCoins(user0.getPlayerCoins()+property.getSellPrice());
                        updatePlayerCoinsDisplay(user0.getId());//update user coin after selling
                        for(int x=0; x<cells.size(); x++){
                            if(property.getName().equals(cells.get(x).getName())){
                                cells.get(x).setOwned(false);
                                cells.get(x).setOwnedBy(0);//remove from user list and from game list ownership
                            }
                        }
                        return true;
                    }
                });
            }
        }
        else if(num==user1.getId()|| num==user2.getId()){
            if (!userProperties.isEmpty()){
                PropertyListAdapterBot adapter = new PropertyListAdapterBot(this, userProperties);
                propertyListView.setAdapter(adapter);
            }
        }
        dialog.show();
    }
    private void showNoPropertiesMessage(){
        TextView noPropertiesTextView = dialog.findViewById(R.id.noPropertiesTextView);
        noPropertiesTextView.setText("Player does not own any properties");
        noPropertiesTextView.setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.propertyListView).setVisibility(View.GONE);
    }
    private void readUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        DocumentReference docRef = fireStore.collection("users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String UName= documentSnapshot.getString("userName");
                    if(UName != null && !(UName.equals(""))) username.setText(UName);
                    else{
                        UName="player";
                        username.setText(UName);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User document does not exist", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error occurred while fetching the document
                Toast.makeText(getApplicationContext(), "Error retrieving user data", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void readImageNameData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        DocumentReference docRef = fireStore.collection("users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("imageName")) {
                        String imageName = documentSnapshot.getString("imageName");
                        if (imageName != null) {
                            readImageData1(imageName);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "imageName field does not exist", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User document does not exist", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error occurred while fetching the document
                Toast.makeText(getApplicationContext(), "Error retrieving user data", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void readImageData1(String imageName) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference imageRef = storage.getReference().child(imageName);
        try {
            File localFile = File.createTempFile("tempImage", ".jpg");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Convert the byte array to a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    userpic.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error occurred while downloading the image
                    Toast.makeText(getApplicationContext(), "Failed to download image", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                    // Update progress if needed
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    progressBar.setVisibility(View.GONE);//after photo been uploaded we go to next activity
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readSoundApproval() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        DocumentReference docRef = fireStore.collection("sound").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Document exists, retrieve the sound approval data
                    sound= Boolean.TRUE.equals(documentSnapshot.getBoolean("soundApprove"));
                } else {
                    // Document does not exist
                    Toast.makeText(getApplicationContext(), "Sound document does not exist", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error occurred while fetching the document
                Toast.makeText(getApplicationContext(), "Error retrieving sound approval", Toast.LENGTH_LONG).show();
            }
        });
    }
}
