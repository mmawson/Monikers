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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class CluegivingActivity extends AppCompatActivity {
    //Full list of words we're playing with
    private ArrayList<String> mMonikerList;
    //The text view that has the timer countdown
    private TextView mTimerText;
    //The actual timer counting down
    private CountDownTimer mTimer;
    //How many words are left to be guessed this round
    private Integer mNumWordsInDeck;
    //How many words have been guessed correctly this TURN
    private Integer mNumWordsCorrect;
    //The round number (1, 2, 3)
    private Integer mRoundNumber;
    //Which team is currently giving clues and guessing (1 or 2)
    private Integer mActiveTeamNum;
    //The Moniker that the player is currently giving clues for (by index)
    private Integer mCurrentMonikerIndex;
    private Integer mTimerLengthSeconds;
    String mWordsDBPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluegiving);

        mWordsDBPath = getIntent().getStringExtra("wordsDBPath");

        //Will be initialized with words from firebase real-time database
        mMonikerList = new ArrayList<String>();

        mNumWordsCorrect = 0;
        mRoundNumber = 1;
        mActiveTeamNum = 1;
        mCurrentMonikerIndex = 0;
        mTimerText = findViewById(R.id.timer);

        mTimerLengthSeconds = 30;

        SetupGame();
    }

    private void SetupGame() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference wordsRef = database.getReference(mWordsDBPath);

        wordsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Monikers", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot wordData = task.getResult();
                    for (DataSnapshot word : wordData.getChildren()) {
                        mMonikerList.add(word.getValue().toString());
                    }

                    //Randomize list before we start
                    Collections.shuffle(mMonikerList);

                    //Now that we have the words, we can initialize the number of words left in deck
                    mNumWordsInDeck = mMonikerList.size();

                    DecrementMonikersLeft();
                    DisplayCurrentWord();
                    StartTimer();

                    ((TextView) findViewById(R.id.numGuessedCorrect)).setText("0");
                    Log.d("Monikers", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }

    public void StartTimer() {
        mTimerText.setText(mTimerLengthSeconds.toString());
        Log.d("Monikers", "Starting timer!");
        mTimer = new CountDownTimer(mTimerLengthSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                Double timeLeft = Math.ceil(millisUntilFinished / 1000.0);
                Integer timeLeftInt = timeLeft.intValue();
                mTimerText.setText(timeLeftInt.toString());
            }

            public void onFinish() {
                mTimerText.setText("0");
                EndTurn();
            }
        };
        mTimer.start();
    }

    //Called when a player hits the 'Skip' button
    public void SkipMoniker(View v) {
        //Move this word to the back of the list, so they will only hit it again if we're out of words
        mMonikerList.add(mMonikerList.get(mCurrentMonikerIndex));
        mMonikerList.remove(mCurrentMonikerIndex.intValue());

        DisplayCurrentWord();
    }

    //Called when a player hits the 'Correct' button
    public void CorrectMoniker(View v) {
        if (mNumWordsInDeck == 0) { EndTurn(); EndRound(); return; }
        IncrementNumCorrect();
        DecrementMonikersLeft();
        ++mCurrentMonikerIndex;
        DisplayCurrentWord();
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

    //Change the text to display the word we are currently on
    private void DisplayCurrentWord() {
        ((TextView) findViewById(R.id.monikerToGuess)).setText(mMonikerList.get(mCurrentMonikerIndex));
    }

    //Increment the number of words that have been gotten correct this round
    private void IncrementNumCorrect() {
        ++mNumWordsCorrect;
        ((TextView) findViewById(R.id.numGuessedCorrect)).setText(mNumWordsCorrect.toString());
    }

    //Decrement the display showing the number of monikers left to guess
    private void DecrementMonikersLeft() {
        --mNumWordsInDeck;
        ((TextView) findViewById(R.id.numMonikersLeft)).setText(mNumWordsInDeck.toString());
    }
}