package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordSetOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.discoverrequest.DictRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.player.SimplePlayer;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.textview.JustifyTextView;
import com.nineoldandroids.animation.Animator;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/10/26.
 */
public class WordCard extends LinearLayout implements View.OnClickListener {
    private View root;
    private Context context;
    private String keyword;
    private TextView key, pron;
    private JustifyTextView def;
    private ImageView speaker;
    private Word word;
    private RoundTextView add, close;
    private AVLoadingIndicatorView loading;
    private View wordContent, wordOperation;
    private PersonalWordOp personalWordOp;
    private WordSetOp wordSetOp;
    private boolean collected;
    private SimplePlayer player;

    public WordCard(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WordCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();
    }

    public WordCard(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        init();
    }

    public void resetWord(String keyword) {
        this.keyword = keyword;
        key.setText("");
        pron.setText("");
        def.setText("");
        initialContent();
    }

    private void init() {
        player = new SimplePlayer(context);
        personalWordOp = new PersonalWordOp();
        wordSetOp = new WordSetOp();
        LayoutInflater inflater = LayoutInflater.from(context);
        root = inflater.inflate(R.layout.wordcard, this);
        key = (TextView) root.findViewById(R.id.word_key);
        pron = (TextView) root.findViewById(R.id.word_pron);
        def = (JustifyTextView) root.findViewById(R.id.word_def);
        speaker = (ImageView) root.findViewById(R.id.word_speaker);
        add = (RoundTextView) root.findViewById(R.id.word_add);
        close = (RoundTextView) root.findViewById(R.id.word_close);
        loading = (AVLoadingIndicatorView) root.findViewById(R.id.word_loading);
        loading.setIndicatorColor(GetAppColor.getInstance().getAppColor());
        wordContent = root.findViewById(R.id.word_content);
        wordOperation = root.findViewById(R.id.word_operation);
        speaker.setOnClickListener(this);
        add.setOnClickListener(this);
        close.setOnClickListener(this);
        root.setVisibility(GONE);
    }

    private void initialContent() {
        collected = (personalWordOp.findDataByName(keyword.toLowerCase(), AccountManager.getInstance().getUserId()) != null);
        word = wordSetOp.findDataByKey(keyword);
        if (word != null) {
            setViewContent();
            wordSetOp.updateWord(keyword);
        } else {
            wordContent.setVisibility(GONE);
            wordOperation.setVisibility(GONE);
            loading.setVisibility(VISIBLE);
        }
        getNetWord(keyword, new IOperationFinish() {
            @Override
            public void finish() {
                setViewContent();
                show();
            }
        });
    }

    private void getNetWord(final String wordkey, final IOperationFinish finish) {
        DictRequest.exeRequest(DictRequest.generateUrl(wordkey), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {

                Word twWord1 = (Word) object;
                String wordStr = twWord1.getWord();
                if (wordStr != null && !wordStr.trim().equals("")) {

                    word = (Word) object;
                    finish.finish();
                }
            }
        });
    }

    private void setViewContent() {
        if (word == null) {
            this.dismiss();
            CustomToast.getInstance().showToast(R.string.word_null);
        } else {
            wordContent.setVisibility(VISIBLE);
            wordOperation.setVisibility(VISIBLE);
            loading.setVisibility(GONE);
            key.setText(word.getWord());
            if (!TextUtils.isEmpty(word.getPron())) {
                if (word.getPron().contains("%")) {
                    pron.setText("/" + TextAttr.decode(word.getPron()) + "/");
                } else {
                    pron.setText("/" + word.getPron() + "/");
                }
            }
            def.setText(word.getDef());

            if (collected) {
                add.setText(R.string.word_card_add_already);
            } else {
                add.setText(R.string.word_card_add);
            }
        }
    }

    @Override
    public void onClick(View view) throws NullPointerException {

        if (view.equals(speaker)) {
            player.setVideoPath(word.getPronMP3());
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.reset();
                }
            });
        } else if (view.equals(add)) {
            if (AccountManager.getInstance().checkUserLogin()) {
                synchroCollect();
            } else {
                CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                    @Override
                    public void finish() {
                        synchroCollect();
                    }
                });
            }
        } else if (view.equals(close)) {
            dismiss();
        }
    }

    private void synchroCollect() {
        if (!collected) {
            CustomToast.getInstance().showToast(R.string.word_adding);
            word.setUser(AccountManager.getInstance().getUserId());
            word.setCreateDate(DateFormat.formatTime(Calendar.getInstance().getTime()));
            word.setViewCount("1");
            word.setIsdelete("-1");
            new PersonalWordOp().saveData(word);
            synchroYun();
        } else {
            CustomToast.getInstance().showToast(R.string.word_add);
        }
    }

    public void show() {
        YoYo.with(Techniques.FlipInX).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                root.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(root);
    }

    public void dismiss() {
        YoYo.with(Techniques.FlipOutX).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                root.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(root);
    }

    private void synchroYun() {
        final String userid = AccountManager.getInstance().getUserId();
        DictUpdateRequest.exeRequest(DictUpdateRequest.generateUrl(userid, "insert", keyword),
                new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                    }

                    @Override
                    public void response(Object object) {
                        personalWordOp.insertWord(keyword, userid);
                        CustomToast.getInstance().showToast(R.string.word_add);
                    }
                });
    }

    public void destroy() {
        if (player != null) {
            player.stopPlayback();
        }
    }

    public boolean isShowing() {
        return root.isShown();
    }
}
