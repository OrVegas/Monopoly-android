package com.example.monopoly;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
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
    protected final ActivityResultHelper<Intent, ActivityResult> activityLauncher = ActivityResultHelper.registerActivityForResult(this);
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
    TextView usercoins, bot1coins,bot2coins;
    ArrayList<Cell>cells=new ArrayList<>();
    private int currentPlayerTurn = 0;
    Player user0=new Player(1);
    Player user1=new Player(2);
    Player user2=new Player(3);
    Dialog d, lose, win, dialog;
    TextView name, price, pay, tv1, tv2;
    Button  ok, ok1;
    AppCompatButton cancel, buy;
    Boolean checking=true, checkDouble=false;//checking is for user that land on property to wait for him to chose if he wants to buy or not
    ProgressBar progressBar;
    ImageButton userbag, bot1bag, bot2bag;
    public static final String ACTION_STOP_BACKGROUND_MUSIC = "com.example.monopoly.STOP_BACKGROUND_MUSIC";
    Animation animFirst, animSec,animSpin,animThir,animFour,animFive,animBigger;
    ImageView crown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        progressBar= findViewById(R.id.progressbar);
        readImageNameData();
        readUserData();
        readSoundApproval();
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
        else if(v==ok){
            Intent stopIntent = new Intent(ACTION_STOP_BACKGROUND_MUSIC);
            sendBroadcast(stopIntent);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            lose.dismiss();
        }
        else if(v==ok1){
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
        if (currentPlayerTurn == 0) {
            user0.setPlayerCellIndex(user0.getPlayerCellIndex()+diceValueSum);
            if(user0.getPlayerCellIndex()>=20){
                user0.setPlayerCellIndex(user0.getPlayerCellIndex()-20);
            }
            cellUserSend();//move the tool on board
            if (isPropertyCell(user0.getPlayerCellIndex())) {
                if (cells.get(user0.getPlayerCellIndex()).getOwned()) {//pay rent for other player
                    if(cells.get(user0.getPlayerCellIndex()).getOwnedBy()!=user0.getId()){//check if property own by himself for not paying himself
                        if(user0.getPlayerCoins()>=cells.get(user0.getPlayerCellIndex()).getPay()){
                            Toast.makeText(this, "you paid "+cells.get(user0.getPlayerCellIndex()).getPay()+"✧ for rent", Toast.LENGTH_SHORT).show();
                            user0.setPlayerCoins(user0.getPlayerCoins() - cells.get(user0.getPlayerCellIndex()).getPay());
                            updatePlayerCoins(user0.getId());
                            payTo(cells.get(user0.getPlayerCellIndex()).getOwnedBy(), cells.get(user0.getPlayerCellIndex()).getPay());
                            updatePlayerCoins(cells.get(user0.getPlayerCellIndex()).getOwnedBy());
                        }
                        else kickPlayer(user0.getId());
                    }
                } else {
                    if(user0.getPlayerCoins()>=cells.get(user0.getPlayerCellIndex()).getPrice()) {
                        checking=false;
                        dialogOfferProperty();
                    }
                }
            }//property
            else if(user0.getPlayerCellIndex()==4||user0.getPlayerCellIndex()==17) {
                if(user0.getPlayerCoins()>=200){
                    user0.setPlayerCoins(user0.getPlayerCoins()-200);
                    updatePlayerCoins(user0.getId());
                    Toast.makeText(this, "you paid 200✧ for tax", Toast.LENGTH_SHORT).show();
                }
                else{
                    kickPlayer(user0.getId());
                }
            }//tax
            else if(user0.getPlayerCellIndex()==3||user0.getPlayerCellIndex()==12){
                int random= (int)((Math.random()*4)+1);
                surpriseHandle(random,user0.getId());
            }//surprise
            else if(user0.getPlayerCellIndex()==0) {
                user0.setPlayerCoins(user0.getPlayerCoins()+cells.get(user0.getPlayerCellIndex()).getPay());
                updatePlayerCoins(user0.getId());
            }//start
            else if(user0.getPlayerCellIndex()==10){
                Toast.makeText(this, "You are on vacation until the next turn", Toast.LENGTH_SHORT).show();
                user0.setTurnSkip(user0.getId());
            }//vacation
            else if(user0.getPlayerCellIndex()==16){
                Toast.makeText(this, "You are in jail for the next 2 turns", Toast.LENGTH_SHORT).show();
                user0.setPlayerCellIndex(20);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cellUserSend();
                    }
                }, 1000);
                user0.setTurnSkip(2);
            }//gotojail
        }
        else if (currentPlayerTurn == 1) {
            user1.setPlayerCellIndex(user1.getPlayerCellIndex()+diceValueSum);
            if(user1.getPlayerCellIndex()>=20){
                user1.setPlayerCellIndex(user1.getPlayerCellIndex()-20);
            }
            cellBot1Send();
            if (isPropertyCell(user1.getPlayerCellIndex())) {
                if (cells.get(user1.getPlayerCellIndex()).getOwned()) {//pay rent for other player
                    if(cells.get(user1.getPlayerCellIndex()).getOwnedBy()!=user1.getId()) {//check if property own by himself for not paying himself
                        if (user1.getPlayerCoins() >= cells.get(user1.getPlayerCellIndex()).getPay()) {
                            Toast.makeText(this, "bot1 paid " + cells.get(user1.getPlayerCellIndex()).getPay() + "✧ for rent", Toast.LENGTH_SHORT).show();
                            user1.setPlayerCoins(user1.getPlayerCoins() - cells.get(user1.getPlayerCellIndex()).getPay());
                            updatePlayerCoins(user1.getId());
                            payTo(cells.get(user1.getPlayerCellIndex()).getOwnedBy(), cells.get(user1.getPlayerCellIndex()).getPay());
                            updatePlayerCoins(cells.get(user1.getPlayerCellIndex()).getOwnedBy());
                        } else kickPlayer(user1.getId());
                    }
                } else {
                    if(user1.getPlayerCoins()>200&& user1.getPlayerCoins()*0.7>cells.get(user1.getPlayerCellIndex()).getPrice()){
                        buyProperty(user1.getId());
                    }
                }
            }//property
            else if(user1.getPlayerCellIndex()==4||user1.getPlayerCellIndex()==17) {
                if(user1.getPlayerCoins()>=200){
                    user1.setPlayerCoins(user1.getPlayerCoins()-200);
                    updatePlayerCoins(user1.getId());
                    Toast.makeText(this, "you paid 200✧ for tax", Toast.LENGTH_SHORT).show();
                }
                else{
                    kickPlayer(user1.getId());
                }
            }//tax
            else if(user1.getPlayerCellIndex()==3||user1.getPlayerCellIndex()==12){
                int random= (int)((Math.random()*4)+1);
                surpriseHandle(random,user1.getId());
            }//surprise
            else if(user1.getPlayerCellIndex()==0) {
                user1.setPlayerCoins(user1.getPlayerCoins()+cells.get(user1.getPlayerCellIndex()).getPay());
                updatePlayerCoins(user1.getId());
            }//start
            else if(user1.getPlayerCellIndex()==10){
                Toast.makeText(this, "Bot1 on vacation until the next turn", Toast.LENGTH_SHORT).show();
                user1.setTurnSkip(1);
            }//vacation
            else if(user1.getPlayerCellIndex()==16){
                Toast.makeText(this, "Bot1 goes to jail for the next 2 turns", Toast.LENGTH_SHORT).show();
                user1.setPlayerCellIndex(20);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cellBot1Send();
                    }
                }, 1000);
                user1.setTurnSkip(2);
            }//gotojail

            if(user1.getPlayerCoins()<100&&!(user1.getOwn().isEmpty())){
                sellBotProperty(user1.getId());
            }
        }
        else if (currentPlayerTurn == 2) {
            user2.setPlayerCellIndex(user2.getPlayerCellIndex()+diceValueSum);
            if(user2.getPlayerCellIndex()>=20){
                user2.setPlayerCellIndex(user2.getPlayerCellIndex()-20);
            }
            cellBot2Send();
            if (isPropertyCell(user2.getPlayerCellIndex())) {
                if (cells.get(user2.getPlayerCellIndex()).getOwned()) {//pay rent for other player
                    if(cells.get(user2.getPlayerCellIndex()).getOwnedBy()!=user2.getId()) {//check if property own by himself for not paying himself
                        if(user2.getPlayerCoins()>=cells.get(user2.getPlayerCellIndex()).getPay()){
                            Toast.makeText(this, "bot2 paid "+cells.get(user2.getPlayerCellIndex()).getPay()+"✧ for rent", Toast.LENGTH_SHORT).show();
                            user2.setPlayerCoins(user2.getPlayerCoins() - cells.get(user2.getPlayerCellIndex()).getPay());
                            updatePlayerCoins(user2.getId());
                            payTo(cells.get(user2.getPlayerCellIndex()).getOwnedBy(), cells.get(user2.getPlayerCellIndex()).getPay());
                            updatePlayerCoins(cells.get(user2.getPlayerCellIndex()).getOwnedBy());
                        }
                        else kickPlayer(user2.getId());
                    }
                } else {
                    if(user2.getPlayerCoins()>200&& user2.getPlayerCoins()*0.8>cells.get(user2.getPlayerCellIndex()).getPrice()){
                        buyProperty(user2.getId());
                    }
                }
            }//property
            else if(user2.getPlayerCellIndex()==4||user2.getPlayerCellIndex()==17) {
                if(user2.getPlayerCoins()>=200){
                    user2.setPlayerCoins(user2.getPlayerCoins()-200);
                    updatePlayerCoins(user2.getId());
                    Toast.makeText(this, "bot2 paid 200✧ for tax", Toast.LENGTH_SHORT).show();
                }
                else{
                    kickPlayer(user2.getId());
                }
            }//tax
            else if(user2.getPlayerCellIndex()==3||user2.getPlayerCellIndex()==12){
                int random= (int)((Math.random()*4)+1);
                surpriseHandle(random,user2.getId());
            }//surprise
            else if(user2.getPlayerCellIndex()==0) {
                user2.setPlayerCoins(user2.getPlayerCoins()+cells.get(user2.getPlayerCellIndex()).getPay());
                updatePlayerCoins(user2.getId());
            }//start
            else if(user2.getPlayerCellIndex()==10){
                Toast.makeText(this, "Bot2 on vacation until the next turn", Toast.LENGTH_SHORT).show();
                user2.setTurnSkip(1);
            }//vacation
            else if(user2.getPlayerCellIndex()==16){
                Toast.makeText(this, "Bot2 goes to jail for the next 2 turns", Toast.LENGTH_SHORT).show();
                user2.setPlayerCellIndex(20);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cellBot2Send();
                    }
                }, 1000);
                user2.setTurnSkip(2);
            }//gotojail

            if(user2.getPlayerCoins()<100&&!(user2.getOwn().isEmpty())){
                sellBotProperty(user2.getId());
            }
        }
        if(checking)switchToNextPlayerTurn();
        else checking=true;//to wait until user chose to buy property or not
    }
    private void switchToNextPlayerTurn() {
        currentPlayerTurn++;
        if (currentPlayerTurn > 2) {
            currentPlayerTurn = 0; // Reset to the first player (user)
        }
        if(checkDouble){
            if(currentPlayerTurn==0)
                currentPlayerTurn=2;
            else currentPlayerTurn--;
            checkDouble=false;
        }
        if(currentPlayerTurn==1&&!user1.isUserIn())switchToNextPlayerTurn();
        else if(currentPlayerTurn==2&&!user2.isUserIn()) switchToNextPlayerTurn();
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
    private void cellUserSend(){
        if(user0.getPlayerCellIndex()==0){
            usertool.setX(1200);
            usertool.setY(795);
        }
        else if(user0.getPlayerCellIndex()==1){
            usertool.setX(1025);
            usertool.setY(795);
        }
        else if(user0.getPlayerCellIndex()==2){
            usertool.setX(845);
            usertool.setY(795);
        }
        else if(user0.getPlayerCellIndex()==3){
            usertool.setX(660);
            usertool.setY(795);
        }
        else if(user0.getPlayerCellIndex()==4){
            usertool.setX(480);
            usertool.setY(795);
        }
        else if(user0.getPlayerCellIndex()==5){
            usertool.setX(300);
            usertool.setY(795);
        }
        else if(user0.getPlayerCellIndex()==6){
            usertool.setX(10);
            usertool.setY(755);
        }
        else if(user0.getPlayerCellIndex()==7){
            usertool.setX(130);
            usertool.setY(630);
        }
        else if(user0.getPlayerCellIndex()==8){
            usertool.setX(130);
            usertool.setY(450);
        }
        else if(user0.getPlayerCellIndex()==9){
            usertool.setX(130);
            usertool.setY(270);
        }
        else if(user0.getPlayerCellIndex()==10){
            usertool.setX(130);
            usertool.setY(80);
        }
        else if(user0.getPlayerCellIndex()==11){
            usertool.setX(300);
            usertool.setY(80);
        }
        else if(user0.getPlayerCellIndex()==12){
            usertool.setX(480);
            usertool.setY(80);
        }
        else if(user0.getPlayerCellIndex()==13){
            usertool.setX(660);
            usertool.setY(70);
        }
        else if(user0.getPlayerCellIndex()==14){
            usertool.setX(840);
            usertool.setY(80);
        }
        else if(user0.getPlayerCellIndex()==15){
            usertool.setX(1020);
            usertool.setY(80);
        }
        else if(user0.getPlayerCellIndex()==16){
            usertool.setX(1200);
            usertool.setY(80);
        }
        else if(user0.getPlayerCellIndex()==17){
            usertool.setX(1200);
            usertool.setY(260);
        }
        else if(user0.getPlayerCellIndex()==18){
            usertool.setX(1200);
            usertool.setY(420);
        }
        else if(user0.getPlayerCellIndex()==19){
            usertool.setX(1200);
            usertool.setY(620);
        }
        else if(user0.getPlayerCellIndex()==20){
            usertool.setX(100);
            usertool.setY(755);
            user0.setPlayerCellIndex(6);
        }
    }
    private void cellBot1Send(){
        if(user1.getPlayerCellIndex()==0){
            bot1tool.setX(1120);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==1){
            bot1tool.setX(950);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==2){
            bot1tool.setX(760);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==3){
            bot1tool.setX(585);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==4){
            bot1tool.setX(400);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==5){
            bot1tool.setX(220);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==6){
            bot1tool.setX(30);
            bot1tool.setY(800);
        }
        else if(user1.getPlayerCellIndex()==7){
            bot1tool.setX(40);
            bot1tool.setY(630);
        }
        else if(user1.getPlayerCellIndex()==8){
            bot1tool.setX(40);
            bot1tool.setY(450);
        }
        else if(user1.getPlayerCellIndex()==9){
            bot1tool.setX(40);
            bot1tool.setY(270);
        }
        else if(user1.getPlayerCellIndex()==10){
            bot1tool.setX(40);
            bot1tool.setY(88);
        }
        else if(user1.getPlayerCellIndex()==11){
            bot1tool.setX(220);
            bot1tool.setY(88);
        }
        else if(user1.getPlayerCellIndex()==12){
            bot1tool.setX(400);
            bot1tool.setY(88);
        }
        else if(user1.getPlayerCellIndex()==13){
            bot1tool.setX(580);
            bot1tool.setY(73);
        }
        else if(user1.getPlayerCellIndex()==14){
            bot1tool.setX(760);
            bot1tool.setY(88);
        }
        else if(user1.getPlayerCellIndex()==15){
            bot1tool.setX(950);
            bot1tool.setY(88);
        }
        else if(user1.getPlayerCellIndex()==16){
            bot1tool.setX(1120);
            bot1tool.setY(88);
        }
        else if(user1.getPlayerCellIndex()==17){
            bot1tool.setX(1120);
            bot1tool.setY(270);
        }
        else if(user1.getPlayerCellIndex()==18){
            bot1tool.setX(1120);
            bot1tool.setY(430);
        }
        else if(user1.getPlayerCellIndex()==19){
            bot1tool.setX(1120);
            bot1tool.setY(630);
        }
        else if(user1.getPlayerCellIndex()==20){
            bot1tool.setX(100);
            bot1tool.setY(800);
            user1.setPlayerCellIndex(6);
        }
    }
    private void cellBot2Send(){
        if(user2.getPlayerCellIndex()==0){
            bot2tool.setX(1160);
            bot2tool.setY(830);
        }
        else if(user2.getPlayerCellIndex()==1){
            bot2tool.setX(990);
            bot2tool.setY(830);
        }
        else if(user2.getPlayerCellIndex()==2){
            bot2tool.setX(815);
            bot2tool.setY(830);
        }
        else if(user2.getPlayerCellIndex()==3){
            bot2tool.setX(630);
            bot2tool.setY(830);
        }
        else if(user2.getPlayerCellIndex()==4){
            bot2tool.setX(450);
            bot2tool.setY(830);
        }
        else if(user2.getPlayerCellIndex()==5){
            bot2tool.setX(260);
            bot2tool.setY(830);
        }
        else if(user2.getPlayerCellIndex()==6){
            bot2tool.setX(25);
            bot2tool.setY(850);
        }
        else if(user2.getPlayerCellIndex()==7){
            bot2tool.setX(90);
            bot2tool.setY(650);
        }
        else if(user2.getPlayerCellIndex()==8){
            bot2tool.setX(90);
            bot2tool.setY(470);

        }
        else if(user2.getPlayerCellIndex()==9){
            bot2tool.setX(90);
            bot2tool.setY(290);
        }
        else if(user2.getPlayerCellIndex()==10){
            bot2tool.setX(90);
            bot2tool.setY(115);
        }
        else if(user2.getPlayerCellIndex()==11){
            bot2tool.setX(260);
            bot2tool.setY(115);
        }
        else if(user2.getPlayerCellIndex()==12){
            bot2tool.setX(450);
            bot2tool.setY(115);
        }
        else if(user2.getPlayerCellIndex()==13){
            bot2tool.setX(630);
            bot2tool.setY(105);
        }
        else if(user2.getPlayerCellIndex()==14){
            bot2tool.setX(810);
            bot2tool.setY(115);
        }
        else if(user2.getPlayerCellIndex()==15){
            bot2tool.setX(990);
            bot2tool.setY(115);
        }
        else if(user2.getPlayerCellIndex()==16){
            bot2tool.setX(1170);
            bot2tool.setY(115);
        }
        else if(user2.getPlayerCellIndex()==17){
            bot2tool.setX(1170);
            bot2tool.setY(300);
        }
        else if(user2.getPlayerCellIndex()==18){
            bot2tool.setX(1170);
            bot2tool.setY(460);
        }
        else if(user2.getPlayerCellIndex()==19){
            bot2tool.setX(1170);
            bot2tool.setY(650);
        }
        else if(user2.getPlayerCellIndex()==20){
            bot2tool.setX(100);
            bot2tool.setY(850);
            user2.setPlayerCellIndex(6);
        }
    }
    private Boolean isPropertyCell(int num){
        if (num==0||num==3||num==4||num==6||num==10||num==12||num==16||num==17) return false;
        else return true;
    }
    private void payTo(int num, int pay){
        if(num==user0.getId()){
            user0.setPlayerCoins(user0.getPlayerCoins()+pay);
        }
        else if(num==user1.getId()){
            user1.setPlayerCoins(user1.getPlayerCoins()+pay);
        }
        else if(num==user2.getId()){
            user2.setPlayerCoins(user2.getPlayerCoins()+pay);
        }
        else Log.d("or","error in setting owned by who property");
    }
    private void updatePlayerCoins(int num){
        if(num==user0.getId()){
            usercoins.setText("coins:"+user0.getPlayerCoins()+"✧");
        }
        else if(num==user1.getId()){
            bot1coins.setText("coins:"+user1.getPlayerCoins()+"✧");
        }
        else if(num==user2.getId()){
            bot2coins.setText("coins:"+user2.getPlayerCoins()+"✧");
        }
    }
    private void dialogOfferProperty(){
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
        pay.setText("Rent payment profit: "+cells.get(user0.getPlayerCellIndex()).getPay());
        if(cells.get(user0.getPlayerCellIndex()).getColor().equals("blue")){
            name.setBackgroundResource(R.color.blue);
        }
        else if(cells.get(user0.getPlayerCellIndex()).getColor().equals("red")){
            name.setBackgroundResource(R.color.red);
        }
        else if(cells.get(user0.getPlayerCellIndex()).getColor().equals("yellow")){
            name.setBackgroundResource(R.color.yellow);
        }
        else if(cells.get(user0.getPlayerCellIndex()).getColor().equals("green")){
            name.setBackgroundResource(R.color.green);
        }
        else if(cells.get(user0.getPlayerCellIndex()).getColor().equals("white")){
            name.setBackgroundResource(R.color.grey);
        }
        buy.setOnClickListener(this);
        cancel.setOnClickListener(this);
        d.show();
    }
    private void buyProperty(int num){
        if(num==user0.getId()){
            user0.setPlayerCoins(user0.getPlayerCoins()-cells.get(user0.getPlayerCellIndex()).getPrice());
            updatePlayerCoins(user0.getId());
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
        else if(num==user1.getId()){
            user1.setPlayerCoins(user1.getPlayerCoins()-cells.get(user1.getPlayerCellIndex()).getPrice());
            updatePlayerCoins(user1.getId());
            cells.get(user1.getPlayerCellIndex()).setOwned(true);
            cells.get(user1.getPlayerCellIndex()).setOwnedBy(user1.getId());
            user1.addOwn(cells.get(user1.getPlayerCellIndex()));
            Toast.makeText(this, "bot1 bought "+cells.get(user1.getPlayerCellIndex()).getName(), Toast.LENGTH_SHORT).show();
        }
        else if(num==user2.getId()){
            user2.setPlayerCoins(user2.getPlayerCoins()-cells.get(user2.getPlayerCellIndex()).getPrice());
            updatePlayerCoins(user2.getId());
            cells.get(user2.getPlayerCellIndex()).setOwned(true);
            cells.get(user2.getPlayerCellIndex()).setOwnedBy(user2.getId());
            user2.addOwn(cells.get(user2.getPlayerCellIndex()));
            Toast.makeText(this, "bot2 bought "+cells.get(user2.getPlayerCellIndex()).getName(), Toast.LENGTH_SHORT).show();
        }
    }
    private void sellAllProperty(int num){
        Log.d("or", "sell all1");
        if(num==user1.getId()){
            ArrayList<Cell>arr=user1.getOwn();
            for(int i=0; i<user1.getOwn().size();i++){
                Log.d("or","ok");
                for(int x=0; x<cells.size(); x++){
                    if(arr.get(i).getName().equals(cells.get(x).getName())){
                        cells.get(x).setOwned(false);
                        cells.get(x).setOwnedBy(0);
                        break;//after removing break second loop
                    }
                }
            }
            user1.clearOwn();
        }
        else if(num==user2.getId()){
            ArrayList<Cell>arr=user2.getOwn();
            for(int i=0; i<user2.getOwn().size();i++){
                for(int x=0; x<cells.size(); x++){
                    if(arr.get(i).getName().equals(cells.get(x).getName())){
                        cells.get(x).setOwned(false);
                        cells.get(x).setOwnedBy(0);
                        break;//after removing break second loop
                    }
                }
            }
            user2.clearOwn();
        }
    }//sell all property after player lost
    private void sellBotProperty(int num){
        if(num==2){
            for(int i=0; i<cells.size();i++){
                if(user1.getOwn().get(0).getName().equals(cells.get(i).getName())){
                    cells.get(i).setOwned(false);
                    cells.get(i).setOwnedBy(0);
                    break;
                }
            }
            Toast.makeText(this, "bot1 sold "+user1.getOwn().get(0).getName(), Toast.LENGTH_SHORT).show();
            user1.setPlayerCoins(user1.getPlayerCoins()+user1.getOwn().get(0).getSell());
            updatePlayerCoins(user1.getId());
            user1.getOwn().remove(0);

        }
        else if(num==3){
            for(int i=0; i<cells.size();i++){
                if(user2.getOwn().get(0).getName().equals(cells.get(i).getName())){
                    cells.get(i).setOwned(false);
                    cells.get(i).setOwnedBy(0);
                    break;
                }
            }
            Toast.makeText(this, "bot2 sold "+user2.getOwn().get(0).getName(), Toast.LENGTH_SHORT).show();
            user2.setPlayerCoins(user2.getPlayerCoins()+user2.getOwn().get(0).getSell());
            updatePlayerCoins(user2.getId());
            user2.getOwn().remove(0);
        }
    }
    private void surpriseHandle(int random,int num){// check if need to kick someone
        if(random==1){
            if(num==1){
                Toast.makeText(this, "Surprise-you earn 50✧", Toast.LENGTH_SHORT).show();
                user0.setPlayerCoins(user0.getPlayerCoins()+50);
                updatePlayerCoins(user0.getId());
            }
            else if(num==2){
                Toast.makeText(this, "Surprise-bot1 earn 50✧", Toast.LENGTH_SHORT).show();
                user1.setPlayerCoins(user1.getPlayerCoins()+50);
                updatePlayerCoins(user1.getId());
            }
            else if(num==3){
                Toast.makeText(this, "Surprise-bot2 earn 50✧", Toast.LENGTH_SHORT).show();
                user2.setPlayerCoins(user2.getPlayerCoins()+50);
                updatePlayerCoins(user2.getId());
            }
        }
        else if(random==2){
            if(num==1){
                Toast.makeText(this, "Surprise-you lost 30✧", Toast.LENGTH_SHORT).show();
                if(user0.getPlayerCoins()>=30){
                    user0.setPlayerCoins(user0.getPlayerCoins()-30);
                    updatePlayerCoins(user0.getId());
                }
                else kickPlayer(user0.getId());
            }
            else if(num==2){
                Toast.makeText(this, "Surprise-bot1 lost 30✧", Toast.LENGTH_SHORT).show();
                if(user1.getPlayerCoins()>=30){
                    user1.setPlayerCoins(user1.getPlayerCoins()-30);
                    updatePlayerCoins(user1.getId());
                }
                else kickPlayer(user1.getId());
            }
            else if(num==3){
                Toast.makeText(this, "Surprise-bot2 lost 30✧", Toast.LENGTH_SHORT).show();
                if(user2.getPlayerCoins()>=30){
                    user2.setPlayerCoins(user2.getPlayerCoins()-30);
                    updatePlayerCoins(user2.getId());
                }
                else kickPlayer(user2.getId());
            }
        }
        else if(random==3) {
            if(num==1){
                Toast.makeText(this, "Surprise-go to Oxford-can't buy it+don't pay rent", Toast.LENGTH_SHORT).show();
                user0.setPlayerCellIndex(5);
                cellUserSend();
            }
            else if(num==2){
                Toast.makeText(this, "Surprise-bot1 go to Oxford but can't buy it", Toast.LENGTH_SHORT).show();
                user1.setPlayerCellIndex(5);
                cellBot1Send();
            }
            else if(num==3){
                Toast.makeText(this, "Surprise-bot2 go to Oxford but can't buy it", Toast.LENGTH_SHORT).show();
                user2.setPlayerCellIndex(5);
                cellBot2Send();
            }
        }
        else if(random==4){
            if(num==1){
                Toast.makeText(this, "Surprise-you earn 70✧", Toast.LENGTH_SHORT).show();
                user0.setPlayerCoins(user0.getPlayerCoins()+70);
                updatePlayerCoins(user0.getId());
            }
            else if(num==2){
                Toast.makeText(this, "Surprise-bot1 earn 70✧", Toast.LENGTH_SHORT).show();
                user1.setPlayerCoins(user1.getPlayerCoins()+70);
                updatePlayerCoins(user1.getId());
            }
            else if(num==3){
                Toast.makeText(this, "Surprise-bot2 earn 70✧", Toast.LENGTH_SHORT).show();
                user2.setPlayerCoins(user2.getPlayerCoins()+70);
                updatePlayerCoins(user2.getId());
            }
        }
    }
    private void kickPlayer(int num){
        if(num==user0.getId()){
            lose= new Dialog(this);
            lose.setContentView(R.layout.losemessage);
            lose.setTitle("You Lost");
            lose.setCancelable(false);
            tv1=(TextView)lose.findViewById(R.id.tv1);
            tv2=(TextView)lose.findViewById(R.id.tv2);
            ok=(Button)lose.findViewById(R.id.ok);
            ok.setOnClickListener(this);
            lose.show();
        }
        else if(num==user1.getId()){
            user1.setUserIn(false);
            Toast.makeText(this, "bot1 lose and left the game", Toast.LENGTH_SHORT).show();
            user1.setPlayerCoins(0);
            updatePlayerCoins(user1.getId());
            bot1tool.setVisibility(View.INVISIBLE);
            sellAllProperty(user1.getId());
        }
        else if(num==user2.getId()){
            user2.setUserIn(false);
            Toast.makeText(this, "bot2 lose and left the game", Toast.LENGTH_SHORT).show();
            user2.setPlayerCoins(0);
            updatePlayerCoins(user2.getId());
            bot2tool.setVisibility(View.INVISIBLE);
            sellAllProperty(user2.getId());
        }
        if(!user1.isUserIn()&&!user2.isUserIn()) userWin();
    }
    private void userWin(){
        win= new Dialog(this);
        win.setContentView(R.layout.winmessage);
        win.setTitle("You Win");
        win.setCancelable(false);
        tv1=(TextView)win.findViewById(R.id.tv10);
        tv2=(TextView)win.findViewById(R.id.tv20);
        ok1=(Button)win.findViewById(R.id.ok1);
        crown=(ImageView)win.findViewById(R.id.crown) ;
        ok1.setOnClickListener(this);
        win.show();
        startMovingAnimation();
    }
    private void showUserPropertiesDialog(int num){
        dialog = new Dialog(this);
        if(num==user0.getId()){
            dialog.setContentView(R.layout.propertylist);
            dialog.setTitle("Properties display");
            dialog.setCancelable(true);
            ListView propertyListView = dialog.findViewById(R.id.propertyListView);
            ArrayList<Cell> userProperties = user0.getOwn();
            if (userProperties.isEmpty()) {
                // Display default message when user has no properties
                TextView noPropertiesTextView = dialog.findViewById(R.id.noPropertiesTextView);
                noPropertiesTextView.setText("Player does not own any properties");
                noPropertiesTextView.setVisibility(View.VISIBLE);
                propertyListView.setVisibility(View.GONE);
            }
            else{
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
                        user0.setPlayerCoins(user0.getPlayerCoins()+property.getSell());
                        updatePlayerCoins(user0.getId());//update user coin after selling
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
        else if(num==user1.getId()){
            dialog.setContentView(R.layout.propertylist);
            dialog.setTitle("Properties display");
            dialog.setCancelable(true);
            ListView propertyListView = dialog.findViewById(R.id.propertyListView);
            ArrayList<Cell> userProperties = user1.getOwn();
            if (userProperties.isEmpty()) {
                // Display default message when user has no properties
                TextView noPropertiesTextView = dialog.findViewById(R.id.noPropertiesTextView);
                noPropertiesTextView.setText("Player does not own any properties");
                noPropertiesTextView.setVisibility(View.VISIBLE);
                propertyListView.setVisibility(View.GONE);
            }
            else{
                PropertyListAdapterBot adapter = new PropertyListAdapterBot(this, userProperties);
                propertyListView.setAdapter(adapter);
            }
        }
        else if(num==user2.getId()){
            dialog.setContentView(R.layout.propertylist);
            dialog.setTitle("Properties display");
            dialog.setCancelable(true);
            ListView propertyListView = dialog.findViewById(R.id.propertyListView);
            ArrayList<Cell> userProperties = user2.getOwn();
            if (userProperties.isEmpty()) {
                // Display default message when user has no properties
                TextView noPropertiesTextView = dialog.findViewById(R.id.noPropertiesTextView);
                noPropertiesTextView.setText("Player does not own any properties");
                noPropertiesTextView.setVisibility(View.VISIBLE);
                propertyListView.setVisibility(View.GONE);
            }
            else {
                PropertyListAdapterBot adapter = new PropertyListAdapterBot(this, userProperties);
                propertyListView.setAdapter(adapter);
            }
        }
        dialog.show();
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
