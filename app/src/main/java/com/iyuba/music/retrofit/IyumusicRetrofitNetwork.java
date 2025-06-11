package com.iyuba.music.retrofit;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.iyuba.music.data.remote.ApiComService;
import com.iyuba.music.retrofit.api.GetVioceApi;
import com.iyuba.music.retrofit.api.NewDeductPointsForDownloadApi;
import com.iyuba.music.retrofit.api.VipServiceApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by iyuba on 2017/12/14.
 */

public class IyumusicRetrofitNetwork {

    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    private static Converter.Factory xmlConverterFactory = SimpleXmlConverterFactory.create();
    public static void initOkHttpClient(){
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }
    private static NewDeductPointsForDownloadApi deductPointsForDownloadApi;

    public static NewDeductPointsForDownloadApi getDeductPointsForDownloadApi() {
        if(deductPointsForDownloadApi == null){
            initOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(NewDeductPointsForDownloadApi.NEWJIFEN_URL)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            deductPointsForDownloadApi = retrofit.create(NewDeductPointsForDownloadApi.class);
        }
        return deductPointsForDownloadApi;
    }

    private static VipServiceApi vipServiceApi;
    public static VipServiceApi getVipServiceApi() {
        if (vipServiceApi == null) {
            initOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).
                    baseUrl(VipServiceApi.BASEURL)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            vipServiceApi = retrofit.create(VipServiceApi.class);
        }
        return vipServiceApi;
    }

    private static ApiComService apiComService;
    public static ApiComService getApiComService() {
        if (apiComService == null) {
            initOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).
                    baseUrl(ApiComService.ENDPOINT)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            apiComService = retrofit.create(ApiComService.class);
        }
        return apiComService;
    }

    private static GetVioceApi vioceApi;
    public static GetVioceApi getVioceApi() {
        if (vioceApi == null) {
            initOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(GetVioceApi.GETVIOCEURL)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            vioceApi = retrofit.create(GetVioceApi.class);
        }
        return vioceApi;
    }
}
