//
// Created by DELL on 2022/10/26.
//

#ifndef HBB_FFMEPG_CODE_VIDEODECODER_H
#define HBB_FFMEPG_CODE_VIDEODECODER_H

#include "AndroidLog.h"
#include "AbleCallJava.h"
#include <unistd.h>
#include "HbbQueue.h"
#include "HbbGlobalStatus.h"

#include <iostream>
#include <chrono>
#include <unistd.h>

extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavutil/time.h"
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
}

class VideoDecoder {
public:
    VideoDecoder(HbbGlobalStatus *globalStatus );
    ~VideoDecoder();
    int DecodeInit();
    int DecoderRelease();
    void DecodeStart();
    void setAbleCallJava(AbleCallJava *callJava);
    void H264Decode(uint8_t *data,int dataLength);
    void decodeAvPacket(AVPacket *avPacket);
    void onFrameData(uint8_t *data,int dataLength);
    void handlerDecodeH264();
    char* getVersion();

private:
    AVCodecContext *pAVCodecCtx_decoder = NULL;
    const AVCodec *pAVCodec_decoder = NULL;
    AVPacket *avPacket = NULL;
    AVFrame *avFrame = NULL;
    AbleCallJava *callJava = NULL;
    unsigned sleepDelta;        //解码延迟时长
    pthread_mutex_t codecMutex;
    pthread_t decodeThread;
    HbbGlobalStatus *globalStatus;
    HbbQueue *queue;
    pthread_cond_t condPacket;  //线程阻塞类


};


#endif //HBB_FFMEPG_CODE_VIDEODECODER_H
