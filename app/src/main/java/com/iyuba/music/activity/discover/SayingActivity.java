package com.iyuba.music.activity.discover;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.word.Saying;
import com.iyuba.music.entity.word.SayingOp;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.RandomUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.textview.JustifyTextView;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.nineoldandroids.animation.Animator;

/**
 * Created by 10202 on 2015/12/2.
 * 经典谚语
 */
public class SayingActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private TextView chinese;
    private JustifyTextView english;
    private RoundTextView next;
    private View sayingContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saying);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        english = (JustifyTextView) findViewById(R.id.saying_english);
        chinese = (TextView) findViewById(R.id.saying_chinese);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        sayingContent = findViewById(R.id.saying_content);
        next = (RoundTextView) findViewById(R.id.saying_next);
        AddRippleEffect.addRippleEffect(next);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfigManager.getInstance().getSayingMode() == 1) {
                    ConfigManager.getInstance().setSayingMode(0);
                } else {
                    ConfigManager.getInstance().setSayingMode(1);
                }
                changeUIResumeByPara();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.word_saying_title);
    }

    protected void changeUIResumeByPara() {
        if (ConfigManager.getInstance().getSayingMode() == 1) {
            toolbarOper.setText(R.string.word_saying_manualchange);
            next.setVisibility(View.INVISIBLE);
        } else {
            toolbarOper.setText(R.string.word_saying_autochange);
            next.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private int getRandomId() {
        return RandomUtil.getRandomInt(1000) % 154 + 1;
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<SayingActivity> {
        @Override
        public void handleMessageByRef(final SayingActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.FadeOutRight).duration(300).interpolate(new AccelerateDecelerateInterpolator()).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            activity.handler.sendEmptyMessage(1);
                            YoYo.with(Techniques.FadeInLeft).duration(300).interpolate(new AccelerateDecelerateInterpolator()).playOn(activity.sayingContent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(activity.sayingContent);
                    break;
                case 1:
                    Saying saying = new SayingOp().findDataById(activity.getRandomId());
                    activity.chinese.setText(saying.getChinese());
                    activity.english.setText(saying.getEnglish());
                    if (ConfigManager.getInstance().getSayingMode() == 0) {
                        activity.handler.removeMessages(0);
                    } else {
                        activity.handler.sendEmptyMessageDelayed(0, 4500);
                    }
                    break;
            }
        }
    }
}
