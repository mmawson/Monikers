package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HostJoinGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_join_game);
    }

    public void hostGame(View v) {
        String gameName = "TEST Game Name";
        String password = "TEST password";
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference thisGame = databaseReference.child("games").child(gameName).child("password");

        thisGame.push().setValue(password);

        goToNextActivity();
    }

    public void joinGame(View v){
        goToNextActivity();
    }

    private void goToNextActivity() {
        Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
    }
}