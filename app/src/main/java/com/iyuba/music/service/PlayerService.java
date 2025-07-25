package com.iyuba.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.iyuba.headlinelibrary.event.HeadlinePlayEvent;
import com.iyuba.imooclib.event.ImoocPlayEvent;
import com.iyuba.module.movies.event.IMoviePlayEvent;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.OnHeadSetListener;
import com.iyuba.music.lockscreenutil.MediaSessionManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.receiver.HeadsetPlugReceiver;
import com.iyuba.music.receiver.NotificationBeforeReceiver;
import com.iyuba.music.receiver.NotificationCloseReceiver;
import com.iyuba.music.receiver.NotificationNextReceiver;
import com.iyuba.music.receiver.NotificationPauseReceiver;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.util.BlurUtil;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.HeadSetUtil;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.widget.player.StandardPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/22.
 */
public class PlayerService extends Service implements OnHeadSetListener {
    private StandardPlayer player;
    private int curArticleId;
    private PhoneStateListener phoneStateListener;
    private HeadsetPlugReceiver headsetPlugReceiver;

    private NotificationCloseReceiver close;
    private NotificationBeforeReceiver before;
    private NotificationNextReceiver next;
    private NotificationPauseReceiver pause;

    private MediaSessionManager mediaSessionManager;

    private Bitmap mbitmap;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        registerNotificationBroadcastReceiver();
        HeadSetUtil.getInstance().open(this);
        init();
        ((MusicApplication) getApplication()).setPlayerService(this);
        EventBus.getDefault().register(this);
        //android 5.0以下版本不设置音乐锁屏
        if (Build.VERSION.SDK_INT >= 21) {
            mediaSessionManager = new MediaSessionManager(this);
        }

    }

    private void registerNotificationBroadcastReceiver() {
        Context context = RuntimeManager.getContext();
        IntentFilter ifr = new IntentFilter("iyumusic.close");
        close = new NotificationCloseReceiver();
        context.registerReceiver(close, ifr);

        ifr = new IntentFilter("iyumusic.pause");
        pause = new NotificationPauseReceiver();
        context.registerReceiver(pause, ifr);

        ifr = new IntentFilter("iyumusic.next");
        next = new NotificationNextReceiver();

        context.registerReceiver(next, ifr);
        ifr = new IntentFilter("iyumusic.before");
        before = new NotificationBeforeReceiver();
        context.registerReceiver(before, ifr);
    }

    private void unRegisterNotificationBroadcaster() {
        Context context = RuntimeManager.getContext();
        context.unregisterReceiver(pause);
        context.unregisterReceiver(before);
        context.unregisterReceiver(next);
        context.unregisterReceiver(close);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationUtil.NOTIFICATION_ID, NotificationUtil.getInstance().initNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterNotificationBroadcaster();
        unRegisterBroadcastReceiver();
        NotificationUtil.getInstance().removeNotification();
        stopForeground(true);
        stopSelf();
        player.stopPlayback();
        player = null;
        EventBus.getDefault().unregister(this);
    }

    public void init() {
        player = new StandardPlayer(RuntimeManager.getContext());
        curArticleId = 0;
    }

    public void setListener(final IPlayerListener playerListener) {
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();

                if (playerListener != null) {
                    playerListener.onPrepare();
                }
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (ConfigManager.getInstance().getStudyPlayMode() != 0) {
                    next(true);
                    if (RuntimeManager.getApplication().isAppointForeground("MainActivity")) {
                        Intent i = new Intent("com.iyuba.music.main");
                        i.putExtra("message", "change");
                        sendBroadcast(i);
                    }
                }
                if (checkNetWorkState()) {
                    ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
                    ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
                }
                player.start();

                if (playerListener != null) {
                    playerListener.onFinish();
                }
            }
        });

        //结束
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (playerListener != null) {
                    playerListener.onBufferChange(percent);
                }
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (playerListener != null) {
                    playerListener.onError();
                }
                return false;
            }
        });
    }

    public StandardPlayer getPlayer() {
        return player;
    }

    public int getCurArticleId() {
        return curArticleId;
    }

    public void setCurArticleId(int curArticleId) {
        this.curArticleId = curArticleId;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void startPlay(Article article, boolean modeChange) {
        if (article != null && article.getId() == curArticleId && !modeChange) {

        } else if (article != null) {
            if (!StudyManager.getInstance().getApp().equals("101")) {
                LocalInfoOp localInfoOp = new LocalInfoOp();

                StudyManager.getInstance().setStartTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                localInfoOp.updateSee(article.getId(), article.getApp());
                ReadCountAddRequest.exeRequest(ReadCountAddRequest.generateUrl(article.getId(), "music"), null);
            }
            String netUrl = getUrl(article);
            String playPath;
            if (netUrl.startsWith("http")) {
                playPath = RuntimeManager.getProxy().getProxyUrl(netUrl);
            } else {
                playPath = netUrl;
            }
            setNotification();
            player.setVideoPath(playPath);

            Log.e("歌曲信息", playPath);
        }
    }

    public void next(boolean finish) {
        if (finish) {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 1);
        } else {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
        }
        StudyManager.getInstance().next();
    }

    public void before() {
        StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
        StudyManager.getInstance().before();
    }

    public String getUrl(Article article) {
        String url;
        StringBuilder localUrl = new StringBuilder();
        File localFile;
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                if (StudyManager.getInstance().getMusicType() == 0) {
                    url = DownloadUtil.getSongUrl(article.getApp(), article.getMusicUrl());
                    localUrl.append(ConstantManager.musicFolder).append(File.separator).append(article.getId()).append(".mp3");
                    localFile = new File(localUrl.toString());
                } else {
                    url = DownloadUtil.getAnnouncerUrl(article.getId(), article.getSoundUrl());
                    localUrl.append(ConstantManager.musicFolder).append(File.separator).append(article.getId()).append("s.mp3");
                    localFile = new File(localUrl.toString());
                }
                if (localFile.exists()) {
                    return localUrl.toString();
                } else {
                    return url;
                }
            case "101":
                return article.getMusicUrl();
            default:
                url = DownloadUtil.getSongUrl(article.getApp(), article.getMusicUrl());
                localUrl.append(ConstantManager.musicFolder).append(File.separator).append(article.getApp()).append("-").append(article.getId()).append(".mp3");
                localFile = new File(localUrl.toString());
                if (localFile.exists()) {
                    return localUrl.toString();
                } else {
                    return url;
                }
        }
    }

    /**
     * 播放广播更换歌曲页面信息
     */
    private void setNotification() {
        Log.e("播放歌曲=====》》》", "ss");
        String url;
        if (StudyManager.getInstance().getApp().equals("209")) {
            url = "http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + StudyManager.getInstance().getCurArticle().getPicUrl();
        } else {
            url = StudyManager.getInstance().getCurArticle().getPicUrl();
        }
        NotificationUtil.getInstance().updateNotification(url);


        setLockScreen();
    }

    /**
     * 音乐锁屏更新界面信息
     */

    public void setLockScreen() {
        final String url;
        if (StudyManager.getInstance().getApp().equals("209")) {
            url = "http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + StudyManager.getInstance().getCurArticle().getPicUrl();
        } else {
            url = StudyManager.getInstance().getCurArticle().getPicUrl();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                mbitmap = BlurUtil.getBitMBitmap(url);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setLockPlayState(true);
            if (Build.VERSION.SDK_INT >= 21) {


                mediaSessionManager.updateLocMsg(mbitmap); //锁屏页面
            }
        }
    };


    public void registerBroadcastReceiver() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (player != null && player.isPlaying()) {
                            sendBroadcast(new Intent("iyumusic.pause"));
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        IntentFilter ifr = new IntentFilter("android.intent.action.HEADSET_PLUG");
        headsetPlugReceiver = new HeadsetPlugReceiver();
        registerReceiver(headsetPlugReceiver, ifr);
    }

    private void unRegisterBroadcastReceiver() {
        HeadSetUtil.getInstance().close();
        unregisterReceiver(headsetPlugReceiver);
        if (phoneStateListener != null) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onClick() {
        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.pause"));
    }

    @Override
    public void onDoubleClick() {
        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.next"));
        setLockScreen();
    }

    @Override
    public void onThreeClick() {
        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.before"));
        setLockScreen();
    }

    private boolean checkNetWorkState() {
        String url = ((MusicApplication) getApplication()).getPlayerService().getUrl(StudyManager.getInstance().getCurArticle());
        if (((MusicApplication) getApplication()).getProxy().isCached(url)) {
            return true;
        } else if (url.startsWith("http")) {
            if (!InterNetUtil.isNetworkConnected(MusicApplication.getApp())) {
                return false;
            } else
                return true; //NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G_3G);
        } else {
            return true;
        }
    }


    /**
     * 锁屏状态设置
     */
    public void setLockPlayState(boolean isPlaying) {

        if (Build.VERSION.SDK_INT >= 21) {


            if (isPlaying) {
                mediaSessionManager.updatePlaybackState(PlaybackState.STATE_PLAYING);
            } else {
                mediaSessionManager.updatePlaybackState(PlaybackState.STATE_PAUSED);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPlayEvent event) {
        if (player != null && player.isPlaying()) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMoviePlayEvent event) {
        if (player != null && player.isPlaying()) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlinePlayEvent event) {
        if (player != null && player.isPlaying()) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }

    }


}
