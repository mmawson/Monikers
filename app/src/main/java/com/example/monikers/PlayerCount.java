package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerCount extends AppCompatActivity {

    private EditText playerCountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_count);
        playerCountEditText = findViewById(R.id.playerCountEditText);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerCountString = playerCountEditText.getText().toString();

                if (playerCountString.matches("[0-9]+")) {
                    int playerCount = Integer.parseInt(playerCountString);
                    // Start game with playerCount number of players

                    Intent intent = new Intent(PlayerCount.this, AddWordActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}