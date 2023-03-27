package com.example.monikers;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivityWithNavDrawer extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();

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
        TextView homeTextView = (TextView) findViewById(R.id.titleText);
        Button homeOnePhone = (Button) findViewById(R.id.onePhoneButton);
        Button homeMultiPhone = (Button) findViewById(R.id.multiPhoneButton);
        Button homeSettings = (Button) findViewById(R.id.settingsButton);
        Button homeHowToPlay = (Button) findViewById(R.id.howToPlayButton);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.ndopen, R.string.ndclose) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                homeTextView.setVisibility(View.VISIBLE);
                homeOnePhone.setVisibility(View.VISIBLE);
                homeMultiPhone.setVisibility(View.VISIBLE);
                homeSettings.setVisibility(View.VISIBLE);
                homeHowToPlay.setVisibility(View.VISIBLE);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                homeTextView.setVisibility(View.INVISIBLE);
                homeOnePhone.setVisibility(View.INVISIBLE);
                homeMultiPhone.setVisibility(View.INVISIBLE);
                homeSettings.setVisibility(View.INVISIBLE);
                homeHowToPlay.setVisibility(View.INVISIBLE);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Intent intent = new Intent(this, MusicPlayerService.class);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(myBroadcastReceiver);
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
                Intent intent1 = new Intent(this, HowToPlayActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_second_fragment:
                Intent intent2 = new Intent(this, ContactUsActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void StartOnePhoneGame(View v) {
        //For a one phone game, we use the user's uid to create a unique game name
        Intent intent = new Intent(this, OnePhoneGameSettingActivity.class);
        String gameDBPath = "localGames/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        intent.putExtra("gameDBPath", gameDBPath);
        startActivity(intent);
    }

    public void StartMultiPhoneGame(View v) {
        Intent intent = new Intent(this, HostJoinGame.class);
        startActivity(intent);
    }

    public void StartSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String Body = "Download Monikers and Play with Me!";
                String Sub = "http://play.google.com/monikers";
                share.putExtra(Intent.EXTRA_TEXT, Body);
                share.putExtra(Intent.EXTRA_TEXT, Sub);
                startActivity(Intent.createChooser(share, "Share!"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}