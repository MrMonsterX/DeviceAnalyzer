package com.madinaappstudio.deviceanalyzer.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.madinaappstudio.deviceanalyzer.R;
import com.madinaappstudio.deviceanalyzer.databinding.ActivityPackageDetailsBinding;

import java.util.ArrayList;

public class PackageDetails extends AppCompatActivity{
    ActivityPackageDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackageDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        PackageManager packageManager = getPackageManager();

        ArrayList<String> intentData = getIntent().getStringArrayListExtra("data");
        String dataName = getIntent().getStringExtra("dataName");
        String packageName = getIntent().getStringExtra("packageName");
        assert packageName != null;

        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            CharSequence name  = packageManager.getApplicationLabel(appInfo);
            if (intentData != null){
                binding.appFtCount.setText(String.valueOf(intentData.size()));
                binding.appFtAppName.setText(name);
                binding.appFtType.setText(dataName);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview_item, intentData);
                binding.listView.setAdapter(adapter);
            } else {
                binding.appFtCount.setText("0");
                binding.appFtAppName.setText(name);
                binding.appFtType.setText(dataName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}