package com.madinaappstudio.deviceanalyzer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HardwareActivity extends AppCompatActivity implements GLSurfaceView.Renderer{

    TextView hardSocFtName, hardSocManufacturer, hardSocModel, hardSocArchitecture, hardSocClockSpeed, hardSocCpuCores, hardSocGpuVendor,
            hardSocGpuRenderer, hardSocGpuVersion, hardSocGpuExtension, hardSocBtnMore, hardDisFtName,hardDisScreenSize, hardDisResolution,
            hardDisDensityDpi, hardDisRefreshRate, hardDisHdrSupport, hardDisWideColorGamut, hardDisLuminance, hardDisPixelPerInch,
            hardMemFtName, hardRamSize, hardRamOccupied, hardRamAvailable, hardStoSize, hardStoOccupied, hardStoAvailable;
    private int numCores;
    TextView[] coreName;
    LinearLayout hardSocCoreLiveFreq;
    StringBuilder sbGpuExt = new StringBuilder();
    ImageView hardSocFtIc, hardDisFtIc, hardMemFtIc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware);

        CrashReporter.startCrashThread(this);

        numCores = Runtime.getRuntime().availableProcessors();
        int textViewHeight = getResources().getDimensionPixelSize(R.dimen.textview_height);
        int textViewWidth = getResources().getDimensionPixelSize(R.dimen.textview_width);

        hardSocFtName = findViewById(R.id.hardSocFtName);
        hardSocFtIc = findViewById(R.id.hardSocFtIc);
        hardSocManufacturer = findViewById(R.id.hardSocManufacturer);
        hardSocModel = findViewById(R.id.hardSocModel);
        hardSocArchitecture = findViewById(R.id.hardSocArchitecture);
        hardSocClockSpeed = findViewById(R.id.hardSocClockSpeed);
        hardSocCpuCores = findViewById(R.id.hardSocCpuCores);
        hardSocGpuVendor = findViewById(R.id.hardSocGpuVendor);
        hardSocGpuRenderer = findViewById(R.id.hardSocGpuRenderer);
        hardSocGpuVersion = findViewById(R.id.hardSocGpuVersion);
        hardSocGpuExtension = findViewById(R.id.hardSocGpuExtension);
        hardSocBtnMore = findViewById(R.id.hardSocBtnMore);
        hardDisFtName = findViewById(R.id.hardDisFtName);
        hardDisFtIc = findViewById(R.id.hardDisFtIc);
        hardDisScreenSize = findViewById(R.id.hardDisScreenSize);
        hardDisResolution = findViewById(R.id.hardDisResolution);
        hardDisDensityDpi = findViewById(R.id.hardDisDensityDpi);
        hardDisRefreshRate = findViewById(R.id.hardDisRefreshRate);
        hardDisHdrSupport = findViewById(R.id.hardDisHdrSupport);
        hardDisWideColorGamut = findViewById(R.id.hardDisWideColorGamut);
        hardDisLuminance = findViewById(R.id.hardDisLuminance);
        hardDisPixelPerInch = findViewById(R.id.hardDisPixelPerInch);
        hardMemFtIc = findViewById(R.id.hardMemFtIc);
        hardMemFtName = findViewById(R.id.hardMemFtName);
        hardRamSize = findViewById(R.id.hardRamSize);
        hardRamOccupied = findViewById(R.id.hardRamOccupied);
        hardRamAvailable = findViewById(R.id.hardRamAvailable);
        hardStoSize = findViewById(R.id.hardStoSize);
        hardStoOccupied = findViewById(R.id.hardStoOccupied);
        hardStoAvailable = findViewById(R.id.hardStoAvailable);
        hardSocCoreLiveFreq = findViewById(R.id.hardSocCoreLiveFreq);

        SetHeadDetails setHeadDetails = new SetHeadDetails();
        setHeadDetails.ofHardSoc(hardSocFtName, hardSocFtIc);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hardSocManufacturer.setText(Build.SOC_MANUFACTURER);
            hardSocModel.setText(Build.SOC_MODEL);
        }
        String[] arch = Build.SUPPORTED_ABIS;
        if (arch.length == 1){
            hardSocArchitecture.setText(String.valueOf(arch[0]));
        } else if (arch.length == 2) {
            hardSocArchitecture.setText(String.valueOf(arch[0] +", "+ arch[1]));
        } else if (arch.length == 3) {
            hardSocArchitecture.setText(String.valueOf(arch[0] +", "+ arch[1] +", "+ arch[2]));
        }
        hardSocClockSpeed.setText(String.format(Locale.US, "%.1f", getClockSpeed()) + "Ghz");
        hardSocCpuCores.setText(String.valueOf(numCores));
        hardSocGpuExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGPUExtensions();
            }
        });
        hardSocBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCPUInfo();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                textViewWidth,
                textViewHeight
        );
        coreName = new TextView[numCores];

        for (int i = 0; i < numCores; i += 2) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            coreName[i] = createTextView(params, rowLayout);
            if (i + 1 < numCores) {
                coreName[i + 1] = createTextView(params, rowLayout);
            }
            hardSocCoreLiveFreq.addView(rowLayout);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCpuUsage();
            }
        }, 0, 1000);

        GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface_view);
        glSurfaceView.setRenderer(HardwareActivity.this);

        // Display
        DisplayMetrics displayMetrics = new DisplayMetrics();
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Rect bounds;
        int width;
        int height;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getMaximumWindowMetrics();
            bounds = windowMetrics.getBounds();
        } else {
            displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
            bounds = new Rect(0, 0, width, height);
        }

        display.getRealMetrics(displayMetrics);
        String displayName = display.getName();

        width = bounds.width();
        height = bounds.height();

        float widthInches = displayMetrics.widthPixels / displayMetrics.xdpi;
        float heightInches = displayMetrics.heightPixels / displayMetrics.ydpi;
        double sizeInch = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));

        setHeadDetails.ofHardDis(hardDisFtIc, hardDisFtName, displayName);

        hardDisScreenSize.setText(String.format(Locale.US,"%.1f", sizeInch) + " In");
        hardDisResolution.setText(String.valueOf(width +" x "+ height));
        hardDisDensityDpi.setText(String.valueOf(displayMetrics.densityDpi));
        hardDisRefreshRate.setText(String.valueOf((int) display.getRefreshRate() + "Hz"));
        if (display.isHdr()){
            hardDisHdrSupport.setText(R.string.supported);
        } else {
            hardDisHdrSupport.setText(R.string.not_supported);
        }
        if (display.isWideColorGamut()){
            hardDisWideColorGamut.setText(R.string.supported);
        } else {
            hardDisWideColorGamut.setText(R.string.not_supported);
        }
        int minLum = (int) display.getHdrCapabilities().getDesiredMinLuminance();
        int maxLum = (int) display.getHdrCapabilities().getDesiredMaxLuminance();
        hardDisLuminance.setText(String.valueOf("Min: "+ minLum +" Max: "+ maxLum));
        int pixX = (int) displayMetrics.xdpi;
        int pixY = (int) displayMetrics.ydpi;
        hardDisPixelPerInch.setText(String.valueOf("X: "+ pixX +" Y: "+ pixY));

        // Memory
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        StatFs statFs = new StatFs(storagePath);

        long tRam = memoryInfo.totalMem;
        long aRam = memoryInfo.availMem;
        long uRam = tRam - aRam;
        float aRamInPer = ((float) aRam / tRam) * 100;

        long tSto = statFs.getTotalBytes();
        long aSto = statFs.getAvailableBytes();
        long uSto = tSto - aSto;
        float aStoInPer = ((float) aSto / tSto) * 100;

        setHeadDetails.ofHardMem(hardMemFtIc, hardMemFtName);

        hardRamSize.setText(formatSize(tRam));
        hardRamOccupied.setText(formatSize(uRam));
        hardRamAvailable.setText(formatSize(aRam) + " - " + String.format(Locale.US, "%.2f", aRamInPer) + "%");
        hardStoSize.setText(formatSize(tSto));
        hardStoOccupied.setText(formatSize(uSto));
        hardStoAvailable.setText(formatSize(aSto) + " - " + String.format(Locale.US, "%.2f", aStoInPer) + "%");

    }

    private void displayGPUExtensions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HardwareActivity.this)
                .setTitle("Extension Information")
                .setMessage(sbGpuExt)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void displayCPUInfo() {
        StringBuilder cpuInfoText = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line;
            while ((line = br.readLine()) != null) {
                cpuInfoText.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("TAG", "Error reading CPU info: " + e.getMessage());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Core Information")
                .setMessage(cpuInfoText.toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private TextView createTextView(LinearLayout.LayoutParams params, LinearLayout layout) {
        TextView textView = new TextView(this);
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER | Gravity.START);
        textView.setLayoutParams(params);
        layout.addView(textView);
        return textView;
    }


    private void updateCpuUsage() {
        for (int i = 0; i < numCores; i++) {
            coreName[i].setText(String.valueOf("Core " + (i+1) + ": " + getCpuFrequency(i)));
        }
    }

    private int getCpuFrequency(int coreIndex) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/scaling_cur_freq"));
            String line = br.readLine();
            br.close();
            return Integer.parseInt(line) / 1000;
        } catch (IOException | NumberFormatException e) {
            Log.d("getCpuFreqTag", "getCpuFrequency: " + e);
            return -1;
        }
    }

    private double getClockSpeed(){
        int cs = 0;
        int core = Runtime.getRuntime().availableProcessors();
        for (int i=0; i<core; i++){
            if (i == core-1){
                try {
                    BufferedReader br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq"));
                    String line = br.readLine();
                    cs += Integer.parseInt(line);
                    br.close();
                } catch (IOException | NumberFormatException e) {
                    Log.d("getClockSpeedTag", "getClockSpeed: " + e);
                }
            }
        }
        return (double) cs/1000/1000;
    }

    public static String formatSize(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format(Locale.US, "%.1f %cB", bytes / 1000.0, ci.current());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        hardSocGpuVendor.setText(gl.glGetString(GL10.GL_VENDOR));
        hardSocGpuRenderer.setText(gl.glGetString(GL10.GL_RENDERER));
        hardSocGpuVersion.setText(gl.glGetString(GL10.GL_VERSION));
        String ext = gl.glGetString(GL10.GL_EXTENSIONS);
        if (ext != null) {
            String[] stringsArr = ext.split(" ");
            for (String string : stringsArr) {
                sbGpuExt.append(string).append("\n");
            }
        } else {
            sbGpuExt.append("Not Found!");
        }
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}