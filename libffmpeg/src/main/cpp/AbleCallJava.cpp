//
// Created by DELL on 2022/10/27.
//

#include "AbleCallJava.h"

AbleCallJava::AbleCallJava(_JavaVM *javaVM, JNIEnv *env, jobject obj) {
    this->javaVm = javaVM;
    this->jniEnv = env;
    this->jobj = env->NewGlobalRef(obj);
    jclass jlz = jniEnv->GetObjectClass(jobj);
    if (!jlz) {
        if (LOG_DEBUG) {
            LOGE("get jclass wrong");
        }
        return;
    }
    jmid_callRenderYUV = env->GetMethodID(jlz, "onCallRenderYUV", "(II[B[B[B)V");
    LOGE("AbleCallJava对象@： %p", this->jobj);
}

AbleCallJava::~AbleCallJava() {
    LOGE("释放对象： %p", this->jobj);
//    jniEnv->DeleteLocalRef(this->jobj);
    if (nullptr == otherJniEnv) {
        javaVm->DetachCurrentThread();
    }
}

void AbleCallJava::onCallRenderYUV(int width, int height, uint8_t *fy, uint8_t *fu, uint8_t *fv) {

    if (nullptr == otherJniEnv) {
        if (javaVm->AttachCurrentThread(&otherJniEnv, 0) != JNI_OK) {
            if (LOG_DEBUG) {
                LOGE("call onCallRenderH264 worng");
            }
            return;
        }
    }

     //将数据进行转换
    jbyteArray y = otherJniEnv->NewByteArray(width * height);
    otherJniEnv->SetByteArrayRegion(y, 0, width * height, reinterpret_cast<const jbyte *>(fy));

    jbyteArray u = otherJniEnv->NewByteArray(width * height / 4);
    otherJniEnv->SetByteArrayRegion(u, 0, width * height / 4, reinterpret_cast<const jbyte *>(fu));

    jbyteArray v = otherJniEnv->NewByteArray(width * height / 4);
    otherJniEnv->SetByteArrayRegion(v, 0, width * height / 4, reinterpret_cast<const jbyte *>(fv));

    //回调到上层
    otherJniEnv->CallVoidMethod(jobj, jmid_callRenderYUV, width, height, y, u, v);


    //释放资源
    otherJniEnv->DeleteLocalRef(y);
    otherJniEnv->DeleteLocalRef(u);
    otherJniEnv->DeleteLocalRef(v);



//        this->javaVm->DestroyJavaVM();

}
