package com.iyuba.music.dubbing.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import timber.log.Timber;

/**
 * Description:
 */
public class AudioEncoder implements Runnable {

    private String mime = "audio/mp4a-latm";
    private AudioRecord mRecorder;
    private MediaCodec mEnc;
    private int rate = 128000;

    //录音设置
    private int sampleRate = 44100;   //采样率，默认44.1k
    private int channelCount = 2;     //音频采样通道，默认2通道
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;        //通道设置，默认立体声
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;     //设置采样数据格式，默认16比特PCM
    private FileOutputStream fos;

    private byte[] buffer;
    private boolean isRecording;
    private Thread mThread;
    private int bufferSize;

    private String mSavePath;

    public String getmSavePath() {
        return mSavePath;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setSavePath(String path) {
        this.mSavePath = path;
    }

    public void prepare()  {
        try {
            fos = new FileOutputStream(mSavePath);
            //音频编码相关
            MediaFormat format = MediaFormat.createAudioFormat(mime, sampleRate, channelCount);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            //format.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            format.setInteger(MediaFormat.KEY_BIT_RATE, rate);
            mEnc = MediaCodec.createEncoderByType(mime);
            mEnc.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            //音频录制相关
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            buffer = new byte[bufferSize];
            mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig,
                    audioFormat, bufferSize);
        } catch (IOException e) {
            Timber.e("文件生成失败！");
            e.printStackTrace();
        }
    }

    public synchronized void start()  {
        try {
            mEnc.start();
            if (mRecorder == null) {
                mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig,
                        audioFormat, bufferSize);
            }
            mRecorder.startRecording();
            if (mThread != null && mThread.isAlive()) {
                isRecording = false;
                mThread.join();
            }
            isRecording = true;
            mThread = new Thread(this);
            mThread.start();
        } catch (InterruptedException e) {
            Timber.e("录音失败！");
            e.printStackTrace();
        }
    }

    private ByteBuffer getInputBuffer(int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mEnc.getInputBuffer(index);
        } else {
            return mEnc.getInputBuffers()[index];
        }
    }

    private ByteBuffer getOutputBuffer(int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mEnc.getOutputBuffer(index);
        } else {
            return mEnc.getOutputBuffers()[index];
        }
    }

    //TODO Add End Flag  注意外部是循环内调用的
    private void readOutputData() throws Exception {
        int index = mEnc.dequeueInputBuffer(-1);
        if (index >= 0) {
            final ByteBuffer buffer = getInputBuffer(index);
            buffer.clear();
            int length = mRecorder.read(buffer, bufferSize);
            if (length > 0) {
                mEnc.queueInputBuffer(index, 0, length, System.nanoTime() / 1000, 0);
            } else {
                Log.e("uwang", "length-->" + length);
            }
        }
        MediaCodec.BufferInfo mInfo = new MediaCodec.BufferInfo();
        int outIndex;
        do {
            outIndex = mEnc.dequeueOutputBuffer(mInfo, 0);
            Log.e("wuwang", "audio flag---->" + mInfo.flags + "/" + outIndex);
            if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                //isRecording=false;
                return;
            } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //isRecording=false;
                return;
            }

            if (outIndex >= 0) {
                ByteBuffer buffer = getOutputBuffer(outIndex);
                buffer.position(mInfo.offset);
                byte[] temp = new byte[mInfo.size + 7];
                buffer.get(temp, 7, mInfo.size);
                addADTStoPacket(temp, temp.length);
                fos.write(temp);
                Timber.e("temp***" + temp.length);
                mEnc.releaseOutputBuffer(outIndex, false);
            }
        } while (outIndex >= 0);
    }

    /**
     * 给编码出的aac裸流添加adts头字段
     *
     * @param packet    要空出前7个字节，否则会搞乱数据
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    /**
     * 停止录制
     */
    public void stop() {
        try {
            isRecording = false;
            mThread.join(); //这句开始的时候是呗注释掉的
            mRecorder.stop();
            mEnc.stop();
            mEnc.release();
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setState(boolean state){
        isRecording =  state;
    }

    @Override
    public void run() {

        while (isRecording) {
           // Timber.e("评测线程开启！while "+isRecording);
            try {
                readOutputData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
