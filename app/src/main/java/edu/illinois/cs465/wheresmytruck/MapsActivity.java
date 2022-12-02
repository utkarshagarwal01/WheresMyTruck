package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    final int RC_LOCATION = 3;

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
        fabReportTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName == null) {
                    showReportErrorDialog();
                } else {
                    openActivityReportTruck(view);
                }
            }
        });

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
        startActivityForResult(intent, 2);
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
        } else if (requestCode == 2) {
            if(resultCode == RESULT_OK && data != null) {
                double lat = Double.parseDouble(data.getStringExtra("lat"));
                double lon = Double.parseDouble(data.getStringExtra("lon"));
                String truckName = data.getStringExtra("truckname");
                String truckId = data.getStringExtra("truckid");
                addMarker(lat, lon, truckName, truckId, 30.0);
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
    @SuppressLint("MissingPermission") // The permission is there and works, it's just android studio
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v(null, "cam perm granted");
            mMap.setMyLocationEnabled(true);
        } else {
            Log.v(null, "cam perm not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }

            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.truck_info_window, null);
                int truckId = Integer.parseInt((String) marker.getTag());

                JSONObject jo = Utils.readJSON(getApplicationContext(),"APIs.json", TAG);
                JSONObject data;
                try {
                    JSONArray trucks = (JSONArray) jo.get("api/getTruck");
                    data = (JSONObject) trucks.get(truckId);
                } catch (Exception e) {
                    Log.e(TAG, "Exception getting truck details for preview: " + e);
                    return null;
                }
                ImageView truckPic = v.findViewById(R.id.image);
                TextView truckName = v.findViewById(R.id.name);
                TextView confidenceScore = v.findViewById(R.id.confidence_value);

                try {
                    JSONArray truckPics = (JSONArray) data.get("truckPics");
                    if (truckPics.length() > 0) {
                        truckPic.setImageBitmap(getImageBitmap(truckPics.getString(0)));
                    } else {
                        truckPic.setImageResource(R.drawable.defaulttruckimage);
                    }

                    truckName.setText(data.getString("truckName"));
                    double confidence = data.getDouble("locConf");
                    confidenceScore.setText(String.valueOf(confidence));
                    if (confidence < 20) {
                        confidenceScore.setTextColor(Color.parseColor("#FFFF0000"));
                    } else if (confidence < 38) {
                        confidenceScore.setTextColor(Color.parseColor("#FFFF8800"));
                    } else if (confidence < 62) {
                        confidenceScore.setTextColor(Color.parseColor("#FF999900"));
                    } else if (confidence < 80) {
                        confidenceScore.setTextColor(Color.parseColor("#FF88FF00"));
                    } else {
                        confidenceScore.setTextColor(Color.parseColor("#FF00FF00"));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception getting truck details for preview: " + e);
                    return null;
                }
                return v;
            }

            public Bitmap getImageBitmap(String location) {
                Context context = getApplicationContext();
                try (FileInputStream fis = context.openFileInput(location)) {
                    Bitmap bmTruckPicTest = BitmapFactory.decodeStream(fis);
                    return bmTruckPicTest;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            String truckId = (String) marker.getTag();
            Intent intent = new Intent(this, TruckDetailsActivity.class);
            intent.putExtra("truckid", truckId);
            if (userName != null) {
                intent.putExtra("loggedin", true);
                intent.putExtra("username", userName);
            } else {
                intent.putExtra("loggedin", false);
                intent.putExtra("username", "Nobody");
            }
            startActivity(intent);
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

    public void addMarker(double lat, double lon, String title, String truckId, double confidence) {
        float color;
        if (confidence < 20) {
            color = 0f;
        } else if (confidence < 38) {
            color = 32f;
        } else if (confidence < 62) {
            color = 60f;
        } else if (confidence < 80) {
            color = 88f;
        } else {
            color = 120f;
        }

        LatLng truckLocation = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(truckLocation)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(color));
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(truckId);
    }

    public void addTruckMarkers() throws Exception {
        JSONObject jo = Utils.readJSON(context, mainAPIJSONFile, TAG);
        JSONArray trucksData = (JSONArray) jo.get("api/getTruck");
        for (int i = 0; i < trucksData.length(); i++) {
            JSONObject truck = trucksData.getJSONObject(i);
            addMarker(
                    (double) truck.get("latitude"),
                    (double) truck.get("longitude"),
                    (String) truck.get("truckName"),
                    String.valueOf(truck.get("truckId")),
                    (double) truck.get("locConf"));
        }
    }

    public void showReportErrorDialog() {
        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.report_error_dialog);

        Button confirm = dialog.findViewById(R.id.buttonConfirmDialog);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openActivityLogin(view);
            }
        });

        dialog.show();
    }

    @SuppressLint("MissingPermission") // The permission is there and works, it's just android studio
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(null, "cam perm granted");
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Location access was not granted.", Toast.LENGTH_LONG).show();
            }
        }
    }
}