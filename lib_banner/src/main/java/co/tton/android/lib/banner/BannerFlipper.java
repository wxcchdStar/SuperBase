package co.tton.android.lib.banner;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

public class BannerFlipper extends ViewFlipper {
    private GestureDetector mDetector;

    public BannerFlipper(Context context) {
        super(context);
    }

    public BannerFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // 设置pager不滑动
                ViewParent parent = getParent();
                while (parent != null) {
                    if (parent instanceof ListView
                            || parent instanceof ScrollView
                            || parent instanceof RecyclerView
                            || parent instanceof ViewPager
                            || parent instanceof SwipeRefreshLayout) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    parent = parent.getParent();
                }
                break;
            default:
                break;
        }
        if (mDetector != null) {
            mDetector.onTouchEvent(ev);
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void setDetector(GestureDetector detector) {
        mDetector = detector;
    }
}
