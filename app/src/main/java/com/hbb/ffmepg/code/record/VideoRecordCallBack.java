package com.hbb.ffmepg.code.record;

public interface VideoRecordCallBack {
    void videoData(byte[] data, int length,int videoMode);

}
