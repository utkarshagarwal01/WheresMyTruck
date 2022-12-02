package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TruckDetailsActivity extends AppCompatActivity {

    FloatingActionButton close;
    FloatingActionButton addPhoto;
    Button leftArrow;
    Button rightArrow;
    ImageView truckPhoto;
    TextView truckName;
    TextView rating;
    TextView distance;
    Button navigate;
    TextView confidenceNumber;
    ImageView confidenceNeedle;
    TextView lastSeen;
    Button thumbUp;
    Button thumbDown;
    LinearLayout menuImages;
    LinearLayout foodImages;

    ArrayList<ImageButton> menuImageList;
    ArrayList<ImageButton> foodImageList;

    double lat;
    double lon;

    String truckId;

    boolean loggedIn = false;
    String user;

    boolean voted = false;
    boolean isVoteUp;
    String savedLastSeen; // Used in case the user changes their vote

    int imgIndex = 0;
    ArrayList<String> truckImages;
    final String TAG = "TruckDetailsActivity";
    private static final DecimalFormat df = new DecimalFormat("00.0");

    double confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_details);
        getSupportActionBar().setTitle("Truck Details");

        truckId = getIntent().getStringExtra("truckid");
        loggedIn = getIntent().getBooleanExtra("loggedin", false);
        if (loggedIn) {
            user = getIntent().getStringExtra("username");
        }

        close = (FloatingActionButton) findViewById(R.id.close_details);
        close.setOnClickListener(this::onClickClose);
        addPhoto = (FloatingActionButton) findViewById(R.id.add_photo);
        addPhoto.setOnClickListener(this::onClickAddPhoto);

        leftArrow = (Button) findViewById(R.id.left_arrow);
        leftArrow.setOnClickListener(this::onClickLeftArrow);
        rightArrow = (Button) findViewById(R.id.right_arrow);
        rightArrow.setOnClickListener(this::onClickRightArrow);
        truckPhoto = (ImageView) findViewById(R.id.truck_photo);
        truckName = (TextView) findViewById(R.id.truck_name);
        rating = (TextView) findViewById(R.id.rating);
        distance = (TextView) findViewById(R.id.distance);

        navigate = (Button) findViewById(R.id.navigate_button);
        navigate.setOnClickListener(this::onClickNavigate);
        confidenceNumber = (TextView) findViewById(R.id.confidence_number);
        confidenceNeedle = (ImageView) findViewById(R.id.confidence_needle);
        lastSeen = (TextView) findViewById(R.id.last_seen);

        thumbUp = (Button) findViewById(R.id.thumb_up);
        thumbUp.setOnClickListener(this::onClickThumbUp);
        thumbDown = (Button) findViewById(R.id.thumb_down);
        thumbDown.setOnClickListener(this::onClickThumbDown);

        menuImages = (LinearLayout) findViewById(R.id.menu_images);
        foodImages = (LinearLayout) findViewById(R.id.food_images);

        try {
            fillTruckInfo();
        } catch (Exception e) {
            Log.e(TAG, "Exception getting truck details: " + e);
        }

        try {
            getVoteHistory();
        } catch (Exception e) {
            Log.e(TAG, "Exception getting user vote history: " + e);
        }
    }

    public void onClickClose(View v) {
        Log.v(TAG, "truck details onClose()");
        finish();
    }

    public void onClickAddPhoto(View v) {
        Log.v(TAG, "clicked add photo");
        Intent intent = new Intent(this, AddPicToTruckActivity.class);
        startActivity(intent);
    }

    public void onClickLeftArrow(View v) {
        if (imgIndex > 0) {
            imgIndex = imgIndex - 1;
            truckPhoto.setImageBitmap(getImageBitmap(truckImages.get(imgIndex)));
            truckPhoto.invalidate();
        }
    }
    public void onClickRightArrow(View v) {
        if (imgIndex < truckImages.size() - 1) {
            imgIndex = imgIndex + 1;
            truckPhoto.setImageBitmap(getImageBitmap(truckImages.get(imgIndex)));
            truckPhoto.invalidate();
        }
    }
    public void onClickNavigate(View v) {
        Log.v(TAG, "onNavigate()");
        String uri = "geo:?q= " + lat + "," + lon;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void onClickThumbUp(View v) {
        if (!voted) {
            thumbUp.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
            updateConfidence(66.0);
            lastSeen.setText("0");
            voted = true;
            isVoteUp = true;
        } else if (isVoteUp) {
            thumbUp.setBackgroundResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
            updateConfidence(34.5);
            lastSeen.setText(savedLastSeen);
            voted = false;
            isVoteUp = false;
        } else {
            thumbUp.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
            thumbDown.setBackgroundResource(R.drawable.ic_baseline_thumb_down_off_alt_24);
            updateConfidence(66.0);
            lastSeen.setText("0");
            thumbDown.invalidate();
            voted = true;
            isVoteUp = true;
        }
        thumbUp.invalidate();
        confidenceNeedle.invalidate();
        lastSeen.invalidate();
        confidenceNumber.invalidate();
    }
    public void onClickThumbDown(View v) {
        if (!voted) {
            thumbDown.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24);
            updateConfidence(12.2);
            voted = true;
            isVoteUp = false;
        } else if (isVoteUp) {
            thumbUp.setBackgroundResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
            thumbDown.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24);
            updateConfidence(12.2);
            lastSeen.setText(savedLastSeen);
            thumbUp.invalidate();
            lastSeen.invalidate();
            voted = true;
            isVoteUp = false;
        } else {
            thumbDown.setBackgroundResource(R.drawable.ic_baseline_thumb_down_off_alt_24);
            updateConfidence(34.5);
            voted = false;
            isVoteUp = false;
        }
        thumbDown.invalidate();
        confidenceNeedle.invalidate();
        confidenceNumber.invalidate();
    }

    public void fillTruckInfo() throws Exception {
        JSONObject jo = Utils.readJSON(getApplicationContext(),"APIs.json", TAG);
        JSONObject profileAPI = (JSONObject) jo.get("api/getTruck?id=0");
        JSONObject data = (JSONObject) profileAPI.get("data");

        truckName.setText((String) data.get("truckName"));
        rating.setText(String.valueOf(data.get("rating")));
        distance.setText(String.valueOf(data.get("distance")));
        lat = (double) data.get("latitude");
        lon = (double) data.get("longitude");
        lastSeen.setText(String.valueOf(data.get("lastSeen")));
        savedLastSeen = String.valueOf(data.get("lastSeen"));
        confidence = data.getDouble("locConf");
        updateConfidence(confidence);

        JSONArray truckPics = (JSONArray) data.get("truckPics");
        truckImages = new ArrayList<>();
        for (int i = 0; i < truckPics.length(); i++) {
            truckImages.add(truckPics.getString(i));
        }
        if (truckImages.size() > 0) {
            truckPhoto.setImageBitmap(getImageBitmap(truckImages.get(0)));
        }

        JSONArray menuPics = (JSONArray) data.get("menuPics");
        menuImageList = new ArrayList<>();
        for (int i = 0; i < menuPics.length(); i++) {
            ImageButton menuImg = new ImageButton(this);
            String img = menuPics.getString(i);
            menuImg.setImageResource(getResources().getIdentifier(img, "drawable", getPackageName()));
//            menuImg.setImageBitmap(getImageBitmap(img));  Use this when we implement the ability to submit these
            menuImg.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            menuImg.setAdjustViewBounds(true);
            menuImages.addView(menuImg);
            menuImageList.add(menuImg);
        }

        JSONArray foodPics = (JSONArray) data.get("foodPics");
        foodImageList = new ArrayList<>();
        for (int i = 0; i < foodPics.length(); i++) {
            ImageButton foodImg = new ImageButton(this);
            String img = foodPics.getString(i);
            foodImg.setImageResource(getResources().getIdentifier(img, "drawable", getPackageName()));
//            foodImg.setImageBitmap(getImageBitmap(img));  Use this when we implement the ability to submit these
            foodImg.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            foodImg.setAdjustViewBounds(true);
            foodImages.addView(foodImg);
            foodImageList.add(foodImg);
        }
    }

    public void updateConfidence(double confidenceScore) {
        double rotation = (confidenceScore / 100) * 180 - 90;
        confidenceNeedle.setPivotX(48f);
        confidenceNeedle.setPivotY(172.8f);
        confidenceNeedle.setRotation((float) rotation);

        if (confidenceScore < 20) {
            confidenceNumber.setTextColor(Color.parseColor("#FFFF0000"));
        } else if (confidenceScore < 38) {
            confidenceNumber.setTextColor(Color.parseColor("#FFFF8800"));
        } else if (confidenceScore < 62) {
            confidenceNumber.setTextColor(Color.parseColor("#FFFFFF00"));
        } else if (confidenceScore < 80) {
            confidenceNumber.setTextColor(Color.parseColor("#FF88FF00"));
        } else {
            confidenceNumber.setTextColor(Color.parseColor("#FF00FF00"));
        }
        confidenceNumber.setText(df.format(confidenceScore));
    }

    public void getVoteHistory() throws Exception {
        JSONObject jo = Utils.readJSON(getApplicationContext(),"APIs.json", TAG);
        JSONObject profileAPI = (JSONObject) jo.get("api/getVote?truck=0&user=0");
        JSONObject data = (JSONObject) profileAPI.get("data");

        voted = data.getBoolean("hasVoted");
        isVoteUp = data.getBoolean("isVoteUp"); // Value doesn't matter if voted is false

        if (voted) {
            if (isVoteUp) {
                thumbUp.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
            } else {
                thumbDown.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24);
            }
        }
    }

    public Bitmap getImageBitmap(String location) {
        Context context = getApplicationContext();
        try (FileInputStream fis = context.openFileInput(location)) {
            Bitmap bmTruckPicTest = BitmapFactory.decodeStream(fis);
            return bmTruckPicTest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}