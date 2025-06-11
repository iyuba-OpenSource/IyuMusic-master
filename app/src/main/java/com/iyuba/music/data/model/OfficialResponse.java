package com.iyuba.music.data.model;

import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

public class OfficialResponse implements SingleParser<OfficialResponse> {

    public int result;
    public int size;
    public String message;
    public List<OfficialAccount> data;

    public class OfficialAccount {
        public String newsfrom;
        public String createTime;
        public String image_url;
        public String title;
        public String url;
        public int count;
        public int id;
    }

    @Override
    public Single<OfficialResponse> parse() {
        if (result == 200) {
            return Single.just(this);
        } else {
            return Single.error(new Throwable(message));
        }
    }
}
