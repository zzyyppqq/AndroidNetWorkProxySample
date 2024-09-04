package com.zyp.proxy.http;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import okio.Source;


public class OkHttpMain {

    public static void main(String[] args) {
        run();
    }

    public static final MediaType JSON = MediaType.get("application/json");
    public static void run() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NotNull String s) {
                System.out.println(s);
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
                String responseBody = response.body().string();
                System.out.println("responseBody: " + responseBody);
            } else {
                System.out.println("请求失败：" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
