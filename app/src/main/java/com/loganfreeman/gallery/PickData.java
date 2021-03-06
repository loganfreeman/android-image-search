package com.loganfreeman.gallery;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by scheng on 5/24/17.
 */

public class PickData implements Serializable {

    private int pickPhotoSize;
    private int spanCount;
    private boolean isShowCamera;
    private String toolbarColor;
    private String statusBarColor;
    private String toolbarIconColor;
    private boolean lightStatusBar;



    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        if(spanCount > 0 && spanCount <= PickConfig.DEFAULT_SPAN_COUNT ) {
            this.spanCount = spanCount;
        }else {
            Log.e(PickConfig.TAG,"Untrue count : span count must between 1 and 4");
            this.spanCount = PickConfig.DEFAULT_SPAN_COUNT;
        }
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public int getToolbarColor() {
        if(SoleUtils.isEmpty(toolbarColor)) {
            return Color.parseColor("#191919");
        }
        return Color.parseColor(toolbarColor);
    }

    public void setToolbarColor(String toolbarColor) {
        this.toolbarColor = toolbarColor;
    }

    public int getStatusBarColor() {
        if(SoleUtils.isEmpty(statusBarColor)) {
            return Color.parseColor("#191919");
        }
        return Color.parseColor(statusBarColor);
    }

    public void setStatusBarColor(String statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public int getToolbarIconColor() {
        if(SoleUtils.isEmpty(toolbarIconColor)) {
            return Color.parseColor("#FFFFFF");
        }
        return Color.parseColor(toolbarIconColor);
    }

    public void setToolbarIconColor(String toolbarIconColor) {
        this.toolbarIconColor = toolbarIconColor;
    }

    public boolean isLightStatusBar() {
        return lightStatusBar;
    }

    public void setLightStatusBar(boolean lightStatusBar) {
        this.lightStatusBar = lightStatusBar;
    }
}