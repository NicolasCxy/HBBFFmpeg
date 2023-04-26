package com.hbb.ffmpeg.decode;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public abstract class BaseDecode implements IVideoDecode {
    private static final String TAG = "BaseDecode";

    protected Handler mDecodeHandler;
    protected HandlerThread mDecodeThread;
    protected String mStreamPath = "";
    protected boolean isStart = false;

    //I帧检测
    protected boolean readIFrame = false;


    public void initThread(String threadName) {
        mDecodeThread = new HandlerThread(threadName);
        mDecodeThread.start();
        mDecodeHandler = new Handler(mDecodeThread.getLooper());
    }


    public String getStreamPath() {
        return mStreamPath;
    }

    public void setStreamPath(String mStreamPath) {
        this.mStreamPath = mStreamPath;
    }

    @Override
    public void deCodeVideo(byte[] data, int length) {
        if (!readIFrame) {
            if ((data[4] & 0x1F) == 7 || (data[4] & 0x1F) == 5) {
                Log.d(TAG, "deCodeVideo: 读取到I帧，streamId: " + mStreamPath);
                readIFrame = true;
                mDecodeHandler.post(() -> deCodeData(data, length));
            } else {
                Log.w(TAG, "deCodeVideo: 没有检测到I帧，丢弃...、streamId: " + mStreamPath);
            }
        } else {
            mDecodeHandler.post(() -> deCodeData(data, length));
        }
    }

    /**
     * 开始解码，无需做线程切换
     */
    public abstract void deCodeData(byte[] data, int length);
}
