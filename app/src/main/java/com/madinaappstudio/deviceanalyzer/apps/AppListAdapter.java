package com.madinaappstudio.deviceanalyzer.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.madinaappstudio.deviceanalyzer.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private final Context context;
    private final List<ApplicationInfo> appList;
    private final PackageManager packageManager;
    AppIconCache appIconCache;

    public AppListAdapter(Context context, List<ApplicationInfo> appList, AppIconCache appIconCache) {
        this.context = context;
        this.appList = appList;
        this.appIconCache = appIconCache;
        this.packageManager = context.getPackageManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationInfo appInfo = appList.get(position);
        holder.appName.setText(appInfo.loadLabel(packageManager));
        holder.packageName.setText(appInfo.packageName);
        Drawable icon = appIconCache.getIconFromCache(appInfo.packageName);
        if (icon != null) {
            holder.appIcon.setImageDrawable(icon);
        } else {
            appIconCache.loadAndCacheIconAsync(appInfo, holder.appIcon);
        }
        holder.appListItemRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppDetails.class);
                intent.putExtra("packageName", appInfo.packageName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, packageName;
        CardView appListItemRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            packageName = itemView.findViewById(R.id.packageName);
            appListItemRow = itemView.findViewById(R.id.appListItemRow);
        }
    }

}
