package com.grafixartist.gallery;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class ImageModel implements Parcelable {

    String url;

    private String height;
    private String width;

    public ImageModel(String url, String height, String width) {
        this.url = url;
        this.height = height;
        this.width = width;
    }

    protected ImageModel(Parcel in) {
        url = in.readString();
        height = in.readString();
        width = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(height);
        dest.writeString(width);
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public String getDimension() {
        return getWidth() + " x " + getHeight();
    }
}
