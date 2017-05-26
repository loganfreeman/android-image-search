package com.loganfreeman.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loganfreeman.gallery.utils.PickUtils;
import com.loganfreeman.gallery.widget.SpaceItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        ButterKnife.bind(this);

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

    }


    @OnClick(R.id.delete_all)
    private void deleteAll(View v) {

    }

    @OnClick(R.id.select_all)
    private void selectAll(View v) {

    }
}
