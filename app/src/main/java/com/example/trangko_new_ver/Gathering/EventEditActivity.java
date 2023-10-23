package com.example.trangko_new_ver.Gathering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trangko_new_ver.Chat.GroupEditActivity;
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

import java.util.Calendar;
import java.util.HashMap;

public class EventEditActivity extends FragmentActivity implements OnMapReadyCallback {

    private FirebaseAuth firebaseAuth;


    private ImageButton backBtn, updateBtn;

    private EditText eventTitleEt, eventDescriptionEt
            ,eventDateEt, eventTimeEt, eventLocEt;

    private ProgressDialog progressDialog;

    private String groupId;
    private String eventId = "";

    private DatePickerDialog picker;
    private TimePickerDialog timePickerDialog;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.eventMap, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

        groupId = getIntent().getStringExtra("groupId");
        firebaseAuth = FirebaseAuth.getInstance();

        //init views
        backBtn = findViewById(R.id.backBtn);
        updateBtn= findViewById(R.id.uploadBtn);

        eventTitleEt = findViewById(R.id.eventTitleEt);
        eventDescriptionEt = findViewById(R.id.eventDescriptionEt);
        eventDateEt = findViewById(R.id.eventDateEt);
        eventTimeEt = findViewById(R.id.eventTimeEt);
        eventLocEt = findViewById(R.id.eventLocEt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        eventDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                picker = new DatePickerDialog(EventEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventDateEt.setText((month + 1)  + "/" + dayOfMonth + "/" + year);
                        System.out.println("after format"+eventDateEt);
                    }
                }, year, month, day);
                picker.show();

                System.out.println("after format"+calendar);
            }
        });

        eventTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(EventEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String  am_pm;
                        if (hourOfDay < 12){
                            am_pm = "AM";
                        }else {
                            am_pm = "PM";
                        }
                        eventTimeEt.setText(hourOfDay + ":" + minute + " " + am_pm);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        loadEventInfo();

        //handle click event
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUpdatingEvent();
            }
        });
    }

    private void startUpdatingEvent() {
        //input update
        String eventTitle = eventTitleEt.getText().toString().trim();
        String eventDescription = eventDescriptionEt.getText().toString().trim();
        String eventDate = eventDateEt.getText().toString().trim();
        String eventTime = eventTimeEt.getText().toString().trim();
        String eventLocation = eventLocEt.getText().toString().trim();

        progressDialog.setMessage("Update Community Info");
        progressDialog.show();

        String g_Timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("groupId", "" + g_Timestamp);
        hashMap.put("evenTitle", "" + eventTitle);
        hashMap.put("eventDescription", "" + eventDescription);
        hashMap.put("eventData", "" + eventDate);
        hashMap.put("eventTime", "" + eventTime);
        hashMap.put("eventLocation", "" + eventLocation);
        hashMap.put("createdBy", "" + firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Gathering").child(g_Timestamp).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                           //success
                        progressDialog.dismiss();
                        Toast.makeText(EventEditActivity.this, "Gathering info updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            //failed
                        progressDialog.dismiss();
                        Toast.makeText(EventEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadEventInfo() {
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Gathering").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get group info
                    String eventId = "" + ds.child("groupId").getValue();
                    String eventTitle = "" + ds.child("evenTitle").getValue();
                    String eventDescription = "" + ds.child("eventDescription").getValue();
                    String eventDate = "" + ds.child("eventData").getValue();
                    String eventTime = "" + ds.child("eventTime").getValue();
                    String eventLocation = "" + ds.child("eventLocation").getValue();

                    eventTitleEt.setText(eventTitle);
                    eventDescriptionEt.setText(eventDescription);
                    eventDateEt.setText(eventDate);
                    eventTimeEt.setText(eventTime);
                    eventLocEt.setText(eventLocation);
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