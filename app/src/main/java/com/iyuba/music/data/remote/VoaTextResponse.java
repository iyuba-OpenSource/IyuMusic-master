package com.iyuba.music.data.remote;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;
import com.iyuba.music.data.model.VoaText;

import java.util.List;

import io.reactivex.Single;

public class VoaTextResponse implements SingleParser<List<VoaText>> {
    @SerializedName("total")
    public String total;
    @SerializedName("voatext")
    public List<VoaText> voaTexts;

    @Override
    public Single<List<VoaText>> parse() {
        if (voaTexts != null) {
            return Single.just(voaTexts);
        } else {
            return Single.error(new Throwable("request failed."));
        }
    }
}
