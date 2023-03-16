package com.example.monikers;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivityWithNavDrawer extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_home_nav_drawer);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.ndopen, R.string.ndclose);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                Toast.makeText(this, "Load How To Play Fragment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_second_fragment:
                Toast.makeText(this, "Biographical Info Fragment", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    public void StartSettings(View v) {
        Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, .class);
//        startActivity(intent);
    }

    public void StartHowToPlay(View v) {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
                mAuth.signOut();
                finish();
                return true;
            case R.id.menu_settings:
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}