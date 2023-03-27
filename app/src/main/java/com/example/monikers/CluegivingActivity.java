package com.example.monikers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

public class CluegivingActivity extends AppCompatActivity {
    static int REQUEST_FOR_POST_NOTIFICATIONS = 0011;
    //Full list of words we're playing with
    private ArrayList<String> mMonikerList;
    //Subset of the Monikers being used for this turn
    private ArrayList<String> mCurrentMonikerList;
    //The text view that has the timer countdown
    private TextView mTimerText;
    //The actual timer counting down
    private CountDownTimer mTimer;
    //How many words are left to be guessed this round
    private Integer mNumWordsInDeck;
    //How many words have been guessed correctly this TURN
    private Integer mNumWordsCorrect;
    private boolean mPostPermissionsGranted;
    //The round number (1, 2, 3)
    private Long mRoundNumber;
    //Which team is currently giving clues and guessing (1 or 2)
    private Integer mActiveTeamNum;
    //The Moniker that the player is currently giving clues for (by index)
    private Integer mCurrentMonikerIndex;
    private Integer mTimerLengthSeconds;
    private TextView mRoundBanner;
    //Amount of time you get for a turn
    Integer mNotificationId;
    String mTimePerTurn;
    //True if we are the host of the game
    private boolean mAreWeHost;

    //local variables that will be updated by the DB as the timer runs out
    Long mMinutesLeft;
    Long mSecondsLeft;
    String mGameDBPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluegiving);

        mPostPermissionsGranted = false;
        mNotificationId = 0;

        mGameDBPath = getIntent().getStringExtra("gameDBPath");
        mTimePerTurn = getIntent().getStringExtra("timePerTurn");
        if (mTimePerTurn == null)
        {
            mTimePerTurn = "1:00";
        }

        mAreWeHost = getIntent().getBooleanExtra("areWeHost", false);

        mRoundBanner = findViewById(R.id.roundNumberBanner);

        mMinutesLeft = 0L;
        mSecondsLeft = 0L;

        //Will be initialized with words from firebase real-time database
        mMonikerList = new ArrayList<String>();
        mCurrentMonikerList = new ArrayList<String>();

        ((TextView) findViewById(R.id.monikerToGuess)).setOnTouchListener(new OnSwipeTouchListener(CluegivingActivity.this) {
            public void onSwipeRight() {
                CorrectMoniker(new View(getApplicationContext()));
            }
            public void onSwipeLeft() {
                SkipMoniker(new View(getApplicationContext()));
            }
        });


        mNumWordsCorrect = 0;
        mRoundNumber = 0L;
        IncrementRoundNumber();
        mActiveTeamNum = 1;
        mCurrentMonikerIndex = 0;
        mTimerText = findViewById(R.id.timer);

        RequestNotificationPermissions();
        SetupGame();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == REQUEST_FOR_POST_NOTIFICATIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                mPostPermissionsGranted = true;
            }
        } else {
            Toast.makeText(this, "No notifications will be used", Toast.LENGTH_LONG).show();
            mPostPermissionsGranted = false;
        }

    }
    private void RequestNotificationPermissions() {
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_FOR_POST_NOTIFICATIONS);
        }
    }

    private void NotifyUserOfTurnStart() {
//        //Only one notification should be up at a time, so cancel any previous one
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.cancel(mNotificationId);

        Intent resultIntent = new Intent(this, CluegivingActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivityWithNavDrawer.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Team " + mActiveTeamNum + " Turn!")
                .setContentText("It is Team " + mActiveTeamNum + "'s turn").setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.d("Monikers", "Creating notification channel");
            String channelId = "my_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is a default channel for notifications");
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(mNotificationId, mBuilder.build());
        Log.d("Monikers", "Notifying user of turn start");
    }

    private void SetInitialTime() {
        String[] splitTime = mTimePerTurn.split(":");

        Integer minutes = Integer.parseInt(splitTime[0]);
        Integer seconds = Integer.parseInt(splitTime[1]);

        UpdateDBTimer(minutes, seconds);
    }

    private void SetupGame() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference wordsRef = database.getReference(mGameDBPath + "/words");

        SetInitialTime();

        SetupTimerToListenForDBChanges();
        ListenForRoundNumberChange();

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

                    mCurrentMonikerList.addAll(mMonikerList);

                    //Randomize list before we start
                    Collections.shuffle(mMonikerList);

                    //Now that we have the words, we can initialize the number of words left in deck
                    mNumWordsInDeck = mMonikerList.size();
                    DecrementMonikersLeft();

                    PromptForTurnStart();
                }
            }
        });
    }

    private void PromptForTurnStart() {
        new AlertDialog.Builder(CluegivingActivity.this)
                .setTitle("Round " + mRoundNumber + " : " + "Team " + String.valueOf(mActiveTeamNum))
                .setMessage("Start turn?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StartTurn();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void StartTurn() {
        if (mPostPermissionsGranted) { NotifyUserOfTurnStart(); }
        mActiveTeamNum = (mActiveTeamNum % 2) + 1;

        ArrayList<String> newMonikerList = new ArrayList<String>();
        for (int i = mCurrentMonikerIndex; i < mCurrentMonikerList.size(); ++i)
        {
            newMonikerList.add(mCurrentMonikerList.get(i));
        }
        mCurrentMonikerList = newMonikerList;
        //Shuffle before starting
        Collections.shuffle(mCurrentMonikerList);
        mCurrentMonikerIndex = 0;

        DisplayCurrentWord();

        String[] splitTime = mTimePerTurn.split(":");
        Integer minutes = Integer.parseInt(splitTime[0]);
        Integer seconds = Integer.parseInt(splitTime[1]);
        StartTimer(minutes, seconds);

        mNumWordsCorrect = 0;
        ((TextView) findViewById(R.id.numGuessedCorrect)).setText(String.valueOf(mNumWordsCorrect));
    }

    public void StartTimer(Integer minutes, Integer seconds) {
        //Only host will update the DB timer, others only read from it
        if (mAreWeHost)
        {
            Log.d("Monikers", "Starting timer!");

            mTimer = new CountDownTimer((minutes * 60 + seconds) * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    Double secLeft = Math.ceil(millisUntilFinished / 1000.0);
                    Integer secLeftInt = secLeft.intValue() % 60;

                    Double minLeft = Math.floor(secLeft.intValue() / 60);
                    Integer minLeftInt = minLeft.intValue();

                    UpdateDBTimer(minLeftInt, secLeftInt);
                }

                public void onFinish() {
                    UpdateDBTimer(0, 0);
                    EndTurn();
                }
            };
            mTimer.start();
        }
    }

    private void UpdateDBTimer(Integer minLeft, Integer secLeft)
    {
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference thisGame = databaseReference.child(mGameDBPath);

        Map<String, Object> dbSecLeft = new HashMap();
        dbSecLeft.put("secondsLeft", secLeft);

        Map<String, Object> dbMinLeft = new HashMap();
        dbMinLeft.put("minutesLeft", minLeft);

        thisGame.push();
        thisGame.updateChildren(dbSecLeft);
        thisGame.updateChildren(dbMinLeft);
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

    //Called when a player hits the 'Skip' button
    public void SkipMoniker(View v) {
        //Move this word to the back of the list, so they will only hit it again if we're out of words
        mCurrentMonikerList.add(mCurrentMonikerList.get(mCurrentMonikerIndex));
        mCurrentMonikerList.remove(mCurrentMonikerIndex.intValue());

        DisplayCurrentWord();
    }

    //Called when a player hits the 'Correct' button
    public void CorrectMoniker(View v) {
        if (mNumWordsInDeck == 0) {
            EndRound();
            if (mRoundNumber >= 4) {return;}
            EndTurn();
            return; }
        IncrementNumCorrect();
        DecrementMonikersLeft();
        ++mCurrentMonikerIndex;
        DisplayCurrentWord();
    }

    //Called when the round ends, due to the last moniker being guessed
    private void EndRound() {
        mTimer.cancel();
        IncrementRoundNumber();

        if (mRoundNumber >= 4) { return; }

        //Randomize list before we start
        Collections.shuffle(mMonikerList);
        mCurrentMonikerList.clear();
        mCurrentMonikerList.addAll(mMonikerList);
        Log.d("Monikers", "Current Moniker list size is " + mCurrentMonikerList.size());

        //Now that we have the words, we can initialize the number of words left in deck
        mNumWordsInDeck = mCurrentMonikerList.size();
        DecrementMonikersLeft();

        mCurrentMonikerIndex = 0;
    }

    private void GoBackToHomeScreen()
    {
        Intent intent = new Intent(this, WordsDisplayActivity.class);
        intent.putExtra("WordList",mMonikerList);
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
                if (mRoundNumber >= 4)
                {
                    GoBackToHomeScreen();
                }
                mRoundBanner.setText("Round " + String.valueOf(mRoundNumber));
            }
            @Override
            public void onCancelled (DatabaseError databaseError)
            {
                Log.w("Monikers", databaseError.toException());
            }
        };
        dbRoundNum.addValueEventListener(postListenerRound);
    }

    private void IncrementRoundNumber() {
        mRoundNumber += 1;
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference thisGame = databaseReference.child(mGameDBPath);

        Map<String, Object> dbRoundNum = new HashMap();
        dbRoundNum.put("roundNum", mRoundNumber);

        thisGame.push();
        thisGame.updateChildren(dbRoundNum);
    }

    //Called when the turn ends, due to the timer running out, or the last moniker being guessed
    private void EndTurn() {
        PromptForTurnStart();
    }

    //Change the text to display the word we are currently on
    private void DisplayCurrentWord() {
        ((TextView) findViewById(R.id.monikerToGuess)).setText(mCurrentMonikerList.get(mCurrentMonikerIndex));
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