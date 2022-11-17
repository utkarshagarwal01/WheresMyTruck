package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
        btnClose = (FloatingActionButton) findViewById(R.id.btn_close_report_truck);
        btnSubmit = (FloatingActionButton) findViewById(R.id.btn_submit_report_truck);

        btnClose.setOnClickListener(this::onClose);
        btnSubmit.setOnClickListener(this::onSubmit);
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