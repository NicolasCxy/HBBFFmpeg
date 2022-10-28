package com.hbb.ffmepg.code.opengl;

import android.content.Context;
import android.opengl.GLES20;

import com.hbb.ffmepg.code.utils.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class AbstractFilter {

    //顶点坐标
    float[] VERTEX = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    //纹理坐标
    float[] TEXTURE = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };


    protected final int program;

    protected final int avPosition;
    protected final int afPosition;

    private final int vTexture;
    protected final FloatBuffer vertexBuffer;
    protected final FloatBuffer textureBuffer;


    public int mWidth;
    public int mHeight;

    public AbstractFilter(Context context, int vertex, int fragment) {
        String vertexShader = OpenGLUtils.readRawTextFile(context, vertex);
        String fragSharder = OpenGLUtils.readRawTextFile(context, fragment);

        //创建总程序
        program = OpenGLUtils.makeProgram(vertexShader, fragSharder);

        avPosition = GLES20.glGetAttribLocation(program, "av_Position");
        afPosition = GLES20.glGetAttribLocation(program, "af_Position");


        //获取片源参数，纹理对象，用来接图层
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        //创建数据源
        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.clear();
        vertexBuffer.put(VERTEX);


        textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.clear();
        textureBuffer.put(TEXTURE);


//        vertexBuffer = ByteBuffer.allocateDirect(TEXTURE.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(VERTEX);
//        vertexBuffer.position(0);
//
//        //初始化坐标  - 纹理坐标
//        textureBuffer = ByteBuffer.allocateDirect(TEXTURE.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(TEXTURE);
//        textureBuffer.position(0);
    }


    public int onDraw(int textureId) {
        return onDraw(textureId, null);
    }

    public int onDraw(int textureId, float[] mtx) {
//        GLES20.glViewport(0, 0, mWidth, mHeight);

        GLES20.glUseProgram(program);
        vertexBuffer.position(0);
        textureBuffer.position(0);

        //顶点赋值 - 世界坐标
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(avPosition);
        //顶点赋值 - 片源着色器
        GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(afPosition);

        //启用图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        //关联
        GLES20.glUniform1i(vTexture, 0);

        beforeDraw();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //将处理后的纹理返回
        return textureId;
    }

    private void beforeDraw() {

    }

    /**
     * 设置size，可选，创建FBO的时候需要
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    private void release() {
        GLES20.glDeleteProgram(program);
    }


}
