package com.madinaappstudio.deviceanalyzer.networks;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.madinaappstudio.deviceanalyzer.CrashReporter;
import com.madinaappstudio.deviceanalyzer.NoConnectionFrag;
import com.madinaappstudio.deviceanalyzer.R;

public class NetworkActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityListener {
    ConnectivityReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        CrashReporter.startCrashThread(this);
        // Leave it Blank

    }

    @Override
    public void onNetworkConnectionChanged(int getConnection) {
        if (getConnection == 1) {
            loadFragment(new WifiFragment(NetworkActivity.this));
            clearAllFragments();
        } else if (getConnection == 2) {
            loadFragment(new CellularFragment(NetworkActivity.this));
            clearAllFragments();
        } else {
            loadFragment(new NoConnectionFrag());
            clearAllFragments();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.netFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void registerReceiver() {
        receiver = new ConnectivityReceiver();
        receiver.setConnectivityListener(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(receiver);
    }

    private void clearAllFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentManager.getFragments()) {
            transaction.remove(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }
}