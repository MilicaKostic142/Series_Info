package com.example.aplikacija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static com.example.aplikacija.MainActivity.GLOBAL_SEARCH;

public class SeriesList extends AppCompatActivity {
    private LinearLayout scrollView;
    private RequestQueue queue;
    private StringRequest stringRequest;
    public String seriesFullDescription;
    private String extraFavorites = "";
    private Intent fullDesc;
    private ArrayList<SeriesShort> seriesShorts;
    private static final String api_key = "243645d5";
    private int Series_ID = 0;

    private void init() {
        queue = Volley.newRequestQueue(this);
        seriesShorts = new ArrayList<>();
        scrollView = findViewById(R.id.scrollView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_list);

        init();
        getSeriesList(api_key, GLOBAL_SEARCH);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<StringRequest>() {
            @Override
            public void onRequestFinished(Request<StringRequest> request) {
                if (request.getTag().toString() == "LIST") {
                    for (SeriesShort ss : seriesShorts) {
                        populateScrollView(ss);
                    }
                } else if (request.getTag().toString() == "FULL") {
                    fullDesc = new Intent(SeriesList.this, SeriesDescription.class);
                    fullDesc.putExtra("full", seriesFullDescription);
                    startActivity(fullDesc);
                }
            }
        });
    }

    public String getTitleDetails(String api_key, String title) {
        String api_string = "http://www.omdbapi.com/?apikey=" + api_key + "&t=" + title.replaceAll(" ", "+") + "&plot=short&type=series";

        stringRequest = new StringRequest(Request.Method.GET, api_string,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        seriesFullDescription = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API: ", error.getMessage());
            }
        });
        stringRequest.setTag("FULL");
        queue.add(stringRequest);
        return seriesFullDescription;
    }

    private void getSeriesList(String api_key, String GLOBAL_SEARCH) {
        String api_string = "http://www.omdbapi.com/?apikey=" + api_key + "&s=" + GLOBAL_SEARCH.replaceAll(" ", "+") + "&type=series";

        stringRequest = new StringRequest(Request.Method.GET, api_string,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray links = jsonObject.getJSONArray("Search");
                            for (int i = 0; i < links.length(); i++) {

                                JSONObject jsonObject1 = links.getJSONObject(i);

                                String title = jsonObject1.get("Title").toString();
                                String year = jsonObject1.get("Year").toString();
                                String type = jsonObject1.get("Type").toString();
                                String poster = jsonObject1.get("Poster").toString();
                                SeriesShort ss = new SeriesShort(title, year, type, poster);
                                ss.setSERIES_ID(Series_ID);
                                Series_ID++;
                                seriesShorts.add(ss);
                            }
                        } catch (JSONException e) {
                            Log.e("ERROR: ", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API: ", error.getMessage());
            }
        });
        stringRequest.setTag("LIST");
        queue.add(stringRequest);
    }

    private boolean areStringSame(String s1, String s2) {
        boolean same = true;
        if (s1.length() < s2.length() || s1.length() > s2.length()) {
            same = false;
        } else {
            for (int i = 0; i < s1.length(); i++) {
                if (s1.charAt(i) != s2.charAt(i)) {
                    same = false;
                }
            }
        }
        return same;
    }

    private void populateScrollView(final SeriesShort ss) {

        if (!areStringSame(ss.getPoster_link(), "N/A")) {

            final ImageView iv = new ImageView(SeriesList.this);
            final TextView title = new TextView(SeriesList.this);
            final TextView year = new TextView(SeriesList.this);
            final TextView details = new TextView(SeriesList.this);
            final ImageView heart = new ImageView(SeriesList.this);

            LinearLayout data_view = new LinearLayout(SeriesList.this);


            details.setText("Click to see more...");
            details.setTextSize(18);

            title.setText(ss.getTitle());
            LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            par.weight = 1;
            title.setTextSize(20);
            year.setText(ss.getYear());
            year.setTextSize(18);
            heart.setImageResource(R.drawable.iconheart48);
            heart.setId(Series_ID);
            heart.setTag(1);


            for (SeriesShort s2 : MainActivity.favoriteSeriesSeriesShortArrayList) {
                if (areStringSame(ss.getPoster_link(), s2.getPoster_link())) {
                    heart.setImageResource(R.drawable.iconheartred48);
                    heart.setTag(2);
                    break;
                }
            }

            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!MainActivity.allFavoritesPoster.contains(seriesShorts.get(ss.getSERIES_ID()).toString())) {

                        extraFavorites = seriesShorts.get(ss.getSERIES_ID()).toString() + ",";
                        MainActivity.allFavoritesPoster = MainActivity.allFavoritesPoster + extraFavorites;
                        MainActivity.editor.putString("allFavoritesPoster", MainActivity.allFavoritesPoster);
                        MainActivity.editor.commit();
                        heart.setImageResource(R.drawable.iconheartred48);
                        Toast.makeText(SeriesList.this, "You added " + seriesShorts.get(ss.getSERIES_ID()).getTitle() + " to Favorites", Toast.LENGTH_LONG).show();

                    } else {
                        extraFavorites = seriesShorts.get(ss.getSERIES_ID()).toString() + ",";
                        MainActivity.allFavoritesPoster = MainActivity.allFavoritesPoster.replace(extraFavorites, "");
                        MainActivity.editor.putString("allFavoritesPoster", MainActivity.allFavoritesPoster);
                        MainActivity.editor.commit();
                        heart.setImageResource(R.drawable.iconheart48);
                        Toast.makeText(SeriesList.this, "You removed " + seriesShorts.get(ss.getSERIES_ID()).getTitle() + " from Favorites", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Picasso.get().load(ss.getPoster_link()).into(iv);

            data_view.setOrientation(LinearLayout.VERTICAL);
            data_view.addView(title);
            data_view.addView(year);
            data_view.addView(details);
            data_view.setLayoutParams(par);

            LinearLayout row = new LinearLayout(SeriesList.this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(iv);
            row.addView(data_view);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTitleDetails("243645d5", title.getText().toString());
                }
            });

            LinearLayout r = new LinearLayout(SeriesList.this);
            row.setLayoutParams(par);
            r.setOrientation(LinearLayout.HORIZONTAL);
            r.addView(row);
            r.addView(heart);
            r.setLayoutParams(par);

            LinearLayout.LayoutParams par2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
            LinearLayout line = new LinearLayout(SeriesList.this);
            line.setLayoutParams(par2);
            line.setBackgroundColor(BLACK);

            LinearLayout ro = new LinearLayout(SeriesList.this);
            ro.setOrientation(LinearLayout.VERTICAL);
            ro.addView(r);
            ro.addView(line);
            ro.setLayoutParams(par);
            scrollView.addView(ro);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent home = new Intent(SeriesList.this, MainActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
