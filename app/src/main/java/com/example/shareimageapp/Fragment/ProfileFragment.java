package com.example.shareimageapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shareimageapp.Adapter.MyPhotoAdapter;
import com.example.shareimageapp.EditProfileActivity;
import com.example.shareimageapp.Model.Post;
import com.example.shareimageapp.Model.User;
import com.example.shareimageapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    //init views
    @Nullable
    @BindView(R.id.image_profile)
    ImageView image_profile;
    @Nullable
    @BindView(R.id.options)
    ImageView options;
    TextView posts;

    @Nullable
    @BindView(R.id.followers)
    TextView followers;
    @Nullable
    @BindView(R.id.following)
    TextView following;
    @Nullable
    @BindView(R.id.fullname)
    TextView fullname;
    @Nullable
    @BindView(R.id.bio)
    TextView bio;
    @Nullable
    @BindView(R.id.username)
    TextView username;
    @Nullable
    @BindView(R.id.edit_profile)
    Button edit_profile;
    @Nullable
    @BindView(R.id.my_fotos)
    ImageButton my_fotos;
    @Nullable
    @BindView(R.id.saved_fotos)
    ImageButton saved_fotos;

    //for save post picture blow user info
    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    MyPhotoAdapter myFotoAdapter_saves;
    List<Post> postList_saves;

    //for MyPhotoAdapter to display pictures in profile
    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init views
        ButterKnife.bind(this, view);
        posts = view.findViewById(R.id.posts);

        //init recycler to display photos in grid
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotoAdapter);

        //init recycler to display saved photos in grid
        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyPhotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //for id
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        userInfo();
        getFollowers();
        getNrPost();
        myPhotos();
        mySaves();

        if (profileid.equals(firebaseUser.getUid())){
            assert edit_profile != null;
            edit_profile.setText(R.string.edit_profile);
        }else {
            checkFollow();
            assert saved_fotos != null;
            saved_fotos.setVisibility(View.GONE);
        }

        //////////////////CLICKABLE

        //edit_profile button clickable and logic for displaying text
        assert edit_profile != null;
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if(btn.equals(R.string.edit_profile)){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else if (btn.equals(R.string.follow)){
                    //set change in realtime database
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                }else if (btn.equals(R.string.following)){
                    //set change in realtime database
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        //for save photos
        assert my_fotos != null;
        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        assert saved_fotos != null;
        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        //////////////////CLICKABLE

        return view;
    }//onCreateView END


    //get user info from database
    private void userInfo(){
        //go to "Users" profileid in realtime database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                assert image_profile != null;
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                assert username != null;
                username.setText(user.getUsername());
                assert fullname != null;
                fullname.setText(user.getFullname());
                assert bio != null;
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        //go to "Follow" following in realtime database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText(R.string.following);
                }else {
                    edit_profile.setText(R.string.follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //get followers number from realtime database
    private void getFollowers(){
        //go to "Follow" followers in realtime database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPost(){
        //go to "Posts" in realtime database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
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
                assert posts != null;
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //my photos below user info
    private void myPhotos(){
        //go to "Posts" in realtime database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                //set change
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //save post pictures
    private void mySaves(){
        mySaves = new ArrayList<>();
        //go to "Saves" and get current user id
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves){
                        if (post.getPostid().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }
                myFotoAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}