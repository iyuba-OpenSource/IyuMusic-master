package com.iyuba.music.data.remote;

import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.ChangeNameResponse;
import com.iyuba.music.data.model.ClearUserResponse;
import com.iyuba.music.data.model.LoginResponse;
import com.iyuba.music.data.model.RegisterMobResponse;
import com.iyuba.music.entity.user.UserInfo;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiComService {

    String ENDPOINT = "http://api." + Constant.IYUBA_COM + "";

    @GET("v2/api.iyuba")
    Call<LoginResponse> LoginUser(@QueryMap Map<String, String> params);
    @GET("v2/api.iyuba")
    Call<ClearUserResponse> clearUser(@Query("protocol") String protocol,
                                        @Query("username") String username,
                                        @Query("password") String password,
                                        @Query("sign") String sign,
                                        @Query("format") String format);
    @GET("v2/api.iyuba")
    Single<UserInfo> getUserInfo(@QueryMap Map<String, String> params);
    @GET("v2/api.iyuba")
    Call<UserInfo> userInfoApi(@QueryMap Map<String, String> params);
    @POST("v2/api.iyuba?")
    Call<RegisterMobResponse> registerByMob(@QueryMap Map<String, String> params);
    @POST("v2/api.iyuba?")
    Call<ChangeNameResponse> ChangeUserName(@QueryMap Map<String, String> params);

    class Creator {
        public static ApiComService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(ApiComService.class);
        }
    }
}
