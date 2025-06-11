package com.iyuba.music.dubbing.comment;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.music.data.model.Comment;
import com.iyuba.music.entity.MusicVoa;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface CommentMvpView extends MvpView {
    void showComments(List<Comment> commentList);

    void showEmptyComment();

    void showToast(int id);

    void clearInputText();

    void setCommentNum(int num);

    void startDubbingActivity(MusicVoa voa);

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showCommentLoadingLayout();

    void dismissCommentLoadingLayout();

    void dismissRefreshingView();

}
