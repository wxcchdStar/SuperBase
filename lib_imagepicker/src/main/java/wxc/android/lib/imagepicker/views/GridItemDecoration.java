package wxc.android.lib.imagepicker.views;

import android.content.Context;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import wxc.android.base.utils.ValueUtils;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public GridItemDecoration(Context context) {
        mSpace = ValueUtils.dpToPx(context, 1);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
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
