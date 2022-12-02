package edu.illinois.cs465.wheresmytruck;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportTruckActivity extends AppCompatActivity {
    private Context context;
    EditText etTruckName;
    Button btnAddPic;
    ImageView ivTruckPic;
    Bitmap bmTruckPic;
    Button btnAddLoc;
    FloatingActionButton btnClose;
    FloatingActionButton btnSubmit;
    final int PC_CAMERA = 1;
    final int RC_CAMERA = 2;
    final int RC_LOCATION = 3;
    private final String TAG = "ReportTruckActivity";

    double lat;
    double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_truck);
        getSupportActionBar().setTitle("Report A Truck?");
        context = getApplicationContext();

        etTruckName = (EditText) findViewById(R.id.et_add_truck_name);

        btnAddPic = (Button) findViewById(R.id.btn_add_truck_pic);
        btnAddPic.setOnClickListener(this::openCamera);

        ivTruckPic = (ImageView) findViewById(R.id.iv_truck_pic);

        btnAddLoc = (Button) findViewById(R.id.btn_add_truck_loc);
        btnAddLoc.setOnClickListener(this::openActivityAddLocation);

        btnClose = (FloatingActionButton) findViewById(R.id.btn_close_report_truck);
        btnClose.setOnClickListener(this::onClose);

        btnSubmit = (FloatingActionButton) findViewById(R.id.btn_submit_report_truck);
        btnSubmit.setOnClickListener(this::onSubmit);
    }

    // StackOverflow "Capture Image from Camera and Display in Activity"
    public void openCamera(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.v(null, "cam perm granted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, RC_CAMERA);  // todo deprecated --> registerForActivityResult()
        } else {
            Log.v(null, "cam perm not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PC_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PC_CAMERA) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RC_CAMERA);
            } else {
                Toast.makeText(this, "Camera access was not granted.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClose(View v) {
        finish();
    }

    private int getNewTruckID() throws JSONException {
        JSONObject api = Utils.readJSON(context, "APIs.json", "ReportTruck reading for getTruck length");
        JSONArray trucks = (JSONArray)api.get("api/getTruck");
        return trucks.length();
    }

    private JSONObject getNewTruckObject(int newTruckId, String imageFilename) throws JSONException {
        JSONObject newTruck = new JSONObject();
        newTruck.put("truckId", newTruckId);
        newTruck.put("truckName", etTruckName.getText().toString());
        newTruck.put("longitude", lon);
        newTruck.put("latitude", lat);
        newTruck.put("rating", 6.0);
        newTruck.put("distance", 15);
        newTruck.put("locConf", 50.1);
        newTruck.put("lastSeen", 0);

        JSONArray truckPics = new JSONArray();
        truckPics.put(imageFilename);
        newTruck.put("truckPics", truckPics);

        JSONArray foodPics = new JSONArray();
        JSONArray menuPics = new JSONArray();
        newTruck.put("foodPics", foodPics);
        newTruck.put("menuPics", menuPics);

        return newTruck;
    }

    // check all fields, save to storage (for now), return to home screen
    public void onSubmit(View v) {
        String truckName = etTruckName.getText().toString();
        if (truckName.length() <= 0) {
            Toast.makeText(getBaseContext(), "Name cannot be empty!", Toast.LENGTH_LONG).show();
        } else if (bmTruckPic == null) {
            Toast.makeText(getBaseContext(), "A picture is needed!", Toast.LENGTH_LONG).show();
//        } else if () {
//            // todo check location input
//            Toast.makeText(getBaseContext(), "Location is needed!", Toast.LENGTH_LONG).show();
        } else {
            // logic:
            // send submission to BE, BE inserts submission into DB,
            // next time FE calls api/truck?id=0, BE responds with updated DB,
            // FE then knows which truck (truckId, truckName) this submission belongs to

            // for now:
            // Add new truck in api/getTrucks, update APIs.json locally,
            // so truck details page can show these pics
            int newTruckId = -1;
            try {
                newTruckId = getNewTruckID();

                String imageFilename = "truck"+ newTruckId+ "pic0.jpg";
                Utils.writeImage(context, imageFilename, TAG, bmTruckPic);

                JSONObject newTruck = getNewTruckObject(newTruckId, imageFilename);
                //write new truck to APIs.json
                JSONObject api = Utils.readJSON(context, "APIs.json", "ReportTruck reading to insert new truck");
                JSONArray trucks = (JSONArray)api.get("api/getTruck");
                trucks.put(newTruck);
                Utils.writeJSONToContext(context, "APIs.json", "ReportTruck writing", api);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), "You reported a truck location!", Toast.LENGTH_LONG).show();
            Intent result = new Intent();
            result.putExtra("lat", String.valueOf(lat));
            result.putExtra("lon", String.valueOf(lon));
            result.putExtra("truckname", truckName);
            result.putExtra("truckid", String.valueOf(newTruckId));
            setResult(RESULT_OK, result);
            finish();
        }
    }

    @Override  // retrieve pic/loc from sub-activities
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CAMERA) {
            assert data != null;
            bmTruckPic = (Bitmap) data.getExtras().get("data");
            btnAddPic.setVisibility(View.GONE);
            ivTruckPic.setImageBitmap(bmTruckPic);
            ivTruckPic.setVisibility(View.VISIBLE);
        } else if (resultCode == Activity.RESULT_OK && requestCode == RC_LOCATION) {
            assert data != null;
            lat = data.getDoubleExtra("lat", 0);
            lon = data.getDoubleExtra("lon", 0);
            Log.v(TAG, "Selected location: Lat="+lat+" Lon="+lon);
        }
    }

    public void openActivityAddLocation(View view) {
        Intent intent = new Intent(this, AddLocationActivity.class);
        startActivityForResult(intent, RC_LOCATION);
    }
}
