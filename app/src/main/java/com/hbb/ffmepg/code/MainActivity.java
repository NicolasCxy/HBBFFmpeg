package com.hbb.ffmepg.code;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb.ffmepg.code.opengl.AbleGLSurfaceView;
import com.hbb.ffmepg.code.utils.StreamFile;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * 1、System.loadLibrary 加载
     * 2、判断ClassLoader是否为空
     *  2.1、不为空 -> DexClassLoader初始化时会加载系统SO和当前程序SO到集合中 -> 对so名进行拼接 -> 遍历集合去做对比。
     *  2.1、为空 -> 拼接路径（ "绝对路径" + fileName + ".so"） -> 去系统SO目录寻找，找到就就加载，找不到就报错
     * 3、找到之后调用nativeLoad加载so，扫描函数，生成函数表 ，key是函数名，value是指令地址。
     */

    String path = Environment.getExternalStorageDirectory().getPath() + "/1026jing.h264";
    byte[] sourceData = null;
    private SoftwareVideoDeCode videoDeCode;
    private AbleGLSurfaceView mAbleGlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
    }

    private void initView() {
        mAbleGlView = (AbleGLSurfaceView) findViewById(R.id.able_glView);
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


    public void initFFmpeg(View view) {
        if (videoDeCode == null) {
            videoDeCode = new SoftwareVideoDeCode("testDecode");
            videoDeCode.setAbleGlSurface(mAbleGlView);
        }

        Toast.makeText(this, "开始初始化", Toast.LENGTH_SHORT).show();
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
                        if (videoDeCode != null)
                            videoDeCode.deCodeVideo(tempData, tempData.length);
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