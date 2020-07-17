package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareimageapp.Fragment.HomeFragment;
import com.example.shareimageapp.Fragment.NotificationFragment;
import com.example.shareimageapp.Fragment.ProfileFragment;
import com.example.shareimageapp.Fragment.SearchFragment;
import com.example.shareimageapp.Model.Post;
import com.example.shareimageapp.Model.User;
import com.example.shareimageapp.PhotoEditor.EditorMainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    String profileid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ////////////////////////////////////////////For Dark Theme

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        ////////////////////////////////////////////For Dark Theme
        super.onCreate(savedInstanceState);
        loadLocale();       //for change language
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

        //for id
        SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        //get "publisherid" from comment, replace fragment
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString(String.valueOf(R.string.StringExtrapublisherid));

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString(String.valueOf(R.string.StringExtraprofileid), publisher);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        userInfo();

    }// onCreate END

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
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

                    if (selectedFragment != null) {
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

        if (item.getItemId() == R.id.edit_profile) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.photo_editor) {
            Intent intent = new Intent(MainActivity.this, EditorMainActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.notifications) {
            selectedFragment = new NotificationFragment();
        }

        if (item.getItemId() == R.id.logout) {
            //logic for logout user
            FirebaseAuth.getInstance().signOut();
            Toasty.info(MainActivity.this, R.string.logged_out, Toast.LENGTH_LONG, true).show();
            //got to start activity
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //get user info from database an set to draw navigation
    private void userInfo() {
        //go to "Users" profileid in realtime database for getting  image profile and full name
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                ImageView drawer_image_profile = (ImageView) findViewById(R.id.drawer_image_profile);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(drawer_image_profile);

                TextView drawer_fullname;
                drawer_fullname = findViewById(R.id.drawer_fullname);
                drawer_fullname.setText(user.getFullname());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //go to "Posts" in realtime database for getting nr posts
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                TextView posts;
                posts = findViewById(R.id.drawer_posts);
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //go to "Follow" followers in realtime database for getting nr of followers
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView followers;
                followers = findViewById(R.id.drawer_followers);
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //go to "Follow" following in realtime database for getting nr of following
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");

        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView following;
                following = findViewById(R.id.drawer_following);
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        //save data to share preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings1", MODE_PRIVATE).edit();
        editor.putString("My_Lang1", lang);
        editor.apply();
    }

    //load language saved in share preferences
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings1", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang1", "");
        setLocale(language);
    }

}
