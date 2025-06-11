package com.iyuba.music.data.remote;

import com.iyuba.music.data.model.HttpUtil;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WordCollectService {

    String ENDPOINT = "http://word.iyuba.cn/";


    @GET("words/updateWord.jsp")
    Single<WordCollectResponse.Update> updateWords(@Query("userId") int userId,
                                                   @Query("mod") String mode,
                                                   @Query("groupName") String groupName,
                                                   @Query("word") String wordsStr);


    @GET("words/wordListService.jsp")
    Single<WordCollectResponse.GetNoteWords> getNoteWords(@Query("u") int userId,
                                                          @Query("pageNumber") int pageNumber,
                                                          @Query("pageCounts") int pageCount);

    @GET("words/apiWord.jsp")
    Observable<WordResponse> getNetWord(@Query("q") String key);

    class Creator {
        public static WordCollectService createService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(HttpUtil.getOkHttpClient())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())//!!!
                    .build();
            return retrofit.create(WordCollectService.class);
        }
    }

}
