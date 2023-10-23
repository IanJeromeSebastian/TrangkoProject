package com.example.trangko_new_ver.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.trangko_new_ver.Notification.FCMSend;
import com.example.trangko_new_ver.R;
import com.example.trangko_new_ver.SafetyContent.HandSign;
import com.example.trangko_new_ver.SafetyContent.SafetyInfo;
import com.example.trangko_new_ver.SafetyContent.TipsInfo;
import com.example.trangko_new_ver.SafetyContent.ToolsInfo;
import com.example.trangko_new_ver.SafetyContent.TrafficInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Safety extends Fragment {

    FusedLocationProviderClient client;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    public Safety() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_safety, container, false);
        ImageButton safepre = (ImageButton) view.findViewById(R.id.safetypre);
        safepre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), SafetyInfo.class);
                startActivity(in);
            }
        });

        ImageButton tools = (ImageButton) view.findViewById(R.id.basictools);
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), ToolsInfo.class);
                startActivity(in);
            }
        });

        ImageButton hands = (ImageButton) view.findViewById(R.id.handsign);
        hands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), HandSign.class);
                startActivity(in);
            }
        });

        ImageButton traffic = (ImageButton) view.findViewById(R.id.traff);
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), TrafficInfo.class);
                startActivity(in);
            }
        });

        ImageButton tips = (ImageButton) view.findViewById(R.id.cyctips);
        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), TipsInfo.class);
                startActivity(in);
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            return;
                        }

                        String token = task.getResult();
                        System.out.println("tokennmnnnnnnnnnnn" + token);
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("Message");
        currentUser = mAuth.getCurrentUser();
        Context context = new ContextThemeWrapper(getContext(), R.style.AppTheme2);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);





        view.findViewById(R.id.need_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialAlertDialogBuilder.setTitle("Do you really need help?");

                materialAlertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        btnreg.setEnabled(true);
                        dialog.dismiss();




                        System.out.println("ssssssssssssssssssssssssssssssssssssssssssss");


                        client = LocationServices.getFusedLocationProviderClient(getContext());
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        System.out.println("client Location");

                        Task<Location> task = client.getLastLocation();
                        task.addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {

                                if (location != null){

                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    System.out.println("222222222222");
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        String city = addresses.get(0).getLocality();
                                        String state = addresses.get(0).getAdminArea();
                                        String country = addresses.get(0).getCountryName();
                                        String postalCode = addresses.get(0).getPostalCode();
                                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                                        System.out.println("fsjfbsdjf" + address);
                                        System.out.println("fsjfbsdjf" + knownName);

                                        String title = currentUser.getDisplayName();
                                        String message = address;
                                        System.out.println("111111111111111111111111111");

                                        if (!title.equals("")&& !message.equals("")){
                                            FCMSend.pushNotification(
                                                    getContext(),
                                                    "/topics/all",
                                                    title+" "+"Need help",
                                                    message
                                                    //cdnk6SmRQqOKxypSJ3BMsh:APA91bG-Jj8Xj5UWC3i6RTqF_QDpU-cgIXcMGaxDfMfm5UAoiUKcz6IOCUa9g8du-CouRI5Hyz7cqj4bUOsIlwMdA8oKeZAhor70--JRxgnihpX420jPuzkzP020Pp-Xsfz3A9HW1-Dx
                                                    //fN0AaLDaTYetTw7HTZRTjp:APA91bEM1hcaFXiBLN-z5b4CsKJIjd17Rp6KiuaikOGGcArGf7-3ky3P46oiBwoGr0SWd5dIoDfB0vobg-llKEzLOyNCUTd8XJ_Sug_IABUE4IHrbqr153TbxwCK0h9YtL86hB7xXqef
                                            );
                                            Toast.makeText(getContext(),"Please stay in your current location",Toast.LENGTH_SHORT).show();
                                            System.out.println("fsjfbsdjf111" + message);
                                        }


                                        Toast.makeText(getContext(),"Please stay in your current locationwwwww",Toast.LENGTH_SHORT).show();

//                                if (!title.equals("")&& !message.equals("")){
//                                    FCMSend.pushNotification(
//                                            getContext(),
//                                            "cHrcG95cSuiXZrgJhDps56:APA91bFYQdqRZ2RRMtQ8uOqeGF46EOF-kbxQtUcHHfR3px4pVav4FJjYUK92EYrvfiZLPnqRM17VldYPBQ6LsualTkKDtMUtJr-vk0OKO9lPgnWVT_V1yCyMZB83_atK6lsyYaGpsLvi",
//                                            title+" "+"Need help",
//                                            message
//
//                                    );
//
//                                    System.out.println("fsjfbsdjf111" + message);
//                                }
//                                Toast.makeText(getContext(),"Please stay in your current location",Toast.LENGTH_SHORT).show();

                                    } catch (IOException e) {
                                        System.out.println("111111111l111111111111111111"+e);

                                        e.printStackTrace();
                                    }

                                }
                            }
                        });

                    }
                });

                materialAlertDialogBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();


                    }
                });

                materialAlertDialogBuilder.show();







            }
        });

        //Device id

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("token" + token);
                    }
                });

        return view;
    }
}