package com.iyuba.music.data.local;

import java.util.List;

public interface EvaluateTableInter {
    String TABLE_NAME = "Evaluate";

    String COLUMN_UID = "uId";
    String COLUMN_VOA_ID = "voaId";
    String COLUMN_PARA_ID = "paraId";
    String COLUMN_SCORE = "score";
    String COLUMN_FLUENT = "fluent";//流畅度
    String COLUMN_URL = "url";
    String COLUMN_PATH = "path";
    String COLUMN_PROGRESS = "progress";
    String COLUMN_PROGRESS_TWO = "secondaryProgress";

    String COLUMN_BEGIN_TIME = "beginTime";//合成接口需要
    String COLUMN_END_TIME = "endTime";//合成接口需要
    String COLUMN_DURATION = "duration";//合成接口需要

    void setEvaluate(String voaId, String uId, String pathId, String paraId, String score, int progress, int progress2);

    List<EvaluateScore> getEvaluate(String voaId, String uId);

    void setFluent(String voaId, String uId, String paraId, int fluent, String url);

    void setEvaluateTime(String voaId, String uId, String paraId, float beginTime, float endTime, float duration);
}
