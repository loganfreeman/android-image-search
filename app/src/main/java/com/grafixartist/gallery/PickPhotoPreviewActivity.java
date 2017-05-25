package com.grafixartist.gallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.grafixartist.gallery.widget.MyToolbar;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.grafixartist.gallery.PickConfig.DEFAULT_PICK_SIZE;

/**
 * Created by scheng on 5/24/17.
 */

public class PickPhotoPreviewActivity extends AppCompatActivity {

    private List<ImageModel> allImagePath;
    private List<String> selectImagePath;
    private String path;
    private ViewPager viewPager;
    private List<ImageView> imageViews;
    private MyToolbar myToolbar;
    private boolean mIsHidden,misSelect;
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_activty_preview_photo);
        path = getIntent().getStringExtra(PickConfig.INTENT_IMG_PATH);
        allImagePath =  getIntent().getExtras().getParcelableArrayList(PickConfig.INTENT_IMG_LIST);
        selectImagePath = (List<String>) getIntent().getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
        imageViews = new ArrayList<>();
        if(selectImagePath == null){
            selectImagePath = new ArrayList<>();
        }
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(this);
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
            if(path.endsWith(allImagePath.get(i).getUrl())) {
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
        judgeSelect(allImagePath.get(indexOf).getUrl());
        viewPager.setAdapter(new listPageAdapter());
        viewPager.setCurrentItem(indexOf);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String path = allImagePath.get(position).getUrl();
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
            final ImageView pic = imageViews.get(i);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(pic,params);
            String path = allImagePath.get(position).getUrl();
            setImage(pic, path);
            return pic;
        }

        private void setImage(ImageView imageView, String url) {
            Glide.with(PickPhotoPreviewActivity.this).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int i = position % 4;
            final ImageView imageView = imageViews.get(i);
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
                        Toast.makeText(PickPhotoPreviewActivity.this, String.format(v.getContext().getString(R.string.pick_photo_size_limit), String.valueOf(DEFAULT_PICK_SIZE)), Toast.LENGTH_SHORT).show();
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
        intent.setClass(PickPhotoPreviewActivity.this, ImageListActivity.class);
        intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, (Serializable) selectImagePath);
        setResult(PickConfig.PREVIEW_PHOTO_DATA,intent);
        finish();
    }


}