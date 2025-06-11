package com.iyuba.music.event;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.file.FileComparator;
import com.iyuba.music.file.FileInfo;
import com.iyuba.music.file.FileUtil;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 播放音乐
 */
public class StudyEvent {


    public List<Article> articles;
    public String type;
    public int indexd;

    public StudyEvent(List<Article> articles, int indexd, String type) {
        this.articles = articles;
        this.type = type;
        this.indexd = indexd;
    }
}