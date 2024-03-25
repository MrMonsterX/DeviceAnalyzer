package com.madinaappstudio.deviceanalyzer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.databinding.ActivityHomeBinding;
import com.madinaappstudio.deviceanalyzer.networks.NetworkActivity;
import com.madinaappstudio.deviceanalyzer.sensors.SensorActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HomeActivity extends AppCompatActivity {
    private Handler handler;
    Runnable timeRunnable;
    
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CrashReporter.startCrashThread(this);

        binding.mDeviceModel.setText(String.valueOf(Build.BRAND));

        setTimeOnMainScreen();

        binding.mBtnSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SystemActivity.class));
            }
        });

        binding.mBtnSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SensorActivity.class));
            }
        });

        binding.mBtnHardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, HardwareActivity.class));
            }
        });


        binding.mBtnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, DeviceActivity.class));
            }
        });

        binding.mBtnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, NetworkActivity.class));
            }
        });

        binding.mBtnConnectivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ConnectivityActivity.class));
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