package com.hbb.ffmpeg;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.hbb.ffmpeg.annotation.NativeCallBack;
import com.hbb.ffmpeg.decode.BaseDecode;
import com.hbb.ffmpeg.decode.IVideoDecode;
import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;
import com.hbb.ffmpeg.utils.ImageUtil;
import com.hbb.ffmpeg.utils.StreamFile;

public class SoftwareVideoDeCode extends BaseDecode implements IVideoDecode {
    private static final String TAG = "SoftwareVideoDeCode";

    static {
        System.loadLibrary("hbbFFmpeg");
    }

    private AbleGLSurfaceView mAbleGlView;
    //native client指针
    private long mDeCodeClient;



    public SoftwareVideoDeCode(String mStreamPath) {
        this.mStreamPath = mStreamPath;
        init();
    }

    private void init() {
        initThread("softWareDecode：" + mStreamPath);
        mDecodeHandler.post(() -> mDeCodeClient = nativeInit());
    }

    /**
     * 交给ffmpeg解码
     *
     * @param data
     * @param length
     */
    @Override
    public void deCodeData(byte[] data, int length) {
        nativeDecodeVideo(mDeCodeClient, data, length);
    }

    @Override
    public void setStreamPath(String mStreamPath) {
        if(!mStreamPath.equals(mStreamPath)){
            this.mStreamPath = mStreamPath;
            this.readIFrame = false;
        }
    }

    /**
     * 获取GLView，方便后面渲染
     *
     * @param ableGlView openGlView
     */
    @Override
    public void setAbleGlSurface(AbleGLSurfaceView ableGlView) {
        this.mAbleGlView = ableGlView;
    }


    @NativeCallBack
    public void onCallRenderYUV(int width, int height, byte[] y, byte[] u, byte[] v) {
//        Log.i(TAG, "onCallRenderYUV: " + width + ",height :" + height + ",ySize:" + y.length
//                + ",u:" + u.length + ",count : " + testCount + ",mStreamPath:" + mStreamPath + ",threadName:" + Thread.currentThread().getName());
        //反馈到上层
        if (this.mAbleGlView != null) {
            this.mAbleGlView.setYUVData(width, height, y, u, v);
        }

    }


    private native long nativeInit();

    private native void nativeDecodeVideo(long deCodeClient, byte[] data, int length);

    public String getStreamPath() {
        return mStreamPath;
    }


}
