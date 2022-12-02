package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class AddPicToTruckActivity extends AppCompatActivity {
    private Context context;
    EditText etTruckName;
    Button btnAddPic;
    ImageView ivPic;
    Bitmap bmPic;
    Button btnTypeTruck;
    Button btnTypeMenu;
    Button btnTypeFood;
    FloatingActionButton btnClose;
    FloatingActionButton btnSubmit;
    int truckId = 0;  // should not have default val
    String truckName = "BurrKing";
    String picType = "pic";
    final int PC_CAMERA = 1;
    final int RC_CAMERA = 2;
    private final String TAG = "AddPicToTruckActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pic_to_truck);
        getSupportActionBar().setTitle("Add a picture?");

        context = getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.get("truckId") != null) {
            truckId = (int) extras.get("truckId");
        }
        JSONObject fakeBE = Utils.readJSON(context, "APIs.json", "ReportTruck reading");
        try {
            truckName = (String) ((JSONArray) fakeBE.get("api/getTruck")).getJSONObject(truckId).get("truckName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnClose = (FloatingActionButton) findViewById(R.id.btn_close_add_pic);
        btnClose.setOnClickListener(this::onClose);
        btnAddPic = (Button) findViewById(R.id.btn_add_pic);
        btnAddPic.setOnClickListener(this::openCamera);
        ivPic = (ImageView) findViewById(R.id.iv_pic);
        btnTypeTruck = (Button) findViewById(R.id.btn_type_truck);
        btnTypeTruck.setOnClickListener(this::changePicType);
        btnTypeMenu = (Button) findViewById(R.id.btn_type_menu);
        btnTypeMenu.setOnClickListener(this::changePicType);
        btnTypeFood = (Button) findViewById(R.id.btn_type_food);
        btnTypeFood.setOnClickListener(this::changePicType);
        etTruckName = (EditText) findViewById(R.id.et_truck_name);
        etTruckName.setText(truckName);

        btnSubmit = (FloatingActionButton) findViewById(R.id.btn_submit_add_pic);
        btnSubmit.setOnClickListener(this::onSubmit);
    }

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

    @Override  // retrieve pic/loc from sub-activities
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CAMERA) {
            assert data != null;
            bmPic = (Bitmap) data.getExtras().get("data");
            btnAddPic.setVisibility(View.GONE);
            ivPic.setImageBitmap(bmPic);
            ivPic.setVisibility(View.VISIBLE);
        }
    }

    public void changePicType(View v) {
        if (v.getId() == R.id.btn_type_truck) {
            picType = "pic";
            btnTypeTruck.setAlpha(1);
            btnTypeMenu.setAlpha(.4f);
            btnTypeFood.setAlpha(.4f);
        }
        if (v.getId() == R.id.btn_type_menu) {
            picType = "menu";
            btnTypeTruck.setAlpha(.4f);
            btnTypeMenu.setAlpha(1);
            btnTypeFood.setAlpha(.4f);
        }
        if (v.getId() == R.id.btn_type_food) {
            picType = "food";
            btnTypeTruck.setAlpha(.4f);
            btnTypeMenu.setAlpha(.4f);
            btnTypeFood.setAlpha(1);
        }
    }

    public void onSubmit(View v) {
        if (bmPic == null) {
            Toast.makeText(getBaseContext(), "A picture is needed!", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONObject fakeBE = Utils.readJSON(context, "APIs.json", "ReportTruck reading");
                if (Objects.equals(picType, "pic")) {
                    JSONArray truckPics = (JSONArray) ((JSONArray) fakeBE.get("api/getTruck")).getJSONObject(truckId).get("truckPics");
                    String filename = "truck" + truckId + picType + truckPics.length() + ".jpg";
                    Utils.writeImage(context, filename, TAG, bmPic);
                    truckPics.put(filename);
                }
                if (Objects.equals(picType, "menu")) {
                    JSONArray truckPics = (JSONArray) ((JSONArray) fakeBE.get("api/getTruck")).getJSONObject(truckId).get("menuPics");
                    String filename = "truck" + truckId + picType + truckPics.length() + ".jpg";
                    Utils.writeImage(context, filename, TAG, bmPic);
                    truckPics.put(filename);
                }
                if (Objects.equals(picType, "food")) {
                    JSONArray truckPics = (JSONArray) ((JSONArray) fakeBE.get("api/getTruck")).getJSONObject(truckId).get("foodPics");
                    String filename = "truck" + truckId + picType + truckPics.length() + ".jpg";
                    Utils.writeImage(context, filename, TAG, bmPic);
                    truckPics.put(filename);
                }
                Utils.writeJSONToContext(context, "APIs.json", "ReportTruck writing", fakeBE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), "You contributed a picture!", Toast.LENGTH_LONG).show();
            Intent result = new Intent();
            setResult(RESULT_OK, result);
            finish();
        }
    }

    public void onClose(View v) {
        finish();
    }
}