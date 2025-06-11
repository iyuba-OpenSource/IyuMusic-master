package com.iyuba.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Record implements Parcelable {
    @SerializedName("timestamp")
    public  long timestamp;
    @SerializedName("VoaId")
    public  int voaId;
    @SerializedName("title")
    public String title;
    @SerializedName("titleCn")
    public String titleCn;
    @SerializedName("Img")
    public String img;
    @SerializedName("totalNum")
    public  int totalNum;
    @SerializedName("finishNum")
    public  int finishNum;
    @SerializedName("date")
    public String date;
    @SerializedName("score")
    public String score;
    @SerializedName("audio")
    public String audio;



    protected Record(Parcel in) {
        timestamp = in.readLong();
        voaId = in.readInt();
        title = in.readString();
        titleCn = in.readString();
        img = in.readString();
        totalNum = in.readInt();
        finishNum = in.readInt();
        date = in.readString();
        score = in.readString();
        audio = in.readString();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeInt(voaId);
        dest.writeString(title);
        dest.writeString(titleCn);
        dest.writeString(img);
        dest.writeInt(totalNum);
        dest.writeInt(finishNum);
        dest.writeString(date);
        dest.writeString(score);
        dest.writeString(audio);
    }

}
