package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if(currentUser==null){
                    Toast.makeText(MainActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, SignupLogin.class));
                    finish();
                }
                else{
                    startActivity(new Intent(MainActivity.this, HomeActivityWithNavDrawer.class));
                    finish();
                }
            }

            //}
        }.start();
    }
}