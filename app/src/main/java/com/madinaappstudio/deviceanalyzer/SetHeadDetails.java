package com.madinaappstudio.deviceanalyzer;

import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class SetHeadDetails {

    public void ofHardMem(ImageView hardMemFtIc, TextView hardMemFtName){
        hardMemFtIc.setImageResource(R.drawable.ic_memory_24px);
        hardMemFtName.setText("Memory Information");
    }

    public void ofHardDis(ImageView hardDisFtIc, TextView hardDisFtName, String displayName){
        hardDisFtIc.setImageResource(R.drawable.ic_android_phone_24px);
        hardDisFtName.setText(displayName);
    }
    public void ofHardSoc(TextView harSocFtName, ImageView harSocFtIc){
        String socName;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            socName = Build.SOC_MANUFACTURER.toLowerCase();
            if (socName.contains("mediatek")){
                harSocFtName.setText(R.string.mediatek);
                harSocFtIc.setImageResource(R.drawable.ic_mediatek);
            } else if (socName.contains("exynos")) {
                harSocFtName.setText(R.string.samsung_exynos);
                harSocFtIc.setImageResource(R.drawable.ic_exynos);
            } else if (socName.contains("snapdragon")) {
                harSocFtName.setText(R.string.qualcomm_snapdragon);
                harSocFtIc.setImageResource(R.drawable.ic_snapdragon);
            } else if (socName.contains("kirin")) {
                harSocFtName.setText(R.string.kirin);
                harSocFtIc.setImageResource(R.drawable.ic_kirin);
            } else if (socName.contains("google") || socName.contains("tensor")){
                harSocFtName.setText(R.string.google_tensor);
                harSocFtIc.setImageResource(R.drawable.ic_tensor);
            }

        } else {
            socName = Build.HARDWARE.toLowerCase();
            if (socName.contains("mt")){
                harSocFtName.setText(R.string.mediatek);
                harSocFtIc.setImageResource(R.drawable.ic_mediatek);
            } else if (socName.contains("s53")){
                harSocFtName.setText(R.string.samsung_exynos);
                harSocFtIc.setImageResource(R.drawable.ic_exynos);
            } else if (socName.contains("sm") || socName.contains("sdm") || socName.contains("msm") || socName.contains("apq")) {
                harSocFtName.setText(R.string.qualcomm_snapdragon);
                harSocFtIc.setImageResource(R.drawable.ic_snapdragon);
            } else if (socName.contains("kirin") || socName.contains("hi")) {
                harSocFtName.setText(R.string.kirin);
                harSocFtIc.setImageResource(R.drawable.ic_kirin);
            } else if (socName.contains("gs")) {
                harSocFtName.setText(R.string.google_tensor);
                harSocFtIc.setImageResource(R.drawable.ic_tensor);
            }
        }
    }

    public void ofSysOs(int apiLevel, TextView sysOsFtName, ImageView sysOsFtIc){
        sysOsFtName.setText("Android " + Build.VERSION.RELEASE);
        switch (apiLevel){
            case 26:
                sysOsFtIc.setImageResource(R.drawable.android_26);
                break;
            case 28:
                sysOsFtIc.setImageResource(R.drawable.android_28);
                break;
            case 29:
                sysOsFtIc.setImageResource(R.drawable.android_29);
                break;
            case 30:
                sysOsFtIc.setImageResource(R.drawable.android_30);
                break;
            case 31:
                sysOsFtIc.setImageResource(R.drawable.android_31);
                break;
            case 33:
                sysOsFtIc.setImageResource(R.drawable.android_33);
                break;
            case 34:
                sysOsFtIc.setImageResource(R.drawable.android_34);
                break;
            default:
                sysOsFtIc.setImageResource(android.R.drawable.stat_notify_error);
                break;
        }
    }

    public void ofSysBat(float batteryPercentage, String status, TextView sysBatFtName, ImageView sysBatFtIc){
        sysBatFtName.setText(status);
        if (Objects.equals(status, "Charging")){
            if (batteryPercentage >= 95){
                sysBatFtIc.setImageResource(R.drawable.ic_battery_charging_full_24px);
            } else if (batteryPercentage >= 80) {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_charging_high_24px);
            } else if (batteryPercentage >= 50) {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_charging_medium_24px);
            } else if (batteryPercentage >= 20) {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_charging_low_24px);
            } else  {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_critical_24px);
            }
        } else {
            if (batteryPercentage >= 95){
                sysBatFtIc.setImageResource(R.drawable.ic_battery_full_24px);
            } else if (batteryPercentage >= 80) {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_high_24px);
            } else if (batteryPercentage >= 50) {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_medium_24px);
            } else if (batteryPercentage >= 20) {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_low_24px);
            } else  {
                sysBatFtIc.setImageResource(R.drawable.ic_battery_critical_24px);
            }
        }
    }

    public void ofDev(String manufacturer, String deviceName, ImageView devFtIc, TextView devFtName){
        switch (manufacturer) {
            case "samsung":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_samsung);
                break;
            case "huawei":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_huawei);
                break;
            case "xiaomi":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_xiaomi);
                break;
            case "oppo":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_oppo);
                break;
            case "vivo":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_vivo);
                break;
            case "oneplus":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_oneplus);
                break;
            case "motorola":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_motorola);
                break;
            case "lg":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_lg);
                break;
            case "sony":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_sony);
                break;
            case "nokia":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_nokia);
                break;
            case "htc":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_htc);
                break;
            case "asus":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_asus);
                break;
            case "realme":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_realme);
                break;
            case "google":
                devFtName.setText(deviceName);
                devFtIc.setImageResource(R.drawable.ic_google);
                break;
            default:
                devFtName.setText(deviceName);
                devFtIc.setImageResource(android.R.drawable.stat_notify_error);
                break;
        }
    }

}
