package com.example.shareimageapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl;
    StorageTask uploadTask;
    StorageReference storageReference;

    //views from activity_post.xml
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.image_added)
    ImageView image_added;
    @BindView(R.id.post)
    TextView post;
    @BindView(R.id.description)
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //bind the current view
        ButterKnife.bind(this);

        //create folder "Posts" in FirebaseStorage
        storageReference = FirebaseStorage.getInstance().getReference("Posts");

        //clickable close button, go to MainActivity
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();   //don't back
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        //Crop Image
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }//onCreate end

    //for Crop Image
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        //progress dialog while posting
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.Posting));
        progressDialog.show();

        //logic for posting
        if (imageUri != null) {
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //when all is good
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        myUrl = downloadUri.toString();

                        //go to/make folder i realtime database
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        //get key for make postid
                        String postid = reference.push().getKey();

                        //put post info to realtime database
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);

                        progressDialog.dismiss();

                        //after all go to MainActivity
                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();   //don't go back
                    } else {
                        //display message when something wrong
                        Toasty.error(PostActivity.this, R.string.Failed, Toast.LENGTH_LONG, true).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //something wrong with realtime database
                    Toasty.error(PostActivity.this, R.string.Failed, Toast.LENGTH_LONG, true).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toasty.warning(PostActivity.this, R.string.NoImageselected, Toast.LENGTH_LONG, true).show();
            progressDialog.dismiss();
        } //logic for posting END

    }

    //for Crop Image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //validation (all good with choose picture)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //get uri from choose picture
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);
        } else {
            Toasty.error(PostActivity.this, R.string.Failed, Toast.LENGTH_LONG, true).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
