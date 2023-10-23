package com.example.trangko_new_ver.Direction;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.trangko_new_ver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback,  DirectionFinderListener  {

    private boolean isPermissionGranted;
    GoogleMap googleMap;
    Boolean buttonOpen = false;
    FusedLocationProviderClient client;
    //LocationRequest locationRequest;
    Marker marker;

    EditText etLocation,etDestination;

    ImageButton pinStart, wrenchBTN, rankBTN, backBTN;
    Dialog dialog;

    private Fragment fragment = null;
    private double latitude, longitude;
    private int proximityRadius = 10000;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    ActivityResultLauncher<Intent> startForResult, getStartForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.mapview, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(DirectionActivity.this);
        etLocation = findViewById(R.id.startDestination);
        etDestination = findViewById(R.id.endDestination);
        pinStart = findViewById(R.id.pinStart);

        Intent intent = getIntent();
        String text   = intent.getStringExtra("text");
//        String address   = intent.getStringExtra("address");
        System.out.println("textetxtetxtetx"+text);
        etDestination.setText(text);
//        etLocation.setText(address);


        DrawerLayout dview = findViewById(R.id.drawer);
        ImageButton iview = findViewById(R.id.maptype);
        iview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonOpen==false)
                {
                    dview.openDrawer(Gravity.LEFT);
                    buttonOpen=true;
                }
                else if(buttonOpen==true)
                {
                    dview.closeDrawer(Gravity.LEFT);
                    buttonOpen=false;

                }
            }
        });

        pinStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        /*String vulcanizing = "vulcanizing";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();*/

        wrenchBTN = findViewById(R.id.wrench);
        wrenchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*googleMap.clear();
                String url = getUrl(latitude, longitude, vulcanizing);
                transferData[0] = googleMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);*/
                Toast.makeText(DirectionActivity.this, "Searching for Nearby Vulcanizing Shop...", Toast.LENGTH_SHORT).show();
                // Toast.makeText(DirectionActivity.this, "Showing Nearby Vulcanizing Shop...", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new Dialog(this);

        rankBTN = findViewById(R.id.silver);
        rankBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        backBTN = findViewById(R.id.backBtn);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fragment = new Map();
//                if (fragment!=null){
//                   getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
//                }
            }
        });

        startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    etLocation.setText(place.getAddress());
                    System.out.println("11111111111111111111"+place.getLatLng());
                    Toast.makeText(DirectionActivity.this, "location", Toast.LENGTH_SHORT).show();

                    if (marker !=null){

                        marker.remove();
                    }

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                }
            }
        });

        Places.initialize(DirectionActivity.this, "AIzaSyB475QTqQaIcwkRD15Z8t-aefbp6UTMcQw");

        etLocation.setFocusable(false);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(DirectionActivity.this);
                startForResult.launch(intent);
            }
        });


        getStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    etDestination.setText(place.getAddress());
                    System.out.println("11111111111111111111"+place.getLatLng());
                    Toast.makeText(DirectionActivity.this, "location", Toast.LENGTH_SHORT).show();

                    if (marker !=null){

                        marker.remove();
                    }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                }
            }
        });

        Places.initialize(DirectionActivity.this, "AIzaSyB475QTqQaIcwkRD15Z8t-aefbp6UTMcQw");

        etDestination.setFocusable(false);
        etDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(DirectionActivity.this);
                getStartForResult.launch(intent);
            }
        });

    }
    private String getUrl(double latitude, double longitude, String nearbyPlace){

        StringBuilder googlerURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlerURL.append("location=" + 14.900249 + "," + 120.772713);
        googlerURL.append("&radius=" + proximityRadius);
        googlerURL.append("&type=" + nearbyPlace);
        googlerURL.append("&sensor=true");
        googlerURL.append("&key=" + "AIzaSyB475QTqQaIcwkRD15Z8t-aefbp6UTMcQw");

        Log.d("GoogleMapsAcitivty", "url= " + googlerURL.toString());

        return googlerURL.toString();
    }

    private void openDialog(){

        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView = dialog.findViewById(R.id.imageView4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void sendRequest() {
        //todo
//        PushNotificationService pushNotificationService = new PushNotificationService();
//        if (pushNotificationService)
        String origin = etLocation.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
            System.out.println("ssssssssssssssssssssssssss444444444444444444444444");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);


        ImageButton defaultmode = findViewById(R.id.viewd);
        defaultmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DirectionActivity.this, R.raw.normal_mode));
                DrawerLayout dview = findViewById(R.id.drawer);
                dview.closeDrawer(Gravity.LEFT);
                buttonOpen=false;

            }
        });

        ImageButton nightmode = findViewById(R.id.viewn);
        nightmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                //  googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DirectionActivity.this, R.raw.night_mode));
//                DrawerLayout dview = findViewById(R.id.drawer);
//                dview.closeDrawer(Gravity.LEFT);
//                buttonOpen=false;
                Toast.makeText(DirectionActivity.this, "Night view is lock", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton trafficmode = findViewById(R.id.viewt);
        trafficmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DirectionActivity.this, R.raw.normal_mode));
//                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                DrawerLayout dview = findViewById(R.id.drawer);
//                dview.closeDrawer(Gravity.LEFT);
//                buttonOpen=false;
                Toast.makeText(DirectionActivity.this, "Traffic view is lock", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton satellitemode = findViewById(R.id.views);
        satellitemode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DirectionActivity.this, R.raw.normal_mode));
//                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                DrawerLayout dview = findViewById(R.id.drawer);
//                dview.closeDrawer(Gravity.LEFT);
//                buttonOpen=false;
                Toast.makeText(DirectionActivity.this, "Satellite view is lock", Toast.LENGTH_SHORT).show();
            }
        });

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(DirectionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DirectionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //googleMap.setMyLocationEnabled(true);
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    LatLng latlang = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(latlang).title("I am Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.iamhere_icon));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,15));
                    googleMap.addMarker(options);

                }
            }

        });

        this.googleMap = googleMap;
    }

    @Override
    public void onDirectionFinderStart() {
        System.out.println("ssssssssssssssssssssssssssssssssssssssssssssss");
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
                System.out.println("sssssssssssssssssssssssssss111111111111111");
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
                System.out.println("ssssssssssssssssssssssssss2222222222222222222222");
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
                System.out.println("ssssssssssssssssssssssssss3333333333333333333333");
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(googleMap.addPolyline(polylineOptions));
        }
    }

}