#include <jni.h>
#include <string>
#include "AndroidLog.h"
#include "VideoDecoder.h"
#include "AbleCallJava.h"

VideoDecoder *mVideoDecoder = NULL;
_JavaVM *javaVM = NULL;
AbleCallJava *callJava = NULL;

extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    jint result = -1;
    javaVM = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hbb_ffmepg_code_SoftwareVideoDeCode_nativeInit(JNIEnv *env, jobject thiz) {
    if (callJava == NULL) {
        callJava = new AbleCallJava(javaVM, env, thiz);
    }

    if (mVideoDecoder == NULL) {
        mVideoDecoder = new VideoDecoder();
        mVideoDecoder->setAbleCallJava(callJava);
        mVideoDecoder->DecodeInit();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hbb_ffmepg_code_SoftwareVideoDeCode_nativeDecodeVideo(JNIEnv *env, jobject thiz,
                                                               jbyteArray data_, jint length) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    if (mVideoDecoder != NULL) {
        mVideoDecoder->H264Decode(reinterpret_cast<uint8_t *>(data), length);
    }
    env->ReleaseByteArrayElements(data_, data, 0);

}