package co.tton.android.lib.imagepreview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImagePreviewPager extends ViewPager {

    private boolean mIsLocked;

    public ImagePreviewPager(Context context) {
        super(context);
        mIsLocked = false;
    }

    public ImagePreviewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsLocked = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mIsLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsLocked) {
            return super.onTouchEvent(event);
        }
        return false;
    }

}