package com.madinaappstudio.deviceanalyzer.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.madinaappstudio.deviceanalyzer.R;
import com.madinaappstudio.deviceanalyzer.databinding.ActivityAppBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    ProgressBar loadProgressBar;
    AppIconCache appIconCache;
    ActivityAppBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadProgressBar = findViewById(R.id.loadProgressBar);

        binding.appRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadProgressBar.setVisibility(View.VISIBLE);
        binding.appRecyclerView.setVisibility(View.GONE);

        PackageManager packageManager = getPackageManager();

        List<ApplicationInfo> infoList = packageManager.getInstalledApplications(0);

        List<ApplicationInfo> all = new ArrayList<>();
        List<ApplicationInfo> user = new ArrayList<>();
        List<ApplicationInfo> system = new ArrayList<>();

        for (ApplicationInfo info : infoList) {
            all.add(info);
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                user.add(info);
            }
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                system.add(info);
            }
        }

        loadIconsInBackground(all);

        binding.appTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        binding.appFtCount.setText(String.valueOf(all.size()));
                        setRecyclerView(all);
                        break;
                    case 1:
                        binding.appFtCount.setText(String.valueOf(user.size()));
                        setRecyclerView(user);
                        break;
                    case 2:
                        binding.appFtCount.setText(String.valueOf(system.size()));
                        setRecyclerView(system);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class AppListComparator implements Comparator<ApplicationInfo> {
        @Override
        public int compare(ApplicationInfo appInfo1, ApplicationInfo appInfo2) {
            return appInfo1.loadLabel(getPackageManager()).toString().compareTo(appInfo2.loadLabel(getPackageManager()).toString());
        }
    }

    private void loadIconsInBackground(List<ApplicationInfo> data) {
        new Thread(() -> {
            appIconCache = new AppIconCache(this);
            for (ApplicationInfo appInfo : data) {
                appIconCache.loadAndCacheIconAsync(appInfo, null);
            }
            runOnUiThread(() -> {
                binding.appFtCount.setText(String.valueOf(data.size()));
                loadProgressBar.setVisibility(View.GONE);
                binding.appRecyclerView.setVisibility(View.VISIBLE);
                setRecyclerView(data);
            });
        }).start();
    }

    private void setRecyclerView(List<ApplicationInfo> data) {
        data.sort(new AppListComparator());
        AppListAdapter adapter = new AppListAdapter(this, data, appIconCache);
        binding.appRecyclerView.setAdapter(adapter);
    }

}
