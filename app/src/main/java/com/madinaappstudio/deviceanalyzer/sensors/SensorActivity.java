package com.madinaappstudio.deviceanalyzer.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;

public class SensorActivity extends AppCompatActivity {
    Button tBtnAccelerometer, tBtnProximity, tBtnGyroscope, tBtnLight, tBtnMagnetic,
    tBtnGravity, tBtnPressure, tBtnTemperature, tBtnOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        CrashReporter.startCrashThread(this);

        tBtnAccelerometer = findViewById(R.id.tBtnAccelerometer);
        tBtnProximity = findViewById(R.id.tBtnProximity);
        tBtnGyroscope = findViewById(R.id.tBtnGyroscope);
        tBtnLight = findViewById(R.id.tBtnLight);
        tBtnMagnetic = findViewById(R.id.tBtnMagnetic);
        tBtnGravity = findViewById(R.id.tBtnGravity);
        tBtnPressure = findViewById(R.id.tBtnPressure);
        tBtnTemperature = findViewById(R.id.tBtnTemperature);
        tBtnOrientation = findViewById(R.id.tBtnOrientation);

        tBtnAccelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(1);
            }
        });

        tBtnProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(2);
            }
        });

        tBtnGyroscope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(3);
            }
        });

        tBtnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(4);
            }
        });

        tBtnMagnetic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(5);
            }
        });

        tBtnGravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(6);
            }
        });

        tBtnPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(7);
            }
        });

        tBtnTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(8);
            }
        });

        tBtnOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(9);
            }
        });
    }

    private void startIntent(int id){
        Intent intent = new Intent(SensorActivity.this, SensorDetails.class)
                .putExtra("ID", id);
        startActivity(intent);
    }

}