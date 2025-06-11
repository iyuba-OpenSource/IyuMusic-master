package com.iyuba.music.activity.study;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.BlurUtil;
import com.iyuba.music.util.Marquee_Textview;
import com.iyuba.music.util.MyAnimatorUpdateListener;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.widget.CircleImageView;


import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyImageFragment extends BaseFragment {
    private Context context;

    private TextView title;

    private Marquee_Textview name;
    private CircleImageView imageView;

    private MyAnimatorUpdateListener listener;
    private BitmapDrawable drawable;
    private RelativeLayout root;

    private Article curArticle;
    private int flag = 1; //标志图片旋转
    private int stopFlag = 1; //标志图片停止旋转
    private int firstFlag = 1; //标记第一次创建fragment

    private long currentTime = 0;

    private Bitmap bitmap, bgbm;

    private ObjectAnimator anim;
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 0:
                    ((StudyActivity) context).studyroot.setBackground(drawable);

                    int tint = Color.parseColor("gray");
                    ((StudyActivity) context).studyroot.getBackground().setColorFilter(tint, PorterDuff.Mode.DARKEN);
                    break;
                case 1:
                    imageView.resetRoatate();
                    flag = 1;
                    break;

            }


        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stu_image, null);
        title = (TextView) view.findViewById(R.id.title);
        name = (Marquee_Textview) view.findViewById(R.id.name);
        imageView = (CircleImageView) view.findViewById(R.id.song_image);

        firstFlag = 1;
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = ScreenUtils.getScreenWidth(getActivity()) / 3 * 2;
        params.height = ScreenUtils.getScreenWidth(getActivity()) / 3 * 2;
        imageView.setLayoutParams(params);
        root = (RelativeLayout) view.findViewById(R.id.root);
        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void refresh() {
        if (imageView == null) {
            return;
        }
        currentTime = 0;
        flag = 1;
        if (firstFlag == 1) {
            firstFlag = 0;
        } else {
            if (getApplication().getPlayerService().getPlayer() != null && getApplication().getPlayerService().getPlayer().isPlaying()) {
                startAnim(imageView);
            }
        }
        curArticle = StudyManager.getInstance().getCurArticle();
        if (StudyManager.getInstance().getApp().equals("209")) {

            Glide.with(MusicApplication.getApp()).load("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl()).placeholder(R.drawable.default_music)
                    .error(R.drawable.default_music).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);

        } else {
            Glide.with(MusicApplication.getApp()).load(curArticle.getPicUrl()).placeholder(R.drawable.default_music)
                    .error(R.drawable.default_music).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);

        }
        try {
            title.setText(curArticle.getTitle().trim());
            StringBuffer buffer = new StringBuffer();
            buffer.append(curArticle.getSinger().trim());
            name.setText(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    bitmap = BlurUtil.getBitMBitmap("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl());

                  Log.e("background","http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl());
                    bgbm = BlurUtil.doBlur(bitmap, 100, 5);
                    drawable = new BitmapDrawable(bgbm);
                    handler.sendEmptyMessage(0);

                } catch (NullPointerException e) {

                    e.printStackTrace();
                }

            }
        }.start();

    }


    @SuppressLint("WrongConstant")
    private void anim() {
        if (anim == null) {
            anim = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);//设置动画为旋转动画，角度是0-360
        }
        LinearInterpolator lin = new LinearInterpolator();//声明为线性变化
        anim.setDuration(50000);//时间15秒，这个可以自己酌情修改
        anim.setInterpolator(lin);
        anim.setRepeatMode(Animation.RESTART);//设置重复模式为重新开始
        anim.setRepeatCount(-1);//重复次数为-1，就是无限循环
        listener = new MyAnimatorUpdateListener(anim, getActivity());//将定义好的ObjectAnimator传给MyAnimatorUpdateListener监听
        anim.addUpdateListener(listener);//给动画加监听
        anim.start();
        listener.pause();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAnim(ImageView imageView) {

        if (imageView == null) {
            return;
        }
        if (anim == null) {
            anim = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);//设置动画为旋转动画，角度是0-360
            anim.setDuration(15000);//时间15秒，这个可以自己酌情修改
            anim.setRepeatCount(-1);//重复次数为-1，就是无限循环
            LinearInterpolator lin = new LinearInterpolator();//声明为线性变化
            anim.setInterpolator(lin);
            anim.start();

        } else {

            Log.e("当前时间", currentTime + "pp");
            anim.setCurrentPlayTime(currentTime);
            anim.start();

        }


    }

    private void stopAnim(ImageView imageView) {
        if (imageView == null || anim == null) {
            return;
        }
        currentTime = anim.getCurrentPlayTime();
        Log.e("当前时间", currentTime + "ppp");
        anim.cancel();

    }

    //改变背景图
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void imageTrans(boolean play) {

        if (imageView == null) {
            return;
        }
//        if (listener == null) {
//            return;
//        }
//
//        flag = play;
//        handler.sendEmptyMessage(1);

        if (play) {
            if (flag == 1) {
                stopFlag = 1;
                flag = 2;
                Log.e("当前时间", currentTime + "pppp");
                startAnim(imageView);
            }

        } else {

            if (stopFlag == 1) {
                flag = 1;
                stopFlag = 2;
                stopAnim(imageView);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imageView.destroyRoatate();
//        listener.setAnimDestroy();

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (bgbm != null && !bgbm.isRecycled()) {
            bgbm.recycle();
            bgbm = null;
        }


        System.gc();

    }


}
