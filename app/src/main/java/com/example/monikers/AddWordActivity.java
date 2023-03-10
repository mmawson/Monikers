package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWordActivity extends AppCompatActivity {

    private EditText editTextWord;
    private TextView textViewCounter;
    private Button buttonSave, buttonNext;
    private int wordCount = 0;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        mGameName = getIntent().getStringExtra("gameName");

        editTextWord = findViewById(R.id.editText_word);
        textViewCounter = findViewById(R.id.textView_counter);
        buttonSave = findViewById(R.id.button_save);
        buttonNext = findViewById(R.id.button_next);
        buttonNext.setEnabled(false);

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get a reference to the Firebase Authentication instance and the current user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //And have an entry under games/<name>/password as well
        DatabaseReference gameWords = databaseReference.child("games").child(mGameName).child("words");

        // Set a click listener for the "Add" button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editTextWord.getText().toString().trim();

                // Check if the word is not empty and the word count is less than 5
                if (!word.isEmpty() && wordCount < 5) {
                    wordCount++;

                    gameWords.push().setValue(word);

                    textViewCounter.setText("Words added: " + wordCount);

                    if (wordCount == 5) {
                        buttonNext.setEnabled(true);
                    }
                }
                editTextWord.setText("");
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddWordActivity.this, CluegivingActivity.class);
                intent.putExtra("gameName", mGameName);
                startActivity(intent);
            }
        });
    }
}
