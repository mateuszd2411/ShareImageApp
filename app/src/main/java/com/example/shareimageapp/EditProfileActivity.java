package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareimageapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

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

        //go to "Users" in realtime database and go to current user by id
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        //set user info to MaterialEditText and Image View
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());

                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //close and go to MainActivity
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            }
        });

        //text change photo clickable and go to choose new photo
        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        // the sane as tv_change
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(fullname.getText().toString(),
                        username.getText().toString(),
                        bio.getText().toString());
            }
        });
    }//onCreate END

    private void updateProfile(String fullname, String username, String bio) {
        //go to "Users" in realtime database and go to current user by id
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        //set new user info to hashMap in realtime database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullname", fullname);
        hashMap.put("username", username);
        hashMap.put("bio", bio);

        reference.updateChildren(hashMap);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //logic for Upload new profile picture
    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        //progress dialog
        pd.setMessage(getResources().getString(R.string.Uploading));
        pd.show();

        if (mInageUri != null){
            //upload new profile image to:
            final StorageReference filereference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mInageUri));

            uploadTask = filereference.putFile(mInageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        //get uid
                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", "" + myUrl);

                        reference.updateChildren(hashMap);

                        pd.dismiss();
                        Toasty.success(EditProfileActivity.this, R.string.updated, Toast.LENGTH_LONG, true).show();
                    } else {
                        Toasty.error(EditProfileActivity.this, R.string.Failed, Toast.LENGTH_LONG, true).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toasty.error(EditProfileActivity.this, R.string.Failed, Toast.LENGTH_LONG, true).show();
                }
            });
        }else {
            Toasty.info(EditProfileActivity.this, R.string.NoImageselected, Toast.LENGTH_LONG, true).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mInageUri = result.getUri();

            uploadImage();
        }else {
            Toasty.error(EditProfileActivity.this, R.string.Failed, Toast.LENGTH_LONG, true).show();
        }

    }
}