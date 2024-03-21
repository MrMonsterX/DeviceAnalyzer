package com.madinaappstudio.deviceanalyzer.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.R;
import com.madinaappstudio.deviceanalyzer.databinding.ActivitySensorDetailsBinding;

public class SensorDetails extends AppCompatActivity implements SensorEventListener {

    private int sensorType = -1;
    private ActivitySensorDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensorDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        handleIntent(getIntent());
        registerSensorListener();
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
            binding.sName.setText(stName);
            binding.sensorNameHead.setText(stName);
        }
        if (stVendor != null) {
            binding.sVendor.setText(stVendor);
        }
        if (stPower != -1) {
            binding.sPower.setText(String.valueOf(stPower));
        }
        if (stVersion != -1) {
            binding.sVersion.setText(String.valueOf(stVersion));
        }
        if (stResolution != -1) {
            binding.sResolution.setText(String.valueOf(stResolution));
        }
        if (stMaxRange != -1) {
            binding.sMaxRange.setText(String.valueOf(stMaxRange));
        }
        if (stWakeup) {
            binding.sWakeup.setText(R.string.yes);
        } else {
            binding.sWakeup.setText(R.string.no);
        }
        if (stDynamic) {
            binding.sDynamic.setText(R.string.yes);
        } else {
            binding.sDynamic.setText(R.string.no);
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
                binding.vTwo.setText(String.valueOf(event.values[0]));
            } else if (length == 2) {
                binding.vOne.setText(String.valueOf(event.values[0]));
                binding.vTwo.setText(String.valueOf(event.values[1]));
            } else {
                binding.vOne.setText(String.valueOf(event.values[0]));
                binding.vTwo.setText(String.valueOf(event.values[1]));
                binding.vThree.setText(String.valueOf(event.values[2]));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
