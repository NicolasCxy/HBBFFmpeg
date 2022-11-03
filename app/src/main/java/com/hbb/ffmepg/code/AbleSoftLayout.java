package com.hbb.ffmepg.code;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hbb.ffmpeg.SoftwareVideoDeCode;
import com.hbb.ffmpeg.opengl.AbleGLSurfaceView;

/**
 * 软件编码layout
 */
public class AbleSoftLayout extends RelativeLayout {

    private Context mContext;
    private AbleGLSurfaceView leftGlSurface;
    private AbleGLSurfaceView rightGlSurface;

    public final int SINGLE_MODLE = 0;
    public final int TWO_MODLE = 1;
    public final int TLEFTVRIGHT = 2;
    public final int TRIGHTVLEFT = 3;
    public  final int TTOPVWHOLE = 4;

    private SoftwareVideoDeCode leftVideoDeCode;
    private SoftwareVideoDeCode rightVideoDeCode;

    public AbleSoftLayout(Context context) {
        this(context,null);
    }


    public AbleSoftLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        initBaseLayout();
    }

    /**
     * 初始化基本布局
     */
    private void initBaseLayout() {
        leftGlSurface = new AbleGLSurfaceView(mContext);
        rightGlSurface = new AbleGLSurfaceView(mContext);


        leftVideoDeCode = new SoftwareVideoDeCode("OneAbleDecode1");
        leftVideoDeCode.setAbleGlSurface(leftGlSurface);


        rightVideoDeCode = new SoftwareVideoDeCode("OneAbleDecode1");
        rightVideoDeCode.setAbleGlSurface(rightGlSurface);

        addView(leftGlSurface);
        addView(rightGlSurface);

    }

    /**
     * 根据模式设置布局
     */
    public void setLayoutPositionByMode(int mode){
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


    public SoftwareVideoDeCode getLeftVideoDeCode() {
        return leftVideoDeCode;
    }

    public SoftwareVideoDeCode getRightVideoDeCode() {
        return rightVideoDeCode;
    }


}
