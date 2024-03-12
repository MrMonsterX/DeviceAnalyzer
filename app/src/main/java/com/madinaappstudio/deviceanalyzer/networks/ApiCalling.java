package com.madinaappstudio.deviceanalyzer.networks;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiCalling {

    String url = "https://api.ipify.org/?format=json";
    OkHttpClient client = new OkHttpClient();
    public interface IpAddressListener {
        void onSuccess(String ipAddress);
        void onError();
    }

    public void getPublicIpAddress(final IpAddressListener listener){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(new TimerTask() {
                    @Override
                    public void run() {
                        listener.onError();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String ipAddress = jsonObject.optString("ip");

                        new Handler(Looper.getMainLooper()).post(new TimerTask() {
                            @Override
                            public void run() {
                                listener.onSuccess(ipAddress);
                            }
                        });
                    } catch (JSONException e) {
                        new Handler(Looper.getMainLooper()).post(new TimerTask() {
                            @Override
                            public void run() {
                                listener.onError();
                            }
                        });
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(new TimerTask() {
                        @Override
                        public void run() {
                            listener.onError();
                        }
                    });
                }
            }
        });
    }
}
