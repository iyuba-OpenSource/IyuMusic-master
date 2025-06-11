package com.iyuba.music.data.local;

/**
 * 点赞本地表，数据记录 配音点赞，本地记录，防止重复点赞
 */
public interface PraiseTableInter {
    String TABLE_NAME = "praise";

    String COLUMN_UID = "uId";
    String COLUMN_ID = "rank_id";

    String COLUMN_OTHER = "other";

    void setAgree(String uid, String id);

    boolean isAgree(String uid, String id);
}
