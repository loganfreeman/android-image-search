package com.loganfreeman.gallery;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
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
import static com.loganfreeman.gallery.utils.ImageUtil.VIDEO_FILTER;
import static com.loganfreeman.gallery.utils.ShareUtil.isInstallWeChart;

/**
 * Created by shanhong on 5/30/17.
 */

public class VideoSelectActivity extends AppCompatActivity {

    private RequestManager manager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.image_list_view)
    RecyclerView photoList;

    @Bind(R.id.select_all)
    CheckBox selectAllCheckBox;

    @Bind(R.id.delete_all)
    Button deleteAllBtn;

    @Bind(R.id.share_to_friend)
    Button shareToFriendBtn;

    @Bind(R.id.share_to_moments)
    Button shareToMomentBtn;

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

        initShareButtons();

    }

    private void initShareButtons() {

    }

    private ArrayList<Uri> getSelectedUris() {
        ArrayList<Uri> files = new ArrayList<Uri>();

        for(String path : adapter.getSelectPath() /* List of the files you want to send */) {

            files.add(Uri.fromFile(new File(path)));
        }

        return files;
    }

    @OnClick(R.id.share)
    void shareSelectedPhotos() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getSelectedUris());
        startActivity(intent);
    }

    @OnClick(R.id.share_to_friend)
    void shareToFriend(View e) {
        if(!isInstallWeChart(this)){
            Toast.makeText(this,"您没有安装微信",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        //intent.putExtra("Kdescription", "分享多张图片到朋友圈");

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getSelectedUris());
        startActivity(intent);
    }


    /**
     * 分享多图到朋友圈，多张图片加文字
     *
     */
    @OnClick(R.id.share_to_moments)
    void shareToTimeLine() {
        if(!isInstallWeChart(this)){
            Toast.makeText(this,"您没有安装微信",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        //intent.putExtra("Kdescription", "分享多张图片到朋友圈");

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getSelectedUris());
        startActivity(intent);
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
            }
        }
    }

    private void initRecyclerView() {
        photoList.setItemAnimator(new DefaultItemAnimator());

        photoList.addItemDecoration(new SpaceItemDecoration(PickUtils.getInstance(VideoSelectActivity.this).dp2px(PickConfig.ITEM_SPACE), PickConfig.DEFAULT_SPAN_COUNT));

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
                    + File.separator + "DCIM/Camera");

            if (file.isDirectory()) {
                File[] listFile = file.listFiles(VIDEO_FILTER);


                for (int i = 0; i < listFile.length; i++) {
                    PhotoItem item = ImageUtil.getDimension(listFile[i].getAbsolutePath());
                    photos.add(item);
                }
            }

        }

        return photos;
    }

    void setupAdapter() {
        adapter = new PhotoItemAdapter(this, listFiles(), imageClick, true);
        photoList.setAdapter(adapter);
    }

    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String imgPath = (String) v.getTag(R.id.pick_image_path);
            Intent intent = new Intent();
            intent.setClass(VideoSelectActivity.this, LocalVideoPreviewActivity.class);
            intent.putExtra(PickConfig.INTENT_IMG_PATH, imgPath);
            intent.putParcelableArrayListExtra(PickConfig.INTENT_IMG_LIST, (ArrayList<? extends Parcelable>) adapter.getData());
            intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, (Serializable) adapter.getSelectPath());
            intent.putExtra(PickConfig.IS_VIDEO, true);
            startActivityForResult(intent,PickConfig.PREVIEW_PHOTO_DATA);
        }
    };


    @OnClick(R.id.delete_all)
    void deleteAll(View v) {
        for(String path: adapter.getSelectPath()) {
            File file = new File(path);
            file.delete();
        }
        adapter.setData(listFiles());
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.select_all)
    void selectAll(View v) {
        if (((CheckBox) v).isChecked()) {
            List<String> selectPath = new ArrayList<String>();
            for(PhotoItem item: adapter.getData()) {
                selectPath.add(item.getPath());
            }
            adapter.setSelectPath(selectPath);
            adapter.notifyDataSetChanged();
        }else {
            adapter.setSelectPath(new ArrayList<String>());
            adapter.notifyDataSetChanged();
        }

    }
}
