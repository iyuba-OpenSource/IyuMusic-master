package com.iyuba.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoaText implements Parcelable {


    @SerializedName("ImgPath")
    @Nullable
    public String imgPath;
    @SerializedName("EndTiming")
    public  float endTiming;  //停止时间(总的时间)
    @SerializedName("ParaId")
    public  int paraId;
    @SerializedName("IdIndex")
    public  int idIndex;
    @SerializedName("sentence_cn")
    public String sentenceCn;
    @SerializedName("ImgWords")
    @Nullable
    public String imgWords;
    @SerializedName("Timing")
    public  float timing;   // 开头的间隔时间(总的时间)
    @SerializedName("Sentence")
    public String sentence;

    private int score = 0;
    private int voaId;
    private String filename;
    private boolean iscore = false;
    private SpannableStringBuilder parseData=null;
    private boolean isshowbq=false;

    public  int progress;
    public  int progress2;

    public boolean isEvaluate;
    public boolean isDataBase;//是否是数据库数据

    public List<SendEvaluateResponse.WordsBean> words;

    public int getVoaId() {
        return voaId;
    }

    public void setIscore(boolean iscore) {
        this.iscore = iscore;
    }

    public boolean isIscore() {
        return iscore;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public boolean isIsshowbq() {
        return isshowbq;
    }

    public void setIsshowbq(boolean isshowbq) {
        this.isshowbq = isshowbq;
    }

    public SpannableStringBuilder getParseData() {
        return parseData;
    }

    public void setParseData(SpannableStringBuilder parseData) {
        this.parseData = parseData;
    }

    public void setVoaId(int voaId) {
        this.voaId = voaId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSentenceEn(){
        sentence = sentence.replace("  "," ");
        return sentence;
    }

    public VoaText(){

    }

    protected VoaText(Parcel in) {
        imgPath = in.readString();
        endTiming = in.readFloat();
        paraId = in.readInt();
        idIndex = in.readInt();
        sentenceCn = in.readString();
        imgWords = in.readString();
        timing = in.readFloat();
        sentence = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgPath);
        dest.writeFloat(endTiming);
        dest.writeInt(paraId);
        dest.writeInt(idIndex);
        dest.writeString(sentenceCn);
        dest.writeString(imgWords);
        dest.writeFloat(timing);
        dest.writeString(sentence);
    }

    public static final Creator<VoaText> CREATOR = new Creator<VoaText>() {
        @Override
        public VoaText createFromParcel(Parcel in) {
            return new VoaText(in);
        }

        @Override
        public VoaText[] newArray(int size) {
            return new VoaText[size];
        }
    };
    public String pathLocal;
}
