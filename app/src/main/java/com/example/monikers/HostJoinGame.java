package com.example.monikers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HostJoinGame extends AppCompatActivity {
    EditText mGameName;
    EditText mGamePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_join_game);

        mGameName = findViewById(R.id.gameNameEditText);
        mGamePassword = findViewById(R.id.gamePassEditText);
    }

    public void hostGame(View v) {
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String gameName = mGameName.getText().toString();
        DatabaseReference thisGame = databaseReference.child("games").child(gameName);

        Map<String, Object> gameDetails = new HashMap();
        gameDetails.put("password", mGamePassword.getText().toString());

        thisGame.push();
        thisGame.updateChildren(gameDetails);

        goToNextActivity(gameName);
    }

    public void joinGame(View v){
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String gameName = mGameName.getText().toString();
        DatabaseReference thisGame = databaseReference.child("games").child(gameName);

        thisGame.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Monikers", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot gameData = task.getResult();

                    if (!gameData.exists())
                    {
                        Toast.makeText(getApplicationContext(), "No game exists with that name!", Toast.LENGTH_LONG).show();
                    }

                    for (DataSnapshot child : gameData.getChildren()) {
                        if (child.getKey().equals("password")) {
                            if (child.getValue().equals(mGamePassword.getText().toString())) {
                                goToNextActivity(gameName);
                            } else {
                                Toast.makeText(getApplicationContext(), "Incorrect password!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
    }

    private void goToNextActivity(String gameName) {
        Intent intent = new Intent(this, AddWordMultiActivity.class);
        intent.putExtra("gameName", gameName);
        startActivity(intent);
    }
}