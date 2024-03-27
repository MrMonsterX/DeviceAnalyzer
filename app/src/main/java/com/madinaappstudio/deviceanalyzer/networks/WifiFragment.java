package com.madinaappstudio.deviceanalyzer.networks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;
import com.madinaappstudio.deviceanalyzer.databinding.FragmentWifiBinding;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;

public class WifiFragment extends Fragment {
    Context context;
    TextView wifiPubIpAddress;
    Button wifiPubIpAddressOk;
    ApiCalling apiCalling;
    Dialog dialog;
    FragmentWifiBinding binding;

    public WifiFragment() {}

    public WifiFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWifiBinding.inflate(inflater, container, false);
        View view =  binding.getRoot();

        CrashReporter.startCrashThread(context);

        getWifiInfo();

        apiCalling = new ApiCalling();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_publicip);
        wifiPubIpAddress = dialog.findViewById(R.id.pubIpAddress);
        wifiPubIpAddressOk = dialog.findViewById(R.id.pubIpAddressOk);
        wifiPubIpAddressOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return view;
    }
    private void getWifiInfo(){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInternetAvailable()) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.netWifiHeadStatus.setText(R.string.inter_access);
                            binding.netWifiHeadIc.setImageResource(R.drawable.ic_wifi_on_24px);
                            binding.netWifiLayoutPubIp.setVisibility(View.VISIBLE);
                            binding.netWifiPublicIp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    apiCalling.getPublicIpAddress(new ApiCalling.IpAddressListener() {
                                        @Override
                                        public void onSuccess(String ipAddress) {
                                            wifiPubIpAddress.setText(ipAddress);
                                            dialog.show();
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        }

                                        @Override
                                        public void onError() {
                                            wifiPubIpAddress.setText(R.string.unable_to_fetch);
                                            dialog.show();
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        }
                                    });
                                }
                            });

                        }
                    });
                } else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.netWifiHeadStatus.setText(R.string.no_inter_access);
                            binding.netWifiHeadIc.setImageResource(R.drawable.ic_wifi_off_24px);
                            binding.netWifiLayoutPubIp.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();

        if (linkProperties != null){
            for (LinkAddress linkAddress: linkProperties.getLinkAddresses()){
                if (linkAddress.getAddress() instanceof Inet4Address){
                    binding.netWifiIpv4Address.setText(linkAddress.getAddress().getHostAddress());
                }
                if (linkAddress.getAddress() instanceof Inet6Address){
                    binding.netWifiIpv6Address.setText(linkAddress.getAddress().getHostAddress());
                }
            }
            binding.netWifiInterface.setText(linkProperties.getInterfaceName());
        } else {
            binding.netWifiInterface.setText(R.string.not_found);
        }
        int gateway = wifiManager.getDhcpInfo().gateway;
        int subnet = wifiManager.getDhcpInfo().netmask;
        int leaseDuration = wifiManager.getDhcpInfo().leaseDuration;

        binding.netWifiGateway.setText(formatAddress(gateway));
        if (subnet <= 0){
            Log.d("TAG", "getWifiInfo: " + subnet);
            binding.netWifiSubnetMask.setText(formatSubnet(subnet));
        } else {
            binding.netWifiSubnetMask.setText(formatSubnet(24));
            Log.d("TAG", "getWifiInfo: " + subnet);
        }

        ArrayList<String> dnsList = new ArrayList<>();
        assert linkProperties != null;
        for (InetAddress inetAddress : linkProperties.getDnsServers()) {
            dnsList.add(inetAddress.getHostAddress());
        }
        if (dnsList.size() == 1) {
            binding.netWifiDNS1.setText(dnsList.get(0));
            binding.netWifiLayoutDNS1.setVisibility(View.VISIBLE);
        } else if (dnsList.size() == 2) {
            binding.netWifiDNS1.setText(dnsList.get(0));
            binding.netWifiDNS2.setText(dnsList.get(1));
            binding.netWifiLayoutDNS1.setVisibility(View.VISIBLE);
            binding.netWifiLayoutDNS2.setVisibility(View.VISIBLE);
        } else if (dnsList.size() == 4) {
            binding.netWifiDNS1.setText(dnsList.get(0));
            binding.netWifiDNS2.setText(dnsList.get(1));
            binding.netWifiDNS3.setText(dnsList.get(2));
            binding.netWifiDNS4.setText(dnsList.get(3));
            binding.netWifiLayoutDNS1.setVisibility(View.VISIBLE);
            binding.netWifiLayoutDNS2.setVisibility(View.VISIBLE);
            binding.netWifiLayoutDNS3.setVisibility(View.VISIBLE);
            binding.netWifiLayoutDNS4.setVisibility(View.VISIBLE);
        }

        binding.netWifiLeaseDuration.setText(leaseDuration + " Seconds");

        binding.netWifiLinkSpeed.setText(wifiInfo.getLinkSpeed() +" "+ WifiInfo.LINK_SPEED_UNITS);
        binding.netWifiFrequency.setText(wifiInfo.getFrequency() +" "+ WifiInfo.FREQUENCY_UNITS);
        binding.netWifiStandard.setText(getWifiStandard(wifiInfo));
        if (wifiManager.is5GHzBandSupported()){
            binding.netWifi5GhzSupported.setText(R.string.yes);
        } else {
            binding.netWifi5GhzSupported.setText(R.string.no);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (wifiManager.is6GHzBandSupported()){
                binding.netWifi6GhzSupported.setText(R.string.yes);
            } else {
                binding.netWifi6GhzSupported.setText(R.string.no);
            }
        }


    }

    private static String getWifiStandard(WifiInfo wifiInfo){
        String wifiStandard;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            int standard = wifiInfo.getWifiStandard();
            if (standard == ScanResult.WIFI_STANDARD_LEGACY) {
                wifiStandard = "802.11a/b/g";
            } else if (standard == ScanResult.WIFI_STANDARD_11N) {
                wifiStandard = "802.11n";
            } else if (standard == ScanResult.WIFI_STANDARD_11AC) {
                wifiStandard = "802.11ac";
            } else if (standard == ScanResult.WIFI_STANDARD_11AX) {
                wifiStandard = "802.11ax";
            } else if (standard == ScanResult.WIFI_STANDARD_11AD) {
                wifiStandard = "802.11ad";
            } else if (standard == ScanResult.WIFI_STANDARD_11BE) {
                wifiStandard = "802.11be";
            } else {
                wifiStandard = "unknown";
            }
        } else {
            int speedMbps = wifiInfo.getLinkSpeed();
            if (speedMbps <= 11) {
                wifiStandard = "802.11b";
            } else if (speedMbps <= 54) {
                wifiStandard = "802.11g";
            } else {
                wifiStandard = "802.11n (or higher)";
            }
        }

        return wifiStandard;
    }

    private static String formatAddress(int address) {
        return String.format(Locale.US,"%d.%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address>> 16 & 0xff),
                (address >> 24 & 0xff));
    }
    public static String formatSubnet(int subnetMask) {
        if (subnetMask < 0 || subnetMask > 32) {
            throw new IllegalArgumentException("Invalid subnet mask value");
        }
        int[] components = new int[4];
        for (int i = 0; i < 4; i++) {
            int bitsToSet = Math.min(subnetMask, 8);
            components[i] = (bitsToSet == 0) ? 0 : (0xFF << (8 - bitsToSet));
            subnetMask -= bitsToSet;
        }
        return String.format(Locale.US, "%d.%d.%d.%d", components[0], components[1], components[2], components[3]);
    }

    private boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("dns.google.com");
            return address.isReachable(1000);
        } catch (IOException e) {
            Log.e("TAG", "isInternetAvailable: Error checking internet connection", e);
            return false;
        }
    }

}