package com.example.shareimageapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareimageapp.MainActivity;
import com.example.shareimageapp.Model.Comment;
import com.example.shareimageapp.Model.User;
import com.example.shareimageapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    //init
    private Context mContext;
    private List<Comment> mComment;

    private FirebaseUser firebaseUser;

    //constructors for recycler view
    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //get current user from auth
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get comment from list
        final Comment comment = mComment.get(i);

        //set info to comment
        viewHolder.comment.setText(comment.getComment());
        getUserInfo(viewHolder.image_profile, viewHolder.username, comment.getPublisher());

        //clickable comment in list
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //comment click, string Extra
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(String.valueOf(R.string.StringExtrapublisherid), comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //comment click, string Extra
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(String.valueOf(R.string.StringExtrapublisherid), comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

    }//onBindViewHolder EDN

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //init views
        @BindView(R.id.image_profile)
        public ImageView image_profile;
        @BindView(R.id.username)
        public TextView username;
        @BindView(R.id.comment)
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //bind the current view
            ButterKnife.bind(this, itemView);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        //go to "Users" in realtime database and get publisherid
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(String.valueOf(R.string.DBUsers)).child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getUserInfo
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
