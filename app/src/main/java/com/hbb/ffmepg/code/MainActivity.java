package com.hbb.ffmepg.code;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.hbb.ffmpeg.SoftwareVideoDeCode;
import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;

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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * 1、System.loadLibrary 加载
     * 2、判断ClassLoader是否为空
     * 2.1、不为空 -> DexClassLoader初始化时会加载系统SO和当前程序SO到集合中 -> 对so名进行拼接 -> 遍历集合去做对比。
     * 2.1、为空 -> 拼接路径（ "绝对路径" + fileName + ".so"） -> 去系统SO目录寻找，找到就就加载，找不到就报错
     * 3、找到之后调用nativeLoad加载so，扫描函数，生成函数表 ，key是函数名，value是指令地址。
     * <p>
     * <p>
     * 一、art、devlik 虚拟机堆区分配
     * 1、imageSpace : 存放编译好的ota机器码以及系统配置内容
     * 2、zygoteSpace: 存放zygote产生的对象
     * 3、applicationSpace：存放普通应用产生java和native对象，内部包含了新生代老年代
     * 4、largeSpace: 存放大于 3 * pagesize = 12k的对象，还有数组对象
     * <p>
     * 二、artGC流程，尽可能避免GC
     * 1、轻量回收：回收上一次GC到本次GC之间发生变化的对象
     * 2、局部回收：回收large和application 区域对象
     * 3、全局回收：整个堆区进行回收，除imageSpace区域
     * 4、进行扩充：尝试对整个堆区进行扩容，但是有上线
     * 5、回收软引用：回收软引用对象
     * 6、如果空间还是不够就触发OOM
     * <p>
     * 三、内存分析工具
     * 1、ADB：宏观分析内存情况
     * 2、MemoryProfiler ：以图表的形式展示内存情况，以及回收相关
     * 3、leakCanary: 傻瓜式统计Activity和Fragment 对象
     * 4、MAT：比较全面分析内存泄漏内存溢出情况，页面比较复杂
     */

    String path = Environment.getExternalStorageDirectory().getPath() + "/1026jing.h264";
    byte[] sourceData = null;
    private SoftwareVideoDeCode videoDeCode, videoDeCode1;
    private AbleGLSurfaceView mAbleGlView, mAbleGlView2;
    private AbleSoftLayout mAbleSoftManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
        initData();
    }


    private void initView() {
            mAbleSoftManager = (AbleSoftLayout) findViewById(R.id.able_soft);
//        mAbleGlView = (AbleGLSurfaceView) findViewById(R.id.able_glView);
//        mAbleGlView2 = (AbleGLSurfaceView) findViewById(R.id.able_glView2);
    }


    private void initData() {

    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_WIFI_STATE,
            }, 1);
        }
        return false;
    }


    int index = 0;

    public void initFFmpeg(View view) {
//        if (videoDeCode == null) {
//            videoDeCode = new SoftwareVideoDeCode("OneAbleDecode1");
//            videoDeCode.setAbleGlSurface(mAbleGlView);
//        }
//
//        if (videoDeCode1 == null) {
//            videoDeCode1 = new SoftwareVideoDeCode("OneAbleDeCode2");
//            videoDeCode1.setAbleGlSurface(mAbleGlView2);
//        }

        mAbleSoftManager.setLayoutPositionByMode(index);

        index++;
        if (index > 4) {
            index = 0;
        }
        Toast.makeText(this, "开始初始化" + index, Toast.LENGTH_SHORT).show();
    }

    int startIndex = 0;
    Disposable mDisposable;

    public void loadData(View view) {
        Log.i(TAG, "loadData_path " + path);
        sourceData = null;
        try {
            sourceData = getBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startIndex = 0;
//总字节数
        int totalSize = sourceData.length;

        //轮询读取数据
        mDisposable = Observable.interval(0, 40, TimeUnit.MILLISECONDS)
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

                        Log.i(TAG, "读取到数据:" + tempData.length + ",data[4]:" + (tempData[4] & 0x1f) + ",neextFrameIndex:" + nextFrameStart);

//                        StreamFile.writeBytes(tempData);

                        //进行处理
                        if (mAbleSoftManager.getLeftVideoDeCode() != null) {
                            mAbleSoftManager.getLeftVideoDeCode().deCodeVideo(tempData, tempData.length);
                        }
//
                        if (mAbleSoftManager.getRightVideoDeCode() != null) {
                            mAbleSoftManager.getRightVideoDeCode().deCodeVideo(tempData, tempData.length);
                        }

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