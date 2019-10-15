package com.example.aplikacija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        final boolean firstRun = sharedPreferences.getBoolean("firstRun", false);
        if(firstRun==false){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        SharedPreferences.Editor editor= sharedPreferences.edit();
                        editor.putBoolean("firstRun", true);
                        editor.commit();
                        sleep(3000);
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }else {
            Intent i2 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i2);
            finish();
        }

    }
}
