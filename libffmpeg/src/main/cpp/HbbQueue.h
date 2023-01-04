//
// Created by DELL on 2023/1/3.
//

#ifndef HBB_FFMEPG_CODE_HBBQUEUE_H
#define HBB_FFMEPG_CODE_HBBQUEUE_H

#include "queue"
#include "pthread.h"
#include "AndroidLog.h"
#include "HbbGlobalStatus.h"

extern "C"
{
#include <libavutil/time.h>
#include "libavformat/avformat.h"
};


class HbbQueue {
public:
    std::queue<AVPacket *> queuePacket;
    pthread_mutex_t mutexPacket;    //线程锁
    pthread_cond_t condPacket;  //线程阻塞类
    HbbGlobalStatus *globalStatus = NULL;


public:
    HbbQueue(HbbGlobalStatus *globalStatus);
    ~HbbQueue();

    int putAvPacket(AVPacket *packet);
    int getAvPacket(AVPacket *packet);

    int getQueueSize();

    void clearAvpacket();

};


#endif //HBB_FFMEPG_CODE_HBBQUEUE_H
