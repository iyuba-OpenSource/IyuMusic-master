package com.iyuba.music.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class GetRankingResponse implements SingleParser<GetRankingResponse> {
    @SerializedName("ResultCode")
    public  int resultCode;
    @SerializedName("Message")
    @Nullable
    public String message;
    @SerializedName("PageNumber")
    @Nullable
    public Integer pageNumber;
    @SerializedName("TotalPage")
    @Nullable
    public Integer totalPage;
    @SerializedName("FirstPage")
    @Nullable
    public Integer firstPage;
    @SerializedName("PrevPage")
    @Nullable
    public Integer prevPage;
    @SerializedName("NextPage")
    @Nullable
    public Integer nextPage;
    @SerializedName("LastPage")
    @Nullable
    public Integer lastPage;
    @SerializedName("AddScore")
    @Nullable
    public Integer addScore;
    @SerializedName("Counts")
    @Nullable
    public Integer counts;
    @SerializedName("data")
    @Nullable
    public List<Ranking> data;


    @Override
    public Single<GetRankingResponse> parse() {
        if (resultCode == 511) {
            return Single.just(this);
        } if (resultCode == 510) {
            return Single.just(new GetRankingResponse());
        } else {
            return Single.error(new Throwable(message));
        }
    }
}
