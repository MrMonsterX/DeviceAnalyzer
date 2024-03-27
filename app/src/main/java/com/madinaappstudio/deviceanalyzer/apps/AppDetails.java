package com.madinaappstudio.deviceanalyzer.apps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.databinding.ActivityAppDetailsBinding;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;


public class AppDetails extends AppCompatActivity {

    ActivityAppDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        PackageManager packageManager = getPackageManager();
        String packageName = getIntent().getStringExtra("packageName");
        setApplicationDetails(packageName, packageManager);
        assert packageName != null;
        binding.appDetActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActivityInfo[] activities = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;
                    ArrayList<String> activity = new ArrayList<>();
                    if (activities != null) {
                        for (ActivityInfo activityInfo : activities) {
                            if (activityInfo != null){
                                if (activityInfo.name != null){
                                    activity.add(activityInfo.name);
                                }
                            }
                        }
                    }
                    startIntent(activity, "Activities", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        binding.appDetService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ServiceInfo[] services = packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES).services;
                    ArrayList<String> service = new ArrayList<>();
                    if (services != null) {
                        for (ServiceInfo serviceInfo : services) {
                            if (serviceInfo.name != null){
                                service.add(serviceInfo.name);
                            }
                        }
                    }
                    startIntent(service, "Services", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        binding.appDetReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActivityInfo[] receivers = packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS).receivers;
                    ArrayList<String> receiver = new ArrayList<>();
                    if (receivers != null) {
                        for (ActivityInfo activityInfo : receivers) {
                            if (activityInfo.name != null){
                                receiver.add(activityInfo.name);
                            }
                        }
                    }
                    startIntent(receiver, "Receivers", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        binding.appDetProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProviderInfo[] providers = packageManager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS).providers;
                    ArrayList<String> provider = new ArrayList<>();
                    if (providers != null) {
                        for (ProviderInfo providerInfo : providers) {
                            if (providerInfo.name != null){
                                provider.add(providerInfo.name);
                            }
                        }
                    }
                    startIntent(provider, "Providers", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        binding.appDetFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FeatureInfo[] features = packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS).reqFeatures;
                    ArrayList<String> feature = new ArrayList<>();
                    if (features != null){
                        for (FeatureInfo featureInfo : features) {
                            if (featureInfo.name != null) {
                                feature.add(featureInfo.name);
                            }
                        }
                    }
                    startIntent(feature, "Features", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("TAG", "PackageManager.NameNotFoundException occurred", e);
                    throw new RuntimeException(e);
                }

            }
        });

        binding.appDetPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] permissions = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
                    ArrayList<String> permission = new ArrayList<>();
                    if (permissions != null){
                        permission.addAll(Arrays.asList(permissions));
                    }
                    startIntent(permission, "Permissions", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });



    }

    private void setApplicationDetails(String packageName, PackageManager packageManager) {
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            CharSequence name = packageManager.getApplicationLabel(appInfo);
            binding.appDetFtName.setText(name);

            binding.appDetFtPackage.setText(packageName);

            Drawable appLogo = packageManager.getApplicationIcon(appInfo);
            binding.appDetFtIc.setImageDrawable(appLogo);

            String versionName = packageManager.getPackageInfo(packageName, 0).versionName;
            int versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
            binding.appDetVersion.setText(String.valueOf(versionName));
            binding.appDetVersionCode.setText(String.valueOf(versionCode));

            int minSdk = appInfo.minSdkVersion;
            binding.appDetMinSdk.setText(String.valueOf(minSdk));

            int targetSdk = appInfo.targetSdkVersion;
            binding.appDetTarSdk.setText(String.valueOf(targetSdk));

            long installedDate = packageManager.getPackageInfo(packageName, 0).firstInstallTime;
            binding.appDetInstalled.setText(formatDate(installedDate));

            long lastUpdatedDate = packageManager.getPackageInfo(packageName, 0).lastUpdateTime;
            binding.appDetLastUpdated.setText(formatDate(lastUpdatedDate));

            String installer = packageManager.getInstallerPackageName(packageName);
            if (installer != null){
                binding.appDetInstaller.setText(packageManager.getApplicationLabel(packageManager
                        .getApplicationInfo(installer, PackageManager.GET_META_DATA)));
            } else {
                binding.appDetInstaller.setText("Not Found!");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("TAG", "retrieveAppInfo: " + e);
        }
    }

    private void startIntent(ArrayList<String> data, String dataName, String packageName){
        Intent intent = new Intent(AppDetails.this, PackageDetails.class);
        intent.putStringArrayListExtra("data", data);
        intent.putExtra("dataName", dataName);
        intent.putExtra("packageName", packageName);
        startActivity(intent);
    }

    private String formatDate(long ms){
        Instant instant = Instant.ofEpochMilli(ms);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-uuuu");
        return dateTime.format(formatter);
    }
}

