package com.iyuba.music.data.remote;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;
import com.iyuba.music.data.model.SendEvaluateResponse;

import io.reactivex.Single;

public interface AiResponse {

    class GetEvaluateResponse implements SingleParser<SendEvaluateResponse> {
        @SerializedName("result")
        public int result;
        @SerializedName("message")
        public String message;
        @SerializedName("data")
        public SendEvaluateResponse data;


        @Override
        public Single<SendEvaluateResponse> parse() {
            if (result == 1) {
                return Single.just(data);
            } else {
                return Single.error(new Throwable("request fail."));
            }
        }

    }
}
