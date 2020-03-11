package com.example.blogpost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
private Toolbar setuptoolbar;
private CircleImageView profile_photo;
private Uri mainImageUri;
private Button save_button;
private EditText name_text;
private StorageReference mStorageRef;
private FirebaseAuth mAuth;
private FirebaseFirestore firestore;
private ProgressBar progressbar;
private String user_id;
private String name,image_url;
private boolean isChanged =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setuptoolbar=findViewById(R.id.setup_toolbar);

        setSupportActionBar(setuptoolbar);
        getSupportActionBar().setTitle("Account Settings");

        profile_photo=findViewById(R.id.profile_picture);
        registerForContextMenu(profile_photo);
        save_button=findViewById(R.id.save_button);
        name_text=findViewById(R.id.name_text);
        progressbar = findViewById(R.id.setup_progress);


        Toast.makeText(SetupActivity.this,"Loading...",Toast.LENGTH_SHORT).show();

        firestore=FirebaseFirestore.getInstance();

       /* FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
*/
        mAuth= FirebaseAuth.getInstance();
        user_id=mAuth.getCurrentUser().getUid();

        progressbar.setVisibility(View.VISIBLE);

        save_button.setEnabled(false);

        mStorageRef= FirebaseStorage.getInstance().getReference();

        //Retrieving Username and image from firestore database when account setting activity is opened

        firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        name = task.getResult().getString("Name");
                        image_url = task.getResult().getString("Image");

                        mainImageUri= Uri.parse(image_url);

                        name_text.setText(name);

                        RequestOptions placeholderreq =new RequestOptions();
                        placeholderreq.placeholder(R.drawable.default_profile);
                        Glide.with(SetupActivity.this).load(image_url).into(profile_photo);

                    }
                    else{

                        Toast.makeText(SetupActivity.this,"Please update your username and profile picture.",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    String ex = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"FIRESTORE Error:"+ex,Toast.LENGTH_LONG).show();
                }
                progressbar.setVisibility(View.INVISIBLE);
                save_button.setEnabled(true);
            }
        });





        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_text.getText().toString();

                if (!name.isEmpty() && mainImageUri != null) {
                if(isChanged) {
                        user_id = mAuth.getCurrentUser().getUid();
                        progressbar.setVisibility(View.VISIBLE);
                        final StorageReference img_path = mStorageRef.child("profile_images").child(user_id + ".jpg");

                        img_path.putFile(mainImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    String e = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "ERROR:" + e, Toast.LENGTH_LONG).show();
                                }
                                return img_path.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    storeToFirestore(task, name);
                                } else {
                                    progressbar.setVisibility(View.INVISIBLE);
                                    String e = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "ERROR:" + e, Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    } else {
                            storeToFirestore(null,name);
                    }
                }
                else{
                    Toast.makeText(SetupActivity.this, "Enter all the information.", Toast.LENGTH_LONG).show();
                }

            }
        });


        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(SetupActivity.this,profile_photo);
                popupMenu.getMenuInflater().inflate(R.menu.account_setting_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.viewprofilephoto){
                            //NOT WORKING
                            Intent intent = new Intent(SetupActivity.this,ViewProfilePhoto.class);

                            intent.putExtra("image",mainImageUri.toString());
                            startActivity(intent);

                        }

                        else if(item.getItemId()==R.id.selectprofilephoto){

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    Toast.makeText(SetupActivity.this,"PERMISSION DENIED",Toast.LENGTH_SHORT).show();
                                    ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                                }
                                else{
                                    /*Toast.makeText(SetupActivity.this,"YOU ALREADY HAVE PERMISSION",Toast.LENGTH_LONG).show();*/
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(SetupActivity.this);

                                }
                            }
                            else{
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(SetupActivity.this);

                            }


                        }
                        else{
                            return false;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflator=getMenuInflater();
        inflator.inflate(R.menu.account_setting_menu,menu);
        menu.setHeaderTitle("What do you want to do?");

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

    }*/






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageUri = result.getUri();
                profile_photo.setImageURI(mainImageUri);
                isChanged=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void storeToFirestore(@NonNull Task<Uri> task,String user_name){
        Uri download_uri;
        if(task!=null){
            download_uri = task.getResult();
        }
        else {
            download_uri = mainImageUri;
        }

        String download_URL = download_uri.toString();
        Map<String,String> userMap = new HashMap<>();
        userMap.put("Name",user_name);//Inserting in Hashmap
        userMap.put("Image",download_URL);
        Log.d("Name",user_name);
        Log.d("Image",download_URL);
        Toast.makeText(SetupActivity.this, "Image uploaded", Toast.LENGTH_LONG).show();


        firestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SetupActivity.this,"User settings are updated!",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(SetupActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    String ex = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"FIRESTORE Error:"+ex,Toast.LENGTH_LONG).show();

                }
            }
        });
    }


}
