package com.madinaappstudio.deviceanalyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.madinaappstudio.deviceanalyzer.networks.NetworkActivity;
import com.madinaappstudio.deviceanalyzer.sensors.SensorActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    Button mBtnSystem, mBtnSensors, mBtnHardware, mBtnDevice, mBtnNetwork, mBtnConnectivity;
    TextView mainText;
    TextView mDeviceModel;
    private Handler handler;
    Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CrashReporter.startCrashThread(this);

        mainText = findViewById(R.id.mainText);
        mDeviceModel = findViewById(R.id.mDeviceModel);

        mBtnSystem = findViewById(R.id.mBtnSystem);
        mBtnSensors = findViewById(R.id.mBtnSensors);
        mBtnHardware = findViewById(R.id.mBtnHardware);
        mBtnDevice = findViewById(R.id.mBtnDevice);
        mBtnNetwork = findViewById(R.id.mBtnNetwork);
        mBtnConnectivity = findViewById(R.id.mBtnConnectivity);
        mDeviceModel.setText(String.valueOf(Build.BRAND));

        setTimeOnMainScreen();

        mBtnSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SystemActivity.class));
            }
        });

        mBtnSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SensorActivity.class));
            }
        });

        mBtnHardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HardwareActivity.class));
            }
        });


        mBtnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeviceActivity.class));
            }
        });

        mBtnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NetworkActivity.class));
            }
        });

        mBtnConnectivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConnectivityActivity.class));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timeRunnable);
    }

    private void setTimeOnMainScreen(){
        handler = new Handler();
        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        if (is24HourFormat) {
            timeRunnable = new Runnable() {
                @Override
                public void run() {
                    LocalTime currentTime = LocalTime.now();
                    String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    mainText.setText(formattedTime);
                    handler.postDelayed(this, 1000);
                }
            };
        } else {
            timeRunnable = new Runnable() {
                @Override
                public void run() {
                    LocalTime currentTime = LocalTime.now();
                    String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
                    mainText.setText(formattedTime.toUpperCase());
                    handler.postDelayed(this, 1000);
                }
            };
        }
        handler.post(timeRunnable);
    }
}