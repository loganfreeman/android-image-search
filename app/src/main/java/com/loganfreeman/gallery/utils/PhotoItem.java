package com.loganfreeman.gallery.utils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.loganfreeman.gallery.ImageModel;

import java.io.File;

/**
 * Created by shanhong on 5/26/17.
 */

public class PhotoItem implements Parcelable {



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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(height);
        dest.writeInt(width);
    }

    protected PhotoItem(Parcel in) {
        path = in.readString();
        height = in.readInt();
        width = in.readInt();
        uri = Uri.fromFile(new File(path));
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

}