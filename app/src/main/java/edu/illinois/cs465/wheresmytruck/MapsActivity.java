package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

import edu.illinois.cs465.wheresmytruck.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final String TAG = "MapsActivity";
    FloatingActionButton fabReportTruck;
    FloatingActionButton fabProfile;
    ImageView btnSearchIcon;
    TextView btnSearchText;

    String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fabReportTruck = (FloatingActionButton) findViewById(R.id.btn_report_truck);
        fabReportTruck.setOnClickListener(this::openActivityReportTruck);
        fabProfile = (FloatingActionButton) findViewById(R.id.btn_profile);
        fabProfile.setOnClickListener(this::openActivityLogin);
        btnSearchIcon = (ImageView) findViewById(R.id.btn_search_icon);
        btnSearchIcon.setOnClickListener(this::openActivitySearchTruck);
        btnSearchText = (TextView) findViewById(R.id.btn_search_text);
        btnSearchText.setOnClickListener(this::openActivitySearchTruck);
    }

    public void openActivitySearchTruck(View view) {
//        Intent intent = new Intent(this, SearchTruckActivity.class);
//        Bundle b = new Bundle();  // pass param to another activity
//        if (view == btnSearchIcon) {  // show all trucks
//            b.putInt("mode", 1);
//        } else if (view == btnSearchText) {  // show all trucks & pop up keyboard for typing
//            b.putInt("mode", 2);
//        }
//        intent.putExtras(b);
//        startActivity(intent);

        // how to use in another activity:
        // Bundle b = getIntent().getExtras();
        // int searchMode = 0;
        // if (b != null) {
        //     searchMode = b.getInt("key");
        // }
    }

    public void openActivityReportTruck(View view) {
        Intent intent = new Intent(this, ReportTruckActivity.class);
        startActivity(intent);
    }

    public void openActivityLogin(View view) {
        if (userName == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("username", userName);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK && data != null) {
                userName = data.getStringExtra("username");
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            addTruckMarkers();
        } catch (Exception e) {
            Log.e(TAG, "Exception in adding truck markers: " + e);
        }

        // Move the camera to Champaign
        LatLng champaign = new LatLng(40.1102396, -88.2343178);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(champaign, 12));

        mMap.setOnMarkerClickListener(marker -> {
            String truckId = (String) marker.getTag();
            Intent intent = new Intent(this, TruckDetailsActivity.class);
            intent.putExtra("truckid", truckId);
            startActivity(intent);
            return false;
        });
    }

    public JSONObject readJSONFile(String path) {
        String text = "";
        try {
            InputStream is = getAssets().open("APIs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
            Log.v(TAG, "JSON object read: \n" + text);
        } catch (IOException e) {
            Log.e(TAG, "IOException in JSON read: " + e);
        }
        JSONObject jo = null;
        try {
            JSONTokener tokener = new JSONTokener(text);
            jo = new JSONObject(tokener);
        } catch (JSONException e) {
            Log.e(TAG, "JSON tokener Exception: " + e);
        }
        return jo;
    }

    public void addMarker(double lat, double lon, String title, String truckId) {
        LatLng truckLocation = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions().position(truckLocation).title(title);
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(truckId);
    }


    public void addTruckMarkers() throws Exception {
        JSONObject jo = readJSONFile("APIs.json");
        JSONObject trucksAPI = (JSONObject) jo.get("api/trucks");
        JSONArray trucksData = (JSONArray) trucksAPI.get("data");
        for (int i = 0; i < trucksData.length(); i++) {
            JSONObject truck = trucksData.getJSONObject(i);
            addMarker((double) truck.get("latitude"), (double) truck.get("longitude"), (String) truck.get("truckName"), String.valueOf(truck.get("truckId")));
        }
    }
}