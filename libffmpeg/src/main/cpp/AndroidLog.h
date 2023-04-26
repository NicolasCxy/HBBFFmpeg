
#ifndef MYMUSIC_ANDROIDLOG_H
#define MYMUSIC_ANDROIDLOG_H

#endif //MYMUSIC_ANDROIDLOG_H

#include "android/log.h"

#define LOG_DEBUG false

#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG,"CxySoftWare",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"CxySoftWare",FORMAT,##__VA_ARGS__);
