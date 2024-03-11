package com.madinaappstudio.deviceanalyzer.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;

public class Accelerometer extends Fragment implements SensorEventListener {

    Context context;
    TextView sensorNameHead, sType, sName, sVendor, sVersion, sMaxRange, sResolution, sPower, sUnit,
    vOne, vTwo, vThree, vOneText, vTwoText, vThreeText;

    public Accelerometer(Context context) {
        this.context = context;
    }
    public Accelerometer() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_details_fragment, container, false);

        CrashReporter.startCrashThread(context);

        sensorNameHead = view.findViewById(R.id.sensorNameHead);
        sType = view.findViewById(R.id.sType);
        sName = view.findViewById(R.id.sName);
        sVendor = view.findViewById(R.id.sVendor);
        sVersion = view.findViewById(R.id.sVersion);
        sMaxRange = view.findViewById(R.id.sMaxRange);
        sResolution = view.findViewById(R.id.sResolution);
        sPower = view.findViewById(R.id.sPower);
        sUnit = view.findViewById(R.id.sUnit);
        vOne = view.findViewById(R.id.vOne);
        vTwo = view.findViewById(R.id.vTwo);
        vThree = view.findViewById(R.id.vThree);
        vOneText = view.findViewById(R.id.vOneText);
        vTwoText = view.findViewById(R.id.vTwoText);
        vThreeText = view.findViewById(R.id.vThreeText);

        sensorNameHead.setText(R.string.accelerometer);

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            sType.setText(R.string.accelerometer);
            sName.setText(event.sensor.getName());
            sPower.setText(String.valueOf(event.sensor.getPower()));
            sUnit.setText(R.string.ms);
            sVendor.setText(event.sensor.getVendor());
            sVersion.setText(String.valueOf(event.sensor.getVersion()));
            sMaxRange.setText(String.valueOf(event.sensor.getMaximumRange()));
            sResolution.setText(String.valueOf(event.sensor.getResolution()));

            vOneText.setText(R.string.x_axis);
            vTwoText.setText(R.string.y_axis);
            vThreeText.setText(R.string.z_axis);
            vOne.setText(String.valueOf(event.values[0]));
            vTwo.setText(String.valueOf(event.values[1]));
            vThree.setText(String.valueOf(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}