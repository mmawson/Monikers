package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class OnePhoneGameSettingActivity extends AppCompatActivity {

    private EditText playerCountEditText, cardsCountEditText;
    private Spinner timeSelect;
    FloatingActionButton homeButton;
    Button nextButton;
    private int playerCount;
    private int cardsCount;
    boolean mAreWeHost;
    String mGameDBPath;
    boolean mLocalGame;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_phone_game_setting);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mGameDBPath = getIntent().getStringExtra("gameDBPath");
        mLocalGame = getIntent().getBooleanExtra("localGame", true);

        mAreWeHost = getIntent().getBooleanExtra("areWeHost", true);

        playerCountEditText = findViewById(R.id.playerCount_et);
        cardsCountEditText = findViewById(R.id.cards_et);
        timeSelect = findViewById(R.id.gameTime_popUp);
        homeButton = findViewById(R.id.homeButton);
        nextButton = findViewById(R.id.nextButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnePhoneGameSettingActivity.this, HomeActivityWithNavDrawer.class);
                startActivity(intent);
                finish();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerCountString = playerCountEditText.getText().toString();
                String cardsCountString = cardsCountEditText.getText().toString();

                if (playerCountString.matches("[0-9]+") && cardsCountString.matches("[0-9]+") ) {
                    playerCount = Integer.parseInt(playerCountString);
                    cardsCount = Integer.parseInt(cardsCountString);
                    // Start game with playerCount number of players

                    Intent intent = new Intent(OnePhoneGameSettingActivity.this, AddWordActivity.class);


                    if (mLocalGame)
                    {
                        intent.putExtra("numOfPlayer", playerCount);
                    }
                    else
                    {
                        //For non-local game, each user should only be entering the amount of words for 1 player
                        intent.putExtra("numOfPlayer", 1);
                    }

                    //Could be used for custom card count in multiphone game
                    //Put numOfCards in DB
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//                    DatabaseReference thisGame = databaseReference.child(mGameDBPath);


//                    Map<String, Object> dbCardsNum = new HashMap();
//                    dbCardsNum.put("numCardsPerPlayer", cardsCount);
//
//                    thisGame.push();
//                    thisGame.updateChildren(dbCardsNum);

                    intent.putExtra("numCards", cardsCount);
                    intent.putExtra("timePerTurn", timeSelect.getSelectedItem().toString());
                    intent.putExtra("gameDBPath", mGameDBPath);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}