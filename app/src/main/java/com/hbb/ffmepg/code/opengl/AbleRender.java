package com.hbb.ffmepg.code.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.hbb.ffmepg.code.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AbleRender implements GLSurfaceView.Renderer {

    private Context context;
    private DeCodeVideoFilter videoFilter;


    public AbleRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        videoFilter = new DeCodeVideoFilter(context);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        videoFilter.setSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);
        getVideoFilter().onDrawYUV();
        //刷新
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    public DeCodeVideoFilter getVideoFilter() {
        return videoFilter;
    }




}
