package com.iyuba.music.data.remote;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiService {

    String ENDPOINT = "http://iuserspeech.iyuba.cn:9001/";
    //String ENDPOINT2 = "https://userspeech.iyuba.cn/";
    //http://userspeech.iyuba.cn/test/concept/

    //@POST("test/concept/")
    @POST("test/eval/")
    Single<AiResponse.GetEvaluateResponse> uploadSentence(@Body RequestBody body);
    class Creator {

        public static AiService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(AiService.class);
        }
    }
}
