package com.example.monikers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private TextView mRoundBanner;
    //The actual timer counting down
    private CountDownTimer mTimer;
    private Integer mTimerLengthSeconds;
    //True if we are the host of the game
    private boolean mAreWeHost;
    //The round number (1, 2, 3)
    private Long mRoundNumber;
    //Which team is currently giving clues and guessing (1 or 2)
    private Integer mActiveTeamNum;
    //local variables that will be updated by the DB as the timer runs out
    Long mMinutesLeft;
    Long mSecondsLeft;
    String mGameDBPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clueguessing);

        mGameDBPath = getIntent().getStringExtra("gameDBPath");

        mAreWeHost = getIntent().getBooleanExtra("areWeHost", false);

        mRoundBanner = findViewById(R.id.roundNumberBanner);

        mMinutesLeft = 0L;
        mSecondsLeft = 0L;

        mRoundNumber = 1L;
        mActiveTeamNum = 1;
        mTimerText = findViewById(R.id.timer);

        mTimerLengthSeconds = 30;

        SetupTimerToListenForDBChanges();
        ListenForRoundNumberChange();
    }

    private void SetupTimerToListenForDBChanges(){
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference dbTimerSec = databaseReference.child(mGameDBPath).child("secondsLeft");
        DatabaseReference dbTimerMin = databaseReference.child(mGameDBPath).child("minutesLeft");


        ValueEventListener postListenerSec = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mSecondsLeft = (Long) dataSnapshot.getValue();

                String secondsStr = String.format("%02d", mSecondsLeft);
                mTimerText.setText(mMinutesLeft.toString() + ":" + secondsStr);
            }

            @Override
            public void onCancelled (DatabaseError databaseError)
            {
                Log.w("Monikers", databaseError.toException());
            }
        };
        ValueEventListener postListenerMin = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mMinutesLeft = (Long) dataSnapshot.getValue();
                String secondsStr = String.format("%02d", mSecondsLeft);

                mTimerText.setText(mMinutesLeft.toString() + ":" + secondsStr);
            }

            @Override
            public void onCancelled (DatabaseError databaseError)
            {
                Log.w("Monikers", databaseError.toException());
            }
        };

        dbTimerSec.addValueEventListener(postListenerSec);
        dbTimerMin.addValueEventListener(postListenerMin);
    }

    private void GoBackToHomeScreen()
    {
        Intent intent = new Intent(this, HomeActivityWithNavDrawer.class);
        startActivity(intent);
    }

    private void ListenForRoundNumberChange() {
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference dbRoundNum = databaseReference.child(mGameDBPath).child("roundNum");

        ValueEventListener postListenerRound = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mRoundNumber = (Long) dataSnapshot.getValue();
                mRoundBanner.setText("Round " + String.valueOf(mRoundNumber));
                if (mRoundNumber >= 4)
                {
                    GoBackToHomeScreen();
                }
            }

            @Override
            public void onCancelled (DatabaseError databaseError)
            {
                Log.w("Monikers", databaseError.toException());
            }
        };
        dbRoundNum.addValueEventListener(postListenerRound);
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