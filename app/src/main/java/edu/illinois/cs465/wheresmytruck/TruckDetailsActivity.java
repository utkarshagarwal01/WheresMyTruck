package edu.illinois.cs465.wheresmytruck;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    ImageView confidenceImage;
    TextView lastSeen;
    Button thumbUp;
    Button thumbDown;
    LinearLayout menuImages;
    LinearLayout foodImages;

    ArrayList<ImageButton> menuImageList;
    ArrayList<ImageButton> foodImageList;

    String truckId;

    boolean voted = false;
    int imgIndex = 0;
    ArrayList<String> truckImages;
    final String TAG = "TruckDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_details);

        truckId = getIntent().getStringExtra("username");

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
        confidenceImage = (ImageView) findViewById(R.id.confidence_image);
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
            Log.e(null, "Exception filling in profile data: " + e);
        }
    }

    public void onClickClose(View v) {
        Log.v(null, "truck details onClose()");
        finish();
    }

    public void onClickAddPhoto(View v) {
        Log.v(null, "clicked add photo");
    }

    public void onClickLeftArrow(View v) {
        if (imgIndex > 0) {
            imgIndex = imgIndex - 1;
            truckPhoto.setImageResource(getResources().getIdentifier(truckImages.get(imgIndex), "drawable", getPackageName()));
            truckPhoto.invalidate();
        }
    }
    public void onClickRightArrow(View v) {
        if (imgIndex < truckImages.size() - 1) {
            imgIndex = imgIndex + 1;
            truckPhoto.setImageResource(getResources().getIdentifier(truckImages.get(imgIndex), "drawable", getPackageName()));
            truckPhoto.invalidate();
        }
    }
    public void onClickNavigate(View v) {
        Log.v(null, "onNavigate()");
    }
    public void onClickThumbUp(View v) {
        if (!voted) {
            thumbUp.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
            updateConfidence(12.0);
            lastSeen.setText("0");
            thumbUp.invalidate();
            confidenceImage.invalidate();
            lastSeen.invalidate();
            voted = true;
        }
    }
    public void onClickThumbDown(View v) {
        if (!voted) {
            thumbDown.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24);
            updateConfidence(5.0);
            thumbDown.invalidate();
            confidenceImage.invalidate();
            voted = true;
        }
    }

    public void fillTruckInfo() throws Exception {
        JSONObject jo = Utils.readJSON(getApplicationContext(),"APIs.json", TAG);
        JSONObject profileAPI = (JSONObject) jo.get("api/getTruck?id=0");
        JSONObject data = (JSONObject) profileAPI.get("data");

        truckName.setText((String) data.get("truckName"));
        rating.setText(String.valueOf(data.get("rating")));
        distance.setText(String.valueOf(data.get("distance")));
        lastSeen.setText(String.valueOf(data.get("lastSeen")));
        double confidenceScore = data.getDouble("locConf");
        updateConfidence(confidenceScore);

        JSONArray truckPics = (JSONArray) data.get("truckPics");
        truckImages = new ArrayList<>();
        for (int i = 0; i < truckPics.length(); i++) {
            truckImages.add(truckPics.getString(i));
        }
        truckPhoto.setImageBitmap(getImageBitmap(truckImages.get(0)));

        JSONArray menuPics = (JSONArray) data.get("menuPics");
        menuImageList = new ArrayList<>();
        for (int i = 0; i < menuPics.length(); i++) {
            ImageButton menuImg = new ImageButton(this);
            String img = menuPics.getString(i);
            menuImg.setImageResource(getResources().getIdentifier(img, "drawable", getPackageName()));
//            menuImg.setImageBitmap(getImageBitmap(img));  Use this when we implement the ability to submit these
            menuImg.setLayoutParams(new ViewGroup.LayoutParams(120, 100));
            menuImages.addView(menuImg);
            menuImageList.add(menuImg);
        }

        JSONArray foodPics = (JSONArray) data.get("foodPics");
        foodImageList = new ArrayList<>();
        for (int i = 0; i < foodPics.length(); i++) {
            ImageButton foodImg = new ImageButton(this);
            String img = foodPics.getString(i);
            foodImg.setBackgroundResource(getResources().getIdentifier(img, "drawable", getPackageName()));
//            foodImg.setImageBitmap(getImageBitmap(img));  Use this when we implement the ability to submit these
            foodImg.setScaleType(ImageButton.ScaleType.FIT_CENTER);
            foodImages.addView(foodImg);
            foodImageList.add(foodImg);
        }
    }

    public void updateConfidence(double confidenceScore) {
        if (confidenceScore < 7.0) {
            confidenceImage.setImageResource(R.drawable.ic_baseline_wifi_1_bar_24);
            confidenceImage.setColorFilter(Color.parseColor("#FFDD0000"));
        } else if (confidenceScore < 10.0) {
            confidenceImage.setImageResource(R.drawable.ic_baseline_wifi_2_bar_24);
            confidenceImage.setColorFilter(Color.parseColor("#FFDDDD00"));
        } else {
            confidenceImage.setImageResource(R.drawable.ic_baseline_wifi_24);
            confidenceImage.setColorFilter(Color.parseColor("#FF00DD00"));
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