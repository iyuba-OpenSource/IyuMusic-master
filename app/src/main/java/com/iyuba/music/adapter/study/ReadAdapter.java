package com.iyuba.music.adapter.study;

import android.Manifest;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.assessment.EvaluateRequset;
import com.iyuba.assessment.EvaluatorListener;
import com.iyuba.assessment.EvaluatorResult;
import com.iyuba.assessment.IseManager;
import com.iyuba.assessment.SpeechError;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.data.model.SendDubbingResponse;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.WavListItem;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.dubbing.utils.MediaRecordHelper;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.AssessmentDialog;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.player.SimplePlayer;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import permissions.dispatcher.PermissionUtils;

/**
 * Created by 10202 on 2015/10/10.
 */
public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.MyViewHolder> implements IOperationResultInt {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ArrayList<Original> originals;
    private String mUid;
    private Context context;
    private int curItem = -1;
    private SimplePlayer player, simplePlayer;
    private TextView curText;
    private boolean isRecord;
    private boolean isSending = false;
    private Article curArticle;
    private IyubaDialog waittingDialog;
    private AssessmentDialog assessmentDialog;
    private MediaRecordHelper mediaRecordHelper;
    private EvaluatorListener evaluatorListener = new EvaluatorListener() {
        private static final String TAG = "assessment";

        @Override
        public void onResult(EvaluatorResult result, boolean isLast) {
            Log.e("ReadAdapter", "assessment onResult isLast " + isLast);
            if (isLast) {
                waittingDialog.dismiss();
//                XmlResultParser resultParser = new XmlResultParser();
//                Result resultEva = resultParser.parse(result.getResultString());
//                if (resultEva.is_rejected) {
//                    CustomToast.getInstance().showToast(R.string.read_refused);
//                } else {
//                    assessmentDialog.show(resultEva.total_score * 20);
//                }
                IseManager.getInstance(context).transformPcmToAmr();
            }
        }

        @Override
        public void onError(SpeechError error) {
            waittingDialog.dismiss();
            if (error != null) {
//                CustomToast.getInstance().showToast("error:" + error.getErrorCode() + "," + error.getErrorDescription());
            }
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
            Log.e("ReadAdapter", "assessment onEndOfSpeech  ");
            handler.sendEmptyMessage(3);
            waittingDialog.show();
            mediaRecordHelper.stop_record();
//            setRequest();
            sendVoice();
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onVolumeChanged(int volume, byte[] arg1) {

        }
    };

    public ReadAdapter(Context context) {
        this.context = context;
        simplePlayer = new SimplePlayer(context);
        curItem = 0;
        isRecord = false;
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.read_assessment));
        assessmentDialog = new AssessmentDialog(context);
        originals = new ArrayList<>();
        player = new SimplePlayer(context);
        curArticle = StudyManager.getInstance().getCurArticle();
        player.setVideoPath(getPath());
        assessmentDialog.setListener(this);
        mediaRecordHelper = new MediaRecordHelper();
        mUid = AccountManager.getInstance().getUserId();
    }

    public void setDataSet(ArrayList<Original> originals) {
        this.originals = originals;
        notifyDataSetChanged();
    }

    @Override
    public void performance(int index) {
        Log.e("ReadAdapter", "performance index " + index);
        switch (index) {
            case 0:
                if (AccountManager.getInstance().checkUserLogin()) {
                    sendVoice();
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            sendVoice();
                        }
                    });
                }
                break;
            case 1:
                simplePlayer.setVideoPath(ConstantManager.recordFile + IseManager.AMR_SUFFIX);
                simplePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        simplePlayer.start();
                        handler.obtainMessage(4, simplePlayer.getDuration(), 0).sendToTarget();
                    }
                });
                simplePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        simplePlayer.reset();
                        handler.sendEmptyMessage(5);
                    }
                });
                break;
            case 2:
                resetFunction();
                isRecord = true;
                initRecorder(originals.get(curItem).getSentence());
                break;
        }
    }

    private void setRequest() {
        Map<String, String> textParams = new HashMap<String, String>();
        File file = new File(ConstantManager.recordFile);
        textParams.put("type", Constant.EVAL_TYPE);
        textParams.put("userId", AccountManager.getInstance().getUserId());
        textParams.put("newsId", curArticle.getId() + "");
        textParams.put("platform", "android");
        textParams.put("protocol", "60003");
        textParams.put("paraId", originals.get(curItem).getParaID() + "");
        textParams.put("IdIndex", originals.get(curItem).getSentenceID() + "");
        String urlSentence = TextAttr.encode(originals.get(curItem).getSentence());
        urlSentence = urlSentence.replaceAll("\\+", "%20");
        textParams.put("sentence", urlSentence);
        Log.e("ReadAdapter", "setRequest file " + file.getAbsolutePath());
        if (file != null && file.exists()) {
            isSending = true;
            try {
                EvaluateRequset.post(Constant.EVALUATE_URL, textParams, ConstantManager.recordFile, handler);
            } catch (Exception e) {
                isSending = false;
                if (e != null) {
                    Log.e("ReadAdapter", "setRequest Exception " + e.getMessage());
                }
            }
        } else {
            Log.e("ReadAdapter", "setRequest file is not exist!");
        }
    }

    private void sendVoice() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String sb = "http://daxue." + ConstantManager.IYUBA_CN + "appApi/UnicomApi?protocol=60003&platform=android&appName=music&format=json"
                        + "&userid=" +
                        AccountManager.getInstance().getUserId() +
                        "&shuoshuotype=" + 2 +
                        "&voaid=" + curArticle.getId();
                Log.e("ReadAdapter", "sendVoice ConstantManager.recordFile " + ConstantManager.recordFile);
                Log.e("ReadAdapter", "sendVoice sb " + sb);
                final File file = new File(ConstantManager.recordFile);
                isSending = true;
                UploadFile.postSound(sb, file, new IOperationResult() {
                    @Override
                    public void success(Object object) {
//                        file.delete();
                        isSending = false;
                        if (object != null) {
                            Log.e("ReadAdapter", "sendVoice success " + object.toString());
                            Message message = Message.obtain();
                            message.what = 8;
                            message.obj = object.toString();
                            message.arg1 = curItem;
                            handler.sendMessage(message);
                        } else {
                            Log.e("ReadAdapter", "sendVoice success with null object.");
                        }
                    }

                    @Override
                    public void fail(Object object) {
                        isSending = false;
                        handler.sendEmptyMessage(7);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_read, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Original original = originals.get(position);
        holder.setOriginal(original);

        holder.num.setText(String.valueOf(position + 1));
        holder.english.setText(original.getSentence());
        if (TextUtils.isEmpty(original.getSentence_cn())) {
            holder.chinese.setVisibility(View.GONE);
        } else {
            holder.chinese.setText(original.getSentence_cn());
            holder.chinese.setVisibility(View.VISIBLE);
        }
        if (position == curItem) {
            curText = holder.recordTime;
            holder.readControl.setVisibility(View.VISIBLE);
        } else {
            holder.readControl.setVisibility(View.GONE);
        }
        if (player.isPlaying()) {
            holder.play.setImageResource(R.drawable.read_play);
            holder.recordTime.setVisibility(View.VISIBLE);

        } else {

            holder.play.setImageResource(R.drawable.read_pause);
            holder.recordTime.setVisibility(View.INVISIBLE);
        }
        if (isRecord) {
            holder.record.setImageResource(R.drawable.recording);
        } else {
            holder.record.setImageResource(R.drawable.record);
        }
        if (original.isRead) {
            holder.replay.setVisibility(View.VISIBLE);
        } else {
            holder.replay.setVisibility(View.INVISIBLE);
        }

        if (simplePlayer != null && simplePlayer.isPlaying()) {

            holder.replay.setImageResource(R.drawable.record_playing);
        } else {

            holder.replay.setImageResource(R.drawable.record_play);
        }
    }

    @Override
    public int getItemCount() {
        return originals.size();
    }

    private String getPath() {
        String url = DownloadUtil.getSongUrl(curArticle.getApp(), curArticle.getMusicUrl());
        StringBuilder localUrl = new StringBuilder();
        localUrl.append(ConstantManager.musicFolder).append(File.separator).append(curArticle.getId()).append(".mp3");
        File localFile = new File(localUrl.toString());
        if (localFile.exists()) {
            return localUrl.toString();
        } else {
            return url;
        }
    }

    private String getMP3FileName() {
        if ((originals != null) && (originals.get(curItem) != null)) {
            return ConstantManager.envir + "/" + originals.get(curItem).getArticleID() + originals.get(curItem).getParaID() + ".mp3";
        } else {
            return ConstantManager.envir + "/" + System.currentTimeMillis() + ".mp3";
        }
    }

    private void initRecorder(String sentence) {
        ConstantManager.recordFile = getMP3FileName();
        IseManager.getInstance(context).startEvaluate(sentence, ConstantManager.recordFile, evaluatorListener);
        mediaRecordHelper.setFilePath(ConstantManager.recordFile);
        mediaRecordHelper.recorder_Media();
        Log.e("ReadAdapter", "initRecorder ConstantManager.recordFile " + ConstantManager.recordFile);
        handler.obtainMessage(2, 0, 0).sendToTarget();
    }

    private void resetFunction() {
        if (player.isPrepared() && player.isPlaying()) {
            player.pause();
        }
        if (isRecord && mediaRecordHelper != null) {
            isRecord = false;
            mediaRecordHelper.stop_record();
            IseManager.getInstance(context).cancelEvaluate();
        }
        if (simplePlayer.isPrepared() && simplePlayer.isPlaying()) {
            simplePlayer.pause();
            simplePlayer.reset();
        }
        handler.removeCallbacksAndMessages(null);
        notifyItemChanged(curItem);
    }

    public void onDestroy() {
        player.stopPlayback();
        simplePlayer.stopPlayback();
        if (isRecord) {
            IseManager.getInstance(context).cancelEvaluate();
        }
        IseManager.getInstance(context).releaseResource();
        handler.removeCallbacksAndMessages(null);
    }

    public class MyViewHolder extends RecycleViewHolder {

        TextView num, english, chinese, recordTime;
        View readControl;
        ImageView play, record;
        ImageView replay, send;
        LinearLayout readTitle;

        Original original;

        public Original getOriginal() {
            return original;
        }

        public void setOriginal(Original original) {
            this.original = original;
        }

        public MyViewHolder(View view) {
            super(view);
            num = (TextView) view.findViewById(R.id.read_num);
            english = (TextView) view.findViewById(R.id.read_text);
            chinese = (TextView) view.findViewById(R.id.read_text_zh);
            readTitle = (LinearLayout) view.findViewById(R.id.read_title);
            replay = (ImageView) view.findViewById(R.id.read_replay);
            send = (ImageView) view.findViewById(R.id.read_send);
            readControl = view.findViewById(R.id.read_control);
            play = (ImageView) view.findViewById(R.id.read_play);
            record = (ImageView) view.findViewById(R.id.read_record);
            recordTime = (TextView) view.findViewById(R.id.record_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isRecord || isSending) {
                        CustomToast.getInstance().showToast("正在录音评测中，请暂时不要选择其它条目。");
                        return;
                    }
                    if (curItem != getBindingAdapterPosition()) {
                        int oldPos = curItem;
                        curItem = getBindingAdapterPosition();
                        notifyItemChanged(oldPos);
                        resetFunction();
//                    player.seekTo((int) original.getStartTime() * 1000);
//                    player.start();
//                    int arg1 = (int) original.getEndTime() * 1000;
//                    if (arg1 == 0) {
//                        if (pos + 1 == originals.size()) {
//                            arg1 = player.getDuration() - 500;
//                        } else {
//                            arg1 = (int) originals.get(pos + 1).getStartTime() * 1000;
//                        }
//                    }
//                    handler.obtainMessage(0, arg1, 0).sendToTarget();
                    }
                }
            });

            replay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (simplePlayer.isPlaying()) {

                        replay.setImageResource(R.drawable.record_play);
                        simplePlayer.pause();
                    } else {

                        resetFunction();
                        if (TextUtils.isEmpty(original.pathLocal)) {
                            Log.e("ReadAdapter", "replay original.pathLocal is null?");
                            original.pathLocal = ConstantManager.recordFile;
                        }
                        simplePlayer.setVideoPath(original.pathLocal);
                        simplePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                simplePlayer.start();
                                handler.obtainMessage(4, simplePlayer.getDuration(), 0).sendToTarget();
                                replay.setImageResource(R.drawable.record_playing);
                            }
                        });
                        simplePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                simplePlayer.reset();
                                handler.sendEmptyMessage(5);
                                replay.setImageResource(R.drawable.record_play);
                            }
                        });
                    }
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (player != null && player.isPlaying()) {

                        resetFunction();
                    } else if (!player.isPlaying()) {

                        resetFunction();
                        player.seekTo((int) (original.getStartTime() * 1000));
                        player.start();
                        int arg1;
                        if (getBindingAdapterPosition() + 1 == originals.size()) {
                            arg1 = player.getDuration() - 500;
                        } else {
                            arg1 = (int) (originals.get(getBindingAdapterPosition() + 1).getStartTime() * 1000);
                        }
                        handler.obtainMessage(0, arg1, 0).sendToTarget();
                    }
                }
            });
            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!PermissionUtils.hasSelfPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})) {
                        CustomToast.getInstance().showToast("存储或录音权限未开启，开启后可正常使用此功能");
                        return;
                    }
                    if (isRecord) {
                        IseManager.getInstance(context).stopEvaluate();
                        handler.sendEmptyMessage(3);
                        waittingDialog.show();
                    } else {
                        resetFunction();
                        isRecord = true;
                        initRecorder(original.getSentence());
                    }
                }
            });
        }
    }

    private class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ReadAdapter> {
        @Override
        public void handleMessageByRef(final ReadAdapter adapter, Message msg) {
            Message message;
            switch (msg.what) {
                case 0:
                    if (adapter.player.getCurrentPosition() < msg.arg1) {
                        adapter.curText.setText(Mathematics.formatTime((msg.arg1 - adapter.player.getCurrentPosition()) / 1000));
                        message = new Message();
                        message.what = 0;
                        message.arg1 = msg.arg1;
                        adapter.handler.sendMessageDelayed(message, 300);
                    } else {
                        adapter.handler.sendEmptyMessage(1);
                    }
                    break;
                case 1:
                    adapter.player.pause();
                    adapter.curText.setText("");
                    if (adapter.curItem != -1) {
                        adapter.notifyItemChanged(adapter.curItem);
                    }
                    adapter.handler.removeMessages(0);
                    break;
                case 2:
                    adapter.curText.setText(Mathematics.formatTime(msg.arg1 / 1000));
                    message = new Message();
                    message.what = 2;
                    message.arg1 = msg.arg1 + 1000;
                    adapter.handler.sendMessageDelayed(message, 1000);
                    break;
                case 3:
                    adapter.isRecord = false;
                    adapter.handler.removeMessages(2);
                    adapter.curText.setText("");
                    adapter.notifyItemChanged(adapter.curItem);
                    break;
                case 4:
                    adapter.curText.setText(Mathematics.formatTime(msg.arg1 / 1000));
                    message = new Message();
                    message.what = 4;
                    message.arg1 = msg.arg1 - 1000;
                    adapter.handler.sendMessageDelayed(message, 1000);
                    break;
                case 5:
                    adapter.handler.removeMessages(4);
                    adapter.curText.setText("");
                    break;
                case 6:
//                    isSending = false;
//                    adapter.waittingDialog.dismiss();
//                    Log.e("ReadAdapter", "handleMessageByRef message.obj " + msg.obj);
//                    CustomToast.getInstance().showToast(R.string.read_send_success);
//                    String result = (String) msg.obj;
//                    original.evaluateBean = GsonUtils.toObject(result, SendEvaluateResponse.class);
//                    double totalScore = Double.parseDouble(original.evaluateBean.getTotal_score());
//                    Log.e("ReadAdapter", "handleMessageByRef totalScore " + totalScore);
//                    original.isRead = true;
//                    int score = (int) (totalScore * 20.0D);
//                    original.readScore = score;
//                    original.pathLocal = ConstantManager.recordFile;
//                    notifyDataSetChanged();
//                    int paraId = original.getParaID();
//                    WavListItem item = new WavListItem();
//                    item.setUrl(original.evaluateBean.getURL());
//                    item.setBeginTime(originals.get(paraId - 1).getStartTime());
//                    if (paraId < originals.size()) {
//                        item.setEndTime(originals.get(paraId).getStartTime());
//                    } else {
//                        item.setEndTime(originals.get(paraId - 1).getEndTime());
//                    }
//                    float duration = getAudioFileVoiceTime(ConstantManager.recordFile) / 1000.0f;
//                    String temp = String.format("%.1f", duration);
//                    item.setDuration(Float.parseFloat(temp));//获取录音文件长度
//                    item.setIndex(paraId);
//                    // need instert to db
//                    DubDBManager dbManager = DubDBManager.getInstance();
//                    dbManager.setEvaluate("" + original.getArticleID(), mUid, ConstantManager.recordFile, String.valueOf(original.getParaID()), String.valueOf(score), original.progress, original.progress2);
//                    dbManager.setFluent("" + original.getArticleID(), mUid, String.valueOf(original.getParaID()), 90, original.evaluateBean.getURL());
//                    dbManager.setEvaluateTime("" + original.getArticleID(), mUid, String.valueOf(original.getParaID()), item.getBeginTime(), item.getEndTime(), item.getDuration());
//                    String wordScore = "";
//                    for (int i = 0; i < original.evaluateBean.getWords().size(); i++) {
//                        SendEvaluateResponse.WordsBean word = original.evaluateBean.getWords().get(i);
//                        dbManager.setEvWord("" + original.getArticleID(), mUid, String.valueOf(paraId), word);
//                        wordScore = wordScore + word.getScore() + ",";
//                    }
//                    Log.e("ReadAdapter", "Record Save add wordScore " + wordScore);
//                    Log.e("ReadAdapter", "Record Save add score " + score);
//                    Log.e("ReadAdapter", "Record clickVoaDetail.evaluateBean.getURL() " + original.evaluateBean.getURL());
                    break;
                case 7:
                    isSending = false;
                    adapter.waittingDialog.dismiss();
                    CustomToast.getInstance().showToast(R.string.read_send_fail);
                    break;
                case 8:

                    int curpPosition = msg.arg1;

                    Log.d("123321", msg.toString());
                    isSending = false;
                    waittingDialog.dismiss();
                    Log.e("ReadAdapter", "handleMessageByRef message.obj " + msg.obj);
                    CustomToast.getInstance().showToast(R.string.read_send_success);

                    Original original = originals.get(curpPosition);

                    SendDubbingResponse response = GsonUtils.toObject((String) msg.obj, SendDubbingResponse.class);
                    original.evaluateBean = new SendEvaluateResponse(response.filePath);
                    original.isRead = true;
                    original.readScore = response.addScore;
                    original.pathLocal = ConstantManager.recordFile;
                    notifyDataSetChanged();
                    int para = original.getParaID();
                    WavListItem items = new WavListItem();
                    items.setUrl(response.filePath);
                    items.setBeginTime(originals.get(para - 1).getStartTime());
                    if (para < originals.size()) {
                        items.setEndTime(originals.get(para).getStartTime());
                    } else {
                        items.setEndTime(originals.get(para - 1).getEndTime());
                    }
                    float durations = getAudioFileVoiceTime(ConstantManager.recordFile) / 1000.0f;
                    items.setDuration(Float.parseFloat(String.format("%.1f", durations)));
                    items.setIndex(para);
                    // need instert to db
                    DubDBManager dbManagers = DubDBManager.getInstance();
                    dbManagers.setEvaluate("" + original.getArticleID(), mUid, ConstantManager.recordFile, String.valueOf(original.getParaID()), "" + response.addScore, original.progress, original.progress2);
                    dbManagers.setFluent("" + original.getArticleID(), mUid, String.valueOf(original.getParaID()), 90, response.filePath);
                    dbManagers.setEvaluateTime("" + original.getArticleID(), mUid, String.valueOf(original.getParaID()), items.getBeginTime(), items.getEndTime(), items.getDuration());
                    break;
            }
        }
    }

    public long getAudioFileVoiceTime(String filePath) {
        long mediaPlayerDuration = 0L;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
            Log.e("ReadAdapter", "getAudioFileVoiceTime " + ioException.getMessage());
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }
}
