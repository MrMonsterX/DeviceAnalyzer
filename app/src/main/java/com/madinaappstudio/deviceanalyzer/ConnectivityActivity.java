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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.madinaappstudio.deviceanalyzer.databinding.ActivityConnectivityBinding;

import java.util.List;

public class ConnectivityActivity extends AppCompatActivity {

    private static final int PHONE_PERMISSION_REQUEST_CODE = 1001;
    private static final String PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    private ActivityConnectivityBinding binding;
    TelephonyManager telephonyManager;
    SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CrashReporter.startCrashThread(this);

        setBasicDetails();
        setBluetoothInfo();
        checkLocationPermission();
    }

    private void setBasicDetails() {
        int data = SubscriptionManager.getDefaultDataSubscriptionId();
        int voice = SubscriptionManager.getDefaultVoiceSubscriptionId();
        int sms = SubscriptionManager.getDefaultSmsSubscriptionId();

        if (data == 1){
            binding.conCellBasData.setText(R.string.sim1);
        } else if (data == 2) {
            binding.conCellBasData.setText(R.string.sim2);
        } else {
            binding.conCellBasData.setText(R.string.not_specified);
        }
        if (voice == 1){
            binding.conCellBasVoice.setText(R.string.sim1);
        } else if (voice == 2) {
            binding.conCellBasVoice.setText(R.string.sim2);
        } else {
            binding.conCellBasVoice.setText(R.string.not_specified);
        }
        if (sms == 1){
            binding.conCellBasSms.setText(R.string.sim1);
        } else if (sms == 2) {
            binding.conCellBasSms.setText(R.string.sim2);
        } else {
            binding.conCellBasSms.setText(R.string.not_specified);
        }
    }

    private void setBluetoothInfo() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        PackageManager packageManager = getPackageManager();
        boolean isBlueLe = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        if (bluetoothAdapter != null){
            if (isBlueLe){
                binding.conBlueLe.setText(R.string.supported);
            } else {
                binding.conBlueLe.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isMultipleAdvertisementSupported()){
                binding.conBlueLeMultiAds.setText(R.string.supported);
            } else {
                binding.conBlueLeMultiAds.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLe2MPhySupported()){
                binding.conBlueLe2MPhy.setText(R.string.supported);
            } else {
                binding.conBlueLe2MPhy.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLeCodedPhySupported()){
                binding.conBlueLeCodedPhy.setText(R.string.supported);
            } else {
                binding.conBlueLeCodedPhy.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLeExtendedAdvertisingSupported()){
                binding.conBlueLeExtendAds.setText(R.string.supported);
            } else {
                binding.conBlueLeExtendAds.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isLePeriodicAdvertisingSupported()){
                binding.conBlueLePeriodicAds.setText(R.string.supported);
            } else {
                binding.conBlueLePeriodicAds.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isOffloadedFilteringSupported()){
                binding.conBlueOffloadFilter.setText(R.string.supported);
            } else {
                binding.conBlueOffloadFilter.setText(R.string.not_supported);
            }

            if (bluetoothAdapter.isOffloadedScanBatchingSupported()){
                binding.conBlueOffloadBatching.setText(R.string.supported);
            } else {
                binding.conBlueOffloadBatching.setText(R.string.not_supported);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (bluetoothAdapter.isLeAudioSupported() == BluetoothStatusCodes.FEATURE_SUPPORTED){
                    binding.conBlueLeAudio.setText(R.string.supported);
                } else {
                    binding.conBlueLeAudio.setText(R.string.not_supported);
                }
            } else {
                binding.conBlueLeAudio.setText(R.string.not_supported);
            }
        }
    }

    private void setSimCardDetails(){
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        @SuppressLint("MissingPermission") List<SubscriptionInfo> infoList = subscriptionManager.getActiveSubscriptionInfoList();
        int slotCount = telephonyManager.getPhoneCount();
        if (slotCount == 2){
            binding.conCellBasDualSim.setText(R.string.yes);
        } else {
            binding.conCellBasDualSim.setText(R.string.no);
        }
        binding.conCellBasPhoneType.setText(getPhoneTypeName());
        if (slotCount == 1){
            if (telephonyManager.getSimState(0) == TelephonyManager.SIM_STATE_READY){
                binding.conCellSim1LinLayout.setVisibility(View.VISIBLE);
                binding.conCellSim1State.setText(R.string.ready);
            } else {
                binding.conCellSim1State.setText(R.string.not_available);
            }
        } else if (slotCount == 2) {
            boolean sim1Ready = false;
            boolean sim2Ready = false;

            if (infoList != null){
                for (SubscriptionInfo subInfo: infoList){
                    int slotIndex = subInfo.getSimSlotIndex();
                    if (slotIndex == 0 && telephonyManager.getSimState(0) == TelephonyManager.SIM_STATE_READY) {
                        binding.conCellSim1LinLayout.setVisibility(View.VISIBLE);
                        binding.conCellSim1State.setText(R.string.ready);
                        binding.conCellSim1OpName.setText(subInfo.getCarrierName().toString().toUpperCase());
                        binding.conCellSim1OpCode.setText(String.valueOf(subInfo.getMcc()) + String.valueOf(subInfo.getMnc()));
                        binding.conCellSim1Country.setText(subInfo.getCountryIso().toUpperCase());
                        int isRoaming = subInfo.getDataRoaming();
                        if (isRoaming != 0){
                            binding.conCellSim1Roaming.setText(R.string.yes);
                        } else {
                            binding.conCellSim1Roaming.setText(R.string.no);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (subInfo.isEmbedded()){
                                binding.conSim1ESim.setText(R.string.yes);
                            } else {
                                binding.conSim1ESim.setText(R.string.no);
                            }
                        } else {
                            binding.conSim1ESim.setText(R.string.no);
                        }
                        binding.conCellSim1Mcc.setText(String.valueOf(subInfo.getMcc()));
                        binding.conCellSim1Mnc.setText(String.valueOf(subInfo.getMnc()));
                        @SuppressLint("MissingPermission") int networkType = telephonyManager.getDataNetworkType();
                        binding.conCellSim1Technology.setText(getSimTech(networkType));
                        sim1Ready = true;
                    }

                    if (slotIndex == 1 && telephonyManager.getSimState(1) == TelephonyManager.SIM_STATE_READY) {
                        binding.conCellSim2LinLayout.setVisibility(View.VISIBLE);
                        binding.conCellSim2State.setText(R.string.ready);
                        binding.conCellSim2OpName.setText(subInfo.getCarrierName().toString().toUpperCase());
                        binding.conCellSim2OpCode.setText(String.valueOf(subInfo.getMcc()) + String.valueOf(subInfo.getMnc()));
                        binding.conCellSim2Country.setText(subInfo.getCountryIso().toUpperCase());
                        int isRoaming = subInfo.getDataRoaming();
                        if (isRoaming != 0){
                            binding.conCellSim2Roaming.setText(R.string.yes);
                        } else {
                            binding.conCellSim2Roaming.setText(R.string.no);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (subInfo.isEmbedded()){
                                binding.conSim2ESim.setText(R.string.yes);
                            } else {
                                binding.conSim2ESim.setText(R.string.no);
                            }
                        } else {
                            binding.conSim2ESim.setText(R.string.no);
                        }
                        binding.conCellSim2Mcc.setText(String.valueOf(subInfo.getMcc()));
                        binding.conCellSim2Mnc.setText(String.valueOf(subInfo.getMnc()));
                        @SuppressLint("MissingPermission") int networkType = telephonyManager.getDataNetworkType();
                        binding.conCellSim2Technology.setText(getSimTech(networkType));
                        sim2Ready = true;
                    }
                }
            }

            if (!sim1Ready){
                binding.conCellSim1State.setText(R.string.not_available);
            }
            if (!sim2Ready){
                binding.conCellSim2State.setText(R.string.not_available);
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
        binding.conCellGPCardView.setVisibility(View.GONE);
        binding.conCellSim1CardView.setVisibility(View.VISIBLE);
        binding.conCellSim2CardView.setVisibility(View.VISIBLE);
        binding.conCellBasCardView.setVisibility(View.VISIBLE);
    }

    private void showPermissionRequestUI() {
        binding.conCellSim1CardView.setVisibility(View.GONE);
        binding.conCellSim2CardView.setVisibility(View.GONE);
        binding.conCellBasCardView.setVisibility(View.GONE);
        binding.conCellGPCardView.setVisibility(View.VISIBLE);
        binding.conCellGPCardView.setOnClickListener(new View.OnClickListener() {
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

