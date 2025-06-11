package com.iyuba.music.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.TalkLesson;
import com.iyuba.music.data.model.VoaText;

import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import timber.log.Timber;

public class DubDBManager implements VoaTextTableInter,CollectTableInter,EvaluateTableInter,
        PraiseTableInter,EvWordTableInter{

    private static DubDBManager sInstance;

    private final VoaTextTable voaTextTable;
    private final CollectTable collectTable;
    private final EvaluateTable evaluateTable;
    private final PraiseTable praiseTable;
    private final EvWordTable evWordTable;


    public static void init(Context appContext) {
        if (sInstance == null) {
            sInstance = new DubDBManager(appContext);
            Timber.e("DubDBManager 创建");
        }
    }

    public static DubDBManager getInstance() {
        if (null == sInstance) throw new NullPointerException("not init");
        return sInstance;
    }

    private DubDBManager(Context context) {
        DubDBHelper dbHelper = new DubDBHelper(context);//onCreate
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        voaTextTable = new VoaTextTable(db);
        collectTable= new CollectTable(db);
        evaluateTable= new EvaluateTable(db);
        praiseTable= new PraiseTable(db);
        evWordTable= new EvWordTable(db);
    }

    @Override
    public void setVoaTexts(Collection<VoaText> voaTexts, int voaId) {
         voaTextTable.setVoaTexts(voaTexts,voaId);
    }

    @Override
    public Observable<List<VoaText>> getVoaTexts(int voaId) {
        return voaTextTable.getVoaTexts(voaId);
    }

    @Override
    public boolean setCollect(String voaId, String uId, String title, String desc, String image, String series, String titleCn, String hot, String read, String sound) {
        // need update
        return collectTable.setCollect(voaId,uId,title,desc,image,series);
    }
    @Override
    public boolean setCollect(String voaId, String uId, String title, String desc, String image, String series) {
        return collectTable.setCollect(voaId,uId,title,desc,image,series);
    }

    @Override
    public void setDownload(String voaId, String uId, String title, String desc, String image, String series) {
        collectTable.setDownload(voaId,uId,title,desc,image,series);
    }

    @Override
    public void deleteCollect(String voaId, String uId) {
      collectTable.deleteCollect(voaId,uId);
    }

    @Override
    public void deleteDown(String voaId) {
        collectTable.deleteDown(voaId);
    }

    @Override
    public boolean getCollect(String voaId, String uId) {
        return collectTable.getCollect(voaId,uId);
    }

    @Override
    public List<TalkLesson> getCollectList(String uId) {
        return collectTable.getCollectList(uId);
    }

    @Override
    public List<TalkLesson> getDownList() {
        return collectTable.getDownList();
    }

    @Override
    public void setEvaluate(String voaId, String uId, String pathId, String paraId, String score, int progress, int progress2) {
          evaluateTable.setEvaluate(voaId,uId,pathId, paraId,score,progress,progress2);
    }

    @Override
    public void setFluent(String voaId, String uId, String paraId, int fluent, String url) {
        evaluateTable.setFluent(voaId,uId,paraId,fluent,url);
    }

    @Override
    public void setEvaluateTime(String voaId, String uId, String paraId, float beginTime, float endTime, float duration) {
        evaluateTable.setEvaluateTime(voaId,uId,paraId,beginTime,endTime,duration);
    }

    @Override
    public List<EvaluateScore> getEvaluate(String voaId, String uId) {
        return evaluateTable.getEvaluate(voaId,uId);
    }

    @Override
    public void setAgree(String uid, String id) {
        praiseTable.setAgree(uid,id);
    }

    @Override
    public boolean isAgree(String uid, String id) {
        return praiseTable.isAgree(uid,id);
    }

    @Override
    public void setEvWord(String voaId, String uId, String paraId, SendEvaluateResponse.WordsBean wordsBean) {
         evWordTable.setEvWord(voaId,uId,paraId,wordsBean);
    }

    @Override
    public List<SendEvaluateResponse.WordsBean> getEvWord(String voaId, String uId, String paraId) {
        return evWordTable.getEvWord(voaId,uId,paraId);
    }
}
