package com.madinaappstudio.deviceanalyzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.databinding.ActivitySystemBinding;

import java.io.IOException;
import java.lang.reflect.Method;

public class SystemActivity extends AppCompatActivity {
    Handler handlerUpTime = new Handler();
    Handler handlerBattery = new Handler();
    Runnable upTimeRunnable, batRunnable;
    SetHeadDetails setHeadDetails = new SetHeadDetails();
    ActivitySystemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySystemBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CrashReporter.startCrashThread(this);

        // Operating System
        binding.sysOsAndroidVersion.setText(String.valueOf(Build.VERSION.RELEASE));
        int apiLevel = Build.VERSION.SDK_INT;
        binding.sysOsApiLevel.setText(String.valueOf(apiLevel));
        setHeadDetails.ofSysOs(apiLevel, binding.sysOsFtName, binding.sysOsFtIc);
        binding.sysOsBuildID.setText(String.valueOf(Build.ID));
        binding.sysOsKernelVersion.setText(String.valueOf(System.getProperty("os.version")));
        binding.sysOsKernelArch.setText(String.valueOf(System.getProperty("os.arch")));
        if (isRooted()){
            binding.sysOsRootAccess.setText(R.string.rooted);
        } else {
            binding.sysOsRootAccess.setText(R.string.non_rooted);
        }
        String  gPlayVersion = getPlayServicesVersion(this);
        if (gPlayVersion != null) {
            binding.sysOsGPlayService.setText(gPlayVersion);
        } else {
            binding.sysOsGPlayService.setText(R.string.not_supported);
        }
        binding.sysOsSecurityPatch.setText(String.valueOf(Build.VERSION.SECURITY_PATCH));
        binding.sysOsArtVersion.setText(String.valueOf(System.getProperty("java.vm.version")));
        binding.sysOsBootloader.setText(String.valueOf(Build.BOOTLOADER));
        upTimeRunnable = new Runnable() {
            @Override
            public void run() {
                long[] upTime = getUpTime();
                binding.sysOsUpTime.setText(String.valueOf(upTime[0] + " Days, " + upTime[1] +":"+ upTime[2] +":"+ upTime[3]));
                handlerUpTime.postDelayed(this, 1000);
            }
        };
        handlerUpTime.post(upTimeRunnable);


        // Battery
        batRunnable = new Runnable() {
            @Override
            public void run() {

                Intent batteryIntent = SystemActivity.this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                BatteryManager batteryManager = (BatteryManager) SystemActivity.this.getSystemService(Context.BATTERY_SERVICE);

                assert batteryIntent != null;
                int batteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int batteryScale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPercentage = batteryLevel / (float) batteryScale * 100;
                binding.sysBatLevel.setText(String.valueOf(batteryPercentage + "%"));

                int staInt = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String status = getStatusString(staInt);
                binding.sysBatStatus.setText(status);

                setHeadDetails.ofSysBat(batteryPercentage, status, binding.sysBatFtName, binding.sysBatFtIc);

                int health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                binding.sysBatHealth.setText(getHealthString(health));

                int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                binding.sysBatVoltage.setText(String.valueOf(voltage + " mV"));

                int temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                binding.sysBatTemperature.setText(String.valueOf(temperature / 10.0 + " °C"));

                String technology = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                binding.sysBatTechnology.setText(technology != null ? technology : "Unknown");

                long currentCapacity = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                long counter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                binding.sysBatFullCapacity.setText(String.valueOf((counter/currentCapacity/10) + " mAh"));

                double batteryCapacityInAds = getBatteryCapacity(SystemActivity.this);
                binding.sysBatCapacityInAds.setText(String.valueOf(batteryCapacityInAds + " mAh"));
                binding.sysBatChargeCounter.setText(String.valueOf(counter/1000 + " mAh"));

                if (status.equals("Charging")){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        long timeFullyCharged = batteryManager.computeChargeTimeRemaining();
                        long hours = timeFullyCharged / (1000 * 60 * 60);
                        long minutes = (timeFullyCharged % (1000 * 60 * 60)) / (1000 * 60);
                        long seconds = ((timeFullyCharged % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
                        binding.sysBatUntilFullyCharge.setText(String.valueOf(hours +"h "+ minutes+"m "+ seconds +"s"));
                        binding.viewUntilFullyCharged.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.viewUntilFullyCharged.setVisibility(View.GONE);
                }
                int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                binding.sysBatChargingSource.setText(String.valueOf(getChargingSource(plugged)));
                handlerBattery.postDelayed(this, 1000);
            }
        };

        handlerBattery.post(batRunnable);
    }

    private boolean isRooted(){
        boolean root;
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.destroy();
            root = true;
        } catch (IOException e) {
            root = false;
        }
        return root;
    }

    public static String getPlayServicesVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.google.android.gms", 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("GPlayServiceTag", "getPlayServicesVersion: " + e);
            return null;
        }
    }


    private long[] getUpTime(){
        long[] upTime = new long[4];
        long uptimeMillis = SystemClock.elapsedRealtime();
        long uptimeSeconds = uptimeMillis / 1000;
        long days = uptimeSeconds / (60 * 60 * 24);
        long hours = (uptimeSeconds % (60 * 60 * 24)) / (60 * 60);
        long minutes = (uptimeSeconds % (60 * 60)) / 60;
        long seconds = uptimeSeconds % 60;
        upTime[0] = days;
        upTime[1] = hours;
        upTime[2] = minutes;
        upTime[3] = seconds;
        return upTime;
    }

    @SuppressLint("PrivateApi")
    public static double getBatteryCapacity(Context context) {
        double batteryCapacity = 0;
        try {
            Class<?> powerProfileClass = Class.forName("com.android.internal.os.PowerProfile");
            Object powerProfile = powerProfileClass.getConstructor(Context.class).newInstance(context);
            Method getBatteryCapacityMethod = powerProfileClass.getMethod("getBatteryCapacity");
            batteryCapacity = (double) getBatteryCapacityMethod.invoke(powerProfile);
        } catch (Exception e) {
            Log.d("BatteryCapacityTag", "getBatteryCapacity: " + e);
        }
        return batteryCapacity;
    }

    private static String getHealthString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified Failure";
            default:
                return "Unknown";
        }
    }
    private String getStatusString(int status){
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            default:
                return "Unknown";
        }
    }

    private static String getChargingSource(int plugged) {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC Charger";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB Charger";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Wireless Charger";
            default:
                return "On Battery";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerBattery.removeCallbacks(batRunnable);
    }
}