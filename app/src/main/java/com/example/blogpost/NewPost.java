package com.example.blogpost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.*;

public class NewPost extends AppCompatActivity {
    private static final int MAX_LENGTH = 100;
    private ImageView photo;
    private Toolbar newposttoolbar;
    private EditText description;
    private Button post_button;
    private ProgressBar progressbar;
    private Uri image_uri=null;
    private String description_text;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        photo = findViewById(R.id.postphoto);
        description=findViewById(R.id.description);
        newposttoolbar=findViewById(R.id.newactivitytoolbar);
        post_button=findViewById(R.id.post_button);

        setSupportActionBar(newposttoolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressbar=findViewById(R.id.progressBar);

        storageReference= FirebaseStorage.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();



        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .start(NewPost.this);
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                description_text=description.getText().toString();

                if(image_uri!=null ){
                    progressbar.setVisibility(View.VISIBLE);
                    final String name_for_image= random();
                    final StorageReference filepath = storageReference.child("post_images").child(name_for_image+".jpg");

                    filepath.putFile(image_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                String e = task.getException().getMessage();
                                Toast.makeText(NewPost.this, "ERROR:" + e, Toast.LENGTH_LONG).show();
                            }
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                //Code for compressing the image_uri for creating thumbnails.

                                /* String image_path= image_uri.getPath();
                                try {
                                    imageBitmap = SiliCompressor.with(NewPost.this).getCompressBitmap(image_path);
                                } catch (IOException e) {
                                    Log.d("Bitmap Error/ try catch",e.getMessage());
                                }
                                final Uri compressedImageUri = getImageUri(NewPost.this,imageBitmap);
                                final StorageReference thumbFilePath= storageReference.child("post_image/thumbnails").child(name_for_image+"thumb.jpg");
                               thumbFilePath.putFile(compressedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                   @Override
                                   public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                       if (!task.isSuccessful()) {
                                           String e1 = task.getException().getMessage();
                                           Toast.makeText(NewPost.this, "ERROR:" + e1, Toast.LENGTH_LONG).show();
                                       }
                                       return thumbFilePath.getDownloadUrl();
                                   }
                               }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Uri> task) {
                                       if(task.isSuccessful()){

                                           Toast.makeText(NewPost.this, "Thumbnail uploaded", Toast.LENGTH_LONG).show();
                                       }else {
                                           Toast.makeText(NewPost.this, "Thumbnail not uploaded", Toast.LENGTH_LONG).show();
                                       }
                                   }
                               });
*/




                                String download_url=task.getResult().toString();
                                Map<String,Object> postMap = new HashMap<>();
                                postMap.put("image_url",download_url);
                                postMap.put("desc",description_text);
                                postMap.put("UID",current_user_id);
                                postMap.put("timestamp",FieldValue.serverTimestamp());


                                    firestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(NewPost.this,"Post Uploaded",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(NewPost.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                            else{
                                                String e = task.getException().getMessage();
                                                Toast.makeText(NewPost.this,"FireStore Error:"+e,Toast.LENGTH_LONG).show();
                                            }
                                            progressbar.setVisibility(View.INVISIBLE);
                                        }
                                    });

                            } else {
                                progressbar.setVisibility(View.INVISIBLE);
                                String e = task.getException().getMessage();
                                Toast.makeText(NewPost.this, "ERROR:" + e, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                else{
                    Toast.makeText(NewPost.this,"Select a photo!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_uri= result.getUri();
                photo.setImageURI(image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
