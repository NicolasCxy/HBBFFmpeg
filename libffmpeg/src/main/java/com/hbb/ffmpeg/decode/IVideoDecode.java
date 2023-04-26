package com.hbb.ffmpeg.decode;

import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;

public interface IVideoDecode {

    void deCodeVideo(byte[] data, int length);

    void setStreamPath(String mStreamPath);

    void setAbleGlSurface(AbleGLSurfaceView ableGlView);


}
