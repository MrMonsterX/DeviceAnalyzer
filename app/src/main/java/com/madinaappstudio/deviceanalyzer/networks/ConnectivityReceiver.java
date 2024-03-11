package com.madinaappstudio.deviceanalyzer.networks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class ConnectivityReceiver extends BroadcastReceiver {

    private ConnectivityListener listener;

    public void setConnectivityListener(ConnectivityListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);

        int getConnection;
        if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            getConnection = 1;
        } else if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            getConnection = 2;
        } else {
            getConnection = 0;
        }

        if (listener != null) {
            listener.onNetworkConnectionChanged(getConnection);
        }
    }

    public interface ConnectivityListener {
        void onNetworkConnectionChanged(int getConnection);
    }
}
