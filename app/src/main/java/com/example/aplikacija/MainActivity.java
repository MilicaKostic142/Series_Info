package com.example.aplikacija;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView searchBar;
    private ImageButton searchButton;
    private androidx.gridlayout.widget.GridLayout gridLayFav;

    private StringRequest stringRequest;
    private String fullDesc;
    private RequestQueue queue;
    public static ArrayList<SeriesShort> favoriteSeriesSeriesShortArrayList;
    public static ArrayList<String> all_searches;
    private ArrayAdapter<String> adapter;
    public static String GLOBAL_SEARCH;
    public static String allFavoritesPoster;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String[] favoriteSeriesSharedPreferenceStringArray;

    public String getTitleDetails(String api_key, String title) {
        String api_string = "http://www.omdbapi.com/?apikey=" + api_key + "&t=" + title.replaceAll(" ", "+") + "&plot=short&type=series";

        stringRequest = new StringRequest(Request.Method.GET, api_string,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fullDesc = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API: ", error.getMessage());
            }
        });
        stringRequest.setTag("FULL");
        queue.add(stringRequest);
        return fullDesc;
    }
    private void init() {

        searchBar    = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        gridLayFav   = findViewById(R.id.gridLayFav);

        favoriteSeriesSeriesShortArrayList = new ArrayList<>();
        all_searches                       = new ArrayList<>();
        adapter                            = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, all_searches);
        sharedPreferences                  = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor                             = sharedPreferences.edit();
        allFavoritesPoster                 = MainActivity.sharedPreferences.getString("allFavoritesPoster", "");

        queue = Volley.newRequestQueue(this);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<StringRequest>() {
            @Override
            public void onRequestFinished(Request<StringRequest> request) {
                Intent fullDesc;
                fullDesc = new Intent(MainActivity.this, SeriesDescription.class);
                fullDesc.putExtra("full", MainActivity.this.fullDesc);
                startActivity(fullDesc);
            }

        });

        if (getArrayList("searches") != null) {
            ArrayList<String> loadedP = getArrayList("searches");
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, loadedP);
            searchBar.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        favoriteSeriesSharedPreferenceStringArray = sharedPreferences.getString("allFavoritesPoster", "").split(",");

        if (!favoriteSeriesSharedPreferenceStringArray[0].isEmpty()) {
            for (String favSeries : favoriteSeriesSharedPreferenceStringArray) {
                String[] series = favSeries.split("`");
                String title = series[0];
                String year = series[1];
                String type = series[2];
                String poster_link = series[3];
                SeriesShort ss = new SeriesShort(title, year, type, poster_link);
                favoriteSeriesSeriesShortArrayList.add(ss);
            }
        }

        if (!favoriteSeriesSharedPreferenceStringArray[0].isEmpty()) {

            GridLayout.LayoutParams gllp = new GridLayout.LayoutParams();

            gllp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            gllp.setMargins(8, 4, 4, 2);

            gridLayFav.setColumnCount(3);

            for (SeriesShort poster : favoriteSeriesSeriesShortArrayList) {
                final ImageView iv   = new ImageView(this);
                final SeriesShort ss = poster;

                Picasso.get().load(poster.getPoster_link()).into(iv);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getTitleDetails("243645d5", ss.getTitle());
                    }
                });

                iv.setLayoutParams(gllp);
                iv.setAdjustViewBounds(true);

                gridLayFav.addView(iv);
            }
        }else {
            LinearLayout layFav                 = findViewById(R.id.layFav);
            TextView no                         = new TextView(MainActivity.this);
            LinearLayout.LayoutParams lp1       = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.weight = 1;
            no.setText("You have no Favorites");
            no.setGravity(Gravity.END);
            no.setLayoutParams(lp1);
            layFav.addView(no);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_query = searchBar.getText().toString();

                if (!search_query.isEmpty()) {
                    all_searches.add(search_query);
                    adapter.add(search_query);
                    searchBar.setText("");
                    Intent i = new Intent(MainActivity.this, SeriesList.class);
                    searchBar.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    startActivity(i);
                    GLOBAL_SEARCH = search_query;
                    searchBar.setAdapter(adapter);
                    saveArrayList(all_searches, "searches");
                } else {
                    Toast.makeText(MainActivity.this, "Show message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveArrayList(ArrayList<String> all_searches, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(all_searches);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}