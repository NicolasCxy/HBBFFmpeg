//
// Created by DELL on 2022/10/27.
//

#ifndef HBB_FFMEPG_CODE_ABLECALLJAVA_H
#define HBB_FFMEPG_CODE_ABLECALLJAVA_H

#include "jni.h"
#include <linux/stddef.h>
#include "AndroidLog.h"

class AbleCallJava {
public:
    _JavaVM *javaVm = NULL;
    JNIEnv *jniEnv = NULL;
    jobject jobj = NULL;
    //子线程
    JNIEnv *otherJniEnv = NULL;
    jmethodID jmid_callRenderYUV = NULL;
public:
    AbleCallJava(_JavaVM *javaVM, JNIEnv *env, jobject obj);
    ~AbleCallJava();
    void onCallRenderYUV(int width, int height, uint8_t *fy, uint8_t *fu, uint8_t *fv);
};


#endif //HBB_FFMEPG_CODE_ABLECALLJAVA_H
