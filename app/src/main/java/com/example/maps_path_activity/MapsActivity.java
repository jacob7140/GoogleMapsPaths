package com.example.maps_path_activity;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String TAG = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.setTitle("Paths Activity");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.argb(255, 0, 0, 255));

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray pointsArray = obj.getJSONArray("points");

            LatLng startLatLng = null;
            LatLng endLatLng = null;

            for (int i = 0; i < pointsArray.length(); i++){
                JSONObject insideObj = pointsArray.getJSONObject(i);

                Double latitude = Double.parseDouble(insideObj.getString("latitude"));
                Double longitude = Double.parseDouble(insideObj.getString("longitude"));


                LatLng pt1 = new LatLng(latitude, longitude);

                if (i == 0) {
                    startLatLng = pt1;
                    mMap.addMarker(new MarkerOptions().position(startLatLng).title("Start"));
                } else if (i == pointsArray.length() - 1){
                    endLatLng = pt1;
                    mMap.addMarker(new MarkerOptions().position(endLatLng).title("End"));
                }

                polylineOptions.add(pt1);

                builder.include(pt1);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        mMap.addPolyline(polylineOptions);

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("trip.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}