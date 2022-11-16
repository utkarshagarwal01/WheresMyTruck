package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ReportTruckActivity extends AppCompatActivity {
    EditText etTruckName;
    Button btnAddPic;
    ImageButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_truck);

        etTruckName = (EditText) findViewById(R.id.et_add_truck_name);
        btnAddPic = (Button) findViewById(R.id.btn_add_truck_pic);
        btnSubmit = (ImageButton) findViewById(R.id.btn_submit_report_truck);

        btnSubmit.setOnClickListener(this::onSubmit);
    }

    public void onSubmit(View v) {
        String truckName = etTruckName.getText().toString();
        Toast msg = Toast.makeText(getBaseContext(), truckName, Toast.LENGTH_LONG);
        msg.show();
    }
}