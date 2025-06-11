package com.iyuba.music.data.local;

public class EvaluateScore {

    public String userId;

    public String voaId;

    public String paraId;

    public String score;

    public int progress;
    public int progress2;

    public int fluent;
    public String url;
    public String path;

    public float beginTime;
    public float endTime;
    public float duration;

    public int getScore(){
        return Integer.parseInt(score);
    }
    public int getParaId(){
        return Integer.parseInt(paraId);
    }


}
