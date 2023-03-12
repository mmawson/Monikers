package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlayerCountActivity extends AppCompatActivity {

    private EditText playerCountEditText;

    public int getPlayerCount() {
        return playerCount;
    }

    private int playerCount;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_count);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        playerCountEditText = findViewById(R.id.playerCountEditText);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerCountString = playerCountEditText.getText().toString();

                if (playerCountString.matches("[0-9]+")) {
                    playerCount = Integer.parseInt(playerCountString);
                    // Start game with playerCount number of players

                    Intent intent = new Intent(PlayerCountActivity.this, AddWordActivity.class);
                    intent.putExtra("numOfPlayer", playerCount);
//                    intent.putExtra("gameName", "User " + mAuth.getCurrentUser().getUid() + " local game");
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}