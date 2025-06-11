package com.iyuba.music.data.remote;

import com.iyuba.music.data.model.IntegralBean;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    String ENDPOINT = "http://api.iyuba.cn/";

    @GET("credits/updateScore.jsp?srid=40&mobile=1")
    Single<IntegralBean> deductIntegral(
            @Query("flag") String flag,
            @Query("uid") int uid,
            @Query("appid") int appid,
            @Query("idindex") int idIndex
    );



    class Creator {
        public static ApiService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(ApiService.class);
        }
    }
}
