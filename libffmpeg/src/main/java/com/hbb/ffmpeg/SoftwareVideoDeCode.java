package com.hbb.ffmpeg;

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

    public SoftwareVideoDeCode(String mStreamPath) {
        this.mStreamPath = mStreamPath;
        nativeInit();
    }

    /**
     * 交给ffmpeg解码
     *
     * @param data
     * @param length
     */
    public void deCodeVideo(byte[] data, int length) {
        Log.i(TAG, "deCodeVideo: " + data.length);
        nativeDecodeVideo(data, length);
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
        Log.i(TAG, "onCallRenderYUV: " + width + ",height :" + height + ",ySize:" + y.length + ",u:" + u.length + ",count : " + testCount);
        if (this.mAbleGlView != null) {
            this.mAbleGlView.setYUVData(width, height, y, u, v);
        }


        if (testCount == 50) {
            byte[] nv12 = new byte[width * height * 3 / 2];
            ImageUtil.yuvToNv21(y, u, v, nv12, width, height);
            StreamFile.writeBytesYUV(nv12);

        }

        testCount++;


    }


    private native void nativeInit();

    private native void nativeDecodeVideo(byte[] data, int length);


}
