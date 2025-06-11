package com.iyuba.music.dubbing;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.VoaText;
import com.iyuba.music.data.remote.WordResponse;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */

public interface DubbingMvpView extends MvpView {
    void showVoaTexts(List<VoaText> voaTextList);

    void showEmptyTexts();

    void dismissDubbingDialog();

    void showMergeDialog();

    void dismissMergeDialog();

    void startPreviewActivity();

    void showToast(int resId);

    void showToast(String message);

    void pause();

    //void onDraftRecordExist(Record record);

    void showWord(WordResponse bean);

    void finish();

    void getEvaluateResponse(SendEvaluateResponse response, int paraId, File flakFile, int progress, int secondaryProgress);

    void evaluateError(String message);
}
