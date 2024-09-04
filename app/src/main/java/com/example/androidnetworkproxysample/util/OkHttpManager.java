package com.example.androidnetworkproxysample.util;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;


public class OkHttpManager {

    public static void main(String[] args) {
        run();
    }

    public static final MediaType JSON = MediaType.get("application/json");
    public static void run() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NotNull String s) {
                Log.i("OkHttpUtil", s);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        String url = "http://test-cloud.aecg.com.cn/api/time/current";
        RequestBody body = RequestBody.create("", JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // 处理响应数据
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String result = responseBody.string();
                System.out.println("result: " + result);
                responseBody.source().readUtf8();
            } else {
                System.out.println("请求失败：" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
