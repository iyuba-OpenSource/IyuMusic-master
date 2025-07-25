package com.iyuba.music.widget.player;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;

/**
 * 视频框架
 *
 * @author chentong
 *         <p/>
 *         <p/>
 *         Displays a video file. The VideoView class can load images from
 *         various sources (such as resources or content providers), takes care
 *         of computing its measurement from the video so that it can be used in
 *         any layout manager, and provides various display options such as
 *         scaling and tinting.
 */
public class SimplePlayer implements MediaPlayerControl {
    private Context mContext;
    // settable by the client
    private Uri mUri;
    private int mDuration;
    // All the stuff we need for playing and showing a video
    private MediaPlayer mMediaPlayer = null;
    private boolean prepared;
    private int mCurrentBufferPercentage;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            // briefly show the mediacontroller
            prepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
        }
    };
    private OnErrorListener mOnErrorListener;
    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err)) {
                    return true;
                }
            }
            return true;
        }
    };
    private OnCompletionListener mOnCompletionListener;
    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    public SimplePlayer(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
    }

    public void setVideoPath(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e("SimplePlayer", "setVideoPath is null.");
        } else {
            setVideoURI(Uri.parse(path));
        }
    }

    private void setVideoURI(Uri uri) {
        mUri = uri;
        openVideo();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void openVideo() {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            prepared = false;
            mDuration = -1;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
        mBufferingUpdateListener = l;
    }

    /**
     * Register a callback to be invoked when the media file is loaded and ready
     * to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been
     * reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or
     * setup. If no listener is specified, or if the listener returned false,
     * VideoView will inform the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void start() {
        if (prepared) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (prepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    public void reset() {
        if (prepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            mMediaPlayer.reset();
        }
    }

    public int getDuration() {
        if (prepared) {
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (prepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (prepared) {
            mMediaPlayer.seekTo(msec);
        }
    }

    public boolean isPlaying() {
        return prepared && mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    @Override
    public boolean canPause() {

        return false;
    }

    @Override
    public boolean canSeekBackward() {

        return false;
    }

    @Override
    public boolean canSeekForward() {

        return false;
    }

    @Override
    public int getAudioSessionId() {

        return 0;
    }

    public boolean isPrepared() {
        return prepared;
    }
}
