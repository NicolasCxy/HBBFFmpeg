//
// Created by DELL on 2022/10/26.
//

#include "VideoDecoder.h"

VideoDecoder::VideoDecoder() {
    sleepDelta = 1000000 / 30;
    pthread_mutex_init(&codecMutex, NULL);

}

VideoDecoder::~VideoDecoder() {

}

int VideoDecoder::DecodeInit() {
    getVersion();
    //设置编码器参数
    AVCodecParameters *codecParameters = avcodec_parameters_alloc();
    codecParameters->codec_id = AV_CODEC_ID_H264;


    //初始化编码器
    pAVCodec_decoder = avcodec_find_decoder(codecParameters->codec_id);
    if (!pAVCodec_decoder) {
        LOGE("Can not find codec:%d\n", codecParameters->codec_id);
        return -2;
    }


    //获取编码器上下文
    pAVCodecCtx_decoder = avcodec_alloc_context3(pAVCodec_decoder);
    if (!pAVCodecCtx_decoder) {
        LOGE("Failed to alloc codec context.");
        DecoderRelease();
        return -3;
    }

    //编码参数和编码上下文绑定
    if (avcodec_parameters_to_context(pAVCodecCtx_decoder, codecParameters) < 0) {
        LOGE("Failed to copy avcodec parameters to codec context.");
        DecoderRelease();
        return -3;
    }

    //打开编码器
    if (avcodec_open2(pAVCodecCtx_decoder, pAVCodec_decoder, NULL) < 0) {
        LOGE("Failed to open h264 decoder");
        DecoderRelease();
        return -4;
    }

    //初始化输入输出容器
    avPacket = av_packet_alloc();
    avFrame = av_frame_alloc();
    LOGE("初始化完毕！");

    return 1;
}

int VideoDecoder::DecoderRelease() {
    if (pAVCodecCtx_decoder != NULL) {
        avcodec_free_context(&pAVCodecCtx_decoder);
        pAVCodecCtx_decoder = NULL;
    }

    if (avFrame != NULL) {
        av_frame_free(&avFrame);
        av_free(avFrame);
        avFrame = NULL;
    }

    if (avPacket) {
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;
    }
    return 0;
}


void VideoDecoder::H264Decode(uint8_t *data, int dataLength) {
    //上锁
    pthread_mutex_lock(&codecMutex);

    //填充数据
    avPacket->data = data;
    avPacket->size = dataLength;

    if (avcodec_send_packet(pAVCodecCtx_decoder, avPacket) != 0) {
        LOGE("avcodec_send_packet Error!");
        pthread_mutex_unlock(&codecMutex);
        return;
    }

    int ret = 0;
    while (ret >= 0) {
        ret = avcodec_receive_frame(pAVCodecCtx_decoder, avFrame);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
            usleep(sleepDelta);
            continue;
        } else if (ret < 0) {
            LOGE("%s Error receive decoding video frame ret=%d", __FUNCTION__, ret);
            usleep(sleepDelta);
            continue;
        }

        LOGD("解码成功@ -> width: %d,height: %d,size:%d,format: %d", avFrame->width, avFrame->height,
             avFrame->pkt_size, avFrame->format);

//        if (avFrame->format != AV_PIX_FMT_YUV420P) {
//            LOGE("start CallBack To Java");
//            //回调给上层
//            if (NULL != callJava) {
//                callJava->onCallRenderYUV(
//                        avFrame->width,
//                        avFrame->height,
//                        avFrame->data[0],
//                        avFrame->data[1],
//                        avFrame->data[2]);
//            }
//        } else {      //如果不是YUV420格式需要进行一次转换
//            LOGE("不是YUV420P，需要转换！！！");
//

        //进行颜色类型转换以及大小缩放
        //创建承载转换后的AVFrame
        AVFrame *pFrameYUV420P = av_frame_alloc();
        //获取大小
        int buffSize = av_image_get_buffer_size(
                AV_PIX_FMT_YUV420P,
                pAVCodecCtx_decoder->width,
                pAVCodecCtx_decoder->height,
                1);

        //创建数据容器
        uint8_t *buffer = static_cast<uint8_t *>(av_malloc(buffSize));
        //AVFrame 和 data[]进行关联
        av_image_fill_arrays(pFrameYUV420P->data,
                             pFrameYUV420P->linesize, buffer,
                             AV_PIX_FMT_YUV420P,
                             pAVCodecCtx_decoder->width,
                             pAVCodecCtx_decoder->height, 1);

        //创建转换器
        SwsContext *sws_ctx = sws_getContext(pAVCodecCtx_decoder->width,
                                             pAVCodecCtx_decoder->height,
                                             pAVCodecCtx_decoder->pix_fmt,
                                             pAVCodecCtx_decoder->width,
                                             pAVCodecCtx_decoder->height, AV_PIX_FMT_YUV420P,
                                             SWS_BICUBIC, NULL, NULL, NULL);

        //创建失败
        if (!sws_ctx) {
            av_frame_free(&pFrameYUV420P);
            av_free(pFrameYUV420P);
            av_free(buffer);
            pthread_mutex_unlock(&codecMutex);
            continue;
        }

        //开始转换
        sws_scale(sws_ctx, avFrame->data, avFrame->linesize, 0, avFrame->height,
                  pFrameYUV420P->data, pFrameYUV420P->linesize);

        //反馈到上层渲染
        callJava->onCallRenderYUV(
                pAVCodecCtx_decoder->width,
                pAVCodecCtx_decoder->height,
                pFrameYUV420P->data[0],
                pFrameYUV420P->data[1],
                pFrameYUV420P->data[2]);

        av_frame_free(&pFrameYUV420P);
        av_free(pFrameYUV420P);
        av_free(buffer);
        sws_freeContext(sws_ctx);
    }

    pthread_mutex_unlock(&codecMutex);

}

void VideoDecoder::setAbleCallJava(AbleCallJava *callJava) {
    this->callJava = callJava;
}

using namespace std;

char *VideoDecoder::getVersion() {
    LOGE(" >>>>  av_version_info = %s ", av_version_info()); //获取ffmpeg的版本号
    LOGE(" >>>>  avcodec_version = %d ", avcodec_version());

    int version = avcodec_version();

    //获取avutil三个子版本号
    int a = version / (int) pow(2, 16);
    int b = (int) (version - a * pow(2, 16)) / (int) pow(2, 8);
    int c = version % (int) pow(2, 8);

    LOGD("版本号：%d.%d.%d", a, b, c)

    return nullptr;
}
