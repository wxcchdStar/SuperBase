package co.tton.android.lib.imagepicker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import co.tton.android.base.utils.ValueUtils;

public class GreyItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private float mDividerWidth;
    private int mDp16;

    public GreyItemDecoration(Context context) {
        this(context, 0.5f);
    }

    public GreyItemDecoration(Context context, float dividerWidth) {
        mDividerWidth = ValueUtils.dpToPx(context, dividerWidth);

        mDp16 = ValueUtils.dpToPx(context, 16);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#c8c8c8"));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final float top = child.getBottom() + layoutParams.bottomMargin;
            final float bottom = top + mDividerWidth;
            c.drawRect(left + mDp16, top, right - mDp16, bottom, mPaint);
        }
    }
}
