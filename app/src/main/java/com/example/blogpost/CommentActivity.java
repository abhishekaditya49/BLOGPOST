package com.example.blogpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentActivity extends AppCompatActivity {
    private Toolbar commentToolBar;
    private EditText addComment;
    private ImageView postComment;
    private String blogPostId;
    private FirebaseAuth uAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private RecyclerView commentRecyclerView;
    private CommentRecyclerAdapter commentRecyclerAdapter;
    private List<Comments> commentsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentToolBar=findViewById(R.id.commenttoolbar);
        setSupportActionBar(commentToolBar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addComment=findViewById(R.id.yourcomment);
        postComment=findViewById(R.id.postcommentbutton);
        commentRecyclerView=findViewById(R.id.commentList);

        uAuth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        userId=uAuth.getCurrentUser().getUid();
        blogPostId=getIntent().getStringExtra("BlogPost ID");



        //RecyclerView Firebase List

        commentsList=new ArrayList<>();
        commentRecyclerAdapter=new CommentRecyclerAdapter(commentsList);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentRecyclerAdapter);


    if(uAuth.getCurrentUser() != null){
        //Comments query
        firebaseFirestore.collection("Posts/"+blogPostId+"/Comments").addSnapshotListener(CommentActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                        if(doc.getType()==DocumentChange.Type.ADDED){

                            String commentID=doc.getDocument().getId();
                            String message = doc.getDocument().getString("message");
                            Timestamp timestamp = doc.getDocument().getTimestamp("timestamp");
                            String userid=doc.getDocument().getString("user_ID");

                            /*Comments comment=doc.getDocument().toObject(Comments.class);*/
                            Log.d("messagecomment",message);
                            /*Log.d("timestamp",timestamp);*/
                            Log.d("userid",userid);
                            Log.d("Commentid",commentID);

                            Comments comment = new Comments(message,timestamp,userid);
                            commentsList.add(comment);
                            commentRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });

    }




        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMsg = addComment.getText().toString();
                if(!commentMsg.isEmpty()){

                    Map<String,Object>commentMap=new HashMap<>();
                    commentMap.put("message",commentMsg);
                    commentMap.put("user_ID",userId);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/"+blogPostId+"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                addComment.setText("");
                                Toast.makeText(CommentActivity.this,"Comment Posted",Toast.LENGTH_SHORT).show();


                            }else{
                                String e= task.getException().getMessage();
                                Toast.makeText(CommentActivity.this,"Unable to post comment.Error:"+e,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(CommentActivity.this,"Post a comment!",Toast.LENGTH_SHORT).show();
                }
            }
        });





    }
}
