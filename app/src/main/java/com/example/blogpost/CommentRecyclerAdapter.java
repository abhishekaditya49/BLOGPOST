package com.example.blogpost;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private List<Comments> commentsList;

    public CommentRecyclerAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentlistitem,parent,false);
        context=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentRecyclerAdapter.ViewHolder holder, int position) {
    holder.setIsRecyclable(true);

    firebaseFirestore=FirebaseFirestore.getInstance();
    String commentMessage= commentsList.get(position).getMessage();
    holder.setCommentMessage(commentMessage);

    String userID= commentsList.get(position).getUserID();
       /* final String[] name = new String[1];
        final String[] image_url = new String[1];*/

        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        /*name[0] = task.getResult().getString("Name");
                        image_url[0] = task.getResult().getString("Image");*/

                        holder.setUsername(task.getResult().getString("Name"));
                        holder.setUserimage(task.getResult().getString("Image"));
                    }

                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private TextView commentMessage;
        private TextView username;
        private CircleImageView userimage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public  void  setCommentMessage(String comment){
            commentMessage=view.findViewById(R.id.commentsbyuser);
            commentMessage.setText(comment);
        }
        public void setUsername(String user_name){
            username=view.findViewById(R.id.usernameincomment);
            username.setText(user_name);
        }

        public void setUserimage(String imageurl){
            userimage=view.findViewById(R.id.userphoto);
            Uri image_uri= Uri.parse(imageurl);

            RequestOptions placeHolder= new RequestOptions();
            placeHolder.placeholder(R.color.colorPrimary);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(image_uri).into(userimage);
        }
    }
}
