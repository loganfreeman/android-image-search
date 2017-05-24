package com.grafixartist.gallery;

import android.provider.DocumentsContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanhong on 5/24/17.
 */

public class ImageSearchClient {
    public static final String URL_PREFIX = "https://www.google.com/search?q=";
    public static final String RESOLUTION = "%20high%20resolution";
    public static String contructUrl(String keyword) {
        keyword = keyword.replace(" ", "%20");

        return URL_PREFIX + keyword + RESOLUTION + "&espv=2&biw=1366&bih=667&site=webhp&source=lnms&tbm=isch&sa=X&ei=XosDVaCXD8TasATItgE&ved=0CAcQ_AUoAg";
    }
    public static List<ImageModel> search(String keyword) {
        List<ImageModel> images = new ArrayList<ImageModel>();
        try {
            Document document = Jsoup.connect(contructUrl(keyword)).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
}

