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

import java.io.FileOutputStream;
import java.io.IOException;

public class ReportTruckActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_truck);

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
            // pretend this submission is for truckId=0, update APIs.json locally,
            // so truck details page can show these pics
            String filename = "truck0pic0.jpg";
            Context context = getApplicationContext();
            Utils.writeImage(context, filename, TAG, bmTruckPic);
            Toast.makeText(getBaseContext(), "You reported a truck location!", Toast.LENGTH_LONG).show();
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
            double lat = data.getDoubleExtra("lat", 0);
            double lon = data.getDoubleExtra("lon", 0);
            Log.v(TAG, "Selected location: Lat="+lat+" Lon="+lon);
        }
    }

    public void openActivityAddLocation(View view) {
        Intent intent = new Intent(this, AddLocationActivity.class);
        startActivityForResult(intent, RC_LOCATION);
    }
}
