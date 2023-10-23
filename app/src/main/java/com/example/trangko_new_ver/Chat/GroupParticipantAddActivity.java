package com.example.trangko_new_ver.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.trangko_new_ver.Adapter.AdapterParticipantAdd;
import com.example.trangko_new_ver.Model.ModelUser;
import com.example.trangko_new_ver.Notification.Data;
import com.example.trangko_new_ver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupParticipantAddActivity extends AppCompatActivity {

    private RecyclerView usersRv;

    private FirebaseAuth firebaseAuth;

    private String groupId;
    private String myGroupRole;

    private ArrayList<ModelUser> userList;
    private AdapterParticipantAdd adapterParticipantAdd;

    private TextView groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant_add);

        firebaseAuth = FirebaseAuth.getInstance();

        usersRv = findViewById(R.id.usersRv);
        groupName = findViewById(R.id.groupName);

        groupId = getIntent().getStringExtra("groupId");
        loadGroupInfo();
    }

    private void getAllUser() {
        //init list
        userList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all user except you
                    if (!firebaseAuth.getUid().equals(modelUser.getUID())){
                        //not my uid
                        userList.add(modelUser);
                    }
                }
                //setup adapter
                adapterParticipantAdd = new AdapterParticipantAdd(GroupParticipantAddActivity.this, userList
                        , "" + groupId, "" + myGroupRole);
                //set adapter to recyclerview
                usersRv.setAdapter(adapterParticipantAdd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds :  snapshot.getChildren()){
                    String groupId = "" + ds.child("groupId").getValue();
                    String groupTitle = "" + ds.child("groupTitle").getValue();
                    String groupDescription = "" + ds.child("groupDescription").getValue();
                    String groupIcon = "" + ds.child("groupIcon").getValue();
                    String createdBy = "" + ds.child("createBy").getValue();
                    String timestamp = "" + ds.child("timestamp").getValue();

                    groupName.setText(groupTitle);

                    ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        myGroupRole = "" + snapshot.child("role").getValue();
                                        groupName.setText(groupTitle+"("+myGroupRole+")");
                                        getAllUser();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}