package com.loganfreeman.gallery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loganfreeman.gallery.ImageModel;
import com.loganfreeman.gallery.PickConfig;
import com.loganfreeman.gallery.R;
import com.loganfreeman.gallery.utils.PhotoItem;
import com.loganfreeman.gallery.utils.PickUtils;
import com.loganfreeman.gallery.utils.ShareUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanhong on 5/26/17.
 */

public class PhotoItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<PhotoItem> data = new ArrayList<>();

    private View.OnClickListener imgClick;

    private List<String> selectPath = new ArrayList<>();

    private int scaleSize;

    public PhotoItemAdapter(Context context, List<PhotoItem> data, View.OnClickListener imgClick) {
        this.context = context;
        this.data = data;
        this.imgClick = imgClick;
        buildScaleSize();
    }

    private void buildScaleSize() {
        int screenWidth = PickUtils.getInstance(context).getWidthPixels();
        int space = PickUtils.getInstance(context).dp2px(PickConfig.ITEM_SPACE);

        scaleSize = (screenWidth - (PickConfig.DEFAULT_SPAN_COUNT + 1) * space) / PickConfig.DEFAULT_SPAN_COUNT;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);
        viewHolder = new GridImageViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        GridImageViewHolder gridImageViewHolder = (GridImageViewHolder) holder;
        gridImageViewHolder.bindItem(data.get(position));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<String> getSelectPath() {
        return selectPath;
    }

    public List<PhotoItem> getData() {
        return data;
    }

    public void setSelectPath(List<String> selectPath) {
        this.selectPath = selectPath;
    }

    public void setData(List<PhotoItem> data) {
        this.data = data;
    }

    private class GridImageViewHolder extends RecyclerView.ViewHolder {

        ImageView gridImage, selectImage;

        private FrameLayout selectLayout;

        private TextView textItemdesc;

        public GridImageViewHolder(View itemView) {
            super(itemView);

            textItemdesc = (TextView) itemView.findViewById(R.id.textItemdesc);

            gridImage = (ImageView) itemView.findViewById(R.id.item_img);

            selectLayout = (FrameLayout) itemView.findViewById(R.id.frame_select_layout);

            selectImage = (ImageView) itemView.findViewById(R.id.iv_select);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) gridImage.getLayoutParams();
            params.width = scaleSize;
            params.height = scaleSize;

        }

        public void bindItem(final PhotoItem imageModel) {

            textItemdesc.setText(imageModel.getDimension());

            gridImage.setImageURI(imageModel.getUri());

            gridImage.setTag(R.id.pick_image_path, imageModel.getPath());

            gridImage.setOnClickListener(imgClick);

            if (selectPath.contains(imageModel.getPath())) {
                select();
            } else {
                unSelect();
            }

            selectLayout.setTag(R.id.pick_image_path, imageModel.getPath());

            selectLayout.setOnClickListener(moreClick);



        }

        void addPath(String path) {
            selectPath.add(path);
        }

        void removePath(String path) {
            selectPath.remove(path);
        }

        void select() {
            selectImage.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.pick_ic_select));
            selectImage.setTag(R.id.pick_is_select, true);
        }

        void unSelect() {
            selectImage.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.pick_ic_un_select));
            selectImage.setTag(R.id.pick_is_select, false);
        }

        View.OnClickListener moreClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = (String) v.getTag(R.id.pick_image_path);
                boolean isSelect = (boolean) selectImage.getTag(R.id.pick_is_select);
                if (isSelect) {
                    if (selectPath.contains(path)) {
                        unSelect();
                        removePath(path);
                    }
                } else {
                    if (!selectPath.contains(path)) {
                        select();
                        addPath(path);
                    }
                }
            }
        };
    }
}
