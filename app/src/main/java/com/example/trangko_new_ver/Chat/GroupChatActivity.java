package com.example.trangko_new_ver.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.trangko_new_ver.Adapter.AdapterGroupChat;
import com.example.trangko_new_ver.Gathering.EventInfoActivity;
import com.example.trangko_new_ver.Model.ModelGroupChat;
import com.example.trangko_new_ver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    
    private String groupId, eventId = "", myGroupRole;

    private ImageView groupIconIv;
    private TextView groupTitleTv, eventLoc;
    private EditText messageEt;
    private ImageButton sendBtn;

    private ImageButton addParticipant;

    private RecyclerView chatRv;

    private ArrayList<ModelGroupChat> groupChatList;
    private AdapterGroupChat adapterGroupChat;

    private RelativeLayout eventRv, eventBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //init view
        groupIconIv = findViewById(R.id.groupIconIv);
        groupTitleTv = findViewById(R.id.groupTitleTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        chatRv = findViewById(R.id.chatRv);
        eventLoc = findViewById(R.id.eventLocTv);

        eventRv = findViewById(R.id.eventRv);
        eventBar = findViewById(R.id.eventBar);

        addParticipant = findViewById(R.id.addParticipant);

        //get id of the group
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        System.out.println("ssssssssssssssssssssssssssssssswwwww"+groupId);

        eventRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GroupChatActivity.this, EventInfoActivity.class);
                intent1.putExtra("groupId", groupId);
                startActivity(intent1);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        loadGroupInfo();
        loadGroupMessages();

        loadMyGroupRole();

        loadEvent();

        addParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent1 = new Intent(GroupChatActivity.this, GroupInfoActivity.class);
                    intent1.putExtra("groupId", groupId);
                    startActivity(intent1);
//                if (myGroupRole.equals("creator") || myGroupRole.equals("admin")){
//
//                    Intent intent1 = new Intent(GroupChatActivity.this, GroupParticipantAddActivity.class);
//                    intent1.putExtra("groupId", groupId);
//                    startActivity(intent1);
//
//                }
//                else {
//                    Toast.makeText(GroupChatActivity.this, "Creator and Admin only can add a participant", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input data
                String message = messageEt.getText().toString().trim();
                //validate
                if (TextUtils.isEmpty(message)){
                    //empty
                    Toast.makeText(GroupChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }
                else {
                    //send message
                    sendMessage(message);
                }
            }
        });

    }

    private void loadEvent() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Gathering")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()){
//                            String eventLocation = "" + ds.child("eventLocation").getValue();
//                            String eventDates = "" + ds.child("eventData").getValue();
//
//                            eventLoc.setText(eventLocation);
//                            eventDate.setText(eventDates);
//
//                            System.out.println("gfdsfgdfdfdfdfdfdfdfdfdfdfdfdfdfdf" + eventLoc);
//                        }
                        String g_Timestamp = "" + System.currentTimeMillis();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            eventId = "" + ds.child(g_Timestamp).getValue();
                            if (eventId.isEmpty()){
                                eventBar.setVisibility(View.GONE);
                            }
                            else{
                                eventBar.setVisibility(View.VISIBLE);
                                String eventLocation = "" + ds.child("eventLocation").getValue();
                                eventLoc.setText("@ " + eventLocation);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//        String eventLocation = getIntent().getStringExtra("eventLocation");
//        String eventDates = getIntent().getStringExtra("eventData");
//
//        eventLoc.setText(eventLocation);
//        eventDate.setText(eventDates);
    }

    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants")
                .orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            myGroupRole = "" + ds.child("role").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupMessages() {
        //init list
        groupChatList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChatList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelGroupChat model = ds.getValue(ModelGroupChat.class);
                            groupChatList.add(model);
                        }
                        //adapter
                        adapterGroupChat = new AdapterGroupChat(GroupChatActivity.this, groupChatList);
                        //set to recycler view
                        chatRv.setAdapter(adapterGroupChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        System.out.println("reffffffffffffffffffffffffffff"+ref);
    }

    private void sendMessage(String message) {
        //timestamp
        String timestamp = "" + System.currentTimeMillis();

        //setup message data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", "" + firebaseAuth.getUid());
        hashMap.put("message", "" + message);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("type", "text");

        //add in db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //message sent
                        //clear messageEt
                        messageEt.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //message failed send
                        Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String groupTitle = "" + ds.child("groupTitle").getValue();
                            String groupDescription = "" + ds.child("groupDescription").getValue();
                            String groupIcon = "" + ds.child("groupIcon").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String createBy = "" + ds.child("createBy").getValue();


                            groupTitleTv.setText(groupTitle);
                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.community_prof).into(groupIconIv);
                            }
                            catch (Exception e){
                                groupIconIv.setImageResource(R.drawable.community_prof);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}