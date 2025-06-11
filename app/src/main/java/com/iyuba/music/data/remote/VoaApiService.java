package com.iyuba.music.data.remote;

import com.iyuba.music.data.model.ChildWordResponse;
import com.iyuba.music.data.model.OfficialResponse;
import com.iyuba.music.data.model.PdfResponse;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VoaApiService {

    String ENDPOINT = "http://apps.iyuba.cn/iyuba/";

    @GET("textExamApi.jsp")
    Single<VoaTextResponse> getVoaTexts(@Query("format") String format,
                                        @Query("voaid") int voaId);

    @GET("getVoapdfFile_new.jsp")
    Single<PdfResponse> getPdf(@Query("type") String type,
                               @Query("voaid") int voaId,
                               @Query("isenglish") int isEnglish);
  //http://cms.iyuba.cn/dataapi/jsp/getTitleBySeries.jsp?type=text&voaid=321001&sign=74c452dcc2055c02b3b291a5bd2bafce

    @GET("getOfficialAccount.jsp?")
    Single<OfficialResponse> getOfficialAccount(
            @Query("pageNumber") int pageNumber ,
            @Query("pageCount") int pageCount,
            @Query("newsfrom") String newsfrom
    );

    @GET("getWordByUnit.jsp")
    Single<ChildWordResponse> getChildWords(@Query("bookid") String bookId);

    class Creator {
        public static VoaApiService createService(OkHttpClient client, GsonConverterFactory gsonFactory,
                                                  RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(VoaApiService.class);
        }
    }
}
