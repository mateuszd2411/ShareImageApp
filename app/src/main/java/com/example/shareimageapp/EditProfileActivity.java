package com.example.shareimageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.image_profile)
    ImageView image_profile;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.tv_change)
    TextView tv_change;
    @BindView(R.id.fullname)
    MaterialEditText fullname;
    @BindView(R.id.username)
    MaterialEditText username;
    @BindView(R.id.bio)
    MaterialEditText bio;

    ProgressDialog pd;

    FirebaseUser firebaseUser;

    private Uri mInageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //init views
        ButterKnife.bind(this);

        //init firebase get current user and add "uploads" folder in storage firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
    }
}