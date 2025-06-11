package com.iyuba.music.data.local;

import com.iyuba.music.data.model.SendEvaluateResponse;

import java.util.List;


public interface EvWordTableInter {
    String TABLE_NAME = "EvaluateWord";
    String COLUMN_UID = "uId";
    String COLUMN_PARA_ID = "paraId";


    String COLUMN_VOA_ID = "voaId";
    String COLUMN_CONTENT = "content";//单词内容
    String COLUMN_SCORE = "score";//单词得分
    String COLUMN_INDEX= "indexId";//单词序号


    void setEvWord(String voaId, String uId, String paraId, SendEvaluateResponse.WordsBean wordsBean);

    List<SendEvaluateResponse.WordsBean> getEvWord(String voaId, String uId, String paraId);

}
