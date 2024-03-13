package com.madinaappstudio.deviceanalyzer.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.R;

public class SensorDetails extends AppCompatActivity implements SensorEventListener {

    private TextView sensorNameHead, sName, sVendor, sVersion, sMaxRange, sResolution, sPower, sWakeup, sDynamic,
            vOne, vTwo, vThree;

    private int sensorType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        initializeViews();
        handleIntent(getIntent());
        registerSensorListener();
    }

    private void initializeViews() {
        sensorNameHead = findViewById(R.id.sensorNameHead);
        sName = findViewById(R.id.sName);
        sVendor = findViewById(R.id.sVendor);
        sVersion = findViewById(R.id.sVersion);
        sMaxRange = findViewById(R.id.sMaxRange);
        sResolution = findViewById(R.id.sResolution);
        sPower = findViewById(R.id.sPower);
        sWakeup = findViewById(R.id.sWakeup);
        sDynamic = findViewById(R.id.sDynamic);
        vOne = findViewById(R.id.vOne);
        vTwo = findViewById(R.id.vTwo);
        vThree = findViewById(R.id.vThree);
    }

    private void handleIntent(Intent intent) {
        sensorType = intent.getIntExtra("type", -1);
        String stName = intent.getStringExtra("name");
        String stVendor = intent.getStringExtra("vendor");
        float stPower = intent.getFloatExtra("power", -1);
        int stVersion = intent.getIntExtra("version", -1);
        float stResolution = intent.getFloatExtra("resolution", -1);
        float stMaxRange = intent.getFloatExtra("maxRange", -1);
        boolean stWakeup = intent.getBooleanExtra("wakeup", false);
        boolean stDynamic = intent.getBooleanExtra("dynamic", false);

        if (stName != null) {
            sName.setText(stName);
            sensorNameHead.setText(stName);
        }
        if (stVendor != null) {
            sVendor.setText(stVendor);
        }
        if (stPower != -1) {
            sPower.setText(String.valueOf(stPower));
        }
        if (stVersion != -1) {
            sVersion.setText(String.valueOf(stVersion));
        }
        if (stResolution != -1) {
            sResolution.setText(String.valueOf(stResolution));
        }
        if (stMaxRange != -1) {
            sMaxRange.setText(String.valueOf(stMaxRange));
        }
        if (stWakeup) {
            sWakeup.setText(R.string.yes);
        } else {
            sWakeup.setText(R.string.no);
        }
        if (stDynamic) {
            sDynamic.setText(R.string.yes);
        } else {
            sDynamic.setText(R.string.no);
        }
    }

    private void registerSensorListener() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(SensorDetails.this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int length = event.values.length;
        if (event.sensor.getType() == sensorType) {
            if (length == 0) {
                Log.d("TAG", "onSensorChanged: 0 ");
            } else if (length == 1) {
                vTwo.setText(String.valueOf(event.values[0]));
            } else if (length == 2) {
                vOne.setText(String.valueOf(event.values[0]));
                vTwo.setText(String.valueOf(event.values[1]));
            } else {
                vOne.setText(String.valueOf(event.values[0]));
                vTwo.setText(String.valueOf(event.values[1]));
                vThree.setText(String.valueOf(event.values[2]));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
