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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddWordActivity extends AppCompatActivity {

    private EditText editTextWord;
    private TextView textViewCounter;
    private TextView textViewTotalWords;
    private Button buttonSave, buttonNext;
    private FloatingActionButton homeButton;
    private ProgressBar progressBar;
    private int totalWords;
    private int playerCount;
    private int cardsCount;
    private int maxWordsTotal;
    private int wordCount=0;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String mWordsDBPath;
    String mGameDBPath;
    //True if we are the host of the game
    boolean mAreWeHost;
    private ArrayList<String> wordList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        mGameDBPath = getIntent().getStringExtra("gameDBPath");
        mWordsDBPath = mGameDBPath + "/words";

        mAreWeHost = getIntent().getBooleanExtra("areWeHost", true);

        editTextWord = findViewById(R.id.editText_word);
        textViewCounter = findViewById(R.id.textView_counter);
        textViewTotalWords = findViewById(R.id.textView_total_words);
        buttonSave = findViewById(R.id.button_save);
        buttonNext = findViewById(R.id.button_next);
        homeButton = findViewById(R.id.homeButton);
        progressBar = findViewById(R.id.progressBar);
        buttonNext.setEnabled(false);

        Intent intent = getIntent();
        playerCount = intent.getIntExtra("numOfPlayer", 1);
        cardsCount = intent.getIntExtra("numOfCards", 5);
        maxWordsTotal = playerCount * cardsCount;
        totalWords = maxWordsTotal;

        textViewTotalWords.setText("Number of remaining words: " + totalWords);

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get a reference to the Firebase Authentication instance and the current user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Set a click listener for the "Add" button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editTextWord.getText().toString().trim();

                // Check if the word is not empty and the word count is less than then number inputted
                if (!word.isEmpty() && wordCount < maxWordsTotal) {
                    if (wordList.contains(word)) {
                        // The word already exists in the list, show a toast and don't add the word
                        Toast.makeText(AddWordActivity.this, "This word already exists, please enter a new word", Toast.LENGTH_SHORT).show();
                    }else {
                        // The word does not exist, add it to the list
                        wordList.add(word);
                        wordCount++;
                        totalWords--;
                        textViewCounter.setText("Words added: " + wordCount);
                        textViewTotalWords.setText("Number of remaining words: " + totalWords);
                        int progress = (int) (((float) wordCount / (float) maxWordsTotal) * 100);
                        progressBar.setProgress(progress);

                        // Show Snackbar with added word and Undo option
                        Snackbar snackbar = Snackbar.make(v, "Word added: " + "\"" + word + "\"", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                wordList.remove(wordList.size() - 1);
                                editTextWord.setText("");
                                wordCount--;
                                totalWords++;
                                textViewCounter.setText("Word added: " + wordCount);
                                textViewTotalWords.setText("Number of remaining words: " + totalWords);
                                int progress = (int) (((float) wordCount / (float) cardsCount) * 100);
                                progressBar.setProgress(progress);
                                Snackbar.make(view, "Word removed", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        snackbar.show();

                        if (wordCount == maxWordsTotal) {
                            buttonNext.setEnabled(true);
                            Toast.makeText(AddWordActivity.this, "Please select 'NEXT' to enter game", Toast.LENGTH_SHORT).show();
                            // Send the array of words to the database
                            databaseReference.child(mWordsDBPath).setValue(wordList);
                            editTextWord.setEnabled(false);
                        }
                    }
                } else {
                    editTextWord.setText("");
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddWordActivity.this, HomeActivityWithNavDrawer.class);
                startActivity(intent);
                finish();
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                //Start with host as cluegiver
                if (mAreWeHost)
                {
                    intent = new Intent(AddWordActivity.this, CluegivingActivity.class);
                }
                else
                {
                    intent = new Intent(AddWordActivity.this, ClueguessingActivity.class);
                }

                intent.putExtra("gameDBPath", mGameDBPath);
                intent.putExtra("areWeHost", mAreWeHost);

                startActivity(intent);
            }
        });
    }
}
