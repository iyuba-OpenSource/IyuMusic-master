package com.iyuba.music.data;

import android.util.Pair;

import com.iyuba.module.toolbox.MD5;
import com.iyuba.module.toolbox.SingleParser;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.data.model.ChildWordResponse;
import com.iyuba.music.data.model.ClearUserResponse;
import com.iyuba.music.data.model.GetMyDubbingResponse;
import com.iyuba.music.data.model.GetRankingResponse;
import com.iyuba.music.data.model.IntegralBean;
import com.iyuba.music.data.model.OfficialResponse;
import com.iyuba.music.data.model.PdfResponse;
import com.iyuba.music.data.model.SendDubbingResponse;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.TalkClass;
import com.iyuba.music.data.model.TalkLesson;
import com.iyuba.music.data.model.ThumbsResponse;
import com.iyuba.music.data.model.VoaText;
import com.iyuba.music.data.model.VoaWord2;
import com.iyuba.music.data.model.Word;
import com.iyuba.music.data.remote.AiResponse;
import com.iyuba.music.data.remote.AiService;
import com.iyuba.music.data.remote.ApiComService;
import com.iyuba.music.data.remote.ApiService;
import com.iyuba.music.data.remote.CmsApiService;
import com.iyuba.music.data.remote.CmsResponse;
import com.iyuba.music.data.remote.VoaApiService;
import com.iyuba.music.data.remote.VoaService;
import com.iyuba.music.data.remote.VoaTextResponse;
import com.iyuba.music.data.remote.WordCollectResponse;
import com.iyuba.music.data.remote.WordCollectService;
import com.iyuba.music.data.remote.WordResponse;
import com.iyuba.music.entity.user.UserInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class DataManager {
    private static DataManager sInstance = new DataManager();

    public static DataManager getInstance() {
        return sInstance;
    }

    private final CmsApiService mCmsApiService;
    private final WordCollectService mWordCollectService;
    private final VoaApiService mVoaApiService;
    private final AiService mAiService;
    private final VoaService mVoaService;
    private final ApiService mApiService;
    private final ApiComService mApiComService;

    private static final SimpleDateFormat CREDIT_TIME = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private final DubDBManager dubDBManager;

    private DataManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

        OkHttpClient client = builder.build();
        SimpleXmlConverterFactory xmlFactory = SimpleXmlConverterFactory.create();
        GsonConverterFactory gsonFactory = GsonConverterFactory.create();
        RxJava2CallAdapterFactory rxJavaFactory = RxJava2CallAdapterFactory.create();


        mCmsApiService = CmsApiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mWordCollectService = WordCollectService.Creator.createService();
        mVoaApiService = VoaApiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mAiService = AiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mVoaService = VoaService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mApiService = ApiService.Creator.createService(client, gsonFactory, rxJavaFactory);
        mApiComService = ApiComService.Creator.createService(client, gsonFactory, rxJavaFactory);

        dubDBManager = DubDBManager.getInstance();
    }

    public Single<List<TalkClass>> getTalkClass(String type) {
        String sign = buildV2Sign("iyuba", getCurrentDate(), type);
        return mCmsApiService.getTalkClass(type, sign, "json")
                .compose(this.<CmsResponse.TalkClassList, List<TalkClass>>applyParser());
    }

    public Single<List<TalkClass>> getTalkClassLesson(String type) {
        String sign = buildV2Sign("iyuba", getCurrentDate(), "series");
        return mCmsApiService.getTalkClassLesson("category",sign, type, "json")
                .compose(this.<CmsResponse.TalkClassList, List<TalkClass>>applyParser());
    }

    public Single<List<TalkLesson>> getTalkLessonOld(String classId) {
        String type = "series";
        String total = "200";
        String sign = buildV2Sign("iyuba", getCurrentDate(), type);
        return mCmsApiService.getTalkLessonOld(type, classId, sign, total, "json")
                .compose(this.<CmsResponse.TalkLessonList, List<TalkLesson>>applyParser());
    }

    public Single<List<TalkLesson>> getTalkLesson(String classId) {
        String type = "title";
        String sign = buildV2Sign("iyuba", getCurrentDate(), "series");
        return mCmsApiService.getTalkLesson(type, classId, sign)
                .compose(this.<CmsResponse.TalkLessonList, List<TalkLesson>>applyParser());
    }

//    public Single<List<VoaText>> syncVoaTexts(int voaId) {
//        String type = "text";
//        String sign = buildV2Sign("iyuba", getCurrentDate(), "series");
//        return mCmsApiService.getTalkLessonText(type, String.valueOf(voaId), sign)
//                .compose(this.<VoaTextResponse, List<VoaText>>applyParser());
//    }



    public Single<Boolean> deleteWords(int userId, List<String> words) {
        String wordsStr = buildUpdateWords(words);
        return mWordCollectService.updateWords(userId, "delete", "Iyuba", wordsStr)
                .compose(this.<WordCollectResponse.Update, Boolean>applyParser());
    }

    public Single<Boolean> insertWords(int userId, List<String> words) {
        String wordsStr = buildUpdateWords(words);
        return mWordCollectService.updateWords(userId, "insert", "Iyuba", wordsStr)
                .compose(this.<WordCollectResponse.Update, Boolean>applyParser());
    }



    public Single<Pair<List<Word>, Integer>> getNoteWords(int useId, final int pageNumber, int pageCounts) {
        return mWordCollectService.getNoteWords(useId, pageNumber, pageCounts)
                .flatMap(new Function<WordCollectResponse.GetNoteWords, SingleSource<? extends Pair<List<Word>, Integer>>>() {
                    @Override
                    public SingleSource<? extends Pair<List<Word>, Integer>> apply(WordCollectResponse.GetNoteWords response) throws Exception {
                        if (pageNumber <= response.lastPage && response.tempWords.size() > 0) {
                            List<Word> words = new ArrayList<>(response.tempWords.size());
                            for (WordCollectResponse.GetNoteWords.TempWord tempWord : response.tempWords) {
                                words.add(new Word(tempWord.word, tempWord.audioUrl,
                                        tempWord.pronunciation, tempWord.definition));
                            }
                            return Single.just(new Pair<>(words, response.counts));
                        } else {
                            List<Word> words = new ArrayList<>();
                            return Single.just(new Pair<>(words, 0));
                        }
                    }
                });
    }

    public Observable<WordResponse> getWordOnNet(String key) {
        return mWordCollectService.getNetWord(key);
    }

    private String buildUpdateWords(List<String> words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i += 1) {
            if (i == 0) {
                sb.append(words.get(i));
            } else {
                sb.append(",").append(words.get(i));
            }
        }
        return sb.toString();
    }

    public Single<SendEvaluateResponse> uploadSentence(RequestBody body) {
        return mAiService.uploadSentence(body)
                .compose(this.<AiResponse.GetEvaluateResponse, SendEvaluateResponse>applyParser());
    }

    public Single<SendDubbingResponse> sendDubbingComment(Map<String, String> params, RequestBody body) {
        return mVoaService.sendDubbingComment(params,body)
                .compose(this.<SendDubbingResponse, SendDubbingResponse>applyParser());
    }

    public Single<GetRankingResponse> getThumbRanking(int voaId, int pageNum, int pageSize) {
        return mVoaService.getThumbRanking( "android","json","60001",voaId,
                pageNum,pageSize, 2,"music","3")
                .compose(this.<GetRankingResponse, GetRankingResponse>applyParser());
    }

    public Single<ThumbsResponse> doAgree(int id) {
        return mVoaService.doThumbs( 61001,id)//61002 是反对
                .compose(this.<ThumbsResponse, ThumbsResponse>applyParser());
    }

    public Single<GetMyDubbingResponse> getMyDubbing(int uId) {
        return mVoaService.getMyDubbing(uId) // 获取我的发布数据
                .compose(this.<GetMyDubbingResponse, GetMyDubbingResponse>applyParser());
    }

    public Single<ThumbsResponse> deleteReleaseRecordList(String Id, int userID) {
        return mVoaService.deleteReleaseRecordList(Id, String.valueOf(userID)) // 获取我的发布数据
                .compose(this.<ThumbsResponse, ThumbsResponse>applyParser());
    }

    public Single<List<VoaText>> syncVoaTexts(int voaId) {
        return mVoaApiService.getVoaTexts("json", voaId)
                .compose(this.<VoaTextResponse, List<VoaText>>applyParser());
    }

    public Single<PdfResponse> getPdf(String type, int voaId , int language) {
        return mVoaApiService.getPdf(type,voaId,language)
                .compose(this.<PdfResponse,PdfResponse>applyParser());
    }

    public Single<OfficialResponse> getOfficialAccount(int pageNumber, int pageCount) {
        return mVoaApiService.getOfficialAccount(pageNumber, pageCount, "all")
                .compose(this.<OfficialResponse,OfficialResponse>applyParser());
    }

    //获取青少版代码
    public Single<List<VoaWord2>> getChildWords(String bookId) {
        return mVoaApiService.getChildWords(bookId)
                .compose(this.<ChildWordResponse, List<VoaWord2>>applyParser());
    }


    /**
     * 减积分
     * @param appId voaId
     * @return -20
     */
    public Single<IntegralBean> deductIntegral(String flag, int uid, int appId, int idIndex) {
        return mApiService.deductIntegral(flag, uid, appId, idIndex)
                .compose(this.<IntegralBean,IntegralBean>applyParser());
    }

    //   return getVoaTexts(voaId);//重新拉取一遍
    //  //dubDBManager.setVoaTexts(voaTextResponse.voaTexts(), voaId);
    //                        //return getVoaTexts(voaId);

    /**
     * 注销账号
     * @param username 用户名
     * @param password 密码
     * @return 返回是否成功
     */
    public Call<ClearUserResponse> clearUser(String username, String password) {
        String protocol = "11005";
        String format = "json";
        String passwordMD5 = MD5.getMD5ofStr(password);
        String sign = buildV2Sign(protocol,username, passwordMD5,"iyubaV2");
        return mApiComService.clearUser(protocol, username, passwordMD5, sign, format);
    }
    public Single<UserInfo> getUserInfo(String id, String myid) {
        Map<String, String> para = new HashMap<>();
        para.put("protocol", "20001");
        para.put("platform", "android");
        para.put("id", id);
        para.put("myid", myid);
        para.put("appid", Constant.APPID);
        para.put("format", "json");
        para.put("sign", com.iyuba.music.util.MD5.getMD5ofStr("20001" + id + "iyubaV2"));
        return mApiComService.getUserInfo(para).compose(this.<UserInfo,UserInfo>applyParser());
    }

    private RequestBody fromString(String text) {
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }


    private MultipartBody.Part fromFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }


//    public Observable<List<VoaText>> getVoaTexts(final int voaId) {
//        return dubDBManager.getVoaTexts(voaId);
//    }


    private String getCurrentDate() {
        long timeStamp = new Date().getTime() / 1000 + 3600 * 8; //东八区;
        long days = timeStamp / 86400;
        return Long.toString(days);
    }

    private String buildV2Sign(String... stuffs) {
        StringBuilder sb = new StringBuilder();
        for (String stuff : stuffs) {
            sb.append(stuff);
        }
        return MD5.getMD5ofStr(sb.toString());
    }

    // ----------------------- divider ---------------------------

    @SuppressWarnings("unchecked")
    private <T, R> SingleTransformer<T, R> applyParser() {
        return (SingleTransformer<T, R>) SingleParser.parseTransformer;
    }

}
