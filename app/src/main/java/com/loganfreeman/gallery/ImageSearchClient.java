package com.loganfreeman.gallery;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loganfreeman.gallery.base.BaseApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * Created by shanhong on 5/24/17.
 */

public class ImageSearchClient {
    public static final String URL_PREFIX = "https://www.google.com/search?q=";

    public static Map<String, String> params = new HashMap<String, String>();

    static {
        params.put("tbm", "isch");
    }
    public static String contructUrl(String keyword) {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getAppContext());

        String size = SP.getString("sizePref", null);

        String type = SP.getString("typePref", null);

        Map<String, String> searchParams = new HashMap<String, String>();

        searchParams.put("isz", size);

        searchParams.put("itp", type);

        String tbs = generateSearchParam(searchParams);

        String query = null;
        try {
            query = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }

        return URL_PREFIX + query + "&tbm=isch&source=lnms&sa=X" + tbs;
    }
    public static String queryBuilder(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for(HashMap.Entry<String, String> e : params.entrySet()){
            if(sb.length() > 0){
                sb.append('&');
            }
            try {
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e1) {

            }
        }
        return sb.toString();
    }

    public static boolean isNotNullAndEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static String generateSearchParam(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for(HashMap.Entry<String, String> e : params.entrySet()){
            if(sb.length() > 0){
                sb.append(',');
            }
            if(isNotNullAndEmpty(e.getValue())) {
                sb.append(e.getKey()).append(':').append(e.getValue());
            }

        }

        if(sb.length() > 0) {
            return "&tbs=" + sb.toString();
        }else {
            return "";
        }

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

