package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.shareimageapp.Fragment.HomeFragment;
import com.example.shareimageapp.Fragment.NotificationFragment;
import com.example.shareimageapp.Fragment.ProfileFragment;
import com.example.shareimageapp.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Drawer Layout
    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    Fragment selectedFragment = null;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind the current view
        ButterKnife.bind(this);
        navigationView.setNavigationItemSelectedListener(this);

        //Drawer Layout

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.RED);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
//        actionBarDrawerToggle.setDrawerSlideAnimationEnabled(true);
        navigationView.setItemIconTintList(null);
        navigationView.bringToFront();

        actionBarDrawerToggle.syncState();

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.setItemIconTintList(null); //original color icon

        //get "publisherid" from comment, replace fragment
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString(String.valueOf(R.string.StringExtrapublisherid));

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString(String.valueOf(R.string.StringExtraprofileid), publisher);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

    }// onCreate END

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_add:
                            selectedFragment = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;

                        case R.id.nav_heart:
                            selectedFragment = new NotificationFragment();
                            break;

                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        item.setCheckable(true);
        drawerLayout.closeDrawers();

        if (item.getItemId() == R.id.home) {
            selectedFragment = new HomeFragment();
        }

        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.notifications) {
            selectedFragment = new NotificationFragment();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
