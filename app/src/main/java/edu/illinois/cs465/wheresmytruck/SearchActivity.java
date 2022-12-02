package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    FloatingActionButton close;
    private Context context;
    private final String TAG = "SearchActivity";
    private final String mainAPIJSONFile = "APIs.json";

    ArrayAdapter<Truck> adapter;
    ArrayList<Truck> trucks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Search...");

        context = getApplicationContext();
        JSONObject jo = Utils.readJSON(context, mainAPIJSONFile, TAG, true);
        Utils.writeJSONToContext(context, mainAPIJSONFile, TAG, jo);

        listView = (ListView) findViewById(R.id.ListView);
        searchView = (SearchView) findViewById(R.id.SearchView);
        close = (FloatingActionButton) findViewById(R.id.close_list);
        close.setOnClickListener(this::onClickClose);

        try {
            trucks = addTrucks();
        } catch (Exception e) {
            e.printStackTrace();
        }


        adapter = new ArrayAdapter<Truck>(this, android.R.layout.simple_list_item_1, trucks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Truck t = trucks.get(position);
                openTruckDetails(t.getTruckId());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (trucks.contains(s)) {
                    adapter.getFilter().filter(s);
                } else {
                    Toast.makeText(SearchActivity.this, "No Trucks Found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    public ArrayList<Truck> addTrucks() throws Exception {
        JSONObject jo = Utils.readJSON(context, mainAPIJSONFile, TAG);
        JSONArray trucksData = (JSONArray) jo.get("api/getTruck");
        //JSONArray trucksData = (JSONArray) trucksAPI.get("data");
        ArrayList<Truck> trucks = new ArrayList<>();
        for (int i = 0; i < trucksData.length(); i++) {
            JSONObject truck = trucksData.getJSONObject(i);
            Truck t = new Truck();
            t.setTruckName((String) truck.get("truckName"));
            t.setTruckId((int) truck.get("truckId"));
            t.setConfidence((double) truck.get("locConf"));
            t.setCoordinates(new LatLng((double) truck.get("latitude"), (double) truck.get("longitude")));
            trucks.add(t);
        }
        return trucks;
    }

    public void openTruckDetails(int truckId) {
        Intent intent = new Intent(this, TruckDetailsActivity.class);
        intent.putExtra("truckid", truckId);
//        if (userName != null) {
//            intent.putExtra("loggedin", true);
//            intent.putExtra("username", userName);
//        } else {
//            intent.putExtra("loggedin", false);
//            intent.putExtra("username", "Nobody");
//        }
        startActivity(intent);
    }

    private void onClickClose(View view) {
        finish();
    }

    public void goToDetails() {
        Intent intent = new Intent(this, TruckDetailsActivity.class);
        intent.putExtra("truckid", "1");
        startActivity(intent);
    }


}