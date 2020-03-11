package com.example.blogpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_email_text;
    private EditText reg_password;
    private EditText reg_cpassword;
    private Button create_account;
    private Button old_account;
    private ProgressBar progressbar;

    private FirebaseAuth uAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_email_text=(EditText) findViewById(R.id.reg_email_text );
        reg_password=(EditText)findViewById(R.id.reg_password_text);
        reg_cpassword=(EditText)findViewById(R.id.reg_conpassword_text);
        create_account=(Button)findViewById(R.id.reg_create_account);
        old_account=(Button)findViewById((R.id.reg_old_account));
        progressbar=(ProgressBar)findViewById(R.id.progressbar);

        uAuth=FirebaseAuth.getInstance();

        old_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        create_account.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String email = reg_email_text.getText().toString();
                String password = reg_password.getText().toString();
                String cpassword =reg_cpassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty() && !cpassword.isEmpty()){

                    if(password.equals(cpassword)){

                        progressbar.setVisibility(View.VISIBLE);

                        uAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                           //IMPLEMENT After creating new account the user is logged out and asked to sign in again.

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    /*sendToMain();*/
                                    Toast.makeText(RegisterActivity.this,"Welcome To BLOGPOST!",Toast.LENGTH_LONG).show();

                                    Intent intent=new Intent (RegisterActivity.this,SetupActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    String e = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,e,Toast.LENGTH_LONG).show();
                                }

                                progressbar.setVisibility(View.INVISIBLE);
                            }

                        });

                    }
                    else{
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Passwords Don't Match!",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this,"Enter All the Fields",Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = uAuth.getCurrentUser();
        if(user != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
