package com.loganfreeman.gallery;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * Created by shanhong on 5/24/17.
 */

public class ImageSearchClient {
    public static final String URL_PREFIX = "https://www.google.com/search?q=";
    public static final String RESOLUTION = "%20high%20resolution";
    public static String contructUrl(String keyword) {

        String query = null;
        try {
            query = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }

        return URL_PREFIX + query + "&tbm=isch&source=lnms&sa=X&ved=0ahUKEwipm_WnuonUAhVlsFQKHYpxB4wQ_AUIBigB&biw=1920&bih=960#q=mushroom&tbm=isch&tbs=isz:l";
    }
    public static List<ImageModel> search(String keyword) {
        List<ImageModel> images = new ArrayList<ImageModel>();
        Gson gson = new Gson();
        try {
            String url = contructUrl(keyword);
            Log.i("Url: ", url);
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select(".rg_di > .rg_meta");
            for(Element metaElement: elements) {
                String metaData = metaElement.html();
                JsonObject json = gson.fromJson(metaData, JsonObject.class);
                ImageModel imageModel = fromJsonObject(json);
                images.add(imageModel);
            }
        } catch (IOException e) {
            Log.e("IO: ", e.getLocalizedMessage());
        }
        return images;
    }


    public static Observable<List<ImageModel>> searchAsync(final String keyword) {
        return  Observable.fromCallable(new Callable<List<ImageModel>>() {
            @Override
            public List<ImageModel> call() throws Exception {
                return ImageSearchClient.search(keyword);
            }
        });
    }

    public static ImageModel fromJsonObject(JsonObject json) {
        return new ImageModel(json.get("ou").getAsString(), json.get("oh").toString(), json.get("ow").toString());
    }


}

