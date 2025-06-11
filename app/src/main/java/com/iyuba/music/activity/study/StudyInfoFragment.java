package com.iyuba.music.activity.study;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.textview.JustifyTextView;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyInfoFragment extends BaseFragment {
    private Context context;
    private ImageView img;
    private TextView title, singer, announcer;
    private JustifyTextView content;

    private boolean isFirst = true;

    private ObjectAnimator anim;

    private long currentTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = initView();
        refresh();
        return view;
    }

    private View initView() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.study_info, null);
        img = (ImageView) view.findViewById(R.id.article_img);
        title = (TextView) view.findViewById(R.id.article_title);
        announcer = (TextView) view.findViewById(R.id.article_announcer);
        singer = (TextView) view.findViewById(R.id.article_singer);
        content = (JustifyTextView) view.findViewById(R.id.article_abstract);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;

    }

    public void refresh() {

        if (img == null) {
            return;
        }

        Article curArticle = StudyManager.getInstance().getCurArticle();
        if (StudyManager.getInstance().getApp().equals("209")) {

//            ImageUtil.loadImage("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl(), img, R.drawable.default_music);


            Glide.with(MusicApplication.getApp()).load("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl()).placeholder(R.drawable.default_music)
                    .error(R.drawable.default_music).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(img);

            announcer.setText(context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
            announcer.setTextColor(Color.WHITE);
            singer.setText(context.getString(R.string.article_singer, curArticle.getSinger()));
            singer.setTextColor(Color.WHITE);
        } else {
            Glide.with(MusicApplication.getApp()).load(curArticle.getPicUrl()).placeholder(R.drawable.default_music)
                    .error(R.drawable.default_music).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(img);

//            ImageUtil.loadImage(curArticle.getPicUrl(), img, R.drawable.default_music);
            singer.setText(curArticle.getTitle_cn());
        }
        title.setText(curArticle.getTitle());
        content.setText(curArticle.getContent());
        content.setTextColor(Color.WHITE);
    }





}
