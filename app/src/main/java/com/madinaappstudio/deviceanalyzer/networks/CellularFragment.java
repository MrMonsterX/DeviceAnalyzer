package com.madinaappstudio.deviceanalyzer.networks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.R;
import com.madinaappstudio.deviceanalyzer.databinding.FragmentCellurarBinding;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CellularFragment extends Fragment {
    Context context;
    TextView cellPubIpAddress;
    Button cellPubIpAddressOk;
    ApiCalling apiCalling;
    Dialog dialog;
    FragmentCellurarBinding binding;

    public CellularFragment() {
    }

    public CellularFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCellurarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        CrashReporter.startCrashThread(context);

        getCellInfo();

        apiCalling = new ApiCalling();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_publicip);
        cellPubIpAddress = dialog.findViewById(R.id.pubIpAddress);
        cellPubIpAddressOk = dialog.findViewById(R.id.pubIpAddressOk);

        cellPubIpAddressOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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
                            binding.netCellHeadStatus.setText(R.string.inter_access);
                            binding.netCellHeadIc.setImageResource(R.drawable.ic_cellular_on_24px);
                            binding.netCellLayoutPubIp.setVisibility(View.VISIBLE);
                            binding.netCellPublicIp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    apiCalling.getPublicIpAddress(new ApiCalling.IpAddressListener() {
                                        @Override
                                        public void onSuccess(String ipAddress) {
                                            cellPubIpAddress.setText(ipAddress);
                                            dialog.show();
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        }

                                        @Override
                                        public void onError() {
                                            cellPubIpAddress.setText(R.string.unable_to_fetch);
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
                            binding.netCellHeadStatus.setText(R.string.no_inter_access);
                            binding.netCellHeadIc.setImageResource(R.drawable.ic_cellular_off_24px);
                            binding.netCellLayoutPubIp.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();


        binding.netCellName.setText(telephonyManager.getSimOperatorName().toUpperCase());
        if (linkProperties != null) {
            for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                if (linkAddress.getAddress() instanceof Inet4Address) {
                    IPv4Address = linkAddress.getAddress().getHostAddress();
                    binding.netCellIpv4Address.setText(IPv4Address);
                }
                if (linkAddress.getAddress() instanceof Inet6Address) {
                    binding.netCellIpv6Address.setText(linkAddress.getAddress().getHostAddress());
                }
            }
            for (RouteInfo routeInfo : linkProperties.getRoutes()) {
                InetAddress inetAddress = routeInfo.getGateway();
                if (inetAddress instanceof Inet4Address) {
                    binding.netCellGateway.setText(inetAddress.getHostAddress());
                    break;
                }
            }

            ArrayList<String> dnsList = new ArrayList<>();
            for (InetAddress inetAddress : linkProperties.getDnsServers()) {
                dnsList.add(inetAddress.getHostAddress());
            }
            if (dnsList.size() == 1) {
                binding.netCellDNS1.setText(dnsList.get(0));
                binding.netCellLayoutDNS1.setVisibility(View.VISIBLE);
            } else if (dnsList.size() == 2) {
                binding.netCellDNS1.setText(dnsList.get(0));
                binding.netCellDNS2.setText(dnsList.get(1));
                binding.netCellLayoutDNS1.setVisibility(View.VISIBLE);
                binding.netCellLayoutDNS2.setVisibility(View.VISIBLE);
            } else if (dnsList.size() == 4) {
                binding.netCellDNS1.setText(dnsList.get(0));
                binding.netCellDNS2.setText(dnsList.get(1));
                binding.netCellDNS3.setText(dnsList.get(2));
                binding.netCellDNS4.setText(dnsList.get(3));
                binding.netCellLayoutDNS1.setVisibility(View.VISIBLE);
                binding.netCellLayoutDNS2.setVisibility(View.VISIBLE);
                binding.netCellLayoutDNS3.setVisibility(View.VISIBLE);
                binding.netCellLayoutDNS4.setVisibility(View.VISIBLE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                if (telephonyManager.getSignalStrength() != null){
                    for (CellSignalStrength cellSignalStrength : telephonyManager.getSignalStrength().getCellSignalStrengths()) {
                        binding.netCellSignalStrength.setText(String.valueOf("dBm " + cellSignalStrength.getDbm()));
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
                            binding.netCellSignalStrength.setText("dBm " + dbm);
                        }
                    }
                } catch (Exception e) {
                    binding.netCellSignalStrength.setText(R.string.not_available);
                    Log.d("getDBmTag", "getCellInfo: " + e);
                }
            }

            try {
                InetAddress ipAddress = InetAddress.getByName(IPv4Address);
                byte[] ipAddressBytes = ipAddress.getAddress();
                char addressClass = getIpAddressClass(ipAddressBytes[0]);
                String subnetMask = getSubnetMask(addressClass);
                binding.netCellSubnetMask.setText(subnetMask);
            } catch (UnknownHostException e) {
                binding.netCellSubnetMask.setText(R.string.not_available);
            }

            binding.netCellInterface.setText(linkProperties.getInterfaceName());
            binding.netCellDeviceType.setText(getPhoneTypeName(telephonyManager));




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