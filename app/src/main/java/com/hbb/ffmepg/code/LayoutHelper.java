package com.hbb.ffmepg.code;

import android.util.Log;
import android.view.View;

import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;


public class LayoutHelper {
    private static final String TAG = "LayoutHelper";

    /**
     * 调整布局位置
     * @param position 位置
     */
    public static void setLayoutPosition(Position position, View view){
        int width  = ScreenUtils.getScreenWidth();
        int height = ScreenUtils.getScreenHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        Log.i(TAG, "setLayoutPosition: width:" + width + ",height:" + height);
        if (position.getX() != 0) {
            double flageX = position.getX() / 100;
            Log.e(TAG, "setStreamLayout_flageX: " + flageX);
            float currentX = (float) (width * flageX);
            Log.e(TAG, "setStreamLayout_currentX: " + currentX);
            layoutParams.leftMargin = (int) currentX;
        } else {
            layoutParams.leftMargin = 0;
        }
        if (position.getY() != 0) {
            float flageY = position.getY() / 100;
            Log.e(TAG, "setStreamLayout_flageY: " + flageY);
            float currentY = (float) (height * flageY);
            Log.e(TAG, "setStreamLayout_currentY: " + currentY);
            layoutParams.topMargin = (int) currentY;
        } else {
            layoutParams.topMargin = 0;
        }

        if (position.getW() != 0) {
            if (position.getW() == 1) {
                layoutParams.width = 1;
            } else {
                double flageW = position.getW() / 100;
                Log.e(TAG, "setStreamLayout_flageW: " + flageW);
                float currentW = (float) (width * flageW);
                Log.e(TAG, "setStreamLayout_currentW: " + (int) currentW);
                layoutParams.width = (int) currentW;
            }
        }

        if (position.getH() != 0) {
            if (position.getH() == 1) {
                layoutParams.width = 1;
            } else {
                double flageH = position.getH() / 100;
                Log.e(TAG, "setStreamLayout_flageH: " + flageH);
                float currentH = (float) (height * flageH);
                Log.e(TAG, "setStreamLayout_currentH: " + currentH);
                layoutParams.height = (int) currentH;
            }
        }
        view.setLayoutParams(layoutParams);

    }

}
