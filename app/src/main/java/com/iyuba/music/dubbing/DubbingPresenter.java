package com.iyuba.music.dubbing;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.IDListener;
import com.iyuba.headlinelibrary.util.PathUtil;
import com.iyuba.imooclib.data.model.Course;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.VoaText;
import com.iyuba.music.data.remote.WordResponse;
import com.iyuba.music.dubbing.utils.NetStateUtil;
import com.iyuba.music.dubbing.utils.RxUtil;
import com.iyuba.music.dubbing.utils.StorageUtil;
import com.iyuba.music.dubbing.utils.VoaMediaUtil;
import com.iyuba.music.dubbing.views.VideoDownMenuDialog;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.event.DownloadEvent;
import com.iyuba.music.manager.ConstantManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static okhttp3.MultipartBody.FORM;

public class DubbingPresenter extends BasePresenter<DubbingMvpView> {
    private static final long SHOW_PEROID = 500;

    private final DataManager mDataManager;
    private final DLManager mDLManager;

    private MusicVoa mVoa;
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;

    private Timer mMsgTimer;
    private String mMsg;
    private Context mContext;
    private DLTaskInfo mVideoTask;
    private DLTaskInfo mAudioTask;

    private IDListener mVideoListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {
                //Timber.e("下载字节数"+progress+"总字节数"+mFileLength);
                long percent = (long) progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(
                            ((Context) getMvpView()).getString(R.string.video_loading_tip), percent);
                    Timber.e("下载字" + mMsg + "percent" + percent);
                }
                if (mContext != null) {
                    mMsg = MessageFormat.format(
                            (mContext).getString(R.string.video_loading_tip), percent);
                    Timber.e("下载字" + mMsg + "percent" + percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            if (mMsgTimer != null) {
                mMsgTimer.cancel();
            }
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameVideoFile(mDir, mVoa.voaId);
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId)) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId));
                //addDownload(); 下载数据库记录
            }
           /* if (mContext != null) {
                       String videoFilename = StorageUtil.getVideoFilename(mVoa.voaId);
                ContentValues values = new ContentValues();
                values.put("_display_name", videoFilename);
                values.put("mime_type", "video/mp4");
                if (Build.VERSION.SDK_INT >= 29) {
                    values.put("relative_path", Environment.DIRECTORY_DCIM);
                } else {
                    values.put("_data", Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DCIM + "/" + videoFilename);
                }
                Uri uri = mContext.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream outputStream = null;
                    try {
                        outputStream = mContext.getContentResolver().openOutputStream(uri);
                        (new FileInputStream(file)).getChannel().transferTo(0L, file.length(), Channels.newChannel(outputStream));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }*/
        }

        @Override
        public void onError(int status, String error) {
            mMsgTimer.cancel();
        }
    };

    private IDListener mMediaListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {
                int percent = progress * 100 / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format(((Context) getMvpView()).getString(R.string.media_loading_tip), percent);
                }
                if (mContext != null) {
                    mMsg = MessageFormat.format(mContext.getString(R.string.media_loading_tip), percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            if (mMsgTimer != null) {
                mMsgTimer.cancel();
            }
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameAudioFile(mDir, mVoa.voaId);
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId)) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId));
                //addDownload();
            }

            //下载视频
            if (!StorageUtil.isVideoExist(mDir, mVoa.voaId)) {
                downloadVideo();
            }
        }

        @Override
        public void onError(int status, String error) {
            String mUrl;
            mUrl = VoaMediaUtil.getAudioErrorUrl("/202002/313116.mp3");
//            mDLManager.dlStart(mUrl, mDir, StorageUtil.getAudioTmpFilename(mVoa.voaId), mMediaListener);
            //下载视频
            if (!StorageUtil.isVideoExist(mDir, mVoa.voaId)) {
                downloadVideo();
            }
        }
    };
    private Disposable mSearchSub;
    private Disposable mInsertDisposable;
    private Disposable mSyncVoaSub;
    private Disposable mMergeRecordSub;

    public DubbingPresenter(DLManager dlManager) {
        this.mDataManager = DataManager.getInstance();
        this.mDLManager = dlManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mMergeRecordSub);
        RxUtil.unsubscribe(mSearchSub);
    }

    public void init(MusicVoa voa) {
        this.mVoa = voa;
        mDir = MusicApplication.getApp().getExternalFilesDir(null).getAbsolutePath();
//        mDir = PathUtil.getMediaPath(mContext);
        Timber.e("下载路径" + mDir);
    }

    public void init(Context context, MusicVoa voa) {
        this.mVoa = voa;
        mContext = context;
        mDir = MusicApplication.getApp().getExternalFilesDir(null).getAbsolutePath();
//        mDir = PathUtil.getMediaPath(mContext);
        Timber.e("下载路径" + mDir);
    }

    public void download() {
        mMsgTimer = new Timer();
        mMsgTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMsg != null) {
                    Timber.e("下载进度" + mMsg);
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mVoa.voaId));
                }
            }
        }, 0, SHOW_PEROID);

        if (!StorageUtil.isAudioExist(mDir, mVoa.voaId)) {
            downloadMedia();
        } else if (!StorageUtil.isVideoExist(mDir, mVoa.voaId)) {
            downloadVideo();
        }
    }

    private void downloadVideo() {
        //mVideoUrl= "http://staticvip.iyuba.cn/video/voa/321/321001.mp4";
//        mVideoUrl = VoaMediaUtil.getVideoVipUrl(mVoa.voaId);
        mVideoUrl = ConstantManager.videoPrefix + mVoa.video;
        Log.e("DubbingPresenter", "downloadVideo mVideoUrl = " + mVideoUrl);
        mVideoTask = new DLTaskInfo();
        mVideoTask.tag = mVideoUrl;
        mVideoTask.filePath = mDir;
        mVideoTask.fileName = StorageUtil.getVideoTmpFilename(mVoa.voaId);
        mVideoTask.initalizeUrl(mVideoUrl);
        mVideoTask.category = Course.DOWNLOAD_VIDEO_CATEGORY;
        mVideoTask.setDListener(mVideoListener);
        mDLManager.addDownloadTask(mVideoTask);
    }

    private void downloadMedia() {
//        mMediaUrl = mAccountManager.isVip() ?
//                VoaMediaUtil.getAudioVipUrl(mVoa.sound()) : VoaMediaUtil.getAudioUrl(mVoa.sound());
        mMediaUrl = VoaMediaUtil.getAudioUrl(mVoa.sound);//"/202002/313116.mp3"
        Log.e("DubbingPresenter", "downloadMedia mMediaUrl = " + mMediaUrl);
        mAudioTask = new DLTaskInfo();
        mAudioTask.tag = mMediaUrl;
        mAudioTask.filePath = mDir;
        mAudioTask.fileName = StorageUtil.getAudioTmpFilename(mVoa.voaId);
        mAudioTask.initalizeUrl(mMediaUrl);
        mAudioTask.category = Course.DOWNLOAD_VIDEO_CATEGORY;
        mAudioTask.setDListener(mMediaListener);
        mDLManager.addDownloadTask(mAudioTask);
    }

    public void cancelDownload() {
        if (mVideoTask != null) {
            mDLManager.cancelTask(mVideoTask);
            mVideoTask = null;
        }

        if (mAudioTask != null) {
            mDLManager.cancelTask(mAudioTask);
            mAudioTask = null;
        }
        if (mMsgTimer != null) {
            mMsgTimer.cancel();
        }
    }

    public void syncVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaSub);
        mSyncVoaSub = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<VoaText>>() {
                    @Override
                    public void accept(List<VoaText> voaTexts) throws Exception {
                        if (voaTexts.size() == 0) {
                            getMvpView().showEmptyTexts();
                        } else {
                            getMvpView().showVoaTexts(voaTexts);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!NetStateUtil.isConnected((Context) getMvpView())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                        getMvpView().showEmptyTexts();
                    }
                });
    }

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mVoa.voaId);
    }


    int getFinishNum(int voaId, long timestamp) {
        return StorageUtil.getRecordNum(((Context) getMvpView()), voaId, timestamp);
    }

    /**
     * 如果存在草稿，取数据，读取分数
     */
//    void checkDraftExist(long mTimeStamp) {
//        mDataManager.getDraftRecord(mTimeStamp)
//                .subscribe(new Subscriber<List<Record>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(List<Record> records) {
//                        if (records != null && records.size() > 0) {
//                            getMvpView().onDraftRecordExist(records.get(0));
//                        }
//                    }
//                });
//    }

    //评测接口 ！！
    public void uploadSentence(String sentence, int index, int newsId, final int paraId,
                               String type, String uid, final File file, String filePath, final int progress,
                               final int secondaryProgress) {
        checkViewAttached();
        RxUtil.unsubscribe(mMergeRecordSub);
        String TYPE = "type";
        String SENTENCE = "sentence";
        String USERID = "userId";
        String NEWSID = "newsId";
        String PARAID = "paraId";
        String IDINDEX = "IdIndex";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(FORM);

        builder.addFormDataPart(SENTENCE, sentence)
                .addFormDataPart(IDINDEX, String.valueOf(index))
                .addFormDataPart(NEWSID, String.valueOf(newsId))
                .addFormDataPart(PARAID, String.valueOf(paraId))
                .addFormDataPart(TYPE, type)
                .addFormDataPart(USERID, uid);


        //String path = EnDecodeUtils.encode(file.getPath());
        //RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addPart(
                Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filePath + "\""),
                RequestBody.create(MediaType.parse("application/octet-stream"), file));

        RequestBody requestBody = builder.build();
        mMergeRecordSub = mDataManager.uploadSentence(requestBody)
                .compose(com.iyuba.module.toolbox.RxUtil.<SendEvaluateResponse>applySingleIoScheduler())
                .subscribe(new Consumer<SendEvaluateResponse>() {
                    @Override
                    public void accept(SendEvaluateResponse pair) throws Exception {
                        if (isViewAttached()) {
                            //getMvpView().showToast("评测请求成功");
                            getMvpView().getEvaluateResponse(pair, paraId, file, progress, secondaryProgress);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        Timber.e("evaluate request fail.");
                        getMvpView().evaluateError("evaluate request fail.");
                    }
                });
    }


//    public void deleteWords(final int userId, List<String> words, final ActionMode mode) {
//        com.iyuba.module.toolbox.RxUtil.dispose(mDeleteDisposable);
//        mDeleteDisposable = mDataManager.deleteWords(userId, words)
//                .compose(com.iyuba.module.toolbox.RxUtil.<Boolean>applySingleIoSchedulerWith(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        if (isViewAttached()) {
//                        }
//                    }
//                }))
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean result) throws Exception {
//                        if (isViewAttached()) {
//                            if (result) {
//                            } else {
//                                getMvpView().showToast("删除失败，请稍后重试!");
//                                mode.finish();
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().showToast("删除失败，请稍后重试!");
//                            mode.finish();
//                        }
//                    }
//                });
//    }

    public void getNetworkInterpretation(String selectText) {
        if (mSearchSub != null)
            mSearchSub.dispose();
        mSearchSub = mDataManager.getWordOnNet(selectText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WordResponse>() {
                    @Override
                    public void accept(WordResponse wordBean) throws Exception {
                        getMvpView().showWord(wordBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }


    public void insertWords(int userId, List<String> words) {
        com.iyuba.module.toolbox.RxUtil.dispose(mInsertDisposable);
        mInsertDisposable = mDataManager.insertWords(userId, words)
                .compose(com.iyuba.module.toolbox.RxUtil.<Boolean>applySingleIoScheduler())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (isViewAttached()) {
                            if (result) {
                                getMvpView().showToast(R.string.play_ins_new_word_success);
                            } else {
                                getMvpView().showToast("添加生词未成功");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showToast("添加生词未成功");
                        }
                    }
                });
    }
}
