package com.iyuba.music.data.remote;

import com.iyuba.music.data.model.GetMyDubbingResponse;
import com.iyuba.music.data.model.GetRankingResponse;
import com.iyuba.music.data.model.SendDubbingResponse;
import com.iyuba.music.data.model.ThumbsResponse;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface VoaService {

    String ENDPOINT = "http://voa.iyuba.cn/voa/";

    @POST("UnicomApi2?")
    Single<SendDubbingResponse> sendDubbingComment(@QueryMap Map<String, String> params, @Body RequestBody body);

    @GET("UnicomApi")
    Single<GetRankingResponse> getThumbRanking(@Query("platform") String platform,
                                               @Query("format") String format,
                                               @Query("protocol") String protocol,
                                               @Query("voaid") int voaId,
                                               @Query("pageNumber") int pageNumber,
                                               @Query("pageCounts") int pageCounts,
                                               @Query("sort") int sort,
                                               @Query("topic") String topic,
                                               @Query("selectType") String selectType);
    @GET("UnicomApi")
    Single<ThumbsResponse> doThumbs(@Query("protocol") int protocol, @Query("id") int id);

    @GET("getTalkShowOtherWorks.jsp")
    Single<GetMyDubbingResponse> getMyDubbing(@Query("uid") int uid);

    //删除自己发布的单个配音
    @GET("UnicomApi?protocol=61003")
    Single<ThumbsResponse> deleteReleaseRecord(@Query("id") int id);

    @GET("UnicomApi?protocol=61004")
    Single<ThumbsResponse> deleteReleaseRecordList(@Query("id") String id);

    //删除自己发布的多个配音 id 以英文逗号分隔
    @GET("delShuoshuo.jsp") //001 成功
    Single<ThumbsResponse> deleteReleaseRecordList(@Query("id") String id,
                                                   @Query("userid") String uId);

    class Creator {
        public static VoaService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(VoaService.class);
        }
    }
}
