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

import androidx.fragment.app.Fragment;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;

public class WifiFragment extends Fragment {
    Context context;
    TextView netWifiHeadStatus, netWifiIpv4Address, netWifiIpv6Address, netWifiPublicIp, netWifiGateway,
            netWifiSubnetMask, netWifiLeaseDuration, netWifiInterface, netWifiLinkSpeed,
            netWifiFrequency, netWifiStandard, netWifi5GhzSupported, netWifi6GhzSupported,
            netWifiDNS1, netWifiDNS2, netWifiDNS3, netWifiDNS4, wifiPubIpAddress;

    LinearLayout netWifiLayoutDNS1, netWifiLayoutDNS2, netWifiLayoutDNS3, netWifiLayoutDNS4, netWifiLayoutPubIp;
    ImageView netWifiHeadIc;
    Button wifiPubIpAddressOk;
    ApiCalling apiCalling;
    Dialog dialog;

    public WifiFragment() {}

    public WifiFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_wifi, container, false);

        CrashReporter.startCrashThread(context);

        netWifiHeadIc = view.findViewById(R.id.netWifiHeadIc);
        netWifiHeadStatus = view.findViewById(R.id.netWifiHeadStatus);
        netWifiIpv4Address = view.findViewById(R.id.netWifiIpv4Address);
        netWifiIpv6Address = view.findViewById(R.id.netWifiIpv6Address);
        netWifiPublicIp = view.findViewById(R.id.netWifiPublicIp);
        netWifiGateway = view.findViewById(R.id.netWifiGateway);
        netWifiSubnetMask = view.findViewById(R.id.netWifiSubnetMask);
        netWifiLeaseDuration = view.findViewById(R.id.netWifiLeaseDuration);
        netWifiInterface = view.findViewById(R.id.netWifiInterface);
        netWifiLinkSpeed = view.findViewById(R.id.netWifiLinkSpeed);
        netWifiFrequency = view.findViewById(R.id.netWifiFrequency);
        netWifiStandard = view.findViewById(R.id.netWifiStandard);
        netWifi5GhzSupported = view.findViewById(R.id.netWifi5GhzSupported);
        netWifi6GhzSupported = view.findViewById(R.id.netWifi6GhzSupported);
        netWifiLayoutDNS1 = view.findViewById(R.id.netWifiLayoutDNS1);
        netWifiLayoutDNS2 = view.findViewById(R.id.netWifiLayoutDNS2);
        netWifiLayoutDNS3 = view.findViewById(R.id.netWifiLayoutDNS3);
        netWifiLayoutDNS4 = view.findViewById(R.id.netWifiLayoutDNS4);
        netWifiDNS1 = view.findViewById(R.id.netWifiDNS1);
        netWifiDNS2 = view.findViewById(R.id.netWifiDNS2);
        netWifiDNS3 = view.findViewById(R.id.netWifiDNS3);
        netWifiDNS4 = view.findViewById(R.id.netWifiDNS4);
        netWifiLayoutPubIp = view.findViewById(R.id.netWifiLayoutPubIp);

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
                            netWifiHeadStatus.setText(R.string.inter_access);
                            netWifiHeadIc.setImageResource(R.drawable.ic_wifi_on_24px);
                            netWifiLayoutPubIp.setVisibility(View.VISIBLE);
                            netWifiPublicIp.setOnClickListener(new View.OnClickListener() {
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
                            netWifiHeadStatus.setText(R.string.no_inter_access);
                            netWifiHeadIc.setImageResource(R.drawable.ic_wifi_off_24px);
                            netWifiLayoutPubIp.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();

        if (linkProperties != null){
            for (LinkAddress linkAddress: linkProperties.getLinkAddresses()){
                if (linkAddress.getAddress() instanceof Inet4Address){
                    netWifiIpv4Address.setText(linkAddress.getAddress().getHostAddress());
                }
                if (linkAddress.getAddress() instanceof Inet6Address){
                    netWifiIpv6Address.setText(linkAddress.getAddress().getHostAddress());
                }
            }
            netWifiInterface.setText(linkProperties.getInterfaceName());
        } else {
            netWifiInterface.setText(R.string.not_found);
        }
        int gateway = wifiManager.getDhcpInfo().gateway;
        int subnet = wifiManager.getDhcpInfo().netmask;
        int leaseDuration = wifiManager.getDhcpInfo().leaseDuration;

        netWifiGateway.setText(formatAddress(gateway));
        if (subnet <= 0){
            Log.d("TAG", "getWifiInfo: " + subnet);
            netWifiSubnetMask.setText(formatSubnet(subnet));
        } else {
            netWifiSubnetMask.setText(formatSubnet(24));
            Log.d("TAG", "getWifiInfo: " + subnet);
        }

        ArrayList<String> dnsList = new ArrayList<>();
        assert linkProperties != null;
        for (InetAddress inetAddress : linkProperties.getDnsServers()) {
            dnsList.add(inetAddress.getHostAddress());
        }
        if (dnsList.size() == 1) {
            netWifiDNS1.setText(dnsList.get(0));
            netWifiLayoutDNS1.setVisibility(View.VISIBLE);
        } else if (dnsList.size() == 2) {
            netWifiDNS1.setText(dnsList.get(0));
            netWifiDNS2.setText(dnsList.get(1));
            netWifiLayoutDNS1.setVisibility(View.VISIBLE);
            netWifiLayoutDNS2.setVisibility(View.VISIBLE);
        } else if (dnsList.size() == 4) {
            netWifiDNS1.setText(dnsList.get(0));
            netWifiDNS2.setText(dnsList.get(1));
            netWifiDNS3.setText(dnsList.get(2));
            netWifiDNS4.setText(dnsList.get(3));
            netWifiLayoutDNS1.setVisibility(View.VISIBLE);
            netWifiLayoutDNS2.setVisibility(View.VISIBLE);
            netWifiLayoutDNS3.setVisibility(View.VISIBLE);
            netWifiLayoutDNS4.setVisibility(View.VISIBLE);
        }

        netWifiLeaseDuration.setText(leaseDuration + " Seconds");

        netWifiLinkSpeed.setText(wifiInfo.getLinkSpeed() +" "+ WifiInfo.LINK_SPEED_UNITS);
        netWifiFrequency.setText(wifiInfo.getFrequency() +" "+ WifiInfo.FREQUENCY_UNITS);
        netWifiStandard.setText(getWifiStandard(wifiInfo));
        if (wifiManager.is5GHzBandSupported()){
            netWifi5GhzSupported.setText(R.string.yes);
        } else {
            netWifi5GhzSupported.setText(R.string.no);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (wifiManager.is6GHzBandSupported()){
                netWifi6GhzSupported.setText(R.string.yes);
            } else {
                netWifi6GhzSupported.setText(R.string.no);
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