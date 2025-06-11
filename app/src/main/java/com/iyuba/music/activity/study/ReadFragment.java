package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.assessment.ResultParse;
import com.iyuba.music.R;
import com.iyuba.music.adapter.study.ReadAdapter;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.data.local.EvaluateScore;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.fragment.BaseRecyclerViewFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 - 解说 - 播放主页面 - 跟唱主页面 - 跟唱fragment
 */
public class ReadFragment extends BaseRecyclerViewFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ReadAdapter readAdapter;
    private Article curArticle;
    private IyubaDialog waittingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        curArticle = StudyManager.getInstance().getCurArticle();
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.read_loading));
        readAdapter = new ReadAdapter(context);
        recyclerView.setAdapter(readAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disableSwipeLayout();
        getOriginal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        readAdapter.onDestroy();
    }

    private void getOriginal() {
        if (LrcParser.getInstance().fileExist(curArticle.getId())) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    LrcParser.getInstance().getOriginal(curArticle.getId(), new IOperationResult() {
                        @Override
                        public void success(Object object) {
                            ArrayList<Original> originals = (ArrayList<Original>) object;
                            Log.e("ReadFragment", "getOriginal curArticle.getId() " + curArticle.getId());
                            // query EvaluateScore in db
                            List<EvaluateScore> list = DubDBManager.getInstance().getEvaluate("" + curArticle.getId(), AccountManager.getInstance().getUserId());
                            if (list != null && list.size() > 0) {
                                for (Original voaText : originals) {
                                    for (EvaluateScore score : list) {
                                        if (score.paraId.equals(String.valueOf(voaText.getParaID()))) {
                                            voaText.words = DubDBManager.getInstance().getEvWord("" + curArticle.getId(), AccountManager.getInstance().getUserId(), score.paraId);
                                            Log.e("ReadFragment", "search " + curArticle.getId() + "=" + AccountManager.getInstance().getUserId() + "=" + score.paraId + "=" + voaText.words.size());
                                            if ((voaText.words != null) && (voaText.words.size() > 0)) {
                                                String[] floats = new String[voaText.words.size()];
                                                int ind = 0;
                                                for (SendEvaluateResponse.WordsBean wordBean: voaText.words) {
                                                    floats[ind++] = wordBean.score + "";
                                                }
                                            }
                                            voaText.isRead = true;
                                            voaText.evaluateBean = new SendEvaluateResponse(score.url);
                                            voaText.isDataBase = true;
                                            voaText.readScore = 0;
                                            try {
                                                voaText.readScore = (Integer.parseInt(score.score));
                                            } catch (Exception e) { }
                                            voaText.progress = score.progress;
                                            voaText.progress2 = score.progress2;
                                            voaText.pathLocal = score.path;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                Log.e("ReadFragment", "getOriginal getEvaluate in db is null.");
                            }
                            handler.obtainMessage(0, object).sendToTarget();
                        }

                        @Override
                        public void fail(Object object) {
                            Log.e("ReadFragment", "getOriginal curArticle.getId() fail.");
                        }
                    });
                }
            });
        } else {
            waittingDialog.show();
            getWebLrc(curArticle.getId(), new IOperationFinish() {
                @Override
                public void finish() {
                    waittingDialog.dismiss();
                }
            });
        }
    }

    private void getWebLrc(final int id, final IOperationFinish finish) {
        LrcRequest.exeRequest(LrcRequest.generateUrl(id, 2), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                Log.e("ReadFragment", "getWebLrc onNetError " + msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                Log.e("ReadFragment", "getWebLrc onServerError " + msg);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Original> originalList = (ArrayList<Original>) listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                }
                // query score in db
                Log.e("ReadFragment", "getWebLrc curArticle.getId() " + id);
                List<EvaluateScore> list = DubDBManager.getInstance().getEvaluate("" + curArticle.getId(), AccountManager.getInstance().getUserId());
                if (list != null && list.size() > 0) {
                    for (Original voaText : originalList) {
                        for (EvaluateScore score : list) {
                            if (score.paraId.equals(String.valueOf(voaText.getParaID()))) {
                                voaText.words = DubDBManager.getInstance().getEvWord("" + curArticle.getId(), AccountManager.getInstance().getUserId(), score.paraId);
                                Log.e("ReadFragment", "search " + curArticle.getId() + "=" + AccountManager.getInstance().getUserId() + "=" + score.paraId + "=" + voaText.words.size());
                                if ((voaText.words != null) && (voaText.words.size() > 0)) {
                                    String[] floats = new String[voaText.words.size()];
                                    int ind = 0;
                                    for (SendEvaluateResponse.WordsBean wordBean: voaText.words) {
                                        floats[ind++] = wordBean.score + "";
                                    }
                                }
                                voaText.isRead = true;
                                voaText.evaluateBean = new SendEvaluateResponse(score.url);
                                voaText.isDataBase = true;
                                voaText.readScore = 0;
                                try {
                                    voaText.readScore = (Integer.parseInt(score.score));
                                } catch (Exception e) { }
                                voaText.progress = score.progress;
                                voaText.progress2 = score.progress2;
                                voaText.pathLocal = score.path;
                                break;
                            }
                        }
                    }
                } else {
                    Log.e("ReadFragment", "getWebLrc getEvaluate in db is null.");
                }
                readAdapter.setDataSet(originalList);
                finish.finish();
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ReadFragment> {
        @Override
        public void handleMessageByRef(final ReadFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    fragment.readAdapter.setDataSet((ArrayList<Original>) msg.obj);
                    break;
            }
        }
    }
}
