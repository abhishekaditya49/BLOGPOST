package com.example.blogpost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.acl.LastOwnerException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class recycleradapter extends RecyclerView.Adapter<recycleradapter.ViewHolder> {
    public List<BlogPost> blogPostList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth uAuth;
    private RequestOptions placeholderoptions;
    public recycleradapter(List<BlogPost> blogList){
        this.blogPostList=blogList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bloglistitem,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        uAuth=FirebaseAuth.getInstance();
        placeholderoptions= new RequestOptions();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final recycleradapter.ViewHolder holder, int position) {



        String description_data=blogPostList.get(position).getDesc();
        holder.setDescription_textView(description_data);

        /*final String[] username = new String[1];
        final String[] profile_photo_url = new String[1];
        String user_id = blogPostList.get(position).getUid();


        //FIX ERROR : user id is null
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    username[0] = task.getResult().getString("Name");
                    profile_photo_url[0] = task.getResult().getString("Image");
                }
            }
        });
        holder.setUsername_textView(username[0]);*/

        String uid = blogPostList.get(position).getUid();
        final String[] username = new String[1];
        final String[] photourl = new String[1];

        if(uAuth.getCurrentUser()!=null){
            firebaseFirestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                   /* username[0] = task.getResult().getString("Name");

                    photourl[0] = task.getResult().getString("Image");*/
                        holder.setProfile_thumbnail(task.getResult().getString("Image"));
                        holder.setUsername_textView(task.getResult().getString("Name"));
                    }
                }
            });
        }

        /*Log.d("User name retrieved",username[0]);
        holder.setUsername_textView(username[0]);*/

        String image_url=blogPostList.get(position).getImage_url();
        holder.setNew_post(image_url);

        Timestamp date_of_post = blogPostList.get(position).getTimestamp();
        holder.setDate(date_of_post);
        Log.e("Checkpoint","After setting timestamp");

        //Like Features
        final String current_user_id= uAuth.getCurrentUser().getUid();
        final String blogPostId = blogPostList.get(position).BlogPostId;

        //Likes
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(current_user_id).addSnapshotListener((Activity)context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    holder.likeImageButton.setImageDrawable(context.getDrawable(R.mipmap.action_like_red));
                }
                else{
                    holder.likeImageButton.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }
            }
        });

        //Likes Count
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener((Activity) context,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    int count = queryDocumentSnapshots.size();
                    holder.setLikesCount(count);
                }
                else {
                    holder.setLikesCount(0);

                }
            }
        });





        holder.likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String,Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(current_user_id).set(likeMap);
                            Toast.makeText(context,"Liked!", Toast.LENGTH_SHORT).show();
                            /*holder.likeImageButton.setImageDrawable(R.drawable.action_like_red);*/
                            //Cannot change. Should be done in real time

                        }
                        else{
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(current_user_id).delete();
                            Toast.makeText(context,"Unliked!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentActivity.class);
                intent.putExtra("BlogPost ID",blogPostId);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }


    //ViewHolder Class
    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView description_textView,username_textView,date_textView;
        private ImageView profile_thumbnail,new_post;
        private ImageView likeImageButton;
        private  TextView likeCount;
        private ImageView commentButton;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;

            likeImageButton=view.findViewById(R.id.like_button);
            commentButton=view.findViewById(R.id.comment);


        }

        public void setDescription_textView(String text) {
            description_textView=view.findViewById(R.id.caption);

            description_textView.setText(text);
        }
        public void setNew_post(String downloadUri){
            new_post=view.findViewById(R.id.post_image);
            placeholderoptions.placeholder(R.color.colorPrimary);
            Glide.with(context).applyDefaultRequestOptions(placeholderoptions).load(downloadUri).into(new_post);
        }

        public void setUsername_textView(String username){
            username_textView=view.findViewById(R.id.username);
            /*Log.d("user name in set func",username);*/
            username_textView.setText(username);

        }

        public void setDate(Timestamp date_added){
            date_textView= view.findViewById(R.id.dateadded);

            Date dateFormattedDate= date_added.toDate();
            DateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
            String dateStr = dateFormat.format(dateFormattedDate);
            Log.d("Date ",dateStr);
            date_textView.setText(dateStr);
        }

        public void setProfile_thumbnail(String downloadUri){
            profile_thumbnail=view.findViewById(R.id.photo_view);


            placeholderoptions.placeholder(R.color.colorPrimary);
            Glide.with(context).applyDefaultRequestOptions(placeholderoptions).load(downloadUri).into(profile_thumbnail);
        }

        public void setLikesCount(int count){
            likeCount=view.findViewById(R.id.likeCount);
            String likesCount;
            if(count<=1){
                likesCount = Integer.toString(count) + " like";
                likeCount.setText(likesCount);
            }
            else{
                likesCount = Integer.toString(count) + " likes";
                likeCount.setText(likesCount);
            }

        }



    }
}
