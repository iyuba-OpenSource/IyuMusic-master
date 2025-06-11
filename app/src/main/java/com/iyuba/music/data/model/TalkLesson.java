package com.iyuba.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class TalkLesson implements Parcelable {


    /**
     * Category :
     * CreateTime : 2020-05-26 15:04:58.0
     * Title : Unit1 Hello
     * Sound : http://staticvip.iyuba.cn/sounds/voa/202005/321001.mp3
     * Pic : http://staticvip.iyuba.cn/images/voa/321001.jpg
     * Flag : 1
     * Type : series
     * DescCn : Unit1 Hello Lesson 1 and Lesson 2
     * Title_cn : Unit1 Hello Lesson 1 and Lesson 2
     * series : 278
     * CategoryName :
     * Id : 321001
     * ReadCount : 1
     */

    @SerializedName("Category")
    public String Category;
    @SerializedName("CreateTime")
    public String CreateTime;
    @SerializedName("Title")
    public String Title;
    @SerializedName("Sound")
    public String Sound;
    @SerializedName("Pic")
    public String Pic;
    @SerializedName("Flag")
    public String Flag;
    @SerializedName("Type")
    public String Type;
    @SerializedName("DescCn")
    public String DescCn;
    @SerializedName("Title_cn")
    public String TitleCn;
    @SerializedName("series")
    public String series;
    @SerializedName("CategoryName")
    public String CategoryName;
    @SerializedName("Id")
    public String Id;
    @SerializedName("ReadCount")
    public String ReadCount;

    public boolean isDelete;//删除收藏，下载

    public TalkLesson(){

    }

    public TalkLesson(String category, String createTime, String title, String sound, String pic, String flag, String type, String descCn, String titleCn, String series, String categoryName, String id, String readCount) {
        Category = category;
        CreateTime = createTime;
        Title = title;
        Sound = sound;
        Pic = pic;
        Flag = flag;
        Type = type;
        DescCn = descCn;
        TitleCn = titleCn;
        this.series = series;
        CategoryName = categoryName;
        Id = id;
        ReadCount = readCount;
    }

    protected TalkLesson(Parcel in) {
        Category = in.readString();
        CreateTime = in.readString();
        Title = in.readString();
        Sound = in.readString();
        Pic = in.readString();
        Flag = in.readString();
        Type = in.readString();
        DescCn = in.readString();
        TitleCn = in.readString();
        series = in.readString();
        CategoryName = in.readString();
        Id = in.readString();
        ReadCount = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Category);
        dest.writeString(CreateTime);
        dest.writeString(Title);
        dest.writeString(Sound);
        dest.writeString(Pic);
        dest.writeString(Flag);
        dest.writeString(Type);
        dest.writeString(DescCn);
        dest.writeString(TitleCn);
        dest.writeString(series);
        dest.writeString(CategoryName);
        dest.writeString(Id);
        dest.writeString(ReadCount);
    }
    public static final Creator<TalkLesson> CREATOR = new Creator<TalkLesson>() {
        @Override
        public TalkLesson createFromParcel(Parcel in) {
            return new TalkLesson(in);
        }

        @Override
        public TalkLesson[] newArray(int size) {
            return new TalkLesson[size];
        }
    };

    public int voaId(){
        return Integer.parseInt(Id);
    }


    public int getDifficulty(){
        switch (series){
            case "278":
                return 1;
            case "279":
                return 2;
            case "280":
                return 3;
            case "281":
                return 4;
            case "282":
            case "283":
                return 5;
            default:
                return 5;
        }
    }

    public String getSound(){
        if (TextUtils.isEmpty(Sound)){
            return "http://staticvip.iyuba.cn/sounds/voa/202006/"+Id+".mp3";
        }
        return Sound;
    }

    public int category(){
        return 321; //新概念青少 固定
    }

    public String getVideoPath(){
        return "http://staticvip.iyuba.cn/video/voa/321/" + Id + ".mp4";

    }
}
