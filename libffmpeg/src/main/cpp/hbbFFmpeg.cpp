#include <jni.h>
#include <string>
#include "AndroidLog.h"
#include "VideoDecoder.h"
#include "AbleCallJava.h"
#include "HbbGlobalStatus.h"

//VideoDecoder *mVideoDecoder = NULL;
//AbleCallJava *callJava = NULL;
_JavaVM *javaVM = NULL;
HbbGlobalStatus *mGlobalStatus = NULL;

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
JNIEXPORT jlong JNICALL
Java_com_hbb_ffmpeg_SoftwareVideoDeCode_nativeInit(JNIEnv *env, jobject thiz) {
    LOGE("_nativeInit!!!");

    AbleCallJava *callJava = new AbleCallJava(javaVM, env, thiz);
    mGlobalStatus = new HbbGlobalStatus();
    VideoDecoder *mVideoDecoder = new VideoDecoder(mGlobalStatus);
    mVideoDecoder->setAbleCallJava(callJava);
    mVideoDecoder->DecodeInit();
    mVideoDecoder->DecodeStart();
    return reinterpret_cast<jlong>(mVideoDecoder);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_hbb_ffmpeg_SoftwareVideoDeCode_nativeDecodeVideo(JNIEnv *env, jobject thiz,
                                                          jlong de_code_client, jbyteArray data_,
                                                          jint length) {
    //通过指针转换一把
    VideoDecoder *mVideoDecoder  = reinterpret_cast<VideoDecoder *>(de_code_client);

    jbyte *data = env->GetByteArrayElements(data_, NULL);
    if (mVideoDecoder != NULL) {
//        mVideoDecoder->H264Decode(reinterpret_cast<uint8_t *>(data), length);

        mVideoDecoder->onFrameData(reinterpret_cast<uint8_t *>(data), length);

//        mVideoDecoder->onFrameData(reinterpret_cast<uint8_t *>(data), length);
    }
    env->ReleaseByteArrayElements(data_, data, 0);
}