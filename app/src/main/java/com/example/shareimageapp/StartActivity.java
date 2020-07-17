package com.example.shareimageapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.login)
    Button login;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.change_language)
    Button changeLang;

    ////////////////////////////////////////////For Dark Theme
    @BindView(R.id.darkModeSwitch)
    SwitchCompat darkModeSwitch;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

    }

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
        loadLocale();   //for language
        setContentView(R.layout.activity_start);

        //init views
        ButterKnife.bind(this);

        //validation for switching to dark mode theme
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkModeSwitch.setChecked(true);
        }

        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    restartApp();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });

        //For change language
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show alert dialog to display list of language, one can be selected
                showChangeLanguageDialog();
            }
        });
    }// onCreate END

    ////////////////////////////////////////////For Dark Theme
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
    }

    //For change language
    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {"Polski", "English"};

        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
        builder.setTitle("Choose Language...");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Polski
                    setLocale("pl");
                    recreate();
                    restartApp();
                }
                if (i == 1) {
                    //English
                    setLocale("en");
                    recreate();
                    restartApp();
                }

                //dismiss alert dialog when language selected
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = builder.create();
        builder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        //save data to share preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings123456789", MODE_PRIVATE).edit();
        editor.putString("My_Lang123456789", lang);
        editor.apply();
    }

    //load language saved in share preferences
    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings123456789", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang123456789", "");
        setLocale(language);
    }
}
