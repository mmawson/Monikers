package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactUsActivity extends AppCompatActivity {

        TextView biographicalText, titleText;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_us);
            titleText = findViewById(R.id.titleText);
            biographicalText = findViewById(R.id.detail);
            titleText.setText("Team Cuddlefish");
            biographicalText.setText(R.string.Contact);
        }

        public void BackToHome(View v){
            Intent intent = new Intent(this, HomeActivityWithNavDrawer.class);
            startActivity(intent);
        }
    }