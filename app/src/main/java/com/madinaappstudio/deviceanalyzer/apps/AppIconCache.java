package com.madinaappstudio.deviceanalyzer.apps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

public class AppIconCache {

    private final LruCache<String, Drawable> iconCache;
    private final PackageManager packageManager;
    private final Context context;

    public AppIconCache(Context context){
        this.packageManager = context.getPackageManager();
        this.context = context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        iconCache = new LruCache<>(cacheSize);
    }

    public Drawable getIconFromCache(String packageName) {
        return iconCache.get(packageName);
    }

    public void cacheIcon(String packageName, Drawable icon) {
        iconCache.put(packageName, icon);
    }

    public void loadAndCacheIconAsync(ApplicationInfo appInfo, ImageView imageView) {
        new Thread(() -> {
            try {
                Drawable icon = appInfo.loadIcon(packageManager);
                if (icon != null) {
                    Drawable drawable = icon;
                    if (icon instanceof AdaptiveIconDrawable) {
                        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        icon.draw(canvas);
                        drawable = new BitmapDrawable(context.getResources(), bitmap);
                    }
                    if (drawable instanceof BitmapDrawable) {
                        Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 64, 64, true);
                        Drawable scaledIcon = new BitmapDrawable(context.getResources(), scaledBitmap);
                        cacheIcon(appInfo.packageName, scaledIcon);
                        if (imageView != null){
                            new Handler(Looper.getMainLooper()).post(() -> imageView.setImageDrawable(scaledIcon));
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("loadAndCacheIconAsync", "Exception: " + e);
            }
        }).start();
    }
}
