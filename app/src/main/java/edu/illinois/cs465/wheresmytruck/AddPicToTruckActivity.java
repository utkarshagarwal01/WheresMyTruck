package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddPicToTruckActivity extends AppCompatActivity {
    // todo copied from ReportTruck
    private Context context;
    EditText etTruckName;
    Button btnAddPic;
    ImageView ivTruckPic;
    Bitmap bmPic;
    FloatingActionButton btnClose;
    FloatingActionButton btnSubmit;
    final int PC_CAMERA = 1;
    final int RC_CAMERA = 2;
    private final String TAG = "AddPicToTruckActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pic_to_truck);

        btnClose = (FloatingActionButton) findViewById(R.id.btn_close_add_pic);
        btnClose.setOnClickListener(this::onClose);
    }

    public void onClose(View v) {
        finish();
    }

}