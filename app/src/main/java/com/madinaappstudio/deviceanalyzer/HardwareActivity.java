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

import com.madinaappstudio.deviceanalyzer.databinding.ActivityHardwareBinding;

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
    
    private int numCores;
    TextView[] coreName;
    StringBuilder sbGpuExt = new StringBuilder();
    ActivityHardwareBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHardwareBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CrashReporter.startCrashThread(this);

        numCores = Runtime.getRuntime().availableProcessors();
        int textViewHeight = getResources().getDimensionPixelSize(R.dimen.textview_height);
        int textViewWidth = getResources().getDimensionPixelSize(R.dimen.textview_width);

        SetHeadDetails setHeadDetails = new SetHeadDetails();
        setHeadDetails.ofHardSoc(binding.hardSocFtName, binding.hardSocFtIc);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.hardSocManufacturer.setText(Build.SOC_MANUFACTURER);
            binding.hardSocModel.setText(Build.SOC_MODEL);
        }
        String[] arch = Build.SUPPORTED_ABIS;
        if (arch.length == 1){
            binding.hardSocArchitecture.setText(String.valueOf(arch[0]));
        } else if (arch.length == 2) {
            binding.hardSocArchitecture.setText(String.valueOf(arch[0] +", "+ arch[1]));
        } else if (arch.length == 3) {
            binding.hardSocArchitecture.setText(String.valueOf(arch[0] +", "+ arch[1] +", "+ arch[2]));
        }
        binding.hardSocClockSpeed.setText(String.format(Locale.US, "%.1f", getClockSpeed()) + "Ghz");
        binding.hardSocCpuCores.setText(String.valueOf(numCores));
        binding.hardSocGpuExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGPUExtensions();
            }
        });
        binding.hardSocBtnMore.setOnClickListener(new View.OnClickListener() {
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
            binding.hardSocCoreLiveFreq.addView(rowLayout);
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

        setHeadDetails.ofHardDis(binding.hardDisFtIc, binding.hardDisFtName, displayName);

        binding.hardDisScreenSize.setText(String.format(Locale.US,"%.1f", sizeInch) + " In");
        binding.hardDisResolution.setText(String.valueOf(width +" x "+ height));
        binding.hardDisDensityDpi.setText(String.valueOf(displayMetrics.densityDpi));
        binding.hardDisRefreshRate.setText(String.valueOf((int) display.getRefreshRate() + "Hz"));
        if (display.isHdr()){
            binding.hardDisHdrSupport.setText(R.string.supported);
        } else {
            binding.hardDisHdrSupport.setText(R.string.not_supported);
        }
        if (display.isWideColorGamut()){
            binding.hardDisWideColorGamut.setText(R.string.supported);
        } else {
            binding.hardDisWideColorGamut.setText(R.string.not_supported);
        }
        int minLum = (int) display.getHdrCapabilities().getDesiredMinLuminance();
        int maxLum = (int) display.getHdrCapabilities().getDesiredMaxLuminance();
        binding.hardDisLuminance.setText(String.valueOf("Min: "+ minLum +" Max: "+ maxLum));
        int pixX = (int) displayMetrics.xdpi;
        int pixY = (int) displayMetrics.ydpi;
        binding.hardDisPixelPerInch.setText(String.valueOf("X: "+ pixX +" Y: "+ pixY));

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

        setHeadDetails.ofHardMem(binding.hardMemFtIc, binding.hardMemFtName);

        binding.hardRamSize.setText(formatSize(tRam));
        binding.hardRamOccupied.setText(formatSize(uRam));
        binding.hardRamAvailable.setText(formatSize(aRam) + " - " + String.format(Locale.US, "%.2f", aRamInPer) + "%");
        binding.hardStoSize.setText(formatSize(tSto));
        binding.hardStoOccupied.setText(formatSize(uSto));
        binding.hardStoAvailable.setText(formatSize(aSto) + " - " + String.format(Locale.US, "%.2f", aStoInPer) + "%");

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
        binding.hardSocGpuVendor.setText(gl.glGetString(GL10.GL_VENDOR));
        binding.hardSocGpuRenderer.setText(gl.glGetString(GL10.GL_RENDERER));
        binding.hardSocGpuVersion.setText(gl.glGetString(GL10.GL_VERSION));
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