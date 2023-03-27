package com.example.monikers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WordsDisplayActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<String> displayWordsList;
    MyRecyclerAdapter adapter;
    FloatingActionButton home_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_display);

        recyclerView = findViewById(R.id.recycler_view);
        home_btn = findViewById(R.id.homeButton);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create an AlertDialog with a custom layout for showing progress
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        final AlertDialog dialog = builder.create();

        // Show the AlertDialog before fetching the word list
        dialog.show();

        // Hide the RecyclerView until the word list is loaded and the adapter is set
        recyclerView.setVisibility(View.GONE);

        Intent intent = getIntent();
        displayWordsList = intent.getStringArrayListExtra("WordList");

        // Create a new adapter with the list of strings
        adapter = new MyRecyclerAdapter(displayWordsList);
        recyclerView.setAdapter(adapter);

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordsDisplayActivity.this, HomeActivityWithNavDrawer.class);
                startActivity(intent);
                finish();
            }
        });

        // Delay the dismissal of the AlertDialog for 8 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Dismiss the AlertDialog and show the RecyclerView
                dialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }
}
