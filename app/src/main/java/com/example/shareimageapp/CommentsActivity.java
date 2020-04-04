package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareimageapp.Model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CommentsActivity extends AppCompatActivity {

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

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
        setContentView(R.layout.activity_comments);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Comments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        //need postid, publisherid
        Intent intent = getIntent();
        postid = intent.getStringExtra(getString(R.string.StringExtrapostid));
        publisherid = intent.getStringExtra("publisherid");

        //post/send comment button clickable
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validation if not empty comment
                if (addcomment.getText().toString().equals("")){
                    //Toast info
                    Toasty.info(CommentsActivity.this, R.string.canysendemptycomment, Toast.LENGTH_LONG, true).show();
                }else {
                    //add comment logic
                    addComment();
                }
            }
        });
        //get profile image to comment
        getImage();
    }//onCreate

    private void addComment() {
        //go to "Comments" in realtime database firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.DB_Comments)).child(postid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        //after all set empty
        addcomment.setText("");
    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.DBUsers)).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}