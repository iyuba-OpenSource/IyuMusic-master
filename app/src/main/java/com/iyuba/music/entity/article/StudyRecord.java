package com.iyuba.music.entity.article;

/**
 * Created by 10202 on 2015/12/15.
 */
public class StudyRecord {
    private int id;
    private String startTime;
    private String endTime;
    private int flag;
    private String lesson;

    // lrcNum ，allTime 用于mtv视频播放
    private int lrcNum; //歌词总数
    private int allTime; //mtv播放总时间

    public int getLrcNum() {
        return lrcNum;
    }

    public void setLrcNum(int lrcNum) {
        this.lrcNum = lrcNum;
    }

    public int getAllTime() {
        return allTime;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }


}