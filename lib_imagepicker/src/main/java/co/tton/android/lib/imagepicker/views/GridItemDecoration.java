package co.tton.android.lib.imagepicker.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import co.tton.android.base.utils.ValueUtils;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public GridItemDecoration(Context context) {
        mSpace = ValueUtils.dpToPx(context, 1);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = layoutManager.getSpanCount();
        int position = parent.getChildLayoutPosition(view);
        outRect.top = mSpace;
        outRect.bottom = mSpace;
        if (position % spanCount == 0) {
            outRect.right = mSpace;
        } else if (position % spanCount == (spanCount - 1)) {
            outRect.left = mSpace;
        } else {
            outRect.left = mSpace;
            outRect.right = mSpace;
        }
    }

}
