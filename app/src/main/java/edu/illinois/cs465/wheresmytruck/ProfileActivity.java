package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton close;
    TextView reputationText;
    ImageView verifiedImage;
    ImageView badgeImage;
    TextView nameText;
    TextView emailText;
    ImageView profileImageView;

    String userName;

    final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("My Profile");

        userName = getIntent().getStringExtra("username");

        close = (FloatingActionButton) findViewById(R.id.close_profile);
        close.setOnClickListener(this::onClickClose);
        reputationText = (TextView) findViewById(R.id.reputation);
        verifiedImage = (ImageView) findViewById(R.id.verified);
        badgeImage = (ImageView) findViewById(R.id.badge);
        nameText = (TextView) findViewById(R.id.profile_username);
        emailText = (TextView) findViewById(R.id.profile_email_address);
        profileImageView = (ImageView) findViewById(R.id.profile_photo);

        try {
            fillProfileInfo();
        } catch (Exception e) {
            Log.e(null, "Exception filling in profile data: " + e);
        }
    }

    public void onClickClose(View v) {
        Log.v(null, "profile onClose()");
        finish();
    }

    public void fillProfileInfo() throws Exception {
        JSONObject jo = Utils.readJSON(getApplicationContext(),"APIs.json", TAG);
        JSONObject profileAPI = (JSONObject) jo.get("api/getProfile?id=0");
        JSONObject data = (JSONObject) profileAPI.get("data");

        String email = (String) data.get("email");
        boolean verified = (boolean) data.get("verified");
        boolean badge = (boolean) data.get("badge");
        String reputation = String.valueOf(data.get("reputation"));
        // Image profilePic = (Image) data.get("profilePic");

        nameText.setText(userName);
        emailText.setText(email);
        if (verified) {
            verifiedImage.setVisibility(View.VISIBLE);
        } else {
            verifiedImage.setVisibility(View.INVISIBLE);
        }
        if (badge) {
            badgeImage.setVisibility(View.VISIBLE);
        } else {
            badgeImage.setVisibility(View.INVISIBLE);
        }
        reputationText.setText(reputation);

        // Hardcoded for now
        profileImageView.setImageResource(R.drawable.stock_guy);
    }
}