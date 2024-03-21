package com.madinaappstudio.deviceanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.madinaappstudio.deviceanalyzer.databinding.ActivityMainBinding;
import com.madinaappstudio.deviceanalyzer.networks.NetworkActivity;
import com.madinaappstudio.deviceanalyzer.sensors.SensorActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    Runnable timeRunnable;
    
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CrashReporter.startCrashThread(this);

        binding.mDeviceModel.setText(String.valueOf(Build.BRAND));

        setTimeOnMainScreen();

        binding.mBtnSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SystemActivity.class));
            }
        });

        binding.mBtnSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SensorActivity.class));
            }
        });

        binding.mBtnHardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HardwareActivity.class));
            }
        });


        binding.mBtnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeviceActivity.class));
            }
        });

        binding.mBtnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NetworkActivity.class));
            }
        });

        binding.mBtnConnectivity.setOnClickListener(new View.OnClickListener() {
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
                    binding.mainText.setText(formattedTime);
                    handler.postDelayed(this, 1000);
                }
            };
        } else {
            timeRunnable = new Runnable() {
                @Override
                public void run() {
                    LocalTime currentTime = LocalTime.now();
                    String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
                    binding.mainText.setText(formattedTime.toUpperCase());
                    handler.postDelayed(this, 1000);
                }
            };
        }
        handler.post(timeRunnable);
    }
}