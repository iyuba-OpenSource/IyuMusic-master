package com.iyuba.music.lockscreenutil;

import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;

import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.service.PlayerService;


/**
 * Created by Clearlee on 2018/1/4 0004.
 */

public class MediaSessionManager {

    private static final String MY_MEDIA_ROOT_ID = "MediaSessionManager";

    private PlayerService musicPlayService;

    private MediaSession mMediaSession;
    private PlaybackState.Builder stateBuilder;

    public MediaSessionManager(PlayerService service) {
        this.musicPlayService = service;
        initSession();
    }

    public void initSession() {
        try {
            mMediaSession = new MediaSession(musicPlayService, MY_MEDIA_ROOT_ID);
            mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
            stateBuilder = new PlaybackState.Builder()
                    .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE
                            | PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS);
            mMediaSession.setPlaybackState(stateBuilder.build());
            mMediaSession.setCallback(sessionCb);
            mMediaSession.setActive(true);
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    public void updatePlaybackState(int currentState) {
//        int state = (currentState == PlayerService.PLAY_STATE_PAUSED) ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_PLAYING;

        int state = currentState;

        stateBuilder.setState(state, musicPlayService.getPlayer().getCurrentPosition(), 1.0f);
        mMediaSession.setPlaybackState(stateBuilder.build());
    }

    public void updateLocMsg(Bitmap bitmap) {
        try {
            //同步歌曲信息
            MediaMetadata.Builder md = new MediaMetadata.Builder();

            md.putString(MediaMetadata.METADATA_KEY_TITLE, StudyManager.getInstance().getCurArticle().getTitle()); //歌名
            md.putString(MediaMetadata.METADATA_KEY_ALBUM, StudyManager.getInstance().getCurArticle().getSinger()); //歌手名字

            md.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap);  //图片
            mMediaSession.setMetadata(md.build());
        } catch (Exception e) {
            LogTool.ex(e);
        }

    }

    private MediaSession.Callback sessionCb = new MediaSession.Callback() {
        //锁屏播放点击事件
        @Override
        public void onPlay() {
            super.onPlay();
            //将事件传递给service处理
            musicPlayService.onClick();

            updatePlaybackState(PlaybackState.STATE_PLAYING);
        }

        //锁屏暂停点击事件
        @Override
        public void onPause() {
            super.onPause();
            musicPlayService.onClick();
            updatePlaybackState(PlaybackState.STATE_PAUSED);
        }

        //锁屏下一曲点击事件
        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            musicPlayService.onDoubleClick();
            updatePlaybackState(PlaybackState.STATE_PLAYING);
        }

        //锁屏上一曲点击事件
        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            musicPlayService.onThreeClick();

            updatePlaybackState(PlaybackState.STATE_PLAYING);
        }


    };

    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

}
