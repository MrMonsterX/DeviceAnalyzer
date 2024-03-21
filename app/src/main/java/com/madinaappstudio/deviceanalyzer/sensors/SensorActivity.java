package com.madinaappstudio.deviceanalyzer.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.R;
import com.madinaappstudio.deviceanalyzer.databinding.ActivitySensorBinding;

import java.util.ArrayList;
import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private ActivitySensorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        binding.senFtSenCount.setText(String.valueOf(sensorList.size()));

        List<String> sensorNames = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            sensorNames.add(sensor.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorNames);
        binding.senListView.setAdapter(arrayAdapter);


        binding.senListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SensorActivity.this, SensorDetails.class);
                intent.putExtra("type", sensorList.get(position).getType());
                intent.putExtra("name", sensorList.get(position).getName());
                intent.putExtra("vendor", sensorList.get(position).getVendor());
                intent.putExtra("power", sensorList.get(position).getPower());
                intent.putExtra("version", sensorList.get(position).getVersion());
                intent.putExtra("resolution", sensorList.get(position).getResolution());
                intent.putExtra("maxRange", sensorList.get(position).getMaximumRange());
                intent.putExtra("wakeup", sensorList.get(position).isWakeUpSensor());
                intent.putExtra("dynamic", sensorList.get(position).isDynamicSensor());
                SensorActivity.this.startActivity(intent);
            }
        });

    }
}