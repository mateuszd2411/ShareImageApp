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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView txt_signup;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init views from activity_login
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);

        auth = FirebaseAuth.getInstance();

        //go to RegisterActivity
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progress dialog
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                //get info from edit text
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                //validations
                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are require...", Toast.LENGTH_SHORT).show();
                }
                //we have info from edit text
                else {
                    //logic for login user
                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //if all is ok
                                    if (task.isSuccessful()) {

                                        //go to "folder" i realtime database to get user info
                                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                                .getReference("UsersApp")
                                                .child(auth.getCurrentUser().getUid());

                                        //get user info from realtime database --> UsersApp
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //destroy progress dialog
                                                pd.dismiss();

                                                //after login go to MainActivity
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                //Don't go back
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                pd.dismiss();
                                            }
                                        });

                                    }//END --> if (task.isSuccessful())  
                                    else {
                                        //something wrong
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this, "Authentication failed...", Toast.LENGTH_SHORT).show();
                                    } 
                                }
                            });
                }


            }
        });
    }
}