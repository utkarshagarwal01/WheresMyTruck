package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.illinois.cs465.wheresmytruck.databinding.ActivityMapsBinding;

public class AddLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final String TAG = "AddLocationActivity";
    FloatingActionButton done, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_add_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_location_map);
        mapFragment.getMapAsync(this);
        done = (FloatingActionButton) findViewById(R.id.add_location_done);
        done.setOnClickListener(this::addLocationCallback);

        close = (FloatingActionButton) findViewById(R.id.add_location_close);
        close.setOnClickListener(this::closeCallback);
    }

    public void addLocationCallback(View v) {
        CameraPosition pos = mMap.getCameraPosition();
        double lat = pos.target.latitude, lon = pos.target.longitude;
        Intent intent = new Intent();
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void closeCallback(View v) {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
//            addTruckMarkers();
        } catch (Exception e) {
            Log.e(TAG, "Exception in adding truck markers: " + e);
        }

        // Move the camera to Champaign
        LatLng champaign = new LatLng(40.1102396, -88.2343178);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(champaign, 12));

        Marker crosshair = mMap.addMarker(new MarkerOptions().position(champaign));

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                // Get the center of the Map.
                LatLng centerOfMap = mMap.getCameraPosition().target;
                // Update your Marker's position to the center of the Map.
                crosshair.setPosition(centerOfMap);
            }
        });
    }
}