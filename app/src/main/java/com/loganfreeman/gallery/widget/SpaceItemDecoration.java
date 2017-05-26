package com.loganfreeman.gallery.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by scheng on 5/24/2017.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int spanCount;

    public SpaceItemDecoration(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = space;
        outRect.bottom = space;
        if (parent.getChildLayoutPosition(view) < spanCount) {
            outRect.top = space;
        }
        if((parent.getChildLayoutPosition(view) + 1) % spanCount == 0 ){
            outRect.right = space;
        }
    }
}