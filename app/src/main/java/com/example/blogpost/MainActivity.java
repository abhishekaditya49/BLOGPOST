package com.example.blogpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainToolBar;
    private FirebaseAuth uAuth;
    private FloatingActionButton fab;

    private FirebaseFirestore firestore;
    private String current_user_id;
    private BottomNavigationView mainbottomview;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mainToolBar = (Toolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle("BLOG POST");
        fab = findViewById(R.id.fab);

        mainbottomview = findViewById((R.id.bottomNavigationView));


        if (uAuth.getCurrentUser() != null) {
            //Fragments
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            loadFragment(homeFragment);

            mainbottomview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.home_fragment: {
                            loadFragment(homeFragment);
                            return true;
                        }
                        case R.id.notification_fragment: {
                            loadFragment(notificationFragment);
                            return true;
                        }
                        case R.id.account_fragment: {
                            loadFragment(accountFragment);
                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });

            Log.d("Checkpoint1", "before fab ");
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("Checkpoint2", "after fab ");
                    /* Toast.makeText(MainActivity.this,"FAB CLICKED",Toast.LENGTH_LONG).show();*/
                    Log.d("Checkpoint5", " After toast");
                    Intent intent = new Intent(MainActivity.this, NewPost.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Checkpoint3","Before getting user instance");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        /*Log.d("Checkpoint4",user.getUid());*/
        if(user==null){
            sendToLogin();
        }
        else{

            current_user_id = uAuth.getCurrentUser().getUid();
            firestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                       if(!task.getResult().exists()){

                           Intent setupintent = new Intent(MainActivity.this, SetupActivity.class);
                           startActivity(setupintent);
                           finish();
                       }else{
                           /*String msg= task.getResult().getString("Name");
                           msg=getFirstWord(msg);
                           Toast.makeText(MainActivity.this,"Hey "+msg+"!",Toast.LENGTH_LONG).show();*/
                       }
                    }
                    else{
                        String e = task.getException().getMessage();


                        Toast.makeText(MainActivity.this,e,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_logout :{
                logOut();

                return true;
            }

            case R.id.action_account_seting:{
                Intent i = new Intent(MainActivity.this,SetupActivity.class);
                startActivity(i);
            }

            default:return false;
        }
    }



    private void logOut() {
        uAuth.signOut();
        sendToLogin();
    }



    private void sendToLogin() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private String getFirstWord(String text) {

        int index = text.indexOf(' ');

        if (index > -1) { // Check if there is more than one word.

            return text.substring(0, index).trim(); // Extract first word.

        } else {

            return text; // Text is the first word itself.
        }
    }
    private void loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }
}
