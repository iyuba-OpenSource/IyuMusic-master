/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.service.NotificationUtil;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.player.StandardPlayer;

import java.util.Calendar;

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class NotificationPauseReceiver extends BroadcastReceiver {

    public static void playNewSong() {
        Article curArticle = StudyManager.getInstance().getCurArticle();
        if (curArticle == null)
            return;
        final PlayerService playerService = RuntimeManager.getApplication().getPlayerService();
        playerService.startPlay(curArticle, false);
        playerService.setCurArticleId(curArticle.getId());
        StandardPlayer player = playerService.getPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerService.getPlayer().start();
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (RuntimeManager.getApplication().getPlayerService().getCurArticleId() == 0 && !StudyManager.getInstance().getApp().equals("101")) {
            NotificationPauseReceiver.playNewSong();
            Intent i;
            if (RuntimeManager.getApplication().isAppointForeground("MainActivity")) {
                i = new Intent("com.iyuba.music.main");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            }
            NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PLAY_FLAG);
            MusicApplication.getApp().getPlayerService().setLockPlayState(true);

        } else if (RuntimeManager.getApplication().getPlayerService().getCurArticleId() == 0 && StudyManager.getInstance().getApp().equals("101")) {
            Intent i = new Intent("com.iyuba.music.localmusic");
            i.putExtra("message", "randomPlay");
            context.sendBroadcast(i);
        } else {
            StandardPlayer player = RuntimeManager.getApplication().getPlayerService().getPlayer();
            if (player.isPlaying()) {
                player.pause();
                NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PAUSE_FLAG);
                MusicApplication.getApp().getPlayerService().setLockPlayState(false);

                //暂停播放--提交学习时间
                if (MusicApplication.getApp().getPlayerService().getCurArticleId() != 0) {
                    StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
                }
            } else {
                player.start();

                StudyManager.getInstance().setStartTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PLAY_FLAG);
                MusicApplication.getApp().getPlayerService().setLockPlayState(true);
            }
            Intent i;
            if (RuntimeManager.getApplication().isAppointForeground("MainActivity")) {
                i = new Intent("com.iyuba.music.main");
                i.putExtra("message", "pause");
                context.sendBroadcast(i);
            } else if (RuntimeManager.getApplication().isAppointForeground("LocalMusicActivity")) {
                i = new Intent("com.iyuba.music.localmusic");
                i.putExtra("message", "pause");
                context.sendBroadcast(i);
            } else if (RuntimeManager.getApplication().isAppointForeground("StudyActivity")) {
                i = new Intent("com.iyuba.music.study");
                i.putExtra("message", "pause");
                context.sendBroadcast(i);
            }
        }

    }
}
