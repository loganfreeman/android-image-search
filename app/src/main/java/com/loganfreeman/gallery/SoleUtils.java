package com.loganfreeman.gallery;

import android.text.TextUtils;

/**
 * Created by scheng on 5/24/17.
 */

public class SoleUtils {

    public static boolean isEmpty(String src) {
        if (TextUtils.isEmpty(src)) {
            return true;
        } else {
            return false;
        }
    }
}
