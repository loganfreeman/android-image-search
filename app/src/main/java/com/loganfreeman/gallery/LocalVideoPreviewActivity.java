package com.loganfreeman.gallery;

/**
 * Created by scheng on 5/31/2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.loganfreeman.gallery.utils.PhotoItem;
import com.loganfreeman.gallery.widget.MyToolbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.loganfreeman.gallery.PickConfig.DEFAULT_PICK_SIZE;



/**
 * Created by shanhong on 5/26/17.
 */

public class LocalVideoPreviewActivity extends AppCompatActivity {

    private List<PhotoItem> allImagePath;
    private List<String> selectImagePath;
    private String path;
    private ViewPager viewPager;
    private List<VideoView> imageViews;
    private MyToolbar myToolbar;
    private boolean mIsHidden,misSelect;
    private boolean isVideo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_activty_preview_photo);
        path = getIntent().getStringExtra(PickConfig.INTENT_IMG_PATH);
        allImagePath =  getIntent().getExtras().getParcelableArrayList(PickConfig.INTENT_IMG_LIST);
        isVideo = getIntent().getBooleanExtra(PickConfig.IS_VIDEO, false);
        selectImagePath = (List<String>) getIntent().getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
        imageViews = new ArrayList<>();
        if(selectImagePath == null){
            selectImagePath = new ArrayList<>();
        }
        for (int i = 0; i < 4; i++) {
            VideoView imageView = new VideoView(this);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideOrShowToolbar();
                }
            });
            imageViews.add(imageView);
        }

        initView();
        Log.d("image size", allImagePath.size() + "");
    }

    private int getSelectedIndex() {
        int selected = 0;
        for(int i = 0; i < allImagePath.size(); i++) {
            if(path.endsWith(allImagePath.get(i).getPath())) {
                selected = i;
            }
        }
        return  selected;
    }

    private void initView() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(PickConfig.STATUS_BAR_COLOR));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        myToolbar = (MyToolbar) findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(Color.parseColor(PickConfig.TOOLBAR_COLOR));
        myToolbar.setIconColor(Color.parseColor(PickConfig.TOOLBAR_ICON_COLOR));
        myToolbar.setLeftIcon(R.drawable.ic_arrow_back);
        myToolbar.setLeftLayoutOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishForResult();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.image_vp);
        int indexOf = getSelectedIndex();
        judgeSelect(allImagePath.get(indexOf).getPath());
        viewPager.setAdapter(new listPageAdapter());
        viewPager.setCurrentItem(indexOf);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String path = allImagePath.get(position).getPath();
                judgeSelect(path);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //通过ViewPager实现滑动的图片
    private class listPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return allImagePath.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override

        public Object instantiateItem(ViewGroup container, final int position) {
            int i = position % 4;
            final VideoView videoView = imageViews.get(i);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(videoView,params);

            PhotoItem item = allImagePath.get(position);
            videoView.setVideoURI(item.getUri());

            videoView.start();
            return videoView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int i = position % 4;
            final VideoView imageView = imageViews.get(i);
            container.removeView(imageView);
        }
    }

    @Override
    public void finish() {
        super.finish();
        viewPager = null;
        overridePendingTransition(0, R.anim.pick_finish_slide_out_left);
    }

    //固定 toolbar
    private void hideOrShowToolbar() {
        myToolbar.animate()
                .translationY(mIsHidden ? 0 : -myToolbar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsHidden = !mIsHidden;
    }

    private void judgeSelect(final String path){
        int indexOf = selectImagePath.indexOf(path);
        if(indexOf != -1){
            myToolbar.setRightIconDefault(R.mipmap.pick_ic_select);
            misSelect = true;
        }else {
            myToolbar.setRightIcon(R.mipmap.pick_ic_un_select_black);
            misSelect = false;
        }

        myToolbar.setRightLayoutOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(misSelect){
                    myToolbar.setRightIcon(R.mipmap.pick_ic_un_select_black);
                    selectImagePath.remove(path);
                    misSelect = false;
                }else {
                    if(selectImagePath.size() <DEFAULT_PICK_SIZE) {
                        myToolbar.setRightIconDefault(R.mipmap.pick_ic_select);
                        selectImagePath.add(path);
                        misSelect = true;
                    }else {
                        Toast.makeText(LocalVideoPreviewActivity.this, String.format(v.getContext().getString(R.string.pick_photo_size_limit), String.valueOf(DEFAULT_PICK_SIZE)), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishForResult();
    }

    private void finishForResult() {
        Intent intent = new Intent();
        intent.setClass(LocalVideoPreviewActivity.this, ImageListActivity.class);
        intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, (Serializable) selectImagePath);
        setResult(PickConfig.PREVIEW_PHOTO_DATA,intent);
        finish();
    }



}