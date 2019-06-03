package com.example.timezero.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.timezero.MainActivity;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(MainActivity.getIntent(this));
        finish();
    }
}
