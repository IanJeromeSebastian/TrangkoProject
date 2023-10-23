package com.example.trangko_new_ver.Gathering;

import static com.facebook.FacebookSdk.getApplicationContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trangko_new_ver.Chat.GroupChatActivity;
import com.example.trangko_new_ver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventCreateActivity extends FragmentActivity implements OnMapReadyCallback {

    private FirebaseAuth firebaseAuth;

    private ImageButton backBtn, uploadBtn;

    private String groupId;

    private EditText eventTitleEt, eventDescriptionEt
            ,eventDateEt, eventTimeEt, eventLocEt;

    private ProgressDialog progressDialog;

    private DatePickerDialog picker;
    private TimePickerDialog timePickerDialog;

    private Marker marker;
    FusedLocationProviderClient client;
    private GoogleMap googleMap;
    LatLng address;

    ActivityResultLauncher<Intent> startForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        firebaseAuth = FirebaseAuth.getInstance();

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.eventMap, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

        //init view
        backBtn = findViewById(R.id.backBtn);
        uploadBtn = findViewById(R.id.uploadBtn);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        System.out.println("sssssssssssssssssssssssssssssssqqqqqqs"+groupId);

        eventTitleEt = findViewById(R.id.eventTitleEt);
        eventDescriptionEt = findViewById(R.id.eventDescriptionEt);
        eventDateEt = findViewById(R.id.eventDateEt);
        eventTimeEt = findViewById(R.id.eventTimeEt);
        eventLocEt = findViewById(R.id.eventLocEt);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroup();
            }
        });
        eventDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                picker = new DatePickerDialog(EventCreateActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                timePickerDialog = new TimePickerDialog(EventCreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
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


        startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    eventLocEt.setText(place.getAddress());
                    System.out.println("11111111111111111111"+place.getLatLng());

                    marker = googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                }
            }
        });

        Places.initialize(EventCreateActivity.this, "AIzaSyB475QTqQaIcwkRD15Z8t-aefbp6UTMcQw");

        eventLocEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(EventCreateActivity.this);
                startForResult.launch(intent);
            }
        });
    }

    private void startCreatingGroup() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Gathering");

        //input title, description, date, time, location
        String eventTitle = eventTitleEt.getText().toString().trim();
        String eventDescription = eventDescriptionEt.getText().toString().trim();
        String eventDate = eventDateEt.getText().toString().trim();
        String eventTime = eventTimeEt.getText().toString().trim();
        String eventLocation = eventLocEt.getText().toString().trim();

//        Intent intent1 = new Intent(EventCreateActivity.this, GroupChatActivity.class);
//        intent1.putExtra("eventLocation", eventLocation);
//        intent1.putExtra("eventData", eventDate);
//        startActivity(intent1);

        if (TextUtils.isEmpty(eventTitle)){
            Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();


        String g_Timestamp = "" + System.currentTimeMillis();
        createEvent(""+g_Timestamp,
                ""+eventTitle,
                ""+eventDescription,
                ""+eventDate,
                ""+eventTime,
                ""+eventLocation);
    }


    private void createEvent(String g_Timestamp, String eventTitle, String eventDescription, String eventDate, String eventTime, String eventLocation) {
        //setup
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("groupId", "" + g_Timestamp);
        hashMap.put("evenTitle", "" + eventTitle);
        hashMap.put("eventDescription", "" + eventDescription);
        hashMap.put("eventData", "" + eventDate);
        hashMap.put("eventTime", "" + eventTime);
        hashMap.put("eventLocation", "" + eventLocation);
        hashMap.put("createdBy", "" + firebaseAuth.getUid());


        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        System.out.println("sssssssssssssssssssssssssssss"+groupId);
        //create
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Gathering").child(g_Timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        progressDialog.dismiss();
                        eventDateEt.setText("");
                        eventTimeEt.setText("");
                        eventDescriptionEt.setText("");
                        eventTitleEt.setText("");
                        eventLocEt.setText("");
                        Toast.makeText(EventCreateActivity.this, "Gathering created successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(EventCreateActivity.this, ""+e.getMessage()  , Toast.LENGTH_SHORT).show();
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