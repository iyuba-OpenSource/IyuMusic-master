package com.iyuba.music.entity;

import java.io.Serializable;

/**
 * 排行榜实体--新
 */

public class RankBean implements Serializable {

    private String uid = "";
    private String name = "";
    private String ranking = "";
    private String imgSrc = "";
    private String sort = "";

    //阅读
    private String wpm = "";
    private String cnt = "";  //文章数
    private String words = "";//单词数

    //听力和学习
    private String totalTime; //学习时间
    private String totalWord; //单词数
    private String totalEssay; //文章数


    //测试

    private String totalRight;//正确数
    private String totalTest;  //总题数

    //口语
    private String scores;//总分
    private String count;  //总文章数
    private String vip;  //vip


    public RankBean() {

    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getWpm() {
        return wpm;
    }

    public void setWpm(String wpm) {
        this.wpm = wpm;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(String totalWord) {
        this.totalWord = totalWord;
    }

    public String getTotalEssay() {
        return totalEssay;
    }

    public void setTotalEssay(String totalEssay) {
        this.totalEssay = totalEssay;
    }

    public String getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(String totalRight) {
        this.totalRight = totalRight;
    }

    public String getTotalTest() {
        return totalTest;
    }

    public void setTotalTest(String totalTest) {
        this.totalTest = totalTest;
    }





}
