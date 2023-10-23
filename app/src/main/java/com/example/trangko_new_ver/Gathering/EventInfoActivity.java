package com.example.trangko_new_ver.Gathering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trangko_new_ver.Adapter.AdapterPosts;
import com.example.trangko_new_ver.Adapter.AdapterUsers;
import com.example.trangko_new_ver.MainPage;
import com.example.trangko_new_ver.Model.ModelGathering;
import com.example.trangko_new_ver.Model.ModelPost;
import com.example.trangko_new_ver.Model.ModelUser;
import com.example.trangko_new_ver.Notification.Data;
import com.example.trangko_new_ver.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private FirebaseAuth firebaseAuth;
    private String groupId;

    private String myGroupRole = "";
    private String myUid;

    private TextView eventName
            ,locationTv
            ,dateTv
            ,timeTv
            ,descriptionTv
            ,editEventTv
            ,cancelEventTv
            ,participantsTv;

    private ImageButton joinBtn,cancelBtn;
    private RecyclerView participantsRv;

    boolean mProcesssJoin = false;
    List<ModelGathering> gatheringList;


    private DatabaseReference joinRef;
    private DatabaseReference countRef;
    private List<ModelUser> userList;
    private AdapterUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.eventMap, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

        eventName = findViewById(R.id.eventName);
        locationTv = findViewById(R.id.locationTv);
        dateTv = findViewById(R.id.dateTv);
        timeTv = findViewById(R.id.timeTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        editEventTv = findViewById(R.id.editEventTv);
        cancelEventTv = findViewById(R.id.cancelEventTv);
        participantsTv = findViewById(R.id.participantsTv);
        joinBtn = findViewById(R.id.joinBtn);
        participantsTv = findViewById(R.id.participantsTv);
        cancelBtn = findViewById(R.id.cancelBtn);
        participantsRv = findViewById(R.id.participantsRv);

        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();

        groupId = getIntent().getStringExtra("groupId");

        loadEventInfo();

        loadMyGroupRole();

        editEventTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventInfoActivity.this, EventEditActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

        cancelEventTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogTitle = "Cancel Gathering";
                String dialogDescription = "Are you sure want to Delete the community?";

                AlertDialog.Builder builder = new AlertDialog.Builder(EventInfoActivity.this);
                builder.setTitle(dialogTitle)
                        .setMessage(dialogDescription)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteGroup();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        joinRef = FirebaseDatabase.getInstance().getReference("Join");
        countRef = FirebaseDatabase.getInstance().getReference("Groups");

        gatheringList = new ArrayList<>();
        userList = new ArrayList<>();
//        joinGathering();
        loadGathering();

    }

    private void loadGathering() {
        //path of all posts
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        countRef.child(groupId).child("Gathering").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gatheringList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelGathering model = ds.getValue(ModelGathering.class);
                    gatheringList.add(model);

                    int participants = Integer.parseInt(model.getParticipants());
                    String uid = model.getGatheringId();
                    System.out.println("gsdgdggggggggggggggg" + uid);

                    String location = model.getEventLocation();

                    joinBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mProcesssJoin = true;

                            joinRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (mProcesssJoin){
                                        if (snapshot.child(uid).hasChild(myUid)){
                                            //already joined, cancel
                                            countRef.child(groupId).child("Gathering").child(uid).child("participants").setValue("" + (participants-1));
                                            joinRef.child(uid).child(myUid).removeValue();
                                            mProcesssJoin = false;
                                        }
                                        else {
                                            //not join
                                            countRef.child(groupId).child("Gathering").child(uid).child("participants").setValue("" + (participants+1));
                                            joinRef.child(uid).child(myUid).setValue("Joined");
                                            mProcesssJoin = false;
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });

                        }
                    });

                    participantsTv.setText("Participants (" + participants + ")");

                    joinRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if (snapshot.child(uid).hasChild(myUid)){
                               //has join
                               joinBtn.setImageDrawable(getResources().getDrawable(R.drawable.join_cancel));
                           }
                           else {
                               joinBtn.setImageDrawable(getResources().getDrawable(R.drawable.join_gathering));
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

    private void deleteGroup() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Gathering")
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //group delete successfully
                        Toast.makeText(EventInfoActivity.this, "You've canceled the gathering", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EventInfoActivity.this, MainPage.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Toast.makeText(EventInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadEventInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Gathering").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    //get event info
                    String eventTitle = "" + ds.child("evenTitle").getValue();
                    String eventDescription = "" + ds.child("eventDescription").getValue();
                    String eventLocation = "" + ds.child("eventLocation").getValue();
                    String eventDate = "" + ds.child("eventData").getValue();
                    String eventTime = "" + ds.child("eventTime").getValue();


                    //set event info
                    eventName.setText(eventTitle);
                    descriptionTv.setText(eventDescription);
                    locationTv.setText(eventLocation);
                    dateTv.setText(eventDate);
                    timeTv.setText(eventTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").orderByChild("uid")
                .equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            myGroupRole = "" + ds.child("role").getValue();

                            if (myGroupRole.equals("participant")){
                                editEventTv.setVisibility(View.GONE);
                                cancelEventTv.setVisibility(View.GONE);
                            }
                            else if (myGroupRole.equals("admin")){
                                editEventTv.setVisibility(View.GONE);
                                cancelEventTv.setVisibility(View.GONE);
                            }
                            else if (myGroupRole.equals("creator")){
                                editEventTv.setVisibility(View.VISIBLE);
                                cancelEventTv.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng philippines = new LatLng(14.9968, 121.1710);
        googleMap.addMarker(new MarkerOptions().position(philippines));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(philippines));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(philippines,8));
    }
}