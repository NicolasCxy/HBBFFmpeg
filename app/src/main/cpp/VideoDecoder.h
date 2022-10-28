//
// Created by DELL on 2022/10/26.
//

#ifndef HBB_FFMEPG_CODE_VIDEODECODER_H
#define HBB_FFMEPG_CODE_VIDEODECODER_H

#include "AndroidLog.h"
#include "AbleCallJava.h"
#include <unistd.h>
#include <pthread.h>
#include <string>

extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavutil/time.h"
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
}

class VideoDecoder {
public:
    VideoDecoder();
    ~VideoDecoder();
    int DecodeInit();
    int DecoderRelease();
    void setAbleCallJava(AbleCallJava *callJava);
    void H264Decode(uint8_t *data,int dataLength);
    char* getVersion();

private:
    AVCodecContext *pAVCodecCtx_decoder = NULL;
    const AVCodec *pAVCodec_decoder = NULL;
    AVPacket *avPacket = NULL;
    AVFrame *avFrame = NULL;
    AbleCallJava *callJava = NULL;
    unsigned sleepDelta;
    pthread_mutex_t codecMutex;
};


#endif //HBB_FFMEPG_CODE_VIDEODECODER_H
