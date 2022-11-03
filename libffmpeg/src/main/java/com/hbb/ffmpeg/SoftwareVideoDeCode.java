package com.hbb.ffmpeg;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.hbb.ffmpeg.annotation.NativeCallBack;
import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;
import com.hbb.ffmpeg.utils.ImageUtil;
import com.hbb.ffmpeg.utils.StreamFile;

public class SoftwareVideoDeCode {
    private static final String TAG = "SoftwareVideoDeCode";

    static {
        System.loadLibrary("native-lib");
    }

    private String mStreamPath = "";
    private AbleGLSurfaceView mAbleGlView;
    private int testCount = 0;

    //native client指针
    private long mDeCodeClient;
    private Handler mDecodeHandler;

    public SoftwareVideoDeCode(String mStreamPath) {
        this.mStreamPath = mStreamPath;
        init();
    }

    private void init() {
        mDeCodeClient = nativeInit();
        HandlerThread mDecodeThread = new HandlerThread("SoftwareVideoDeCode - " + mStreamPath);
        mDecodeThread.start();
        mDecodeHandler = new Handler(mDecodeThread.getLooper());
    }

    /**
     * 交给ffmpeg解码
     *
     * @param data
     * @param length
     */
    public void deCodeVideo(byte[] data, int length) {
        Log.i(TAG, "deCodeVideo: " + data.length + ",path@:" + mStreamPath);
        mDecodeHandler.post(() -> nativeDecodeVideo(mDeCodeClient, data, length));
//        nativeDecodeVideo(mDeCodeClient, data, length);
    }

    /**
     * 获取GLView，方便后面渲染
     *
     * @param ableGlView openGlView
     */
    public void setAbleGlSurface(AbleGLSurfaceView ableGlView) {
        this.mAbleGlView = ableGlView;
    }


    @NativeCallBack
    public void onCallRenderYUV(int width, int height, byte[] y, byte[] u, byte[] v) {
        Log.i(TAG, "onCallRenderYUV: " + width + ",height :" + height + ",ySize:" + y.length
                + ",u:" + u.length + ",count : " + testCount + ",mStreamPath:" + mStreamPath + ",threadName:" + Thread.currentThread().getName());
        //反馈到上层
        if (this.mAbleGlView != null) {
            this.mAbleGlView.setYUVData(width, height, y, u, v);
        }

        //测试模块
        if (testCount == 50) {
            byte[] nv12 = new byte[width * height * 3 / 2];
            ImageUtil.yuvToNv21(y, u, v, nv12, width, height);
            StreamFile.writeBytesYUV(nv12);

        }
        testCount++;
    }


    private native long nativeInit();

    private native void nativeDecodeVideo(long deCodeClient, byte[] data, int length);


}
