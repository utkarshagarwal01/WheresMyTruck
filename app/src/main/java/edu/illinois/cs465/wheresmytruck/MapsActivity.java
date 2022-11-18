package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.illinois.cs465.wheresmytruck.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Context context;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final String TAG = "MapsActivity";
    private final String mainAPIJSONFile = "APIs.json";
    FloatingActionButton fabReportTruck;
    FloatingActionButton fabProfile;
    ExtendedFloatingActionButton fabSearch;

//    FloatingActionButton fabTruckPicTest;  // test only, to be removed
//    ImageView ivTruckPicTest;

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

//        fabTruckPicTest = (FloatingActionButton) findViewById(R.id.btn_truck_pic_test);
//        fabTruckPicTest.setOnClickListener(this::truckPicTest);
//        ivTruckPicTest = (ImageView) findViewById(R.id.iv_truck_pic_test);

        fabProfile = (FloatingActionButton) findViewById(R.id.btn_profile);
        fabProfile.setOnClickListener(this::openActivityLogin);
//         fabProfile.setOnClickListener(this::openActivityProfile);  // todo name/logic refactoring
        
        fabSearch = (ExtendedFloatingActionButton) findViewById(R.id.btn_search);
        fabSearch.setOnClickListener(this::openActivitySearchTruck);

        context = getApplicationContext();
        JSONObject jo = Utils.readJSON(context, mainAPIJSONFile, TAG, true);
        Utils.writeJSONToContext(context, mainAPIJSONFile, TAG, jo);
    }

//    public void truckPicTest(View view) {
//        String filename = "truck0pic0.jpg";
//        Bitmap bmTruckPicTest = Utils.readImage(context, filename, TAG);
//        ivTruckPicTest.setImageBitmap(bmTruckPicTest);
//    }

    public void openActivitySearchTruck(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        Bundle b = new Bundle();  // pass param to another activity
        intent.putExtras(b);
        startActivity(intent);
    }

    public void openActivityReportTruck(View view) {
        Intent intent = new Intent(this, ReportTruckActivity.class);
        startActivity(intent);
    }

//    public void openActivityProfile(View view) {
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }

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
    }

    public void addMarker(double lat, double lon, String title) {
        LatLng truckLocation = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(truckLocation).title(title));
    }

    public void addTruckMarkers() throws Exception {
        JSONObject jo = Utils.readJSON(context, mainAPIJSONFile, TAG);
        JSONObject trucksAPI = (JSONObject) jo.get("api/trucks");
        JSONArray trucksData = (JSONArray) trucksAPI.get("data");
        for (int i = 0; i < trucksData.length(); i++) {
            JSONObject truck = trucksData.getJSONObject(i);
            addMarker((double) truck.get("latitude"), (double) truck.get("longitude"), (String) truck.get("truckName"));
        }
    }
}