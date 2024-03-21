package com.madinaappstudio.deviceanalyzer;

import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.databinding.ActivityDeviceBinding;

public class DeviceActivity extends AppCompatActivity {

    ActivityDeviceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CrashReporter.startCrashThread(this);

        //Device
        String deviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        binding.devName.setText(deviceName);
        SetHeadDetails setHeadDetails = new SetHeadDetails();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        setHeadDetails.ofDev(manufacturer, deviceName, binding.devFtIc, binding.devFtName);
        binding.devManufacturer.setText(manufacturer);
        binding.devModel.setText(String.valueOf(Build.MODEL));
        binding.devBrand.setText(String.valueOf(Build.BRAND));
        binding.devProduct.setText(String.valueOf(Build.PRODUCT));
        binding.devBoard.setText(String.valueOf(Build.BOARD));
        binding.devHardware.setText(String.valueOf(Build.HARDWARE));
        binding.devRadio.setText(String.valueOf(Build.getRadioVersion()));
        binding.devIncremental.setText(String.valueOf(Build.VERSION.INCREMENTAL));


    }


}