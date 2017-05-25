package com.grafixartist.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.grafixartist.gallery.adapter.GalleryAdapter;
import com.grafixartist.gallery.utils.PickUtils;
import com.grafixartist.gallery.widget.SpaceItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanhong on 5/24/17.
 */

public class ImageListActivity extends AppCompatActivity {

    private RecyclerView photoList;

    private static final String MODELS = "IMAGE_MODELS";

    private List<ImageModel> imageModels;

    private GalleryAdapter adapter;

    private GridLayoutManager lLayout;

    private RequestManager manager;

    private TextView selectImageSize;

    private Button sharePhotosButton;

    public static void start(Context context, List<ImageModel> imageModels) {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putParcelableArrayListExtra(MODELS, (ArrayList<? extends Parcelable>) imageModels);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        manager = Glide.with(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

        initSelectLayout();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initRecyclerView() {
        photoList = (RecyclerView) findViewById(R.id.image_list_view);

        photoList.setItemAnimator(new DefaultItemAnimator());

        photoList.addItemDecoration(new SpaceItemDecoration(PickUtils.getInstance(ImageListActivity.this).dp2px(PickConfig.ITEM_SPACE), PickConfig.DEFAULT_SPAN_COUNT));

        photoList.addOnScrollListener(scrollListener);

        imageModels = getIntent().getExtras().getParcelableArrayList(MODELS);

        adapter = new GalleryAdapter(this, manager, imageModels, imageClick);

        lLayout = new GridLayoutManager(this, PickConfig.DEFAULT_SPAN_COUNT);

        photoList.setLayoutManager(lLayout);

        photoList.setAdapter(adapter);
    }

    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String imgPath = (String) v.getTag(R.id.pick_image_path);
            Intent intent = new Intent();
            intent.setClass(ImageListActivity.this, PickPhotoPreviewActivity.class);
            intent.putExtra(PickConfig.INTENT_IMG_PATH, imgPath);
            intent.putParcelableArrayListExtra(PickConfig.INTENT_IMG_LIST, (ArrayList<? extends Parcelable>) imageModels);
            intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, (Serializable) adapter.getSelectPath());
            startActivityForResult(intent,PickConfig.PREVIEW_PHOTO_DATA);
        }
    };

    private void initSelectLayout() {
        LinearLayout selectLayout = (LinearLayout) findViewById(R.id.select_layout);
        selectLayout.setVisibility(View.VISIBLE);

        sharePhotosButton = (Button) findViewById(R.id.tv_pick_photo);
        selectImageSize = (TextView) findViewById(R.id.tv_preview_photo);
        selectImageSize.setText(String.valueOf("0"));

        sharePhotosButton.setOnClickListener(sharePhotoClick);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (Math.abs(dy) > PickConfig.SCROLL_THRESHOLD) {
                manager.pauseRequests();
            } else {
                manager.resumeRequests();
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                manager.resumeRequests();
            }
        }
    };

    private View.OnClickListener sharePhotoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareSelectedPhotos();
        }
    };

    private void shareSelectedPhotos() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(String path : adapter.getSelectPath() /* List of the files you want to send */) {

            files.add(GalleryAdapter.cache.get(path));
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
    }

    private void select() {

    }

    public void updateSelectText(String selectSize) {
        if (selectSize.equals("0")) {
            selectImageSize.setText(String.valueOf(0));
            sharePhotosButton.setTextColor(getResources().getColor(R.color.pick_gray));
            sharePhotosButton.setEnabled(false);
        } else {
            selectImageSize.setText(String.valueOf(selectSize));
            sharePhotosButton.setTextColor(getResources().getColor(R.color.pick_blue));
            sharePhotosButton.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (requestCode == PickConfig.LIST_PHOTO_DATA) {

        } else if (requestCode == PickConfig.CAMERA_PHOTO_DATA) {

        }else if(requestCode == PickConfig.PREVIEW_PHOTO_DATA){
            if (data != null) {
                List<String> selectPath = (List<String>) data.getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
                adapter.setSelectPath(selectPath);
                adapter.notifyDataSetChanged();
                updateSelectText(String.valueOf(selectPath.size()));
            }
        }
    }
}
