package com.example.monopoly;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected final ActivityResultHelper<Intent, ActivityResult> activityLauncher = ActivityResultHelper.registerActivityForResult(this);
    FirebaseAuth firebaseAuth;
    Button about;
    Button btnReg,btnLogin;//dialog buttons
    Button btnMainLogin,btnMainRegister;
    EditText etEmail,etPass;
    Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        about=(Button)findViewById(R.id.btnAbout);
        about.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btnMainLogin = (Button)findViewById(R.id.btnLogin);
        btnMainLogin.setOnClickListener(this);
        btnMainRegister = (Button)findViewById(R.id.btnRegister);
        btnMainRegister.setOnClickListener(this);
    }
    public static boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();//check if email is ok
        }
    }
    public void createRegisterDialog() {
        d= new Dialog(this);
        d.setContentView(R.layout.register);
        d.setTitle("Register");
        d.setCancelable(true);
        etEmail=(EditText)d.findViewById(R.id.etEmail);
        etPass=(EditText)d.findViewById(R.id.etPass);
        btnReg=(Button)d.findViewById(R.id.etReg);
        btnReg.setOnClickListener(this);
        d.show();
    }
    public void createLoginDialog() {
        d= new Dialog(this);
        d.setContentView(R.layout.login);
        d.setTitle("Login");
        d.setCancelable(true);
        etEmail=(EditText)d.findViewById(R.id.etEmail);
        etPass=(EditText)d.findViewById(R.id.etPass);
        btnLogin=(Button)d.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        d.show();
    }

    public void register() {
        if (isValidEmail(etEmail.getText().toString()) && !(etPass.getText().toString().isEmpty())) {
            firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                    }
                    d.dismiss();
                }
            });
        }
        else Toast.makeText(MainActivity.this, "invalid email or password", Toast.LENGTH_LONG).show();
    }
    public  void login() {
        firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "login_success", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    openSomeActivityForResult2(etEmail.getText().toString(), etPass.getText().toString());
                }
                else {
                    Toast.makeText(MainActivity.this, "login_failed", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                }
            }
        });
    }
    public void openSomeActivityForResult2(String email, String password) {
        Intent intent = new Intent(this, activity_players.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == btnMainRegister) {
            createRegisterDialog();
        }
        else if (v == btnMainLogin) {
            createLoginDialog();
        }
        else if (btnReg == v) {
            register();
        }
        else if (v == btnLogin) {
            login();
        }
        else if (v == about) {
            String url = "https://en.wikipedia.org/wiki/Monopoly_(game)";
            Intent abt = new Intent(Intent.ACTION_VIEW);
            abt.setData(Uri.parse(url));
            activityLauncher.launch(abt, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                }
            });
        }
    }
}