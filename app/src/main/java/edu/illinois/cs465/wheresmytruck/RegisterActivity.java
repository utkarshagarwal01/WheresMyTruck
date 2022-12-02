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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RegisterActivity extends AppCompatActivity {

    EditText nameText;
    EditText emailText;
    EditText passwordText;
    Button photoButton;
    Button registerButton;
    FloatingActionButton closeButton;
    Intent result;

    Bitmap profilePic;

    final int PC_CAMERA = 1;
    final int RC_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        nameText = (EditText) findViewById(R.id.register_username_input);
        emailText = (EditText) findViewById(R.id.register_email_input);
        passwordText = (EditText) findViewById(R.id.register_password_input);
        photoButton = (Button) findViewById(R.id.camera_button);
        photoButton.setOnClickListener(this::onClickPhoto);
        registerButton = (Button) findViewById(R.id.register_account);
        registerButton.setOnClickListener(this::onClickRegister);
        closeButton = (FloatingActionButton) findViewById(R.id.close_register);
        closeButton.setOnClickListener(this::onClickClose);

        result = new Intent();
        result.putExtra("username", (String) null);
        setResult(RESULT_OK, result);
    }

    public void onClickPhoto(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.v(null, "cam perm granted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, RC_CAMERA);
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

    public void onClickRegister(View v) {
        Log.v(null, "onClickRegister()");
        String username = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        result.putExtra("username", username);
        setResult(RESULT_OK, result);

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("username", username);
        finish();
        startActivity(intent);
    }

    public void onClickClose(View v) {
        Log.v(null, "login onClose()");
        finish();
    }

    @Override  // retrieve pic/loc from sub-activities
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CAMERA) {
            assert data != null;
            profilePic = (Bitmap) data.getExtras().get("data");
        }
    }
}