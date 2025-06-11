package com.iyuba.music.model;


import com.iyuba.music.data.Constant;
import com.iyuba.music.util.SSLSocketFactoryUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit的框架
 * 网络请求管理者
 */
public class NetWorkManager {

    private static NetWorkManager mInstance;

    private static Retrofit retrofitDev;
    private static volatile DevServer devServer = null;




    public static NetWorkManager getInstance() {
        if (mInstance == null) {
            synchronized (NetWorkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkManager();
                }
            }
        }
        return mInstance;
    }



    public void initDev() {
        // 初始化okhttp
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
                .build();

        // 初始化Retrofit
        retrofitDev = new Retrofit.Builder()
                .client(client)
                .baseUrl(Constant.URL_DEV)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }




    public static DevServer getRequestForDev() {

        if (devServer == null) {
            synchronized (DevServer.class) {
                devServer = retrofitDev.create(DevServer.class);
            }
        }
        return devServer;
    }

}
