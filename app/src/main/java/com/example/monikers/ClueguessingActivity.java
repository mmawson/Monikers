package com.example.monikers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClueguessingActivity extends AppCompatActivity {
    //The text view that has the timer countdown
    private TextView mTimerText;
    //The actual timer counting down
    private CountDownTimer mTimer;
    private Integer mTimerLengthSeconds;
    //True if we are the host of the game
    private boolean mAreWeHost;
    //The round number (1, 2, 3)
    private Integer mRoundNumber;
    //Which team is currently giving clues and guessing (1 or 2)
    private Integer mActiveTeamNum;
    String mGameDBPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clueguessing);

        mGameDBPath = getIntent().getStringExtra("gameDBPath");

        mAreWeHost = getIntent().getBooleanExtra("areWeHost", false);

        mRoundNumber = 1;
        mActiveTeamNum = 1;
        mTimerText = findViewById(R.id.timer);

        mTimerLengthSeconds = 30;

        SetupTimerToListenForDBChanges();
    }

    private void SetupTimerToListenForDBChanges(){
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference dbTimer = databaseReference.child(mGameDBPath).child("timeLeft");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Long timerEntry = (Long) dataSnapshot.getValue();
                mTimerText.setText(timerEntry.toString());
            }

            @Override
            public void onCancelled (DatabaseError databaseError)
            {
                Log.w("Monikers", databaseError.toException());
            }
        };
        dbTimer.addValueEventListener(postListener);
    }

    //Called when the round ends, due to the last moniker being guessed
    private void EndRound() {
        mTimer.cancel();
        Toast.makeText(this, "Round over!", Toast.LENGTH_LONG).show();
    }

    //Called when the turn ends, due to the timer running out, or the last moniker being guessed
    private void EndTurn() {
        Toast.makeText(this, "Turn over!", Toast.LENGTH_LONG).show();
    }
}