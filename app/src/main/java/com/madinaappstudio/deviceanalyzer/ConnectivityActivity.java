package com.madinaappstudio.deviceanalyzer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothStatusCodes;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class ConnectivityActivity extends AppCompatActivity {

    private static final int PHONE_PERMISSION_REQUEST_CODE = 1001;
    private static final String PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE;

    TextView conCellBasDualSim, conCellBasPhoneType, conCellBasData, conCellBasVoice, conCellBasSms, conCellSim1State,
            conCellSim1OpName, conCellSim1OpCode, conCellSim1Country, conSim1ESim, conCellSim1Roaming, conCellSim1Technology, conCellSim1Mcc,
            conCellSim1Mnc, conCellSim2State, conCellSim2OpName, conCellSim2OpCode, conCellSim2Country, conCellSim2Roaming, conSim2ESim,
            conCellSim2Technology, conCellSim2Mcc, conCellSim2Mnc, conBlueLe, conBlueLeMultiAds, conBlueLe2MPhy, conBlueLeCodedPhy, conBlueLeExtendAds,
            conBlueLePeriodicAds, conBlueOffloadFilter, conBlueOffloadBatching, conBlueLeAudio;

    LinearLayout conCellSim1LinLayout, conCellSim2LinLayout;
    CardView conCellSim1CardView, conCellSim2CardView, conCellGPCardView, conCellBasCardView;
    TelephonyManager telephonyManager;
    SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity);

        CrashReporter.startCrashThread(this);

        conCellBasDualSim = findViewById(R.id.conCellBasDualSim);
        conCellBasPhoneType = findViewById(R.id.conCellBasPhoneType);
        conCellBasData = findViewById(R.id.conCellBasData);
        conCellBasVoice = findViewById(R.id.conCellBasVoice);
        conCellBasSms = findViewById(R.id.conCellBasSms);
        conCellSim1State = findViewById(R.id.conCellSim1State);
        conCellSim1OpName = findViewById(R.id.conCellSim1OpName);
        conCellSim1OpCode = findViewById(R.id.conCellSim1OpCode);
        conCellSim1Country = findViewById(R.id.conCellSim1Country);
        conCellSim1Roaming = findViewById(R.id.conCellSim1Roaming);
        conCellSim1Technology = findViewById(R.id.conCellSim1Technology);
        conSim1ESim = findViewById(R.id.conSim1ESim);
        conCellSim1Mcc = findViewById(R.id.conCellSim1Mcc);
        conCellSim1Mnc = findViewById(R.id.conCellSim1Mnc);
        conCellSim2State = findViewById(R.id.conCellSim2State);
        conCellSim2OpName = findViewById(R.id.conCellSim2OpName);
        conCellSim2OpCode = findViewById(R.id.conCellSim2OpCode);
        conCellSim2Country = findViewById(R.id.conCellSim2Country);
        conCellSim2Roaming = findViewById(R.id.conCellSim2Roaming);
        conCellSim2Technology = findViewById(R.id.conCellSim2Technology);
        conSim2ESim = findViewById(R.id.conSim2ESim);
        conCellSim2Mcc = findViewById(R.id.conCellSim2Mcc);
        conCellSim2Mnc = findViewById(R.id.conCellSim2Mnc);
        conBlueLe = findViewById(R.id.conBlueLe);
        conBlueLeMultiAds = findViewById(R.id.conBlueLeMultiAds);
        conBlueLe2MPhy = findViewById(R.id.conBlueLe2MPhy);
        conBlueLeCodedPhy = findViewById(R.id.conBlueLeCodedPhy);
        conBlueLeExtendAds = findViewById(R.id.conBlueLeExtendAds);
        conBlueLePeriodicAds = findViewById(R.id.conBlueLePeriodicAds);
        conBlueOffloadFilter = findViewById(R.id.conBlueOffloadFilter);
        conBlueOffloadBatching = findViewById(R.id.conBlueOffloadBatching);
        conBlueLeAudio = findViewById(R.id.conBlueLeAudio);

        conCellSim1LinLayout = findViewById(R.id.conCellSim1LinLayout);
        conCellSim2LinLayout = findViewById(R.id.conCellSim2LinLayout);
        conCellSim1CardView = findViewById(R.id.conCellSim1CardView);
        conCellSim2CardView = findViewById(R.id.conCellSim2CardView);
        conCellGPCardView = findViewById(R.id.conCellGPCardView);
        conCellBasCardView = findViewById(R.id.conCellBasCardView);

        setBasicDetails();
        setBluetoothInfo();
        checkLocationPermission();
    }

    private void setBasicDetails() {
        int data = SubscriptionManager.getDefaultDataSubscriptionId();
        int voice = SubscriptionManager.getDefaultVoiceSubscriptionId();
        int sms = SubscriptionManager.getDefaultSmsSubscriptionId();

        if (data == 1){
            conCellBasData.setText(R.string.sim1);
        } else if (data == 2) {
            conCellBasData.setText(R.string.sim2);
        } else {
            conCellBasData.setText(R.string.not_specified);
        }
        if (voice == 1){
            conCellBasVoice.setText(R.string.sim1);
        } else if (voice == 2) {
            conCellBasVoice.setText(R.string.sim2);
        } else {
            conCellBasVoice.setText(R.string.not_specified);
        }
        if (sms == 1){
            conCellBasSms.setText(R.string.sim1);
        } else if (sms == 2) {
            conCellBasSms.setText(R.string.sim2);
        } else {
            conCellBasSms.setText(R.string.not_specified);
        }
    }

    private void setBluetoothInfo() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        PackageManager packageManager = getPackageManager();
        boolean isBlueLe = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        if (bluetoothAdapter != null){
            if (isBlueLe){
                conBlueLe.setText(R.string.supported);
            } else {
                conBlueLe.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isMultipleAdvertisementSupported()){
                conBlueLeMultiAds.setText(R.string.supported);
            } else {
                conBlueLeMultiAds.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLe2MPhySupported()){
                conBlueLe2MPhy.setText(R.string.supported);
            } else {
                conBlueLe2MPhy.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLeCodedPhySupported()){
                conBlueLeCodedPhy.setText(R.string.supported);
            } else {
                conBlueLeCodedPhy.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLeExtendedAdvertisingSupported()){
                conBlueLeExtendAds.setText(R.string.supported);
            } else {
                conBlueLeExtendAds.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLePeriodicAdvertisingSupported()){
                conBlueLePeriodicAds.setText(R.string.supported);
            } else {
                conBlueLePeriodicAds.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isOffloadedFilteringSupported()){
                conBlueOffloadFilter.setText(R.string.supported);
            } else {
                conBlueOffloadFilter.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isOffloadedScanBatchingSupported()){
                conBlueOffloadBatching.setText(R.string.supported);
            } else {
                conBlueOffloadBatching.setText(R.string.not_supported);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (bluetoothAdapter.isLeAudioSupported() == BluetoothStatusCodes.FEATURE_SUPPORTED){
                    conBlueLeAudio.setText(R.string.supported);
                } else {
                    conBlueLeAudio.setText(R.string.not_supported);
                }
            } else {
                conBlueLeAudio.setText(R.string.not_supported);
            }
        }
    }

    private void setSimCardDetails(){
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        @SuppressLint("MissingPermission") List<SubscriptionInfo> infoList = subscriptionManager.getActiveSubscriptionInfoList();
        int slotCount = telephonyManager.getPhoneCount();
        if (slotCount == 2){
            conCellBasDualSim.setText(R.string.yes);
        } else {
            conCellBasDualSim.setText(R.string.no);
        }
        conCellBasPhoneType.setText(getPhoneTypeName());
        if (slotCount == 1){
            if (telephonyManager.getSimState(0) == TelephonyManager.SIM_STATE_READY){
                conCellSim1LinLayout.setVisibility(View.VISIBLE);
                conCellSim1State.setText(R.string.ready);
            } else {
                conCellSim1State.setText(R.string.not_available);
            }
        } else if (slotCount == 2) {
            boolean sim1Ready = false;
            boolean sim2Ready = false;

            if (infoList != null){
                for (SubscriptionInfo subInfo: infoList){
                    int slotIndex = subInfo.getSimSlotIndex();
                    if (slotIndex == 0 && telephonyManager.getSimState(0) == TelephonyManager.SIM_STATE_READY) {
                        conCellSim1LinLayout.setVisibility(View.VISIBLE);
                        conCellSim1State.setText(R.string.ready);
                        conCellSim1OpName.setText(subInfo.getCarrierName().toString().toUpperCase());
                        conCellSim1OpCode.setText(String.valueOf(subInfo.getMcc()) + String.valueOf(subInfo.getMnc()));
                        conCellSim1Country.setText(subInfo.getCountryIso().toUpperCase());
                        int isRoaming = subInfo.getDataRoaming();
                        if (isRoaming != 0){
                            conCellSim1Roaming.setText(R.string.yes);
                        } else {
                            conCellSim1Roaming.setText(R.string.no);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (subInfo.isEmbedded()){
                                conSim1ESim.setText(R.string.yes);
                            } else {
                                conSim1ESim.setText(R.string.no);
                            }
                        } else {
                            conSim1ESim.setText(R.string.no);
                        }
                        conCellSim1Mcc.setText(String.valueOf(subInfo.getMcc()));
                        conCellSim1Mnc.setText(String.valueOf(subInfo.getMnc()));
                        @SuppressLint("MissingPermission") int networkType = telephonyManager.getDataNetworkType();
                        conCellSim1Technology.setText(getSimTech(networkType));
                        sim1Ready = true;
                    }

                    if (slotIndex == 1 && telephonyManager.getSimState(1) == TelephonyManager.SIM_STATE_READY) {
                        conCellSim2LinLayout.setVisibility(View.VISIBLE);
                        conCellSim2State.setText(R.string.ready);
                        conCellSim2OpName.setText(subInfo.getCarrierName().toString().toUpperCase());
                        conCellSim2OpCode.setText(String.valueOf(subInfo.getMcc()) + String.valueOf(subInfo.getMnc()));
                        conCellSim2Country.setText(subInfo.getCountryIso().toUpperCase());
                        int isRoaming = subInfo.getDataRoaming();
                        if (isRoaming != 0){
                            conCellSim2Roaming.setText(R.string.yes);
                        } else {
                            conCellSim2Roaming.setText(R.string.no);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (subInfo.isEmbedded()){
                                conSim2ESim.setText(R.string.yes);
                            } else {
                                conSim2ESim.setText(R.string.no);
                            }
                        } else {
                            conSim2ESim.setText(R.string.no);
                        }
                        conCellSim2Mcc.setText(String.valueOf(subInfo.getMcc()));
                        conCellSim2Mnc.setText(String.valueOf(subInfo.getMnc()));
                        @SuppressLint("MissingPermission") int networkType = telephonyManager.getDataNetworkType();
                        conCellSim2Technology.setText(getSimTech(networkType));
                        sim2Ready = true;
                    }
                }
            }

            if (!sim1Ready){
                conCellSim1State.setText(R.string.not_available);
            }
            if (!sim2Ready){
                conCellSim2State.setText(R.string.not_available);
            }
        }
    }

    private String getSimTech(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Unknown";
        }
    }

    private String getPhoneTypeName(){
        int typeInt = telephonyManager.getPhoneType();
        switch (typeInt){
            case 0:
                return "None";
            case 1:
                return "GSM";
            case 2:
                return "CDMA";
            case 3:
                return "SIP";
            default:
                return "Unknown";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (hasLocationPermission()) {
            showPermissionGrantedUI();
        } else {
            showPermissionRequestUI();
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, PHONE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionGrantedUI() {
        setSimCardDetails();
        conCellGPCardView.setVisibility(View.GONE);
        conCellSim1CardView.setVisibility(View.VISIBLE);
        conCellSim2CardView.setVisibility(View.VISIBLE);
        conCellBasCardView.setVisibility(View.VISIBLE);
    }

    private void showPermissionRequestUI() {
        conCellSim1CardView.setVisibility(View.GONE);
        conCellSim2CardView.setVisibility(View.GONE);
        conCellBasCardView.setVisibility(View.GONE);
        conCellGPCardView.setVisibility(View.VISIBLE);
        conCellGPCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermission();
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{PHONE_PERMISSION}, PHONE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPermissionGrantedUI();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required!")
                .setMessage("Oops! It seems like you've denied the permission. Without this permission, certain features may not work as intended. Please allow manually from settings.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToSettings();
                    }
                })
                .show();
    }

    private void goToSettings() {
        Intent iSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        iSetting.setData(uri);
        startActivity(iSetting);
    }

}

