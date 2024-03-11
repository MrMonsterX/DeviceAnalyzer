package com.madinaappstudio.deviceanalyzer.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;

public class Orientation extends Fragment implements SensorEventListener {

    Context context;
    TextView sensorNameHead, sType, sName, sVendor, sVersion, sMaxRange, sResolution, sPower, sUnit,
    vOne, vTwo, vThree, vOneText, vTwoText, vThreeText;
    float[] accData;
    float[] magData;

    public Orientation(Context context) {
        this.context = context;
    }
    public Orientation() {}

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

        sensorNameHead.setText(R.string.orientation);

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accData = new float[3];
        magData = new float[3];
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accData, 0, 3);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magData, 0, 3);
        }
        float[] rotationMatrix = new float[9];
        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, accData, magData);
        if (success){
            float[] angles = new float[3];
            SensorManager.getOrientation(rotationMatrix, angles);
            sType.setText(R.string.orientation);
            sName.setText(R.string.none);
            sPower.setText(R.string.none);
            sUnit.setText(R.string.degrees);
            sVendor.setText(R.string.none);
            sVersion.setText(R.string.none);
            sMaxRange.setText(R.string.none);
            sResolution.setText(R.string.none);

            vOneText.setText(R.string.x_axis_ortn);
            vTwoText.setText(R.string.y_axis_ortn);
            vThreeText.setText(R.string.z_axis_ortn);
            vOne.setText(String.valueOf(angles[1]));
            vTwo.setText(String.valueOf(angles[2]));
            vThree.setText(String.valueOf(angles[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}