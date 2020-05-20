package com.example.shareimageapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    Button changeLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();       //for change language
        setContentView(R.layout.activity_settings);

        changeLang = findViewById(R.id.change_language);

        //For change language
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show alert dialog to display list of language, one can be selected
                showChangeLanguageDialog();
            }
        });

    }   //onCreate END


    //For change language
    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {"Polski", "English"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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

    private void restartApp() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}