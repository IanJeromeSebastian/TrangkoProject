package com.example.trangko_new_ver.NewsfeedContent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trangko_new_ver.Adapter.AdapterPosts;
import com.example.trangko_new_ver.MainActivity;
import com.example.trangko_new_ver.Model.ModelPost;
import com.example.trangko_new_ver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TheirProfileActivity extends AppCompatActivity {

    RecyclerView postRecyclerView;
    FirebaseAuth firebaseAuth;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    CircleImageView circleImageView;
    TextView usernameTV, descriptionTV;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_their_profile);

        postRecyclerView = findViewById(R.id.recyclerview_posts);
        firebaseAuth = FirebaseAuth.getInstance();

        circleImageView = findViewById(R.id.profpic);
        usernameTV = findViewById(R.id.username);
        descriptionTV = findViewById(R.id.description);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        System.out.println("Their Profile-----------"+uid);

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("UID").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //check until required data get
                for (DataSnapshot ds : snapshot.getChildren()){
                    //get data
                    String name = "" + ds.child("UserName").getValue();
                    String description = "" + ds.child("Desciption").getValue();
                    String profile = "" + ds.child("ProfileImage").getValue();

                    //set data
                    usernameTV.setText(name);
                    descriptionTV.setText(description);
                    try {
                        //set profile
                        Picasso.get().load(profile).into(circleImageView);
                    }catch (Exception e){
                        //set default
                        Picasso.get().load(R.drawable.profile).into(circleImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        postList = new ArrayList<>();
//        checkUserStatus();
        loadHisPost();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void loadHisPost() {
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRecyclerView.setLayoutManager(layoutManager);
        System.out.println("load his post-------"+uid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPost myPost = ds.getValue(ModelPost.class);
                    postList.add(myPost);

                    //adapter
                    adapterPosts = new AdapterPosts(getApplicationContext(), postList);
                    //set adapter to recyclerview
                    postRecyclerView.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHisPost(String searchQuery) {
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRecyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPost myPost = ds.getValue(ModelPost.class);



                    if (myPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())  ||
                            myPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(myPost);
                    }
                    //adapter
                    adapterPosts = new AdapterPosts(TheirProfileActivity.this, postList);
                    //set adapter to recyclerview
                    postRecyclerView.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TheirProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

//            email = user.getEmail();
            uid = user.getUid();



        }
        else{
            startActivity(new Intent(this, MainActivity.class));
            finish();




        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}