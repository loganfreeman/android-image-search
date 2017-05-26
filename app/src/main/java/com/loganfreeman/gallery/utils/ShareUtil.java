package com.loganfreeman.gallery.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.loganfreeman.gallery.PickConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by scheng on 5/25/2017.
 */

public class ShareUtil {

    public static Map<String, Uri> cache = new HashMap<String, Uri>();



    public static void download(Context context, final String url) {
        Glide
                .with(context)
                .load(url)
                .asBitmap()
                .toBytes(Bitmap.CompressFormat.JPEG, PickConfig.QUALITY)
                .into(new SimpleTarget<byte[]>() {
                    @Override public void onResourceReady(final byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override protected Void doInBackground(Void... params) {
                                Uri uri = ShareUtil.getLocalBitmapUri(resource);
                                cache.put(url, uri);
                                return null;
                            }
                        }.execute();
                    }
                })
        ;

    }

    public static Uri getLocalBitmapUri(Bitmap bmp) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static Uri getLocalBitmapUri(byte[] resource) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            BufferedOutputStream s = new BufferedOutputStream(new FileOutputStream(file));
            s.write(resource);
            s.flush();
            s.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static boolean isInstallWeChart(Context context){

        PackageInfo packageInfo = null;

        try {

            packageInfo = context.getPackageManager().getPackageInfo("com.tencent.mm", 0);

        } catch (Exception e) {

            packageInfo = null;

            e.printStackTrace();

        }

        if (packageInfo == null) {

            return false;

        } else {

            return true;

        }

    }
}
