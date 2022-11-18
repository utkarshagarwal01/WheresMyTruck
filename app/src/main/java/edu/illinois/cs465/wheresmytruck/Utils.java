package edu.illinois.cs465.wheresmytruck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class Utils {
    public static JSONObject readJSON(Context context, String path, String TAG, boolean fromAssets) {
        return readJSONFileHelper(context, path, TAG, fromAssets);
    }

    public static JSONObject readJSON(Context context, String path, String TAG) {
        return readJSONFileHelper(context, path, TAG, false);
    }

    private static JSONObject readJSONFileHelper(Context context, String path, String TAG, boolean fromAssets) {
        String text = "";
        try {
            InputStream is = fromAssets? context.getAssets().open(path): context.openFileInput(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
            Log.v(TAG, "JSON object read: \n" + text);
        } catch (IOException e) {
            Log.e(TAG, "IOException in JSON read: " + e);
        }
        JSONObject jo = null;
        try {
            JSONTokener token = new JSONTokener(text);
            jo = new JSONObject(token);
        } catch (Exception e) {
            Log.e(TAG, "JSON tokener Exception: " + e);
        }
        return jo;
    }

    public static void writeJSONToContext(Context context, String path, String TAG, JSONObject jo) {
        try (PrintWriter pw = new PrintWriter(context.openFileOutput(path, Context.MODE_PRIVATE))) {
            pw.write(jo.toString());
            Log.v(TAG, "JSON object written: \n" + jo.toString());
        } catch (IOException e) {
            Log.e(TAG, "IOException in JSON write: " + e);
        }
    }

    public static void writeImage(Context context, String path, String TAG, Bitmap bmImage) {
        try (FileOutputStream fos = context.openFileOutput(path, Context.MODE_PRIVATE)) {
            bmImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            Log.v(TAG, "Error in image write: " + e);
        }
    }

    public static Bitmap readImage(Context context, String path, String TAG) {
        Bitmap bmImage = null;
        try (FileInputStream fis = context.openFileInput(path)) {
            bmImage = BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
             Log.v(TAG, "Error in image read: " + e);
        }
        return bmImage;
    }
}
