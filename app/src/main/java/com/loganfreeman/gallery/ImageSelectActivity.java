package com.loganfreeman.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loganfreeman.gallery.adapter.GalleryAdapter;
import com.loganfreeman.gallery.adapter.PhotoItemAdapter;
import com.loganfreeman.gallery.utils.ImageUtil;
import com.loganfreeman.gallery.utils.PhotoItem;
import com.loganfreeman.gallery.utils.PickUtils;
import com.loganfreeman.gallery.widget.SpaceItemDecoration;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.loganfreeman.gallery.utils.ImageUtil.IMAGE_FILTER;

/**
 * Created by shanhong on 5/26/17.
 */

public class ImageSelectActivity extends AppCompatActivity {

    private RequestManager manager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.image_list_view)
    RecyclerView photoList;

    @Bind(R.id.select_all)
    CheckBox selectAllCheckBox;

    @Bind(R.id.delete_all)
    Button deleteAllBtn;

    PhotoItemAdapter adapter;

    private GridLayoutManager lLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.image_select_title));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

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
        photoList.setItemAnimator(new DefaultItemAnimator());

        photoList.addItemDecoration(new SpaceItemDecoration(PickUtils.getInstance(ImageSelectActivity.this).dp2px(PickConfig.ITEM_SPACE), PickConfig.DEFAULT_SPAN_COUNT));

        lLayout = new GridLayoutManager(this, PickConfig.DEFAULT_SPAN_COUNT);

        photoList.setLayoutManager(lLayout);

        setupAdapter();

    }

    List<PhotoItem> listFiles() {
        List<PhotoItem> photos = new ArrayList<PhotoItem>();
        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // Locate the image folder in your SD Card
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Download");

            if (file.isDirectory()) {
                File[] listFile = file.listFiles(IMAGE_FILTER);


                for (int i = 0; i < listFile.length; i++) {
                    PhotoItem item = ImageUtil.getDimension(listFile[i].getAbsolutePath());
                    photos.add(item);
                }
            }

        }

        return photos;
    }

    void setupAdapter() {
        adapter = new PhotoItemAdapter(this, listFiles(), imageClick);
        photoList.setAdapter(adapter);
    }

    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String imgPath = (String) v.getTag(R.id.pick_image_path);
            Intent intent = new Intent();
            intent.setClass(ImageSelectActivity.this, PickPhotoPreviewActivity.class);
            intent.putExtra(PickConfig.INTENT_IMG_PATH, imgPath);
            intent.putParcelableArrayListExtra(PickConfig.INTENT_IMG_LIST, (ArrayList<? extends Parcelable>) adapter.getData());
            intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, (Serializable) adapter.getSelectPath());
            startActivityForResult(intent,PickConfig.PREVIEW_PHOTO_DATA);
        }
    };


    @OnClick(R.id.delete_all)
    void deleteAll(View v) {

    }

    @OnClick(R.id.select_all)
    void selectAll(View v) {

    }
}
