package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddWordActivity extends AppCompatActivity {

    private EditText editTextWord;
    private TextView textViewCounter;
    private TextView textViewTotalWords;
    private Button buttonSave, buttonNext;
    private ProgressBar progressBar;
    private int totalWords;
    private int playerCount;
    private int maxWordsTotal;

    private int wordCount=0;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

//        mGameName = getIntent().getStringExtra("gameName");

        editTextWord = findViewById(R.id.editText_word);
        textViewCounter = findViewById(R.id.textView_counter);
        textViewTotalWords = findViewById(R.id.textView_total_words);
        buttonSave = findViewById(R.id.button_save);
        buttonNext = findViewById(R.id.button_next);
        progressBar = findViewById(R.id.progressBar);
        buttonNext.setEnabled(false);

        Intent intent = getIntent();
        playerCount = intent.getIntExtra("numOfPlayer", 0);
        maxWordsTotal = playerCount * 5;
        totalWords = maxWordsTotal;

        textViewTotalWords.setText("Number of remaining words: " + totalWords);

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get a reference to the Firebase Authentication instance and the current user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //And have an entry under games/<name>/password as well
//        DatabaseReference gameWords = databaseReference.child("games").child(mGameName).child("words");

        // Set a click listener for the "Add" button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editTextWord.getText().toString().trim();

                // Check if the word is not empty and the word count is less than 5
                if (!word.isEmpty() && wordCount < maxWordsTotal) {

                    // Check if the word already exists
                    databaseReference.child("words").child(firebaseUser.getUid()).orderByValue().equalTo(word)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // The word already exists, show a toast and don't add the word
                                        Toast.makeText(AddWordActivity.this, "This word already exists, please enter a new word", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // The word does not exist, add it to the database
                                        wordCount++;
                                        totalWords--;
                                        databaseReference.child("words").child(firebaseUser.getUid()).push().setValue(word);
                                        textViewCounter.setText("Words added: " + wordCount);
                                        textViewTotalWords.setText("Number of remaining words: " + totalWords);
                                        int progress = (int) (((float) wordCount / (float) maxWordsTotal) * 100);
                                        progressBar.setProgress(progress);

                                        if (wordCount == maxWordsTotal) {
                                            buttonNext.setEnabled(true);
                                            Toast.makeText(AddWordActivity.this, "Please select 'NEXT' to enter game", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    editTextWord.setText("");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle database error
                                }
                            });

                } else {
                    editTextWord.setText("");
                }
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddWordActivity.this, CluegivingActivity.class);
//                intent.putExtra("gameName", mGameName);
                startActivity(intent);
            }
        });
    }
}
