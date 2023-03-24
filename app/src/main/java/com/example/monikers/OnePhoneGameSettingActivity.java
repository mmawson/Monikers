package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OnePhoneGameSettingActivity extends AppCompatActivity {

    private EditText playerCountEditText, cardsCountEditText;
    private Spinner timeSelect;
    FloatingActionButton homeButton;
    Button nextButton;
    private int playerCount;
    private int cardsCount;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_phone_game_setting);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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
                    intent.putExtra("numOfPlayer", playerCount);
                    intent.putExtra("numOfCards", cardsCount);
                    //Use user's uid as the game name
                    String gameDBPath = "localGames/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
                    intent.putExtra("gameDBPath", gameDBPath);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}