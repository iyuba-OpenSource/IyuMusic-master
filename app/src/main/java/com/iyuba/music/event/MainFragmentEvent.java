package com.iyuba.music.event;

import com.iyuba.music.entity.article.Article;

import java.util.List;

/**
 * 播放音乐
 */
public class MainFragmentEvent {


    public List<Article> articles;
    public String type;
    public int indexd;

    public MainFragmentEvent(List<Article> articles, int indexd, String type) {
        this.articles = articles;
        this.type = type;
        this.indexd = indexd;
    }
}