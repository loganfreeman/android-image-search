package com.loganfreeman.gallery.utils;

import android.net.Uri;

import java.io.File;

/**
 * Created by shanhong on 5/26/17.
 */

public class PhotoItem {



    private int width;
    private int height;
    private String path, type;
    private Uri uri;

    public PhotoItem(int width, int height, String path, String type) {
        this.width = width;
        this.height = height;
        this.path = path;
        this.uri = Uri.fromFile(new File(path));
        this.type = type;
    }

    public String toString() {
        return  width + "x" + height + " " + path + " " + type;
    }

    public String getDimension() {
        return getWidth() + " x " + getHeight();
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}