package com.iyuba.music.data.model;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * HttpUtil
 */
public class HttpUtil {

    private static OkHttpClient okHttpClient;
//    private static Interceptor loggingInterceptor;



    public static OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (okHttpClient == null) {
            synchronized (HttpUtil.class) {
                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
            }
        }
        return okHttpClient;
    }

    public static Interceptor getInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}
