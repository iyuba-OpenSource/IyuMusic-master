package com.iyuba.music.dubbing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.VoaText;
import com.iyuba.music.dubbing.utils.NumberUtil;
import com.iyuba.music.dubbing.utils.StorageUtil;
import com.iyuba.music.dubbing.utils.TimeUtil;
import com.iyuba.music.dubbing.views.DubbingProgressBar;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.textpage.TextPage;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class DubbingAdapter extends RecyclerView.Adapter<DubbingAdapter.RecordHolder> {

    private static final int POSITION = 65;
    private RecordingCallback mRecordingCallback;
    private PlayRecordCallback mPlayRecordCallback;
    private PlayVideoCallback mPlayVideoCallback;
    private ScoreCallback mScoreCallback;

    private List<VoaText> mList;
    private long mTimeStamp;
    private VoaText mOperateVoaText;
    public RecordHolder mOperateHolder;

    private int mActivitePosition;
    int fluent = 0;


    public DubbingPresenter mPresenter;

    Context mContext;

    //public PreferencesHelper mHelper;

    private static final int FULL_PERCENT = 100;


    public boolean isRecording = false;

    public int mEvaluateNum; // 非会员评测句子数量 小于3

    public DubbingAdapter(DubbingPresenter presenter) {
        mPresenter = presenter;
        mList = new ArrayList<>();
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_talk, parent, false);
        mContext = parent.getContext();
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {
        holder.setItem(mList.get(position), mList, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(List<VoaText> mList) {
        this.mList = mList;
    }

    void setRecordingCallback(RecordingCallback mRecordingCallback) {
        this.mRecordingCallback = mRecordingCallback;
    }

    void setPlayRecordCallback(PlayRecordCallback mPlayRecordCallback) {
        this.mPlayRecordCallback = mPlayRecordCallback;
    }

    void setPlayVideoCallback(PlayVideoCallback mPlayVideoCallback) {
        this.mPlayVideoCallback = mPlayVideoCallback;
    }

    void setScoreCallback(ScoreCallback mScoreCallback) {
        this.mScoreCallback = mScoreCallback;
    }

    int getProgress(int total, int curPosition, float beginTime, float endTiming) {
        float curSec = TimeUtil.milliSecToSec(curPosition);
        return (int) (total * (curSec - beginTime) / (endTiming - beginTime));
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int curPosition = mPlayVideoCallback.getCurPosition();
            float recordEndTiming;
            float perfectTiming;
            int sidePos = mList.size() - 1;
            int firstProgress;
            if (mActivitePosition == sidePos) {
                recordEndTiming = mList.get(mActivitePosition).endTiming;//+ 0.1f;
                perfectTiming = mList.get(mActivitePosition).endTiming;
            } else {
                recordEndTiming = mList.get(mActivitePosition + 1).timing - 0.1f;//下一句的开始时间？
                perfectTiming = mList.get(mActivitePosition).endTiming;
            }
            switch (msg.what) {
                //  读配音进度条的更改
                case 1:
                    if (TimeUtil.milliSecToSec(curPosition) >= recordEndTiming) {
                        mPlayRecordCallback.stop();
                        //mOperateHolder.progressBar.setProgress(POSITION);
                        mOperateHolder.iPlay.setVisibility(View.VISIBLE);
                        mOperateHolder.iPause.setVisibility(View.INVISIBLE);
                        //mOperateHolder.progressBar.setSecondaryProgress(mOperateHolder.secondPosition);
                    } else {
                        handler.sendEmptyMessageDelayed(1, 25);
                        return;
                    }
                    break;
                // 录音计时
                case 2:
                    //视频不能停止！！！
                    firstProgress = getProgress(POSITION, curPosition, mList.get(mActivitePosition).timing, mList.get(mActivitePosition).endTiming);
                    mOperateHolder.progressBar.setProgress(firstProgress);
                    float progressPosition = TimeUtil.milliSecToSec(curPosition);
                    Timber.d("评测 progress 进度进行中" + progressPosition + " recordEndTi" + recordEndTiming + " curPosition" + curPosition);
                    if (progressPosition >= (recordEndTiming)) {
                        Timber.e("评测 progress结束 持续时间" + recordEndTiming);
                        endRecording();//未执行停止操作？？？
                        mOperateHolder.progressBar.setSecondaryProgress(getProgress(FULL_PERCENT, curPosition, mList.get(mActivitePosition).timing, recordEndTiming));
                        mOperateHolder.progressBar.setProgress(POSITION);
                    } else if (TimeUtil.milliSecToSec(curPosition) >= perfectTiming) {
                        if (mActivitePosition < sidePos) {
                            mOperateHolder.progressBar.setSecondaryProgress(getProgress(FULL_PERCENT,
                                    curPosition, mList.get(mActivitePosition).timing, mList.get(mActivitePosition + 1).timing));
                        } else {
                            mOperateHolder.progressBar.setSecondaryProgress(getProgress(FULL_PERCENT,
                                    curPosition, mList.get(mActivitePosition).timing, recordEndTiming));
                        }
                        mOperateHolder.progressBar.setProgress(POSITION);
                        handler.sendEmptyMessageDelayed(2, 80);
                    } else {
                        handler.sendEmptyMessageDelayed(2, 80);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void setTimeStamp(long timeStamp) {
        this.mTimeStamp = timeStamp;
    }

    class RecordHolder extends RecyclerView.ViewHolder implements TextPage.OnSelectListener {
        TextView tIndex;
        TextPage tContentEn;
        TextView tContentCh;
        DubbingProgressBar progressBar;
        TextView tTime;
        ImageView iPlay;
        ImageView iPause;
        ImageView iRecord;
        TextView scoreText;
        LinearLayout recordLayout;

        ImageView iv_pause;

        private float rate;

        int position1 = 0;
        int secondPosition = 100;
        VoaText mVoaText;
        AnimatorSet animatorSet = new AnimatorSet();


        RecordHolder(View itemView) {
            super(itemView);

            recordLayout = itemView.findViewById(R.id.record_layout);
            tIndex = itemView.findViewById(R.id.tV_index);
            scoreText = itemView.findViewById(R.id.voice_score);
            tContentEn = itemView.findViewById(R.id.tv_content_en);
            tContentCh = itemView.findViewById(R.id.tv_content_ch);
            iRecord = itemView.findViewById(R.id.iv_record);
            iPlay = itemView.findViewById(R.id.iv_play);
            iPause = itemView.findViewById(R.id.iv_pause);
            tTime = itemView.findViewById(R.id.tv_time);
            progressBar = itemView.findViewById(R.id.progress);
            iv_pause = itemView.findViewById(R.id.iv_pause);
            iPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onPlayClick();
                }
            });
            iv_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onPauseClick();
                }
            });
            iRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecordClick();
                }
            });
            recordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onLayoutClick();
                }
            });
        }

        @SuppressLint("CheckResult")
        public void setItem(VoaText voaText, List<VoaText> voaTextList, int pos) {
            tContentEn.setOnSelectListener(this);
            tContentEn.setLongClickable(false);//禁止长按，小米崩溃
            this.mVoaText = voaText;
            position1 = voaText.paraId;
            if (pos < voaTextList.size() - 1) {//如果不是最后一个
                progressBar.setAddingTime(MessageFormat.format(itemView.getResources().getString(R.string.record_time),
                        NumberUtil.keepOneDecimal((voaTextList.get(pos + 1).timing - voaText.endTiming))));
                rate = (voaText.endTiming - voaText.timing) * 100 / (voaTextList.get(pos + 1).timing - voaText.timing);
            } else {
                progressBar.setAddingTime(MessageFormat.format(itemView.getResources().getString(R.string.record_time),
                        NumberUtil.keepOneDecimal((voaText.endTiming - voaText.timing) / 4)));
                rate = 70;
            }
            progressBar.setPerfectTime(MessageFormat.format(itemView.getResources().getString(R.string.record_time),
                    NumberUtil.keepOneDecimal(voaText.endTiming - voaText.timing)));
            progressBar.setPosition(POSITION);
            //设置默认 视图
            setDefaultView();

            tTime.setText(MessageFormat.format(itemView.getResources().getString(R.string.record_time),
                    NumberUtil.keepOneDecimal(voaText.endTiming - voaText.timing)));
            if (mVoaText.getParseData() != null) {
                tContentEn.setText(mVoaText.getParseData());
            }
            if (mVoaText.isIscore()) {
                scoreText.setVisibility(View.VISIBLE);
                scoreText.setText(mVoaText.getScore() + "");
                if (mVoaText.getScore() >= 80) {
                    scoreText.setBackgroundResource(R.drawable.blue_circle);
                } else if (mVoaText.getScore() >= 45 && mVoaText.getScore() < 80) {
                    scoreText.setBackgroundResource(R.drawable.red_circle);
                } else {
                    scoreText.setBackgroundResource(R.drawable.scroe_low);
                    scoreText.setText("");
                }
            } else {
                scoreText.setVisibility(View.GONE);
            }
            if (mVoaText.isDataBase) {//写入数据库中记录的句子得分
                setTextColorSpan(this, voaText.words);
            }

            if (mVoaText.isIsshowbq()) {
                iPlay.setVisibility(View.VISIBLE);
                tIndex.setBackgroundResource(R.drawable.index_green);
            } else {
                iPlay.setVisibility(View.GONE);
                tIndex.setBackgroundResource(R.drawable.index_gray);
            }

            if (null != animatorSet) {
                animatorSet.cancel();
            }
        }

        private void setDefaultView() {
            iPlay.setVisibility(View.INVISIBLE);
            iPause.setVisibility(View.INVISIBLE);
            scoreText.setVisibility(View.GONE);
            tContentEn.setText(mVoaText.getSentenceEn());
            tContentCh.setText(mVoaText.sentenceCn);
            tIndex.setText(mVoaText.paraId + "/" + mList.size());
            if (checkExist(mVoaText.getVoaId(), mTimeStamp, mVoaText.paraId)) {
                tIndex.setBackgroundResource(R.drawable.index_green);
                progressBar.setProgress(POSITION);
                progressBar.setSecondaryProgress(secondPosition);
                iPlay.setVisibility(View.VISIBLE);
                iPause.setVisibility(View.INVISIBLE);
            } else {
                tIndex.setBackgroundResource(R.drawable.index_gray);
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                iPlay.setVisibility(View.INVISIBLE);
                iPause.setVisibility(View.INVISIBLE);
            }
        }

        private boolean checkExist(int voaId, long timestamp, int paraId) {
            File file = StorageUtil.getAccRecordFile(tIndex.getContext(), voaId, timestamp, paraId);
            return file.exists();
        }


        void onPlayClick() {
            mOperateHolder = this;
            isRecording = false;
            if (iPlay.getVisibility() == View.VISIBLE) {
                if (isRecording) {
                    mRecordingCallback.stop();
                }
                mActivitePosition = getAdapterPosition();
                if (mOperateHolder != null) {
                    mOperateHolder.tIndex.setBackgroundResource(R.drawable.index_green);
                    mOperateHolder.iPause.setVisibility(View.INVISIBLE);
                    mOperateHolder.iPlay.setVisibility(View.VISIBLE);
                }
                mOperateVoaText = mVoaText;
                iPlay.setVisibility(View.INVISIBLE);
                iPause.setVisibility(View.VISIBLE);
                playRecordTask(mOperateVoaText);
            }
        }

        public void playRecordTask(final VoaText voaText) {
            if (isRecording) {
                mRecordingCallback.stop();
                int progress = mOperateHolder.progressBar.getProgress();
                int secondaryProgress = mOperateHolder.progressBar.getSecondaryProgress();
                mRecordingCallback.upload(voaText.paraId, mList, progress, secondaryProgress);
                mRecordingCallback.convert(voaText.paraId, mList);
                isRecording = false;
            }
            mPlayVideoCallback.stop();
            mPlayRecordCallback.start(voaText);
            handler.sendEmptyMessage(1);
        }


        void onPauseClick() {
            if (iPause.getVisibility() == View.VISIBLE) {
                iPause.setVisibility(View.INVISIBLE);
                iPlay.setVisibility(View.VISIBLE);
                mPlayRecordCallback.stop();
                mPlayVideoCallback.stop();
                if (isRecording) {
                    isRecording = false;
                    mRecordingCallback.stop();
                    int progress = mOperateHolder.progressBar.getProgress();
                    int secondaryProgress = mOperateHolder.progressBar.getSecondaryProgress();
                    mRecordingCallback.upload(mVoaText.paraId, mList, progress, secondaryProgress);

                    mRecordingCallback.convert(mVoaText.paraId, mList);
                }
            }
            handler.removeCallbacksAndMessages(null);
        }

        //评测按钮  深入屎海，无法自拔
        void onRecordClick() {
            if (null != animatorSet) {
                animatorSet.cancel();
            }
            if (isRecording) {
                //getAdapterPosition() == mActivitePosition &&
                //int position = mPlayVideoCallback.getCurPosition();
//                this.secondPosition = progressBar.getSecondaryProgress() > POSITION ? progressBar.getSecondaryProgress() : 100;
//                if ( mOperateHolder.progressBar.getProgress() <= POSITION) {
//                    endRecording();//手动停止评测
//                    Timber.e("评测停止，终止");
//                }else {
//                    Timber.e("评测停止失败，不是当前评测item,继续评测");
//                }
                endRecording();
                return;
            } else {
                mOperateHolder = this;
                //非会员3句限制
//                if (!mVoaText.isEvaluate&&!AccountManager.Instance(itemView.getContext()).isVip()){
//                    if (mEvaluateNum>=3){
//                        final MaterialDialog materialDialog = new MaterialDialog(mContext);
//                        materialDialog.setTitle("提醒");
//                        materialDialog.setMessage("本篇你已评测3句！成为VIP后可评测更多");
//                        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                materialDialog.dismiss();
//                            }
//                        });
//                        materialDialog.show();
//                        return;
//                    }
//                }
            }

            scoreText.setText("");
            scoreText.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.white));
            if (mOperateVoaText == mVoaText && isRecording) {
                //handler.removeCallbacksAndMessages(null);
                //endRecording();//手动停止评测
                //mRecordingCallback.stop();
//                int progress = mOperateHolder.progressBar.getProgress();
//                int secondaryProgress =  mOperateHolder.progressBar.getSecondaryProgress();
//                mRecordingCallback.upload(mVoaText.paraId, mList,progress,secondaryProgress);
//
//                mRecordingCallback.convert(mVoaText.paraId, mList);
//                tIndex.setBackgroundResource(R.drawable.index_green);
//                iPlay.setVisibility(View.VISIBLE);
            } else {
                isRecording = true;
                mActivitePosition = getAdapterPosition();
                if (mOperateHolder != null) {
                    mOperateHolder.tIndex.setBackgroundResource(R.drawable.index_green);
                    mOperateHolder.iPlay.setVisibility(View.VISIBLE);
                    mOperateHolder.iPause.setVisibility(View.INVISIBLE);
                }
                mOperateVoaText = mVoaText;
                mVoaText.setIscore(true);


                //是否需要重新初始化
                File saveFile = StorageUtil.getAccRecordFile(
                        itemView.getContext(), mVoaText.getVoaId(), mTimeStamp, mVoaText.paraId);

                mRecordingCallback.init(saveFile.getAbsolutePath());
                mPlayRecordCallback.stop();
                //开始录音倒计时
                handler.sendEmptyMessage(2);
                mOperateHolder.progressBar.setProgress(0);
                mOperateHolder.progressBar.setSecondaryProgress(0);
                Timber.e("评测开始------111");
                mRecordingCallback.start(mVoaText);
                iPlay.setVisibility(View.INVISIBLE);
                iPause.setVisibility(View.INVISIBLE);

                //评测回调，通知adapter
                setCallBack(this);
            }
        }


        void onLayoutClick() {
            if (isRecording) {
                showMessage("评测中");
            } else {
                mActivitePosition = getAdapterPosition();
                repeatPlayVoaText(iPlay, mVoaText);
            }
        }

        private void setCallBack(final RecordHolder recordHolder) {
            //activity  通知adapter 更新数据
            ((DubbingActivity) mContext).setNewScoreCallback(new DubbingActivity.NewScoreCallback() {
                @Override
                public void onResult(int pos, int score, SendEvaluateResponse beans) {
                    if (pos == recordHolder.position1) {
                        if (null != recordHolder.animatorSet) {
                            recordHolder.animatorSet.cancel();
                        }
                        resetText(recordHolder);
                        //将计算后的 数返回给activity
                        int paraID = recordHolder.getAdapterPosition() + 1;//不再使用position 改为 paraId
                        mScoreCallback.onResult(paraID, score, fluent, beans.getURL());
                        setTextColorSpan(recordHolder, beans.getWords());
                        setScoreColor(recordHolder, score);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    ToastUtil.showToast(mContext, errorMessage);
                    if (null != recordHolder.animatorSet) {
                        recordHolder.animatorSet.cancel();
                    }
                }
            });
        }

        private void setTextColorSpan(RecordHolder recordHolder, List<SendEvaluateResponse.WordsBean> beans) {
            String string = recordHolder.tContentEn.getText().toString();
            SpannableString spannableString = new SpannableString(string);
            int curentIndex = 0;
            for (int i = 0; i < beans.size(); i++) {
                if (beans.get(i).getScore() < 2.5) {
                    spannableString.setSpan(new ForegroundColorSpan(Color.RED), curentIndex,
                            curentIndex + beans.get(i).getContent().length()
                            , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else if (beans.get(i).getScore() > 3.75) {
                    spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.GREEN)),
                            curentIndex, curentIndex + beans.get(i).getContent().length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                curentIndex += beans.get(i).getContent().length() + 1;
            }
            recordHolder.tContentEn.setText(spannableString);
            recordHolder.mVoaText.setParseData(new SpannableStringBuilder(spannableString));
        }

        private void setScoreColor(RecordHolder holder, int score) {
            if (score >= 80) {
                holder.scoreText.setBackgroundResource(R.drawable.blue_circle);
                holder.scoreText.setText(score + "");
                holder.mVoaText.setScore(score);
            } else if (score >= 50 && score < 80) {
                holder.scoreText.setBackgroundResource(R.drawable.red_circle);
                holder.scoreText.setText(score + "");
                holder.mVoaText.setScore(score);
            } else {
                holder.mVoaText.setScore(score);
                holder.scoreText.setBackgroundResource(R.drawable.scroe_low);
            }
            if (holder.mVoaText.isIsshowbq()) {
                holder.iPlay.setVisibility(View.VISIBLE);
                holder.tIndex.setBackgroundResource(R.drawable.index_green);
            }
        }

        private void resetText(RecordHolder holder) {
            holder.mVoaText.setIsshowbq(true);
            holder.mVoaText.setIscore(true);
            holder.scoreText.setRotation(0);
            holder.scoreText.setAlpha(1);
            holder.scoreText.setVisibility(View.VISIBLE);
        }

        private void showMessage(String msg) {
            ToastUtil.showToast(itemView.getContext(), msg);
        }


        @Override
        public void onSelect(String text) {
            if (!TextUtils.isEmpty(text)) {
                mPresenter.getNetworkInterpretation(text);
            } else {
                ToastUtil.showToast(mContext, "请取英文单词");
            }
        }
    }

    public void repeatPlayVoaText(final View view, final VoaText voaText) {
        mOperateVoaText = voaText;
        mPlayVideoCallback.start(voaText);
    }

    public VoaText getOperateVoaText() {
        return mOperateVoaText;
    }

    private void beginUITransform() {
        mOperateHolder.scoreText.setVisibility(View.VISIBLE);
        mOperateHolder.scoreText.setBackgroundResource(R.drawable.ic_wait_64px);
        ValueAnimator rotateAnim = ObjectAnimator.ofFloat(mOperateHolder.scoreText, "rotation", 0f, 45f);
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(mOperateHolder.scoreText, "alpha", 0f, 1f, 0f);
        rotateAnim.setRepeatCount(3000);
        fadeAnim.setRepeatCount(300);
        mOperateHolder.animatorSet.playTogether(rotateAnim, fadeAnim);
        mOperateHolder.animatorSet.setDuration(1050);
        mOperateHolder.animatorSet.start();
    }

    private void endRecording() {
        handler.removeCallbacksAndMessages(null);
        int temp = (int) (mOperateHolder.rate * 100 / mOperateHolder.progressBar.getSecondaryProgress());
        fluent = Math.min(temp, 100);
        beginUITransform();
        Timber.e("mRecordingTimer: >>> cancel");
        stopRecordTask(mOperateVoaText);
        //mOperateHolder.progressBar.setProgress(FULL_PERCENT);
        isRecording = false;
        mOperateHolder.iPlay.setVisibility(View.VISIBLE);
        mOperateHolder.tIndex.setBackgroundResource(R.drawable.index_green);
    }

    private void stopRecordTask(final VoaText voaText) {
        if (isRecording) {
            mRecordingCallback.stop();//stopRecording 停止评测
            int progress = mOperateHolder.progressBar.getProgress();
            int secondaryProgress = mOperateHolder.progressBar.getSecondaryProgress();
            mRecordingCallback.upload(voaText.paraId, mList, progress, secondaryProgress);
            mRecordingCallback.convert(voaText.paraId, mList);
        }
        mPlayVideoCallback.stop();
    }


    interface RecordingCallback {
        void init(String path);

        void start(VoaText voaText);

        boolean isRecording();

        void stop();

        void convert(int paraId, List<VoaText> list);

        void upload(int paraId, List<VoaText> list, int position, int secondaryProgress);
    }

    interface PlayRecordCallback {
        void start(VoaText voaText);

        void stop();

        int getLength();
    }

    interface PlayVideoCallback {
        void start(VoaText voaText);

        boolean isPlaying();

        int getCurPosition();

        void stop();
    }

    interface ScoreCallback {
        void onResult(int pos, int score, int fluence, String url);
    }

}

