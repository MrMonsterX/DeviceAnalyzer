package com.madinaappstudio.deviceanalyzer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class CrashReporter {

    private static final String CRASH_LOG_FILE_NAME = "CrashLog-";

    public static void startCrashThread(Context context){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                CrashReporter.reportCrash(context, throwable);
            }
        });
    }

    public static void reportCrash(Context context, Throwable throwable) {
        saveCrashToFile(context, throwable);
    }


    private static void saveCrashToFile(Context context, Throwable throwable) {
        String getTimeStamp = String.valueOf((LocalDateTime.now()));
        try {
             File crashReportDir = new File(context.getExternalFilesDir(null), "Crash Reports");
            if (!crashReportDir.exists()) {
                crashReportDir.mkdirs();
            }
            File crashLogFile = new File(crashReportDir, CRASH_LOG_FILE_NAME);
            FileWriter writer = new FileWriter(crashLogFile + getTimeStamp,  true);
            writer.append(Log.getStackTraceString(throwable));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(context, "Crash Report Not Created!", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "saveCrashToFile: " + e);
        }
    }
}
