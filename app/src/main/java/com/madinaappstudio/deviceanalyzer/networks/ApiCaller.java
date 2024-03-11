package com.madinaappstudio.deviceanalyzer.networks;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiCaller {

    OkHttpClient client = new OkHttpClient();
    String publicIp = null;

    public String getPublicIpAddress(){

        Request request = new Request.Builder().url("https://api.ipify.org").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                publicIp = null;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null){
                    try {
                        String responseData = response.body().toString();
                        JSONObject jsonObject = new JSONObject(responseData);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return publicIp;
    }
}
