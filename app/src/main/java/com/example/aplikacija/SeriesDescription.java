package com.example.aplikacija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class SeriesDescription extends AppCompatActivity{

    private ImageView imageView;
    private TextView titleView;
    private TextView yearView;
    private TextView rateView;
    private TextView runtimeView;
    private TextView genreView;
    private TextView writerView;
    private TextView seasonsView;
    private TextView actorsView;
    private TextView plot_shortView;
    private TextView languageView;
    private TextView countryView;
    private TextView imdbRatingView;
    private TextView awardsView;

    private void init() {


        imageView       = findViewById(R.id.poster);
        titleView       = findViewById(R.id.title);
        yearView        = findViewById(R.id.year);
        rateView        = findViewById(R.id.rated);
        runtimeView     = findViewById(R.id.runtime);
        genreView       = findViewById(R.id.genre);
        writerView      = findViewById(R.id.writer);
        seasonsView     = findViewById(R.id.seasons);
        actorsView      = findViewById(R.id.actors);
        plot_shortView  = findViewById(R.id.plot_short);
        languageView    = findViewById(R.id.language);
        countryView     = findViewById(R.id.country);
        imdbRatingView  = findViewById(R.id.imdb_rating);
        awardsView      = findViewById(R.id.awards);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_description);
        init();

        String full = getIntent().getStringExtra("full");

        try {
             if(full != null) {
                JSONObject fullDesc = new JSONObject(full);

                String poster = fullDesc.get("Poster").toString();
                String title = fullDesc.getString("Title");
                String year = fullDesc.getString("Year");
                String rated = fullDesc.getString("Rated");
                String runtime = fullDesc.getString("Runtime");
                String genre = fullDesc.getString("Genre");
                String writer = fullDesc.getString("Writer");
                String seasons = fullDesc.getString("totalSeasons");
                String actors = fullDesc.getString("Actors");
                String plot_short = fullDesc.getString("Plot");
                String language = fullDesc.getString("Language");
                String country = fullDesc.getString("Country");
                String imdbRating = fullDesc.getString("imdbRating");
                final String imdbID = fullDesc.getString("imdbID");
                String awards = fullDesc.getString("Awards");

                if(poster.matches("N/A")){
                    Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png").into(imageView);
                } else {
                    Picasso.get().load(poster).into(imageView);
                }

                titleView.setText(title);
                yearView.setText(year);
                rateView.setText(rated);
                runtimeView.setText(runtime);
                genreView.setText(genre);
                writerView.setText(writer);
                actorsView.setText(actors);
                seasonsView.setText(seasons);
                plot_shortView.setText(plot_short);
                languageView.setText(language);
                countryView.setText(country);
                imdbRatingView.setText(imdbRating);
                awardsView.setText(awards);

                ImageView button = findViewById(R.id.imageButton);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.imdb.com/title/" + imdbID));
                        startActivity(i);
                    }
                });
            } else {
                throw new JSONException("JSON Response is Empty");
            }
        } catch(JSONException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        }

    }

