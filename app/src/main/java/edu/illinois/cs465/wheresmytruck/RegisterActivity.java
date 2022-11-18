package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RegisterActivity extends AppCompatActivity {

    EditText nameText;
    EditText emailText;
    EditText passwordText;
    Button photoButton;
    Button registerButton;
    FloatingActionButton closeButton;
    Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            startActivity(intent);
        } else {
            // todo
            Log.v(null, "cam perm not granted");
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
}