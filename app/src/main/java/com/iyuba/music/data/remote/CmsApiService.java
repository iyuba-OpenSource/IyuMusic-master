package com.iyuba.music.data.remote;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CmsApiService {
    String ENDPOINT = "http://cms.iyuba.cn/";

    //http://cms.iyuba.cn/dataapi/jsp/getSeries.jsp?type=321&sign=4a54f0798f3cf6157afebdef82e18d16&format=json
    @GET("dataapi/jsp/getSeries.jsp")
    Single<CmsResponse.TalkClassList> getTalkClass(@Query("type") String level,
                                                   @Query("sign") String source,
                                                   @Query("format") String format);
    //http://cms.iyuba.cn/dataapi/jsp/getTitleBySeries.jsp?type=category&category=314&sign=19d74bdebbc6413105ebc37bf2e551e0&format=json
    @GET("dataapi/jsp/getTitleBySeries.jsp")
    Single<CmsResponse.TalkClassList> getTalkClassLesson(@Query("type") String type,
                                                         @Query("sign") String sign,
                                                         @Query("category") String category,
                                                         @Query("format") String format);

    //http://cms.iyuba.cn/dataapi/jsp/getTitle.jsp?type=series&id=278&sign=581b8b7be62f6e132bdbe72945863061
    // &format=json&total=200
    @GET("dataapi/jsp/getTitle.jsp")
    Single<CmsResponse.TalkLessonList> getTalkLessonOld(@Query("type") String level,
                                                        @Query("id") String id,
                                                        @Query("sign") String sign,
                                                        @Query("total") String total,
                                                        @Query("format") String format);

    @GET("dataapi/jsp/getTitleBySeries.jsp")
    Single<CmsResponse.TalkLessonList> getTalkLesson(@Query("type") String level,
                                                     @Query("seriesid") String id,
                                                     @Query("sign") String sign);

    //http://cms.iyuba.cn/dataapi/jsp/getTitleBySeries.jsp?type=text&voaid=321001&sign=74c452dcc2055c02b3b291a5bd2bafce
    @GET("dataapi/jsp/getTitleBySeries.jsp")
    Single<VoaTextResponse> getTalkLessonText(@Query("type") String level,
                                              @Query("voaid") String id,
                                              @Query("sign") String sign);

    class Creator {
        public static CmsApiService createService(OkHttpClient client, GsonConverterFactory gsonFactory,
                                                  RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(CmsApiService.class);
        }
    }
}
