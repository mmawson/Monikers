package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CluegivingActivity extends AppCompatActivity {
    //Full list of words we're playing with
    private ArrayList<String> mMonikerList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluegiving);

        //Initialize with dummy words for now
        mMonikerList = new ArrayList<String>();
        mMonikerList.add("Alpha");
        mMonikerList.add("Bravo");
        mMonikerList.add("Charlie");
        mMonikerList.add("Delta");
        mMonikerList.add("Echo");
        mMonikerList.add("Foxtrot");
        mMonikerList.add("Golf");

        mNumWordsInDeck = mMonikerList.size();
        mNumWordsCorrect = 0;
        mRoundNumber = 1;
        mActiveTeamNum = 1;
        mCurrentMonikerIndex = 0;

        SetupGame();
    }

    private void SetupGame() {
        //Randomize list before we start
        Collections.shuffle(mMonikerList);

        ((TextView) findViewById(R.id.numGuessedCorrect)).setText("0");
        DecrementMonikersLeft();
        DisplayCurrentWord();
    }

    //Called when a player hits the 'Skip' button
    public void SkipMoniker(View v) {
        //Move this word to the back of the list, so they will only hit it again if we're out of words
        Collections.swap(mMonikerList, mCurrentMonikerIndex, mMonikerList.size() - 1);
        DisplayCurrentWord();
    }

    //Called when a player hits the 'Correct' button
    public void CorrectMoniker(View v) {
        if (mNumWordsInDeck == 0) { return; }
        IncrementNumCorrect();
        DecrementMonikersLeft();
        ++mCurrentMonikerIndex;
        DisplayCurrentWord();
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