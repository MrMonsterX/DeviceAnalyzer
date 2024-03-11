package com.madinaappstudio.deviceanalyzer.networks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.RouteInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.MainActivity;
import com.madinaappstudio.deviceanalyzer.R;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CellularFragment extends Fragment {
    Context context;
    TextView netCellHeadStatus, netCellName, netCellIpv4Address, netCellIpv6Address, netCellSignalStrength, netCellGateway,
            netCellDNS1, netCellDNS2, netCellDNS3, netCellDNS4, netCellSubnetMask,
            netCellInterface, netCellDeviceType, netCellSimTechnology;
    LinearLayout netCellLayoutDNS1, netCellLayoutDNS2, netCellLayoutDNS3, netCellLayoutDNS4;
    ImageView netCellHeadIc;

    public CellularFragment() {
    }

    public CellularFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cellurar, container, false);

        CrashReporter.startCrashThread(context);

        netCellHeadStatus = view.findViewById(R.id.netCellHeadStatus);
        netCellHeadIc = view.findViewById(R.id.netCellHeadIc);
        netCellName = view.findViewById(R.id.netCellName);
        netCellIpv4Address = view.findViewById(R.id.netCellIpv4Address);
        netCellIpv6Address = view.findViewById(R.id.netCellIpv6Address);
        netCellSignalStrength = view.findViewById(R.id.netCellSignalStrength);
        netCellGateway = view.findViewById(R.id.netCellGateway);
        netCellSubnetMask = view.findViewById(R.id.netCellSubnetMask);
        netCellInterface = view.findViewById(R.id.netCellInterface);
        netCellDeviceType = view.findViewById(R.id.netCellDeviceType);
        netCellLayoutDNS1 = view.findViewById(R.id.netCellLayoutDNS1);
        netCellLayoutDNS2 = view.findViewById(R.id.netCellLayoutDNS2);
        netCellLayoutDNS3 = view.findViewById(R.id.netCellLayoutDNS3);
        netCellLayoutDNS4 = view.findViewById(R.id.netCellLayoutDNS4);
        netCellDNS1 = view.findViewById(R.id.netCellDNS1);
        netCellDNS2 = view.findViewById(R.id.netCellDNS2);
        netCellDNS3 = view.findViewById(R.id.netCellDNS3);
        netCellDNS4 = view.findViewById(R.id.netCellDNS4);


        getCellInfo();

        return view;
    }

    private void getCellInfo() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
        String IPv4Address = null;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInternetAvailable()) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netCellHeadStatus.setText(R.string.inter_access);
                            netCellHeadIc.setImageResource(R.drawable.ic_cellular_on_24px);
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netCellHeadStatus.setText(R.string.no_inter_access);
                            netCellHeadIc.setImageResource(R.drawable.ic_cellular_off_24px);
                        }
                    });
                }
            }
        }).start();


        netCellName.setText(telephonyManager.getSimOperatorName().toUpperCase());
        if (linkProperties != null) {
            for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                if (linkAddress.getAddress() instanceof Inet4Address) {
                    IPv4Address = linkAddress.getAddress().getHostAddress();
                    netCellIpv4Address.setText(IPv4Address);
                }
                if (linkAddress.getAddress() instanceof Inet6Address) {
                    netCellIpv6Address.setText(linkAddress.getAddress().getHostAddress());
                }
            }
            for (RouteInfo routeInfo : linkProperties.getRoutes()) {
                InetAddress inetAddress = routeInfo.getGateway();
                if (inetAddress instanceof Inet4Address) {
                    netCellGateway.setText(inetAddress.getHostAddress());
                    break;
                }
            }

            ArrayList<String> dnsList = new ArrayList<>();
            for (InetAddress inetAddress : linkProperties.getDnsServers()) {
                dnsList.add(inetAddress.getHostAddress());
            }
            if (dnsList.size() == 1) {
                netCellDNS1.setText(dnsList.get(0));
                netCellLayoutDNS1.setVisibility(View.VISIBLE);
            } else if (dnsList.size() == 2) {
                netCellDNS1.setText(dnsList.get(0));
                netCellDNS2.setText(dnsList.get(1));
                netCellLayoutDNS1.setVisibility(View.VISIBLE);
                netCellLayoutDNS2.setVisibility(View.VISIBLE);
            } else if (dnsList.size() == 4) {
                netCellDNS1.setText(dnsList.get(0));
                netCellDNS2.setText(dnsList.get(1));
                netCellDNS3.setText(dnsList.get(2));
                netCellDNS4.setText(dnsList.get(3));
                netCellLayoutDNS1.setVisibility(View.VISIBLE);
                netCellLayoutDNS2.setVisibility(View.VISIBLE);
                netCellLayoutDNS3.setVisibility(View.VISIBLE);
                netCellLayoutDNS4.setVisibility(View.VISIBLE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                if (telephonyManager.getSignalStrength() != null){
                    for (CellSignalStrength cellSignalStrength : telephonyManager.getSignalStrength().getCellSignalStrengths()) {
                        netCellSignalStrength.setText(String.valueOf("dBm " + cellSignalStrength.getDbm()));
                    }
                }
            } else {
                try {
                    Method method = TelephonyManager.class.getMethod("getSignalStrength");
                    Object signalStrength = method.invoke(telephonyManager);
                    if (signalStrength != null) {
                        Method getDbmMethod = signalStrength.getClass().getMethod("getDbm");
                        Object dbmObject = getDbmMethod.invoke(signalStrength);
                        if (dbmObject != null) {
                            int dbm = (Integer) dbmObject;
                            netCellSignalStrength.setText("dBm " + dbm);
                        }
                    }
                } catch (Exception e) {
                    netCellSignalStrength.setText(R.string.not_available);
                    Log.d("getDBmTag", "getCellInfo: " + e);
                }
            }

            try {
                InetAddress ipAddress = InetAddress.getByName(IPv4Address);
                byte[] ipAddressBytes = ipAddress.getAddress();
                char addressClass = getIpAddressClass(ipAddressBytes[0]);
                String subnetMask = getSubnetMask(addressClass);
                netCellSubnetMask.setText(subnetMask);
            } catch (UnknownHostException e) {
                netCellSubnetMask.setText(R.string.not_available);
            }

            netCellInterface.setText(linkProperties.getInterfaceName());
            netCellDeviceType.setText(getPhoneTypeName(telephonyManager));




        }
    }

    private String getPhoneTypeName(TelephonyManager telephonyManager){
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

    private static char getIpAddressClass(byte firstOctet) {
        if ((firstOctet & 0x80) == 0) {
            return 'A';
        } else if ((firstOctet & 0xC0) == 0x80) {
            return 'B';
        } else if ((firstOctet & 0xE0) == 0xC0) {
            return 'C';
        } else if ((firstOctet & 0xF0) == 0xE0) {
            return 'D';
        } else {
            return 'E';
        }
    }

    private static String getSubnetMask(char addressClass) {
        switch (addressClass) {
            case 'A':
                return "255.0.0.0";
            case 'B':
                return "255.255.0.0";
            case 'C':
                return "255.255.255.0";
            default:
                return "";
        }
    }

    private boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("dns.google.com");
            return address.isReachable(1500);
        } catch (IOException e) {
            Log.e("TAG", "isInternetAvailable: Error checking internet connection", e);
            return false;
        }
    }



}