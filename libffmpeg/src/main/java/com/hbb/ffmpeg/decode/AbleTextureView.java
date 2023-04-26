package com.hbb.ffmpeg.decode;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

public class AbleTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private Context mContext;
    private Surface surface;

    private HardwareVideoDecode mAbleDecode;


    public AbleTextureView(Context context) {
        this(context,null);
    }

    public AbleTextureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AbleTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        mContext = context;
        setSurfaceTextureListener(this);
    }


    public void setEncode(IVideoDecode encode) {
        mAbleDecode = (HardwareVideoDecode) encode;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
        texture.setDefaultBufferSize(getWidth(), getHeight());
        surface = new Surface(texture);
        mAbleDecode.initDecoder(surface);

    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mAbleDecode.release();

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }



}
