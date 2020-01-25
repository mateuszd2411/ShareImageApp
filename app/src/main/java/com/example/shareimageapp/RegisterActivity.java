package com.example.shareimageapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                //for get information from EditText (in xml layout)
                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                //validations - not empty edit text
                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required..", Toast.LENGTH_SHORT).show();
                }
                //validation - to short password
                else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    //all is good, send user info to register logic
                    register(str_username, str_fullname, str_email, str_password);
                }

            }
        });

    }//onCreate

    //Logic for Register
    private void register(final String username, final String fullname, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                //check if everything is ok
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            //create and go to "folder" in realtime database -> UsersApp
                            reference = FirebaseDatabase.getInstance().getReference().child("UsersApp")
                                    .child(userid);

                            //put information about new user to realtime database
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());    //toLowerCase for search users
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            //default profile picture
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/facebook-login-9128b.appspot.com/o/profilePic.gif?alt=media&token=aeea53b5-1efc-4a12-98f4-1b34f7932806");

                            //set values from hashMap to realtime database
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //if all ok
                                    if (task.isSuccessful()) {
                                        //destroy progress dialog
                                        pd.dismiss();

                                        //go to MainActivity after register
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        //Don't go back
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }//if (task.isSuccessful())   --> End
                        // Something wrong
                        else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}