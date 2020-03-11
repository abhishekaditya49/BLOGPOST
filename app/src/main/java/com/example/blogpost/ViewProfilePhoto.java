package com.example.blogpost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewProfilePhoto extends AppCompatActivity {
    private Toolbar viewprofilephoto;
    private ImageView profilephoto;
    private Matrix matrix = new Matrix();
    private Float scale=1f;
    private ScaleGestureDetector SGD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_photo);
        viewprofilephoto=findViewById(R.id.viewprofiletoolbar);
        setSupportActionBar(viewprofilephoto);
        getSupportActionBar().setTitle("Profile Photo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Checkpoint1","This is viewing activity");
        profilephoto=findViewById(R.id.profileimage);

       String image_string = getIntent().getStringExtra("image");
       Log.d("string uri",image_string);
       Uri image_uri = Uri.parse(image_string);
       Glide.with(ViewProfilePhoto.this).load(image_uri).into(profilephoto);
       Log.d("Checkpoint2","After loading image");

       SGD=new ScaleGestureDetector(this,new ScaleListener());
    }

    //TODO Scale Gesture not working
    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector){
            scale=scale* detector.getScaleFactor();
            scale=Math.max(0.1f,Math.min(scale,5f));
            matrix.setScale(scale,scale);
            profilephoto.setImageMatrix(matrix);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        SGD.onTouchEvent(event);
        return true;
    }
}
