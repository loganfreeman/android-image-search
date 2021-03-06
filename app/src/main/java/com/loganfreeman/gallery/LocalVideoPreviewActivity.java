package com.loganfreeman.gallery;

/**
 * Created by scheng on 5/31/2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.loganfreeman.gallery.utils.PhotoItem;
import com.loganfreeman.gallery.widget.MyToolbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;




/**
 * Created by shanhong on 5/26/17.
 */

public class LocalVideoPreviewActivity extends AppCompatActivity {

    private List<PhotoItem> videoItems;
    private List<String> selectImagePath;
    private String path;
    private ViewPager viewPager;
    private List<VideoView> videoViews;
    private MyToolbar myToolbar;
    private boolean mIsHidden,misSelect;
    private boolean isVideo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_activty_preview_photo);
        path = getIntent().getStringExtra(PickConfig.INTENT_IMG_PATH);
        videoItems =  getIntent().getExtras().getParcelableArrayList(PickConfig.INTENT_IMG_LIST);
        isVideo = getIntent().getBooleanExtra(PickConfig.IS_VIDEO, false);
        selectImagePath = (List<String>) getIntent().getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
        videoViews = new ArrayList<>();
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
            videoViews.add(imageView);
        }

        initView();
        Log.d("image size", videoItems.size() + "");
    }

    private int getSelectedIndex() {
        int selected = 0;
        for(int i = 0; i < videoItems.size(); i++) {
            if(path.endsWith(videoItems.get(i).getPath())) {
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
        judgeSelect(videoItems.get(indexOf).getPath());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.setVideoItems(videoItems);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(indexOf);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String path = videoItems.get(position).getPath();
                judgeSelect(path);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<PhotoItem> videoItems;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return VideoViewFragment.newInstance(position, videoItems.get(position));
        }

        @Override
        public int getCount() {
            return videoItems.size();
        }

        public List<PhotoItem> getVideoItems() {
            return videoItems;
        }

        public void setVideoItems(List<PhotoItem> videoItems) {
            this.videoItems = videoItems;
        }
    }

    //通过ViewPager实现滑动的图片
    private class listPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return videoItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override

        public Object instantiateItem(ViewGroup container, final int position) {
            int i = position % 4;
            final VideoView videoView = videoViews.get(i);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(videoView,params);

            PhotoItem item = videoItems.get(position);
            videoView.setVideoURI(item.getUri());

            start(videoView);
            return videoView;
        }

        private void start(VideoView view) {
            for(VideoView videoView : videoViews) {
                videoView.stopPlayback();
            }
            view.start();
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int i = position % 4;
            final VideoView imageView = videoViews.get(i);
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
                    myToolbar.setRightIconDefault(R.mipmap.pick_ic_select);
                    selectImagePath.add(path);
                    misSelect = true;
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