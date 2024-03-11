package com.madinaappstudio.deviceanalyzer;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceActivity extends AppCompatActivity {

    TextView devFtName, devName, devManufacturer, devModel, devBrand, devProduct, devBoard, devHardware, devRadio, devIncremental;
    ImageView devFtIc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        CrashReporter.startCrashThread(this);

        devFtIc = findViewById(R.id.devFtIc);
        devFtName = findViewById(R.id.devFtName);
        devName = findViewById(R.id.devName);
        devManufacturer = findViewById(R.id.devManufacturer);
        devModel = findViewById(R.id.devModel);
        devBrand = findViewById(R.id.devBrand);
        devProduct = findViewById(R.id.devProduct);
        devBoard = findViewById(R.id.devBoard);
        devHardware = findViewById(R.id.devHardware);
        devRadio = findViewById(R.id.devRadio);
        devIncremental = findViewById(R.id.devIncremental);

        //Device
        String deviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        devName.setText(deviceName);
        SetHeadDetails setHeadDetails = new SetHeadDetails();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        setHeadDetails.ofDev(manufacturer, deviceName, devFtIc, devFtName);
        devManufacturer.setText(manufacturer);
        devModel.setText(String.valueOf(Build.MODEL));
        devBrand.setText(String.valueOf(Build.BRAND));
        devProduct.setText(String.valueOf(Build.PRODUCT));
        devBoard.setText(String.valueOf(Build.BOARD));
        devHardware.setText(String.valueOf(Build.HARDWARE));
        devRadio.setText(String.valueOf(Build.getRadioVersion()));
        devIncremental.setText(String.valueOf(Build.VERSION.INCREMENTAL));


    }


}