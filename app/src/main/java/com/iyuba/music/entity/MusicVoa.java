package com.iyuba.music.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by carl shen on 2020/9/18.
 */
public class MusicVoa implements Serializable {
    @SerializedName("IntroDesc")
    public String introDesc;
    @SerializedName("CreatTime")
    public String createTime;
    @SerializedName("Category")
    public int category;
    @SerializedName("Keyword")
    public String keyword;
    @SerializedName("Title")
    public String title;
    @SerializedName("Texts")
    public String texts;
    @SerializedName("Sound")
    public String sound;
    @SerializedName("Pic")
    public String pic;
    @SerializedName("VoaId")
    public int voaId;
    @SerializedName(value = "video", alternate = {"Video"})
    public String video;
    @SerializedName("Pagetitle")
    public String pageTitle;
    @SerializedName("Url")
    public String url;
    @SerializedName("DescCn")
    public String descCn;
    @SerializedName("Title_cn")
    public String titleCn;
    @SerializedName("PublishTime")
    public String publishTime;
    @SerializedName("HotFlg")
    public int hotFlag;
    @SerializedName("ReadCount")
    public int readCount;

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("MusicVoa{");
        stringBuffer.append("IntroDesc=").append(introDesc);
        stringBuffer.append(",CreatTime=").append(createTime);
        stringBuffer.append(",Category=").append(category);
        stringBuffer.append(",Keyword=").append(keyword);
        stringBuffer.append(",Title=").append(title);
        stringBuffer.append(",Texts=").append(texts);
        stringBuffer.append(",Sound=").append(sound);
        stringBuffer.append(",Pic=").append(pic);
        stringBuffer.append("VoaId=").append(voaId);
        stringBuffer.append(",Pagetitle=").append(pageTitle);
        stringBuffer.append(",Url=").append(url);
        stringBuffer.append(",DescCn=").append(descCn);
        stringBuffer.append(",Title_cn=").append(titleCn);
        stringBuffer.append(",PublishTime=").append(publishTime);
        stringBuffer.append(",HotFlg=").append(hotFlag);
        stringBuffer.append(",ReadCount=").append(readCount);
        stringBuffer.append(",Video=").append(video);
        stringBuffer.append("}\n");
        return stringBuffer.toString();
    }
}
