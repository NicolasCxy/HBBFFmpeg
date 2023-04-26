package com.hbb.ffmpeg.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HardwareVideoDecode extends BaseDecode   {
    private static final String TAG = "AbleHarWareDecode";
    private MediaCodec mediaCodec;

    public HardwareVideoDecode(String mStreamPath) {
        this.mStreamPath = mStreamPath;
        initThread("hardWareDecode：" + mStreamPath);
    }

    public void initDecoder(Surface surface) {
        mDecodeHandler.post(() -> {
            try {
                mediaCodec = MediaCodec.createDecoderByType("video/avc");
                final MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720);
                mediaCodec.configure(format,
                        surface,
                        null, 0);
                mediaCodec.start();
                isStart = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void deCodeData(byte[] data, int length) {
        onDeCode(data);
    }


    public void onDeCode(byte[] data) {
        if(!isStart) return;
//        Log.i(TAG, "onHardwareDeCode: " + data.length + ",flag: " + (data[4] & 0x1f));
        int index= mediaCodec.dequeueInputBuffer(100);
        if (index >= 0) {
            //从DSP解码输入队列
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            //将编码数据放到队列中
            inputBuffer.put(data, 0, data.length);
            //通知DSP芯片去解码
            mediaCodec.queueInputBuffer(index,
                    0, data.length, System.currentTimeMillis(), 0);
        }
        
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        //获取输出队列索引
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100);
        //如果解码成功，则一直从队列取数据
        while (outputBufferIndex >=0) {
            //释放队列，并渲染到surfaceView上
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }



    @Override
    public void setAbleGlSurface(AbleGLSurfaceView ableGlView) {

    }


    public void release(){
        Log.i(TAG, "releaseDecode@!");
        try {
            isStart = false;

            if (mDecodeHandler != null) {
                mDecodeHandler.removeCallbacksAndMessages(null);
            }

            if (mediaCodec!=null) {
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
