package com.example.monopoly;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class activity_players extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener,avatarFragment.OnImageSelectedListener {

    EditText userName;
    Switch sound;
    AppCompatButton startGame;
    ImageButton avatar;
    FragmentManager fragmentManager;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar progressBar;
    Bitmap bitmapavatar= null;
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    Boolean approve = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        userName= findViewById(R.id.uname);
        avatar= findViewById(R.id.ibavatar);
        avatar.setOnClickListener(this);
        sound= findViewById(R.id.sound);
        sound.setOnCheckedChangeListener(this);
        startGame= findViewById(R.id.start);
        startGame.setOnClickListener(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBar= findViewById(R.id.progress_bar);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startBackgroundMusicService();
            approve = true;
        }
        else {
            stopBackgroundMusicService();
            approve = false;
        }
    }
    private void startBackgroundMusicService() {
        Intent serviceIntent = new Intent(this, BackgroundMusicService.class);
        startService(serviceIntent);
    }
    private void stopBackgroundMusicService() {
        Intent serviceIntent = new Intent(this, BackgroundMusicService.class);
        stopService(serviceIntent);
    }
    @Override
    public void onClick(View v) {
        if(v==startGame){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            startGame.setEnabled(false);
            sound.setEnabled(false);
            avatar.setEnabled(false);
            insertUserData();
            insertSoundApprove();
            uploadPicture();
        }
        else if (v==avatar){
            //open fragment
            fragmentManager = getSupportFragmentManager();//create fragment instance
            androidx.fragment.app.FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();//allow to add remove or replace any fragment
            avatarFragment avatarFragment = new avatarFragment();
            fragmentTransaction.replace(R.id.configure, avatarFragment, "avatarFragment");//replaces content of container view with the specified fragment
            //first one is what we replace second is what we put instead and last is name to identify the fragment
            fragmentTransaction.commit();//start
            startGame.setVisibility(View.GONE);
        }
    }
    private void uploadPicture() {
        if(bitmapavatar==null)
            bitmapavatar=BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.lionavatar);
        Uri imageUri = saveBitmapToFile(bitmapavatar);
        if (imageUri != null) {
            // Upload the image to storage reference
            StorageReference fileRef = storageReference.child(imageUri.getLastPathSegment());//create storageref that pointing details from savebitmaptofile func to here(name for example)
            progressBar.setVisibility(View.VISIBLE);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//image upload to storage
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the image download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Store the image name in the user document
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = Objects.requireNonNull(currentUser).getUid();
                            DocumentReference userRef = fireStore.collection("users").document(userId);
                            userRef.update("imageName", imageUri.getLastPathSegment())//update the image name in the firestore
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Image name update succeeded
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Image name update failed
                                            Toast.makeText(activity_players.this, "Failed to update image name", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity_players.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    // Update progress of uploading
                     double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                     progressBar.setProgress((int) progress);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressBar.setVisibility(View.GONE);//after photo been uploaded we go to next activity
                    Intent intent = new Intent(getApplication(), Game.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private Uri saveBitmapToFile(Bitmap bitmap) {
        // Get the directory path
        //ContentResolver provides access to the content model allowing to perform operations on various types of data
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image" + System.currentTimeMillis() + ".png");//name of image
        //highly unlikely that two connections will get the exact same name for the image file due to the use of System.currentTimeMillis() to generate a unique timestamp
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");//image type
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            OutputStream outputStream = contentResolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();//ensure all data is written well
            outputStream.close();
            return imageUri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onImageSelected(Bitmap imageBitmap) {
        avatar.setBackground(null);
        avatar.setImageBitmap(imageBitmap);
        bitmapavatar=imageBitmap;
        //close the fragment:
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("avatarFragment");
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        if (startGame.getVisibility() != View.VISIBLE) startGame.setVisibility(View.VISIBLE);
    }
    private void insertUserData() {
        String uName = userName.getText().toString();
        Map<String, Object> userData = new HashMap<>();
        userData.put("userName", uName);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        fireStore.collection("users").document(userId).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("or", "insert user data ok");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error adding user name", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void insertSoundApprove() {
        Map<String, Boolean> soundApprove = new HashMap<>();
        soundApprove.put("soundApprove", approve);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        fireStore.collection("sound").document(userId).set(soundApprove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error adding approve sound", Toast.LENGTH_LONG).show();
            }
        });
    }//after we chose if we want sound or not its upload true or false to the firestore
}
