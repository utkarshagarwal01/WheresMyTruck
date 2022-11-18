package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginActivity extends AppCompatActivity {

    EditText nameInput;
    EditText passwordInput;
    Button loginButton;
    Button registerButton;
    FloatingActionButton close;
    Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameInput = (EditText) findViewById(R.id.username_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::onClickLogin);
        registerButton = (Button) findViewById(R.id.create_account_button);
        registerButton.setOnClickListener(this::onClickRegister);
        close = (FloatingActionButton) findViewById(R.id.close_login);
        close.setOnClickListener(this::onClickClose);

        result = new Intent();
        result.putExtra("username", (String) null);
        setResult(RESULT_OK, result);
    }

    public void onClickLogin(View v) {
        Log.v(null, "onClickLogin()");
        String username = nameInput.getText().toString();
        String password = passwordInput.getText().toString();

        result.putExtra("username", username);
        setResult(RESULT_OK, result);

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("username", username);
        finish();
        startActivity(intent);
    }

    public void onClickRegister(View v) {
        Log.v(null, "onClickRegister()");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onClickClose(View v) {
        Log.v(null, "login onClose()");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK && data != null) {
                String name = data.getStringExtra("username");
                result.putExtra("username", name);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }
}