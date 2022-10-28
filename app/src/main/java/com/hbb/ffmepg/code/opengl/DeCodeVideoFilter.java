package com.hbb.ffmepg.code.opengl;

import android.content.Context;
import android.opengl.GLES20;

import com.hbb.ffmepg.code.R;

import java.nio.ByteBuffer;

public class DeCodeVideoFilter extends AbstractFilter{

    private int sampler_y,sampler_u,sampler_v;
    private final int[] textureId_yuv;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;

    public DeCodeVideoFilter(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);

        //初始化
        sampler_y = GLES20.glGetUniformLocation(program, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(program, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(program, "sampler_v");

        //生成纹理
        textureId_yuv = new int[3];
        GLES20.glGenTextures(3, textureId_yuv, 0);

        //遍历设置纹理拉伸属性，3个对应着YUV
        for(int i = 0; i < 3; i++)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[i]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
    }


    public void onDrawYUV() {
        if(mWidth > 0 && mHeight > 0 && y != null && u != null && v != null)
        {

            vertexBuffer.position(0);
            textureBuffer.position(0);
            //使用程序 (里面包含了顶点和片源)
            GLES20.glUseProgram(program);

            GLES20.glEnableVertexAttribArray(avPosition);
            GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

            GLES20.glEnableVertexAttribArray(afPosition);
            GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mWidth, mHeight, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mWidth / 2, mHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId_yuv[2]);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, mWidth / 2, mHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);

            GLES20.glUniform1i(sampler_y, 0);
            GLES20.glUniform1i(sampler_u, 1);
            GLES20.glUniform1i(sampler_v, 2);

            y.clear();
            u.clear();
            v.clear();
            y = null;
            u = null;
            v = null;
        }

    }

    public void showData(int width, int height, byte[] y, byte[] u, byte[] v) {
        this.mWidth = width;
        this.mHeight = height;
        this.y = ByteBuffer.wrap(y);
        this.u = ByteBuffer.wrap(u);
        this.v = ByteBuffer.wrap(v);
    }
}
