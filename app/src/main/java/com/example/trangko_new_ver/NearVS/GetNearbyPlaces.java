package com.example.trangko_new_ver.NearVS;

import android.os.AsyncTask;

import com.example.trangko_new_ver.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googleplaceData, url;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceData = downloadUrl.ReadTheUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleplaceData;
    }

    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);

        DisplayNearbyPlaces(nearbyPlacesList);

    }

    private void DisplayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList){

        for (int i=0; i<nearbyPlacesList.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            double lat = 14.900249;
            double lng = 120.772713;

            LatLng latlang = new LatLng(lat, lng);
            markerOptions.position(latlang)
                    .title(nameOfPlace + ":" + vicinity)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.wrench_icon));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,15));
            mMap.addMarker(markerOptions);

        }
    }

}
