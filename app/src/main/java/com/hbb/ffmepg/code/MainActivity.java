package com.hbb.ffmepg.code;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hbb.ffmepg.code.record.LocalFileVideoRecord;
import com.hbb.ffmepg.code.record.RemoteRtspVideoRecord;
import com.hbb.ffmepg.code.record.VideoRecordCallBack;
import com.hbb.ffmpeg.utils.StreamFile;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements VideoRecordCallBack {
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
     * 4、进行扩充：尝试对整个堆区进行扩容，但是有上限
     * 5、回收软引用：回收软引用对象
     * 6、如果空间还是不够就触发OOM
     * <p>
     * 三、内存分析工具
     * 1、ADB：宏观分析内存情况
     * 2、MemoryProfiler ：以图表的形式展示内存情况，以及回收相关
     * 3、leakCanary: 傻瓜式统计Activity和Fragment 对象
     * 4、MAT：比较全面分析内存泄漏内存溢出情况，页面比较复杂
     */

//    String localPath = Environment.getExternalStorageDirectory().getPath() + "/1026jing.h264";
    String localPath = Environment.getExternalStorageDirectory().getPath() + "/x26422_1920_1080.h264";
//    String localPath = Environment.getExternalStorageDirectory().getPath() + "/1080pNoB.h264";
//       String localPath = Environment.getExternalStorageDirectory().getPath() + "/0323data.h264";
    String remotePath = "rtsp://admin:able1234@192.168.50.111/h264/ch1/main/av_stream";

//    String remotePath = "rtsp://192.168.7.150/chn2";


    private AbleSoftLayout mAbleSoftManager;
    private Disposable deCodeDisposable;
    private boolean isRemote = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity->onCreate!!");
        Bundle bundle = new Bundle();
        bundle.putString("value", "测试时测试");
        super.onCreate(bundle);
        hideStatusBar(this);
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
        Toast.makeText(this, "开始初始化!!@ -> index: " + index, Toast.LENGTH_SHORT).show();

        view.setVisibility(View.GONE);
    }

    int count = 0;
    private boolean isStart = false;
    private boolean isReadIFrame = false;

    public void loadData(View view) {
        isStart = true;
        if(!isRemote){
            LocalFileVideoRecord localFileVideo = new LocalFileVideoRecord();
            localFileVideo.start(localPath);
            localFileVideo.setVideoCallBack(this);
        }else{
            RemoteRtspVideoRecord remoteRtspVideo = new RemoteRtspVideoRecord();
            remoteRtspVideo.start(remotePath);
            remoteRtspVideo.setVideoCallBack(this);
        }


        deCodeDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        Log.i(TAG, "getRstpStreamCount: " + count);
                        count = 0;
                    }
                });

        view.setVisibility(View.GONE);

    }


    @Override
    public void videoData(byte[] data, int length, int videoMode) {
        count++;

//        StreamFile.writeBytes(data);

        if (isStart) {
            int flg = data[4] & 0x1f;
            Log.i(TAG, "videoDataOffset: " + flg + "\tdataLength: " + data.length);

            if (!isReadIFrame) {
                if (flg == 7) {
                    isReadIFrame = true;
                    if (mAbleSoftManager.getLeftVideoDeCode() != null) {
                        mAbleSoftManager.getLeftVideoDeCode().deCodeVideo(data, length);
                    }
                } else {
                    Log.e(TAG, "videoData -> 丢帧！！");
                }
            } else {
                if (mAbleSoftManager.getLeftVideoDeCode() != null) {
                    mAbleSoftManager.getLeftVideoDeCode().deCodeVideo(data, length);
                }
            }
        }


//
//        if (mAbleSoftManager.getRightVideoDeCode() != null) {
//            mAbleSoftManager.getRightVideoDeCode().deCodeVideo(data, length);
//        }

    }


    public static void hideStatusBar(Activity activity) {
        if (activity == null) return;
        Window window = activity.getWindow();
        if (window == null) return;
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(lp);
    }
}