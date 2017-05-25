package com.grafixartist.gallery.utils;

import android.widget.Toast;

import com.grafixartist.gallery.base.BaseApplication;

/**
 * Created by scheng on 5/24/17.
 */

public class ToastUtil {

    public static void showShort(String msg) {
        Toast.makeText(BaseApplication.getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg) {
        Toast.makeText(BaseApplication.getAppContext(), msg, Toast.LENGTH_LONG).show();
    }
}
