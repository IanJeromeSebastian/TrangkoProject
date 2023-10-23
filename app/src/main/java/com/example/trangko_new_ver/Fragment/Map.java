package com.example.trangko_new_ver.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trangko_new_ver.Direction.DirectionActivity;
import com.example.trangko_new_ver.NearVS.GetNearbyPlaces;
import com.example.trangko_new_ver.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

public class Map extends Fragment implements OnMapReadyCallback  {

    private boolean isPermissionGranted;
    GoogleMap googleMap;
    Boolean buttonOpen;
    FusedLocationProviderClient client;
    //LocationRequest locationRequest;
    Marker marker;

    EditText editText;
    ImageButton destinationBTN, wrenchBTN, rankBTN;
    Dialog dialog;

    private double latitude, longitude;
    private int proximityRadius = 10000;

    ActivityResultLauncher<Intent> startForResult;

    public Map() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null, false);
        buttonOpen=false;

        checkPermission();
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (isPermissionGranted) {
            if (checkGooglePlayServices()) {
                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.mapview, supportMapFragment).commit();
                supportMapFragment.getMapAsync(this);
            }
            else {
                Toast.makeText(getActivity(), "Google Play Services Not Available", Toast.LENGTH_LONG).show();
            }

        }


        startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    editText.setText(place.getAddress());
                    System.out.println("11111111111111111111"+place.getLatLng());
                    Toast.makeText(getActivity(), "location", Toast.LENGTH_SHORT).show();

                    if (marker !=null){

                        marker.remove();
                    }

                    marker = googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                }
            }
        });

        editText = (EditText) view.findViewById(R.id.search_autocomplete);

        Places.initialize(getActivity(), "AIzaSyB475QTqQaIcwkRD15Z8t-aefbp6UTMcQw");

        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(getActivity());
                startForResult.launch(intent);
            }
        });

        DrawerLayout dview = (DrawerLayout) view.findViewById(R.id.drawer);
        ImageButton iview = (ImageButton) view.findViewById(R.id.maptype);
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

        destinationBTN = (ImageButton) view.findViewById(R.id.ic_destination);
        destinationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), DirectionActivity.class);
                startActivity(in);
            }
        });

        String vulcanizing = "vulcanizing";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        wrenchBTN = (ImageButton) view.findViewById(R.id.wrench);
        wrenchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                String url = getUrl(latitude, longitude, vulcanizing);
                transferData[0] = googleMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(getContext(), "Searching for Nearby Vulcanizing Shop...", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Showing Nearby Vulcanizing Shop...", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new Dialog(getContext());

        rankBTN = (ImageButton) view.findViewById(R.id.silver);
        rankBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        return view;
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

    private String getUrl(double latitude, double longitude, String nearbyPlace){

        StringBuilder googlerURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=vulcanizing");
        googlerURL.append("&location=" + 14.900249 + "," + 120.772713);
        googlerURL.append("&radius=" + proximityRadius);
        googlerURL.append("&type=" + nearbyPlace);
        googlerURL.append("&sensor=true");
        googlerURL.append("&key=" + "AIzaSyBv2h4iC9enbEfRqXsD0s235LGsPDgbGuw");

        Log.d("GoogleMapsAcitivty", "url= " + googlerURL.toString());

        return googlerURL.toString();
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(getActivity(), "User Cancel Dialog", Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        }

        return false;
    }

    private void checkPermission() {
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("Package", getActivity().getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapview);


        ImageButton defaultmode = (ImageButton) getView().findViewById(R.id.viewd);
        defaultmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.normal_mode));
                DrawerLayout dview = (DrawerLayout) getActivity().findViewById(R.id.drawer);
                dview.closeDrawer(Gravity.LEFT);
                buttonOpen=false;

            }
        });

        ImageButton nightmode = (ImageButton) getView().findViewById(R.id.viewn);
        nightmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                //  googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.night_mode));
//                DrawerLayout dview = (DrawerLayout) getActivity().findViewById(R.id.drawer);
//                dview.closeDrawer(Gravity.LEFT);
//                buttonOpen=false;
                Toast.makeText(getActivity(), "Night view is lock", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton trafficmode = (ImageButton) getView().findViewById(R.id.viewt);
        trafficmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.normal_mode));
//                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                DrawerLayout dview = (DrawerLayout) getActivity().findViewById(R.id.drawer);
//                dview.closeDrawer(Gravity.LEFT);
//                buttonOpen=false;
                Toast.makeText(getActivity(), "Traffic view is lock", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton satellitemode = (ImageButton) getView().findViewById(R.id.views);
        satellitemode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.normal_mode));
//                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                DrawerLayout dview = (DrawerLayout) getActivity().findViewById(R.id.drawer);
//                dview.closeDrawer(Gravity.LEFT);
//                buttonOpen=false;
                Toast.makeText(getActivity(), "Satellite view is lock", Toast.LENGTH_SHORT).show();
            }
        });

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //googleMap.setMyLocationEnabled(true);
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null){

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    LatLng latlang = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(latlang).title("I am Here").icon(bitmapDescriptorFromVector(getActivity(),R.drawable.iamhere_icon));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,15));
                    googleMap.addMarker(options);


                }
            }

        });

        this.googleMap = googleMap;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable= ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap= Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(googleMap != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            googleMap.clear();

            // add markers from database to the map
        }
    }
}