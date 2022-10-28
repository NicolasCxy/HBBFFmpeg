package com.hbb.ffmepg.code.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class AbleGLSurfaceView extends GLSurfaceView {

    private AbleRender mRender;

    public AbleGLSurfaceView(Context context) {
        this(context, null);
    }

    public AbleGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initOpenGL(context);
    }

    private void initOpenGL(Context context) {
        setEGLContextClientVersion(2);
        mRender = new AbleRender(context);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    //交给OPEN GL 渲染
    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v) {
        GLES20.glViewport(0, 0, width, height);
        if (mRender != null) {
            mRender.getVideoFilter().showData(width, height, y, u, v);
            requestRender();
        }
    }
}
