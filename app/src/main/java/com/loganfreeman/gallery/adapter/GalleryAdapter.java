package com.loganfreeman.gallery.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loganfreeman.gallery.ImageListActivity;
import com.loganfreeman.gallery.ImageModel;
import com.loganfreeman.gallery.PickConfig;
import com.loganfreeman.gallery.R;
import com.loganfreeman.gallery.utils.PickUtils;
import com.loganfreeman.gallery.utils.ShareUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ImageListActivity context;
    List<ImageModel> data = new ArrayList<>();

    private List<String> selectPath;

    private int scaleSize;

    private RequestManager manager;

    private View.OnClickListener imgClick;

    public GalleryAdapter(Context context, RequestManager manager, List<ImageModel> data,  View.OnClickListener imgClick) {
        this.context = (ImageListActivity) context;
        this.data = data;
        this.manager = manager;
        selectPath = new ArrayList<>();

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

        if (position == data.size() - 1) {
            context.loadmore();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<String> getSelectPath() {
        return selectPath;
    }

    public void setSelectPath(List<String> selectPath){
        this.selectPath = selectPath;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        GridImageViewHolder gridImageViewHolder = (GridImageViewHolder) holder;
        Glide.clear(gridImageViewHolder.gridImage);
        super.onViewRecycled(holder);
    }

    private class GridImageViewHolder extends RecyclerView.ViewHolder {
        ImageView gridImage, selectImage, weekImage;

        private FrameLayout selectLayout;

        private int maxSelectSize = PickConfig.MAX_SELECT_SIZE;

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

            final WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(gridImage);
            weekImage = imageViewWeakReference.get();
        }

        public void bindItem(final ImageModel imageModel) {

            textItemdesc.setText(imageModel.getDimension());

            final String url = imageModel.getUrl();
            if (selectPath.contains(url)) {
                select();
            } else {
                unSelect();
            }
            if (weekImage != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        manager
                                .load(url)
                                .thumbnail(0.5f)
                                .override(200,200)
                                .crossFade()
                                .into(weekImage);
                    }
                });
                selectLayout.setTag(R.id.pick_image_path, url);
                selectLayout.setOnClickListener(moreClick);
                weekImage.setTag(R.id.pick_image_path, url);
                weekImage.setOnClickListener(imgClick);
            }

            ShareUtil.download(context, url);


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
                    if (selectPath.size() < maxSelectSize) {
                        if (!selectPath.contains(path)) {
                            select();
                            addPath(path);
                        }
                    } else {
                        Toast.makeText(context, String.format(context.getString(R.string.pick_photo_size_limit), String.valueOf(maxSelectSize)), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        void addPath(String path) {
            selectPath.add(path);
            context.updateSelectText(String.valueOf(selectPath.size()));
        }

        void removePath(String path) {
            selectPath.remove(path);
            context.updateSelectText(String.valueOf(selectPath.size()));
        }

        void select() {
            selectImage.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.pick_ic_select));
            selectImage.setTag(R.id.pick_is_select, true);
        }

        void unSelect() {
            selectImage.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.pick_ic_un_select));
            selectImage.setTag(R.id.pick_is_select, false);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());


}
