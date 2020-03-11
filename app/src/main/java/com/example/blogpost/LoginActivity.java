package com.example.blogpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText email_text;
    private EditText password_text;
    private Button login_button;
    private Button new_account_button;
    private ProgressBar progressbar;
    FirebaseAuth uAuth; /* declare here because we need uAth in many places*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_text=(EditText)findViewById(R.id.emailtext);
        password_text=(EditText)findViewById((R.id.password));
        login_button=(Button)findViewById((R.id.login_button));
        new_account_button=(Button)findViewById((R.id.new_account_button));
        progressbar=(ProgressBar)findViewById(R.id.progressbar);
        uAuth=FirebaseAuth.getInstance();

        new_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });



        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_email_text = email_text.getText().toString();
                String login_password_text= password_text.getText().toString();

                //Check whether login fields are empty or not
                if(!login_email_text.isEmpty() && !login_password_text.isEmpty()){
                    progressbar.setVisibility(View.VISIBLE);
                    uAuth.signInWithEmailAndPassword(login_email_text,login_password_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressbar.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()){
                                //SEND USER TO MAIN ACTIVITY
                                Toast.makeText(LoginActivity.this,"Welcome To BLOGPOST!", Toast.LENGTH_LONG).show();

                                sendToMainActivity();
                            }

                            else{
                                String e = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,e, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    email_text.setText(" ");
                    password_text.setText(" ");
                    Toast.makeText(LoginActivity.this,"Please Enter Your Email ID And Password!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = uAuth.getCurrentUser();
        if(user!= null){
            sendToMainActivity();
        }
    }

    private void sendToMainActivity(){


        Intent intent= new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
