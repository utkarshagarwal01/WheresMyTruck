package edu.illinois.cs465.wheresmytruck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    ArrayList<String> trucks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.ListView);
        searchView = (SearchView) findViewById(R.id.SearchView);

        trucks.add("Mo's Burritos");
        trucks.add("Fernando's");
        trucks.add("Burrito King");
        trucks.add("La Paloma");
        trucks.add("Watson's Chicken");
        trucks.add("Maize");
        

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trucks);
        listView.setAdapter(adapter);

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
                return false;
            }
        });

    }
}