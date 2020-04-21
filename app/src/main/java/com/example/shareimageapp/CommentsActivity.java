package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareimageapp.Adapter.CommentAdapter;
import com.example.shareimageapp.Model.Comment;
import com.example.shareimageapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CommentsActivity extends AppCompatActivity {

    //for displaying comments in recycler view
//    @BindView(R.id.recycle_view)
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    //init views
    @BindView(R.id.add_comment)
    EditText addcomment;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.post)
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

        //bind the current view
        ButterKnife.bind(this);

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

        //recycler view for displaying comments
        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //need postid, publisherid
        Intent intent = getIntent();
        postid = intent.getStringExtra(getString(R.string.StringExtrapostid));
        publisherid = intent.getStringExtra(String.valueOf(R.string.StringExtrapublisherid));

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
        //read comments
        readComments();
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

    private void readComments(){
        //go to "Comments" folder in realtime database and get post id
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(String.valueOf(R.string.DB_Comments)).child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                //add comment to list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                //set change
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}