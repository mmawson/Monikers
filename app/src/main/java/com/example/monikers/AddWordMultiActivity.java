package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWordMultiActivity extends AppCompatActivity {

    private EditText editTextWord;
    private TextView textViewCounter;
    private Button buttonSave, buttonNext;
    private ProgressBar progressBar;
    private int wordCount = 0;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_multi);

        editTextWord = findViewById(R.id.editText_word);
        textViewCounter = findViewById(R.id.textView_counter);
        buttonSave = findViewById(R.id.button_save);
        buttonNext = findViewById(R.id.button_next);
        progressBar = findViewById(R.id.progressBar);
        buttonNext.setEnabled(false);

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

                // Check if the word is not empty and the word count is less than 5
                if (!word.isEmpty() && wordCount < 5) {
                    wordCount++;

                    textViewCounter.setText("Words added: " + wordCount);

                    int progress = (int) (((float) wordCount / (float) 5) * 100);
                    progressBar.setProgress(progress);

                    if (wordCount == 5) {
                        buttonNext.setEnabled(true);
                        Toast.makeText(AddWordMultiActivity.this, "Please select 'NEXT' to enter game", Toast.LENGTH_SHORT).show();
                    }
                }
                editTextWord.setText("");
            }
        });

//        buttonNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AddWordMultiActivity.this, .class);
//                startActivity(intent);
//            }
//        });
    }
}