package com.iyuba.music.data.remote;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;
import com.iyuba.music.data.model.TalkClass;
import com.iyuba.music.data.model.TalkLesson;

import java.util.List;

import io.reactivex.Single;

public interface CmsResponse {

    class TalkClassList implements SingleParser<List<TalkClass>> {

        @SerializedName("total")
        public int total;
        @SerializedName("result")
        public int result;
        @SerializedName("message")
        public String message;
        @SerializedName("data")
        public List<TalkClass> data;


        @Override
        public Single<List<TalkClass>> parse() {
            if (result == 1) {
                return Single.just(data);
            }else {
                return Single.error(new Throwable("request fail."));
            }
        }
    }

    class TalkLessonList implements SingleParser<List<TalkLesson>> {

        @SerializedName("total")
        public int total;
        @SerializedName("result")
        public int result;
        @SerializedName("message")
        public String message;
        @SerializedName("data")
        public List<TalkLesson> data;


        @Override
        public Single<List<TalkLesson>> parse() {
            if (result == 1) {
                return Single.just(data);
            }else {
                return Single.error(new Throwable("request fail."));
            }
        }
    }
}
