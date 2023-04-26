package com.hbb.ffmepg.code.decode;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hbb.ffmepg.code.LayoutHelper;
import com.hbb.ffmepg.code.Position;
import com.hbb.ffmpeg.SoftwareVideoDeCode;
import com.hbb.ffmpeg.decode.AbleTextureView;
import com.hbb.ffmpeg.decode.HardwareVideoDecode;
import com.hbb.ffmpeg.decode.IVideoDecode;
import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 软硬解码混合使用
 */
public class AbleSoftLayout extends RelativeLayout {

    private Context mContext;
    private AbleGLSurfaceView leftGlSurface;
    private AbleTextureView rightGlSurface;
//    private AbleGLSurfaceView rightGlSurface;

    public final int SINGLE_MODLE = 0;
    public final int TWO_MODLE = 1;
    public final int TLEFTVRIGHT = 2;
    public final int TRIGHTVLEFT = 3;
    public final int TTOPVWHOLE = 4;

    private IVideoDecode leftVideoDeCode,rightVideoDeCode;
//    private SoftwareVideoDeCode rightVideoDeCode;
    private HashMap<String, IVideoDecode> deCodeHashMap = new HashMap<>();


    public AbleSoftLayout(Context context) {
        this(context, null);
    }


    public AbleSoftLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
//
        initBaseLayout();
        setLayoutPositionByMode(TWO_MODLE);
    }

    /**
     * 初始化基本布局
     */
    private void initBaseLayout() {
        //软解OpenGL控件
        leftGlSurface = new AbleGLSurfaceView(mContext);
        //硬解码渲染控件
        rightGlSurface = new AbleTextureView(mContext);

        leftVideoDeCode = new SoftwareVideoDeCode("leftVideoDeCode");
        leftVideoDeCode.setAbleGlSurface(leftGlSurface);

        rightVideoDeCode = new HardwareVideoDecode("rightVideoDeCode");
        rightGlSurface.setEncode(rightVideoDeCode);


        deCodeHashMap.put("leftVideoDeCode", leftVideoDeCode);
        deCodeHashMap.put("rightVideoDeCode", rightVideoDeCode);

        addView(leftGlSurface);
        addView(rightGlSurface);

    }

    /**
     * 根据模式设置布局
     */
    public void setLayoutPositionByMode(int mode) {
        switch (mode) {
            case SINGLE_MODLE:
                LayoutHelper.setLayoutPosition(new Position(0, 0, 0, 100, 100), leftGlSurface);
                LayoutHelper.setLayoutPosition(new Position(0, 0, 0, 1, 1), rightGlSurface);
                break;
            case TWO_MODLE:
                LayoutHelper.setLayoutPosition(new Position(0, 25, 0, 50, 50), leftGlSurface);
                LayoutHelper.setLayoutPosition(new Position(50, 25, 0, 50, 50), rightGlSurface);
                break;
            case TLEFTVRIGHT:
                LayoutHelper.setLayoutPosition(new Position(-25, (float) 12.5, 0, 75, 75), leftGlSurface);
                LayoutHelper.setLayoutPosition(new Position(25, (float) 12.5, 0, 75, 75), rightGlSurface);
                break;
            case TRIGHTVLEFT:
                LayoutHelper.setLayoutPosition(new Position(50, (float) 12.5, 0, 75, 75), leftGlSurface);
                LayoutHelper.setLayoutPosition(new Position(0, (float) 12.5, 0, 75, 75), rightGlSurface);
                break;
            case TTOPVWHOLE:
                LayoutHelper.setLayoutPosition(new Position(0, 0, 0, 100, 100), leftGlSurface);
                LayoutHelper.setLayoutPosition(new Position(75, 1, 0, 25, 25), rightGlSurface);
                break;
        }
    }


    public void updateDecodeUrl(ArrayList<String> list) {
        //给编码器设置标识，方便使用的时候快速找到
        leftVideoDeCode.setStreamPath(list.get(0) + "");
        if (list.size() > 1)
            rightVideoDeCode.setStreamPath(list.get(1) + "");

        deCodeHashMap.clear();
        deCodeHashMap.put(list.get(0), leftVideoDeCode);
        if (list.size() > 1)
            deCodeHashMap.put(list.get(1), rightVideoDeCode);
    }


    public IVideoDecode getLeftVideoDeCode() {
        return leftVideoDeCode;
    }

    public IVideoDecode getRightVideoDeCode() {
        return rightVideoDeCode;
    }


    public IVideoDecode getDeCodeByStreamUrl(String url) {
        return deCodeHashMap.get(url);
    }


}
