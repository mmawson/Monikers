package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HowToPlayActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        textView = findViewById(R.id.description);
        textView.setText(R.string.gameDescription);
    }

    public void BackToHome(View v){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}