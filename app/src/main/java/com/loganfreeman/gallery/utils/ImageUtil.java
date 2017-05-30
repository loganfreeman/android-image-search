package com.loganfreeman.gallery.utils;

import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Created by shanhong on 5/26/17.
 */

public class ImageUtil {

    // array of supported extensions (use a List if you prefer)
    public static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp" // and other formats you need
    };

    public static final String[] VIDEO_EXTENSIONS = new String[] {
            "mp4"
    };
    public static final FilenameFilter VIDEO_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : VIDEO_EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    // filter to identify images based on their extensions
    public static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    public static PhotoItem getDimension(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        //If you want, the MIME type will also be decoded (if possible)
        String type = options.outMimeType;


        return new PhotoItem(width, height, path, type);
    }

}
