package com.madinaappstudio.deviceanalyzer.sensors;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;

public class SensorDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        CrashReporter.startCrashThread(this);

        int id = getIntent().getIntExtra("ID", 0);
        switch (id){
            case 1:
                loadFragment(new Accelerometer(this));
                break;
            case 2:
                loadFragment(new Proximity(this));
                break;
            case 3:
                loadFragment(new Gyroscope(this));
                break;
            case 4:
                loadFragment(new Light(this));
                break;
            case 5:
                loadFragment(new Magnetic(this));
                break;
            case 6:
                loadFragment(new Gravity(this));
                break;
            case 7:
                loadFragment(new Pressure(this));
                break;
            case 8:
                loadFragment(new Temperature(this));
                break;
            case 9:
                loadFragment(new Orientation(this));
                break;
            default:
                //TODO
                break;
        }
    }

    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frag, fragment);
        fragmentTransaction.commit();
    }
}