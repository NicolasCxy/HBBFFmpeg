package com.hbb.ffmepg.code.record;

public abstract class BaseVideoRecord implements VideoRecord {

    protected VideoRecordCallBack mCallBack;


    public static int VIDEO_MODE_LOCAL_FILE = 0X11;
    public static int VIDEO_MODE_REMOTE_RTSP = 0X22;

    public int videoMode = VIDEO_MODE_LOCAL_FILE;


    @Override
    public void setVideoCallBack(VideoRecordCallBack callBack) {
        this.mCallBack = callBack;
    }

    /**
     * 回调数据到上层处理
     */
    protected void videoSendDataToApp(byte[] data, int length){

        if(mCallBack != null){
            mCallBack.videoData(data,length,videoMode);
        }
    }
}
