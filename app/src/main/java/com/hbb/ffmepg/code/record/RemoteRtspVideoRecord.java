package com.hbb.ffmepg.code.record;

import com.cxy.rtsp.RtspHelper;
import com.cxy.rtsp.RtspVideo;

public class RemoteRtspVideoRecord extends BaseVideoRecord {

    RtspVideo mIpc1;

    @Override
    public void start(String url) {
        RtspHelper.setRtspVideoListener(mRtspDataCallBack);
        mIpc1 = RtspHelper.createRtspClient(RtspVideo.IPC_MODE_IPC1);
        mIpc1.start(url);
    }

    @Override
    public void stop() {
        if (mIpc1 != null) {
            mIpc1.release();
        }
    }

    RtspVideo.RtspDataCallBack mRtspDataCallBack = new RtspVideo.RtspDataCallBack() {
        @Override
        public void onRtspDataReceive(int index, byte[] data, int dataLen, int frameType, long captureTimeNs) {
            videoSendDataToApp(data, dataLen);
        }
    };


}
