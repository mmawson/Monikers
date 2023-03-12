package com.example.monikers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       // getMenuInflater().inflate(R.menu.actionbar_menu, menu);
      return super.onCreateOptionsMenu(menu);
    }

    public void StartOnePhoneGame(View v) {
        //For a one phone game, we use the user's uid to create a unique game name
        Intent intent = new Intent(this, PlayerCountActivity.class);
        startActivity(intent);
    }

    public void StartMultiPhoneGame(View v) {
        Intent intent = new Intent(this, HostJoinGame.class);
        startActivity(intent);
    }
}
