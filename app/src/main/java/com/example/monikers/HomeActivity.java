package com.example.monikers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       // getMenuInflater().inflate(R.menu.actionbar_menu, menu);
      return super.onCreateOptionsMenu(menu);
    }

    public void StartOnePhoneGame(View v) {
        //For a one phone game, we use the user's uid to create a unique game name
        Intent intent = new Intent(this, AddWordActivity.class);
        intent.putExtra("gameName", "User " + mAuth.getCurrentUser().getUid() + " local game");
        startActivity(intent);
    }

    public void StartMultiPhoneGame(View v) {
        Intent intent = new Intent(this, HostJoinGame.class);
        startActivity(intent);
    }
}
