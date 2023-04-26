//
// Created by DELL on 2023/3/24.
//

#ifndef TESTYUVLIB_YUVHANDLER_H
#define TESTYUVLIB_YUVHANDLER_H

#include <string.h>
#include "libyuv.h"

using namespace libyuv;

class YuvHandler {

public:

    YuvHandler();

    ~YuvHandler();

    void mergeYuvPip(char *big_yuv_arr, int big_w, int big_h,
                     char *small_yuv_arr, int small_w, int small_h,
                     int small_final_w, int small_final_h,
                     int small_start_pos_x, int small_start_pos_y);

    void nv12Scale(uint8_t *src, int src_width, int src_height, uint8_t *dst, int dst_width,
                   int dst_height);

    void yuv420pScale(uint8_t *src_y, uint8_t *src_u, uint8_t *src_v, int src_width, int src_height,
                      uint8_t *dst_y, uint8_t *dst_u, uint8_t *dst_v, int dst_width, int dst_height);

public:
    int mSmallFinalWidth;
    int mSmallFinalHeight;
    char *mSmallDstYuvArr;

};


#endif //TESTYUVLIB_YUVHANDLER_H
