package com.iyuba.music.manager;

import androidx.collection.ArrayMap;

import com.iyuba.music.entity.article.Article;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyManager {
    private Article curArticle;
    private ArrayList<Article> sourceArticleList;
    private ArrayList<Article> curArticleList;
    private String app;
    private String listFragmentPos;
    private String lesson;
    private String startTime;
    private boolean isStartPlaying;
    private Map<String, String> singleInstanceRequest;

    private String studyWord; //听完一首歌的单词数
    private int allTime;//正常播放一首歌的时间

    private StudyManager() {
        app = ConstantManager.appId;
        sourceArticleList = new ArrayList<>();
        singleInstanceRequest = new ArrayMap<>();
    }

    public static StudyManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public void next() {
        int posRandom;

        if (ConfigManager.getInstance().getStudyPlayMode() == 2) {
            //随机播放


            int pos = curArticleList.indexOf(curArticle);
            posRandom = (int) (Math.random() * curArticleList.size());

            while (pos == posRandom) {
                posRandom = (int) (Math.random() * curArticleList.size());
            }
            curArticle = curArticleList.get(posRandom);
            app = curArticle.getApp();

        } else {
            int pos = curArticleList.indexOf(curArticle);
            pos = (pos + 1) % curArticleList.size();
            curArticle = curArticleList.get(pos);
            app = curArticle.getApp();

        }
    }

    public void before() {
        int posRandom;

        if (ConfigManager.getInstance().getStudyPlayMode() == 2) {
            //随机播放


            int pos = curArticleList.indexOf(curArticle);
            posRandom = (int) (Math.random() * curArticleList.size());

            while (pos == posRandom) {
                posRandom = (int) (Math.random() * curArticleList.size());
            }
            curArticle = curArticleList.get(posRandom);
            app = curArticle.getApp();

        } else {
            int pos = curArticleList.indexOf(curArticle);
            pos = (pos - 1 + curArticleList.size()) % curArticleList.size();
            curArticle = curArticleList.get(pos);
            app = curArticle.getApp();

        }
    }

    public void setSourceArticleList(ArrayList<Article> sourceArticleList) {
        this.sourceArticleList = sourceArticleList;
        generateArticleList();
    }

    public ArrayList<Article> getCurArticleList() {
        return curArticleList;
    }

    public Article getCurArticle() {
        return curArticle;
    }

    public void setCurArticle(Article curArticle) {
        this.curArticle = curArticle;
        this.app = curArticle.getApp();
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getListFragmentPos() {
        return listFragmentPos;
    }

    public void setListFragmentPos(String listFragmentPos) {
        this.listFragmentPos = listFragmentPos;
    }

    public boolean isStartPlaying() {
        return isStartPlaying;
    }

    public void setStartPlaying(boolean startPlaying) {
        isStartPlaying = startPlaying;
    }

    public int getMusicType() {
        if (!app.equals("209")) {
            return 0;
        } else if (curArticle != null && curArticle.getSimple() == 1) {
            return 0;
        } else {
            return ConfigManager.getInstance().getStudyMode();
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void generateArticleList() {
        curArticleList = new ArrayList<>();
        switch (ConfigManager.getInstance().getStudyPlayMode()) {
            case 0:
//                curArticleList.add(curArticle);
                curArticleList.addAll(sourceArticleList);
                break;
            case 1:
                curArticleList.addAll(sourceArticleList);
                break;
            case 2:
//                ArrayList<Article> temp = new ArrayList<>();
//                temp.addAll(sourceArticleList);
//                Collections.shuffle(temp);
//                curArticleList.addAll(temp);

                curArticleList.addAll(sourceArticleList);
                break;
        }
    }

    public int getAllTime() {
        return allTime;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }

    public String getStudyWord() {
        return studyWord;
    }

    public void setStudyWord(String studyWord) {
        this.studyWord = studyWord;
    }

    public Map<String, String> getSingleInstanceRequest() {
        return singleInstanceRequest;
    }

    private static class SingleInstanceHelper {
        private static StudyManager instance = new StudyManager();
    }
}
