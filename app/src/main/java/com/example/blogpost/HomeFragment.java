package com.example.blogpost;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView bloglistview;
    private List<BlogPost> blogPostList;
    private FirebaseFirestore firestore;
    private recycleradapter recyclerAdapter;
    private FirebaseAuth uAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstLoad=true;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blogPostList = new ArrayList<>();
        bloglistview = view.findViewById(R.id.blog_list_view);

        recyclerAdapter = new recycleradapter(blogPostList);
        bloglistview.setLayoutManager(new LinearLayoutManager(getActivity()));
        bloglistview.setAdapter(recyclerAdapter);
        uAuth = FirebaseAuth.getInstance();

        if (uAuth.getCurrentUser() != null) {

            firestore = FirebaseFirestore.getInstance();

            bloglistview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom){
                        /*String desc = lastVisible.getString("desc");
                        Toast.makeText(container.getContext(),"Reached : "+desc,Toast.LENGTH_LONG).show();*/

                        loadNextPosts();
                    }
                }
            });


            Query firstquery = firestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstquery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(isFirstLoad){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    }

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogpostid =doc.getDocument().getId();

                            final String bloguid = doc.getDocument().getString("UID");
                           /* Log.d("user id 1", "this is before user id");
                            Log.d("user id : ", bloguid);*/
                            BlogPost blogpost = doc.getDocument().toObject(BlogPost.class).withId(blogpostid);
                            blogpost.setUid(bloguid);
                          /* Log.d("DESC",blogpost.getDesc());
                           Log.d("URL",blogpost.getImage_url());
                           Log.d("UID",blogpost.getUid());*/


                            if(isFirstLoad){
                                blogPostList.add(blogpost);
                            }else{

                                blogPostList.add(0,blogpost);
                            }

                            recyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstLoad=false;
                }
            });

        }
        return view;
    }

    public void loadNextPosts(){

        if(uAuth.getCurrentUser()!=null){
            Query nextquery = firestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextquery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if(!queryDocumentSnapshots.isEmpty()){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogpostid = doc.getDocument().getId();
                                final String bloguid = doc.getDocument().getString("UID");
                                Log.d("user id 1", "this is before user id");
                                Log.d("user id : ", bloguid);
                                BlogPost blogpost = doc.getDocument().toObject(BlogPost.class).withId(blogpostid);

                          /* Log.d("DESC",blogpost.getDesc());
                           Log.d("URL",blogpost.getImage_url());
                           Log.d("UID",blogpost.getUid());*/
                                blogpost.setUid(bloguid);
                                blogPostList.add(blogpost);
                                recyclerAdapter.notifyDataSetChanged();


                            }
                        }
                    }

                }
            });
        }



    }
}
