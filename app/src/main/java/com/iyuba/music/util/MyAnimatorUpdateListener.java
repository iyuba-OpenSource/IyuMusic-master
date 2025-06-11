package com.iyuba.music.util;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;

import com.iyuba.music.activity.study.StudyImageFragment;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;

import java.util.Locale;

public class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private Activity context;

    private CountDownTimer countDownTimer;

    public MyAnimatorUpdateListener(ObjectAnimator animator, Activity context) {
        this.animator = animator;
        this.context = context;
    }

    /**
     * 暂停状态
     */
    private boolean isPause = false;
    /**
     * 是否已经暂停，如果一已经暂停，那么就不需要再次设置停止的一些事件和监听器了
     */
    private boolean isPaused = false;

    private boolean isPlay = true;
    /**
     * 当前的动画的播放位置
     */
    private float fraction = 0.0f;
    /**
     * 当前动画的播放运行时间
     */
    private long mCurrentPlayTime = 0l;

    /**
     * 是否是暂停状态
     *
     * @return
     */
    private ObjectAnimator animator;

    public boolean isPause() {
        return isPause;
    }

    public boolean isPlay() {
        return isPlay;
    }

    /**
     * 停止方法，只是设置标志位，剩余的工作会根据状态位置在onAnimationUpdate进行操作
     */
    public void pause() {
        isPause = true;
        isPlay = false;
    }

    public void play() {
        isPause = false;
        isPaused = false;
        isPlay = true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        /**
         * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
         * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
         */
        if (isPause) {
            if (!isPaused) {
                mCurrentPlayTime = animation.getCurrentPlayTime();
                fraction = animation.getAnimatedFraction();
                animation.setInterpolator(new TimeInterpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return fraction;
                    }
                });
                isPaused = true;
            }
            // 每隔动画播放的时间，我们都会将播放时间往回调整，以便重新播放的时候接着使用这个时间,同时也为了让整个动画不结束
            countDownTimer = new CountDownTimer(ValueAnimator.getFrameDelay(),
                    ValueAnimator.getFrameDelay()) {

                @Override
                public void onTick(long millisUntilFinished) {


                    if (context != null) {
                        countDownTimer.cancel();
                    }
                }

                @Override
                public void onFinish() {
                    animator.setCurrentPlayTime(mCurrentPlayTime);
                }
            };
            countDownTimer.start();

        } else {
            // 将时间拦截器恢复成线性的，如果您有自己的，也可以在这里进行恢复
            animation.setInterpolator(null);
        }
    }

    //不销毁动画的情况下重置动画
    public void reSetAnim() {
        mCurrentPlayTime = 0l;
        animator.setCurrentPlayTime(mCurrentPlayTime);

    }

    //销毁动画和计时器
    public void setAnimDestroy() {

        if (animator != null) {
            animator.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

    }
}


