package com.hbb.ffmepg.code.record;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocalFileVideoRecord extends BaseVideoRecord {
    private static final String TAG = "LocalFileVideoRecord";


    private int startIndex = 0;
    private Disposable mDisposable;
    private int count = 0;
    byte[] sourceData = null;


    public LocalFileVideoRecord() {
        videoMode = VIDEO_MODE_LOCAL_FILE;
    }

    @Override
    public void start(String url) {
        startReadVideoData(url);
    }

    @Override
    public void stop() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }

        sourceData = null;
        startIndex = 0;
    }


    public void startReadVideoData(String url) {
        Log.i(TAG, "loadData_path " + url);
        if (null != mDisposable && !mDisposable.isDisposed()) {
            return;
        }

        sourceData = null;
        try {
            sourceData = getBytes(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startIndex = 0;
        int totalSize = sourceData.length;

        //轮询读取数据
        mDisposable = Observable.interval(0, 66, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {

                        if (totalSize == 0 || startIndex >= totalSize) {
                            Log.i(TAG, "停止当前寻轮1");
                            mDisposable.dispose();
                            return;
                        }

                        int nextFrameStart = findByFrame(sourceData, startIndex + 2, totalSize);

                        if (nextFrameStart <= 0) {
                            Log.d(TAG, "停止当前寻轮2");
                            mDisposable.dispose();
                            return;
                        }

                        byte[] tempData = new byte[nextFrameStart - startIndex];

                        System.arraycopy(sourceData, startIndex, tempData, 0, tempData.length);

                        Log.i(TAG, "读取到数据:" + tempData.length + ",data[4]:" + (tempData[4] & 0x1f) + ",neextFrameIndex:" + nextFrameStart + ",count :" + count);

//                        StreamFile.writeBytes(tempData);
//
//                        if(count > 4){
//                            return;
//                        }
//                        Log.w(TAG, "读取到数据@@:" + tempData.length + ",data[4]:" + (tempData[4] & 0x1f) + ",neextFrameIndex:" + nextFrameStart + ",count :" + count);
//
//                        count++;
                        //反馈到上层处理进行处理
                        videoSendDataToApp(tempData,tempData.length);
                        startIndex = nextFrameStart;
                    }
                });

    }


    private int findByFrame(byte[] sourceData, int index, int totalSize) {
        for (int i = index; i < totalSize - 4; i++) {
            if (sourceData[i] == 0x00
                    && sourceData[i + 1] == 0x00
                    && sourceData[i + 2] == 0x00
                    && sourceData[i + 3] == 0x01) {

                return i;
            }
        }
        return -1;
    }

    public byte[] getBytes(String path) throws IOException {
        File file = new File(path);
        InputStream is = new DataInputStream(new FileInputStream(file));
        Log.i(TAG, "getBytes - > path: " + file.getPath());
        int len;
        int size = 1024;
        byte[] buf;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1)
            bos.write(buf, 0, len);
        buf = bos.toByteArray();
        return buf;
    }
}
