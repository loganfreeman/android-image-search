package com.grafixartist.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanhong on 5/24/17.
 */

public class ImageListActivity extends AppCompatActivity {

    private RecyclerView imageListView;

    private static final String MODELS = "IMAGE_MODELS";

    private List<ImageModel> imageModels;

    private GalleryAdapter adapter;

    private GridLayoutManager lLayout;


    public static void start(Context context, List<ImageModel> imageModels) {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putParcelableArrayListExtra(MODELS, (ArrayList<? extends Parcelable>) imageModels);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageListView = (RecyclerView) findViewById(R.id.image_list_view);

        imageModels = getIntent().getExtras().getParcelableArrayList(MODELS);

        GalleryAdapter adapter = new GalleryAdapter(this, imageModels);

        lLayout = new GridLayoutManager(this, 3);

        imageListView.setLayoutManager(lLayout);

        imageListView.setAdapter(adapter);


    }
}
