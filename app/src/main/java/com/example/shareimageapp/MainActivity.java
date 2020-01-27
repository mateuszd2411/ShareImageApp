package com.example.shareimageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.shareimageapp.Fragments.HomeFragment;
import com.example.shareimageapp.Fragments.NotificationFragment;
import com.example.shareimageapp.Fragments.ProfileFragment;
import com.example.shareimageapp.Fragments.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init from activity_main.xml
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //set default displaying fragment -->Home
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectFragment = new HomeFragment();
                            break;

                        case R.id.nav_search:
                            selectFragment = new SearchFragment();
                            break;

                        case R.id.nav_add:
                            selectFragment = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;

                        case R.id.nav_heart:
                            selectFragment = new NotificationFragment();
                            break;

                        case R.id.nav_profile:
                            //for individual user id
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectFragment = new ProfileFragment();
                            break;
                    }

                    //set choose fragment to fragment_container in activity_main.xml
                    if (selectFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectFragment).commit();
                    }

                    return true;
                }
            };
}