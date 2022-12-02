package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    FloatingActionButton close;

    ArrayAdapter<String> adapter;
    ArrayList<String> trucks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Search...");

        listView = (ListView) findViewById(R.id.ListView);
        searchView = (SearchView) findViewById(R.id.SearchView);
        close = (FloatingActionButton) findViewById(R.id.close_list);
        close.setOnClickListener(this::onClickClose);

        trucks = new ArrayList<>();
        trucks.add("Mo's Burritos");
        trucks.add("Fernando's");
        trucks.add("Burrito King");
        trucks.add("La Paloma");
        trucks.add("Watson's Chicken");
        trucks.add("Maize");
        

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trucks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                goToDetails();
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

    private void onClickClose(View view) {
        finish();
    }

    public void goToDetails() {
        Intent intent = new Intent(this, TruckDetailsActivity.class);
        intent.putExtra("truckid", "1");
        startActivity(intent);
    }
}