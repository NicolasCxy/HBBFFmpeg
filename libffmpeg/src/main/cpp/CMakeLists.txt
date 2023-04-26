cmake_minimum_required(VERSION 3.10.2)
project("code")

#FFMPEG
include_directories(.)
include_directories(include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
#aux_source_directory(. SOURCE)

#LIBYUV
add_subdirectory(libyuv)
include_directories(libyuv/include)


file(GLOB cpp_source *.cpp)

# 这一句用来导出编译生成的so库
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})


add_library(
        hbbFFmpeg
        SHARED
        ${cpp_source})


target_link_libraries(
        hbbFFmpeg
        avcodec
        avdevice
        avfilter
        avformat
        avutil
        swresample
        swscale
        hbblibyuv
        log
        android)