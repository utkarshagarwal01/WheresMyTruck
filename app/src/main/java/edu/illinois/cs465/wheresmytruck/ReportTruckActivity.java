package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class ReportTruckActivity extends AppCompatActivity {
    EditText etTruckName;
    Button btnAddPic;
    ImageView ivTruckPic;
    FloatingActionButton btnClose;
    FloatingActionButton btnSubmit;
    final int CAMERA_PERMISSION_CODE = 1;
    final int CAMERA_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_truck);

        etTruckName = (EditText) findViewById(R.id.et_add_truck_name);

        btnAddPic = (Button) findViewById(R.id.btn_add_truck_pic);
        btnAddPic.setOnClickListener(this::openCamera);

        ivTruckPic = (ImageView) findViewById(R.id.iv_truck_pic);

        btnClose = (FloatingActionButton) findViewById(R.id.btn_close_report_truck);
        btnClose.setOnClickListener(this::onClose);

        btnSubmit = (FloatingActionButton) findViewById(R.id.btn_submit_report_truck);
        btnSubmit.setOnClickListener(this::onSubmit);
    }

    // todo upload/take a picture and save to internal storage for now
    // StackOverflow "Capture Image from Camera and Display in Activity"
    public void openCamera(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.v(null, "cam perm granted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);  // todo deprecated --> registerForActivityResult()
        } else {
            Log.v(null, "cam perm not granted");
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Camera access was not granted.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClose(View v) {
        Log.v(null, "onClose()");
        finish();
    }

    public void onSubmit(View v) {
        Log.v(null, "onSubmit()");
        String truckName = etTruckName.getText().toString();
        Toast.makeText(getBaseContext(), truckName, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            assert data != null;
            Bitmap bmTruckPic = (Bitmap) data.getExtras().get("data");
            ivTruckPic.setImageBitmap(bmTruckPic);
        }
    }
}