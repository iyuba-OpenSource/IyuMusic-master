package com.iyuba.music.data.model;

/**
 * Created by Administrator on 2017/1/7/007.
 */

public  class Thumb {
    public  int uid;
    public  int commentId;
    public int action;

    public abstract static class Builder {
        public abstract Builder setUid(int uid);
        public abstract Builder setCommentId(int commentId);
        public abstract Thumb build();
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}
