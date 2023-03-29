package com.hbb.ffmepg.code.record;

public interface VideoRecord {

    void start(String url);

    void stop();

    void setVideoCallBack(VideoRecordCallBack callBack);

}
