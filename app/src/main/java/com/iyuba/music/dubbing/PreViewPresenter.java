package com.iyuba.music.dubbing;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.google.gson.Gson;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.model.PostItem;
import com.iyuba.music.data.model.SendDubbingResponse;
import com.iyuba.music.data.model.WavListItem;
import com.iyuba.music.dubbing.utils.RxUtil;
import com.iyuba.music.dubbing.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;
import timber.log.Timber;

import static java.util.Collections.sort;

public class PreViewPresenter extends BasePresenter<PreviewMvpView> {

    private final DataManager mDataManager;

    private Disposable mReleaseDubbingSub;
    private Subscription mSaveRecordSub;

    private int mBackId;
    private int shuoshuoId;

    public PreViewPresenter() {
        this.mDataManager = DataManager.getInstance();
    }

    public String getMp3RecordPath(int voaId, long timeStamp) {
        File file = getAacRecordFile(voaId, timeStamp);
        return file.getAbsolutePath().replace("aac", "mp3");
    }

    public String getMp3Path(int voaId) {
        return StorageUtil.getAudioFile(((Context) getMvpView()), voaId).getAbsolutePath();
    }

    private File getAacRecordFile(int voaId, long timeStamp) {
        return StorageUtil.getAacMergeFile((Context) getMvpView(), voaId, timeStamp);
    }

    public Uri getVideoUri(int voaId) {
        return Uri.fromFile(StorageUtil.getVideoFile((Context) getMvpView(), voaId));
    }

    public void releaseDubbing(Map<Integer, WavListItem> wavMap, int voaId, String sound,
                               int score, int cat, String uid, String userName) {
        checkViewAttached();
        RxUtil.unsubscribe(mReleaseDubbingSub);
        getMvpView().showLoadingDialog();

        //登录后操作

        String PROTOCOL = "protocol";
        String USER_ID = "userid";
        String CONTENT = "content";

        Map<String, String> map = new HashMap<>();
        map.put(PROTOCOL, "60002");
        map.put(CONTENT, "3");
        map.put(USER_ID, String.valueOf(uid));

        PostItem item = new PostItem() ;
        item.setAppName(Constant.AppName);
        item.setFlag(1);
        item.setFormat("json");
        item.setParaId(0);
        item.setIdIndex(0);
        item.setPlatform("android");
        item.setScore(score);
        item.setShuoshuotype(3);
        item.setSound(sound);
        item.setTopic("music");
        item.setUsername(userName);
        item.setVoaid(voaId);
        item.setCategory(cat);
        item.setWavListItems(buildList(wavMap));

        mReleaseDubbingSub = mDataManager.sendDubbingComment(map,getBody(item))
                .compose(com.iyuba.module.toolbox.RxUtil.<SendDubbingResponse>applySingleIoScheduler())
                .subscribe(new Consumer<SendDubbingResponse>() {
                    @Override
                    public void accept(SendDubbingResponse response) throws Exception {
                        if (isViewAttached()) {
                            Timber.e("@@@SendDubbingResponse ~~");
                            if (TextUtils.equals(response.message, "OK")) {
                                Integer tmp = response.shuoshuoId;
                                mBackId = tmp != null ? tmp : 0;
//                                response.
                                shuoshuoId = mBackId;
                                getMvpView().showPublishSuccess(R.string.released_success2);
                                getMvpView().showShareHideReleaseButton();
                                getMvpView().showShareView(response.filePath);
                                getMvpView().showToast(R.string.released_success);
                                DubbingActivity.isSend = true;
                            } else {
                                getMvpView().showPublishFailure(R.string.released_failure);
                                getMvpView().showToast(R.string.released_failure);
                                DubbingActivity.isSend = false;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        getMvpView().showToast("请求失败~");
                    }
                });
        ;
    }

    private RequestBody getBody(PostItem item) {
        Gson gson = new Gson();
        String json = gson.toJson(item);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        return body ;
    }

    private List<WavListItem> buildList(Map<Integer, WavListItem> recordItems) {
        List<WavListItem> requests = new ArrayList<>();
        for (Integer i : recordItems.keySet()) {
            requests.add(recordItems.get(i));
        }
        sort(requests);
        return requests;
    }

    public int getBackId() {
        return mBackId;
    }

    public int getShuoshuoId() {
        return shuoshuoId;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mReleaseDubbingSub);
        RxUtil.unsubscribe(mSaveRecordSub);
    }

    public void cancelUpload() {
        Timber.e("cancelUpload cancelUpload cancelUpload");
    }

    public CharSequence formatMessage(int wordCount, int averageScore, String time) {
        SpannableStringBuilder builder = new SpannableStringBuilder(
                "您一共配音了" + wordCount + "个单词，\n读了" + time + "s秒的时间，\n");

        SpannableString spannableString = new SpannableString("正确率" + averageScore + "%");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F85778"));
        spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        builder.append(spannableString);

        if (averageScore >= 80) {
            builder.append("，读的真棒~");
        } else if (averageScore >= 60) {
            builder.append("，\n读的一般般，还需要努力呀～");
        } else {
            builder.append("，\n分数好低，还需努力啊~");
        }
        return builder;
    }

    public CharSequence formatTitle(String string) {
        SpannableString spannableString = new SpannableString(string);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F85778"));
        spannableString.setSpan(colorSpan, string.length() - 5, string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
