package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportTruckActivity extends AppCompatActivity {
    EditText etTruckName;
    Button btnAddPic;
    FloatingActionButton btnClose;
    FloatingActionButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_truck);

        etTruckName = (EditText) findViewById(R.id.et_add_truck_name);

        btnAddPic = (Button) findViewById(R.id.btn_add_truck_pic);
        btnAddPic.setOnClickListener(this::toAddPic);

        btnClose = (FloatingActionButton) findViewById(R.id.btn_close_report_truck);
        btnClose.setOnClickListener(this::onClose);

        btnSubmit = (FloatingActionButton) findViewById(R.id.btn_submit_report_truck);
        btnSubmit.setOnClickListener(this::onSubmit);
    }

    // todo upload/take a picture and save to internal storage for now
    // https://www.youtube.com/watch?v=xZZQ5q5pOp0
    public void toAddPic(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.v(null, "cam perm granted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        } else {
            // todo
            Log.v(null, "cam perm not granted");
        }
    }

    public void onClose(View v) {
        Log.v(null, "onClose()");
        finish();
    }

    public void onSubmit(View v) {
        Log.v(null, "onSubmit()");
        String truckName = etTruckName.getText().toString();
        Toast msg = Toast.makeText(getBaseContext(), truckName, Toast.LENGTH_LONG);
        msg.show();
        finish();
    }
}