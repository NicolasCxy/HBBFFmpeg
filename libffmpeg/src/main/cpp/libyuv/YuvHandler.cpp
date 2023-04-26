//
// Created by DELL on 2023/3/24.
//

#include "YuvHandler.h"

YuvHandler::YuvHandler() {
    mSmallFinalWidth = 0;
    mSmallFinalHeight = 0;
    mSmallDstYuvArr = nullptr;

}

YuvHandler::~YuvHandler() {
    if (nullptr != mSmallDstYuvArr) {
        delete mSmallDstYuvArr;
        mSmallDstYuvArr = nullptr;
    }
}

void
YuvHandler::mergeYuvPip(char *big_yuv_arr, int big_w, int big_h, char *small_yuv_arr, int small_w,
                        int small_h, int small_final_w, int small_final_h, int small_start_pos_x,
                        int small_start_pos_y) {

    if (big_yuv_arr == nullptr || big_w < 0 || big_h < 0) {
        return;
    }


    if (small_w <= 0 || small_h <= 0 || small_yuv_arr == nullptr) {
        return;
    }

    if (small_final_w > small_w || small_final_h > small_h) {
        return;
    }

    if (small_start_pos_x > big_w) {
        return;
    }

    char *big_y = big_yuv_arr;
    char *big_uv = big_yuv_arr + (big_w * big_h);

    char *small_y = small_yuv_arr;
    char *small_uv = small_yuv_arr + (small_w * small_h);

    if (mSmallFinalWidth != small_final_w || mSmallFinalHeight != small_final_h) {
        mSmallFinalWidth = small_final_w;
        mSmallFinalHeight = small_final_h;

        if (mSmallDstYuvArr != nullptr) {
            delete mSmallDstYuvArr;
        }

        mSmallDstYuvArr = new char[small_final_w * small_final_h * 3 / 2];

    }

    char *dst_small_y = mSmallDstYuvArr;
    char *dst_small_uv = mSmallDstYuvArr + (small_final_w * small_final_h);


    //双线性插值缩小 NV12
    NV12Scale((const uint8_t *) small_y,
              small_w,
              (const uint8_t *) small_uv,
              small_w,
              small_w,
              small_h,
              (uint8_t *) dst_small_y,
              small_final_w,
              (uint8_t *) dst_small_uv,
              small_final_w,
              small_final_w,
              small_final_h,
              libyuv::kFilterBilinear);


    int x_delta = big_w - small_start_pos_x;

    for (int i = 0; i < small_final_h; i++) {
        int j = i + small_start_pos_y;
        memcpy(big_y + j * big_w + small_start_pos_x, dst_small_y + i * small_final_w,
               small_final_w);
    }

    for (int i = 0; i < small_final_h / 2; i++) {
        int j = i + small_start_pos_y;
        memcpy(big_uv + j * big_w + small_start_pos_x, dst_small_uv + i * small_final_w,
               small_final_w);
    }

}

void YuvHandler::nv12Scale(uint8_t *src, int src_width, int src_height, uint8_t *dst, int dst_width,
                           int dst_height) {

//    LOGE("开始缩放");
    /**
     * 1、获取src和dst y、uv分量的首地址
     * 2、调用api进行转换
     */

    int src_y_size = src_width * src_height;
    uint8_t *src_y = src;
    uint8_t *src_uv = src + src_y_size;


    int dst_y_size = dst_width * dst_height;
    uint8_t *dst_y = dst;
    uint8_t *dst_uv = dst + dst_y_size;


    NV12Scale(reinterpret_cast<const uint8_t *>(src_y),
              src_width,
              reinterpret_cast<const uint8_t *>(src_uv),
              src_width,
              src_width,
              src_height,
              reinterpret_cast<uint8_t *>(dst_y),
              dst_width,
              reinterpret_cast<uint8_t *>(dst_uv),
              dst_width, dst_width, dst_height, FilterModeEnum::kFilterBox);

//    LOGE("结束缩放！");

}

void YuvHandler::yuv420pScale(uint8_t *src_y, uint8_t *src_u, uint8_t *src_v, int src_width,
                              int src_height, uint8_t *dst_y, uint8_t *dst_u, uint8_t *dst_v,
                              int dst_width, int dst_height) {

    I420Scale(src_y, src_width,
              src_u, src_width,
              src_v, src_width,
              src_width, src_height,
              dst_y, dst_width,
              dst_u, dst_width,
              dst_v, dst_height,
              dst_width, dst_height,
              FilterModeEnum::kFilterBox);

}


