package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.fullname)
    EditText fullname;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.txt_login)
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

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
        setContentView(R.layout.activity_register);

        //bind the current views
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();

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
                pd.setMessage(getString(R.string.Pleasewait));
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();


                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                       || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                {
                    pd.dismiss();
                    Toasty.info(RegisterActivity.this, R.string.Allfilesarerequired, Toast.LENGTH_LONG, true).show();
                }else if (str_password.length() <6){
                    pd.dismiss();
                    Toasty.info(RegisterActivity.this, R.string.Passwordmusthave6characters, Toast.LENGTH_LONG, true).show();
                }else
                {
                    pd.dismiss();
                    register(str_username, str_fullname, str_email, str_password);
                }
            }
        });


    }

    private void register(final String username, final String fullnaem, String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                            pd.setMessage(getString(R.string.Pleasewait));
                            pd.show();

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullnaem);
                            hashMap.put("bio", "");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/facebook-login-9128b.appspot.com/o/placeholder.png?alt=media&token=9c182ad7-a369-439f-98b2-470a397aecf9" );

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        pd.dismiss();
                                        Toasty.success(RegisterActivity.this,R.string.LoggedSuccessfully, Toast.LENGTH_LONG, true).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });


                        }
                        else
                        {
                            pd.dismiss();
                            Toasty.error(RegisterActivity.this,R.string.cantRegister, Toast.LENGTH_LONG, true).show();
                        }

                    }
                });
    }
}
