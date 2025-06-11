package com.iyuba.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public  class Ranking implements Parcelable {//
    @SerializedName("id")
    @Nullable
    public  int id;
    @SerializedName("backId")
    @Nullable
    public  int backId;
    @SerializedName("score")
    @Nullable
    public  float score;
    @SerializedName("Userid")
    @Nullable
    public  int userId;
    @SerializedName("UserName")
    @Nullable
    public String userName;
    @SerializedName("ImgSrc")
    @Nullable
    public String imgSrc;
    @SerializedName("againstCount")
    @Nullable
    public  int againstCount;
    @SerializedName("agreeCount")
    @Nullable
    public  int agreeCount;
    @SerializedName("Title")
    @Nullable
    public String title;
    @SerializedName("Title_cn")
    @Nullable
    public String titleCn;
    @SerializedName("TopicId")
    @Nullable
    public  int topicId ;
    @SerializedName("ShuoShuoType")
    @Nullable
    public  int shuoShuoType;
    @SerializedName("ShuoShuo")
    @Nullable
    public String shuoShuo;
    @SerializedName("CreateDate")
    @Nullable
    public String createDate;
    @SerializedName("videoUrl")
    @Nullable
    public String videoUrl;

    public int agreeNum;
    public boolean isAudioCommentPlaying = false;

    public boolean isDelete;

    public String getID(){
        return String.valueOf(id);
    }

    public Ranking(Parcel in) {
        id = in.readInt();
        backId = in.readInt();
        score = in.readFloat();
        userId = in.readInt();
        userName = in.readString();
        imgSrc = in.readString();
        againstCount = in.readInt();
        agreeCount = in.readInt();
        title = in.readString();
        titleCn = in.readString();
        topicId = in.readInt();
        shuoShuoType = in.readInt();
        shuoShuo = in.readString();
        createDate = in.readString();
        videoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(backId);
        dest.writeFloat(score);
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeString(imgSrc);
        dest.writeInt(againstCount);
        dest.writeInt(agreeCount);
        dest.writeString(title);
        dest.writeString(titleCn);
        dest.writeInt(topicId);
        dest.writeInt(shuoShuoType);
        dest.writeString(shuoShuo);
        dest.writeString(createDate);
        dest.writeString(videoUrl);
    }

    public static final Creator<Ranking> CREATOR = new Creator<Ranking>() {
        @Override
        public Ranking createFromParcel(Parcel in) {
            return new Ranking(in);
        }

        @Override
        public Ranking[] newArray(int size) {
            return new Ranking[size];
        }
    };
}
