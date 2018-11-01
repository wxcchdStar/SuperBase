package co.tton.android.lib.banner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class BannerIndicator extends View {

    private int mCount;
    private int mCurrentPosition;
    private float mSpacing;
    private float mRadius = 20;

    private Paint mPaintInactive;
    private Paint mPaintActive;

    private float mLeftPadding = -1;
    private float mRightPadding = -1;

    public BannerIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public BannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BannerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mCount = 3;
        mSpacing = 8.0f;

        int activeColor = 0x8CFFF4B5;
        int inactiveColor = 0x4CFFF5BD;
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.BannerIndicator);
        try {
            mSpacing = a.getDimension(R.styleable.BannerIndicator_spacing, mSpacing);
            mRadius = a.getDimension(R.styleable.BannerIndicator_radius, mRadius);
            activeColor = a.getColor(R.styleable.BannerIndicator_activeColor, activeColor);
            inactiveColor = a.getColor(R.styleable.BannerIndicator_inactiveColor, inactiveColor);
        } finally {
            a.recycle();
        }

        mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInactive.setStyle(Style.FILL);
        mPaintInactive.setColor(inactiveColor);

        mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintActive.setStyle(Style.FILL);
        mPaintActive.setColor(activeColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (getPaddingLeft() + getPaddingRight()
                    + (mCount * 2 * mRadius) + (mCount - 1) * mSpacing);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mLeftPadding < 0) {
            mLeftPadding = getPaddingLeft();
        }
        if (mRightPadding < 0) {
            mRightPadding = getPaddingTop();
        }

        // draw inactive indicator
        float cy = mRightPadding + mRadius;
        for (int i = 0; i < mCount; i++) {
            float cx = getCx(i);
            canvas.drawCircle(cx, cy, mRadius, mPaintInactive);
        }

        // draw active indicator
        float cx = getCx(mCurrentPosition);
        canvas.drawCircle(cx, cy, mRadius, mPaintActive);
    }

    private float getCx(int position) {
        return mLeftPadding + (2 * position + 1) * mRadius + mSpacing * position;
    }

    public void setSelection(int position) {
        mCurrentPosition = position;
        invalidate();
    }

    public void setCount(int count) {
        mCount = count;
        requestLayout();
        invalidate();
    }
}
