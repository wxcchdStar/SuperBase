package wxc.android.lib.banner2;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;

public abstract class Banner<T> extends FrameLayout implements ViewPager.OnPageChangeListener {
    public static final int DELAY_TIME_DEFAULT = 5000; // ms
    public static final int SCROLL_TIME_DEFAULT = 800; // ms

    private int mDelayTime = DELAY_TIME_DEFAULT;
    private int mScrollTime = SCROLL_TIME_DEFAULT;
    private boolean mIsAutoPlay = true;
    private int mPageMargin = 0;

    private List<T> mImageUrls = new ArrayList<>();
    private int mCount = 0;
    private int mCurrentItem;

    private BannerViewPager mViewPager;
    private BannerPagerAdapter mPagerAdapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnBannerClickListener mOnBannerListener;

    private Handler mHandler = new Handler();

    private Runnable mAutoPlayTask = new Runnable() {
        @Override
        public void run() {
            if (mCount > 1 && mIsAutoPlay) {
                mCurrentItem = mCurrentItem % (mCount + 1) + 1;
                if (mCurrentItem == 1) {
                    mViewPager.setCurrentItem(mCurrentItem, false);
                    mHandler.post(mAutoPlayTask);
                } else {
                    mViewPager.setCurrentItem(mCurrentItem);
                    mHandler.postDelayed(mAutoPlayTask, mDelayTime);
                }
            }
        }
    };

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initViewPager(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        mPageMargin = typedArray.getDimensionPixelSize(R.styleable.Banner_pager_margin, 0);
        mDelayTime = typedArray.getInt(R.styleable.Banner_delay_time, DELAY_TIME_DEFAULT);
        mScrollTime = typedArray.getInt(R.styleable.Banner_scroll_time, SCROLL_TIME_DEFAULT);
        mIsAutoPlay = typedArray.getBoolean(R.styleable.Banner_is_auto_play, true);
        typedArray.recycle();
    }

    private void initViewPager(Context context) {
        mViewPager = new BannerViewPager(context);
        mViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mViewPager.setPageMargin(mPageMargin);
        mPagerAdapter = new BannerPagerAdapter();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mPagerAdapter);
        addView(mViewPager);
        // hook scroller
        try {
            BannerScroller scroller = new BannerScroller(mViewPager.getContext());
            scroller.setDuration(mScrollTime);
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Banner isAutoPlay(boolean isAutoPlay) {
        mIsAutoPlay = isAutoPlay;
        return this;
    }

    public Banner setDelayTime(int delayTime) {
        mDelayTime = delayTime;
        return this;
    }

    public Banner setImages(List<T> imageUrls) {
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        mImageUrls = imageUrls;
        mCount = imageUrls.size();
        return this;
    }

    public void update(List<T> imageUrls) {
        setImages(imageUrls);
        mPagerAdapter.notifyDataSetChanged();
        start();
    }

    public Banner start() {
        setData();
        return this;
    }

    private void setData() {
        mCurrentItem = 1;
        mViewPager.setCurrentItem(1);
        mViewPager.setScrollable(mCount > 1);
        if (mIsAutoPlay) {
            startAutoPlay();
        }
    }

    public void startAutoPlay() {
        mHandler.removeCallbacks(mAutoPlayTask);
        mHandler.postDelayed(mAutoPlayTask, mDelayTime);
    }

    public void stopAutoPlay() {
        mHandler.removeCallbacks(mAutoPlayTask);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mIsAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoPlay();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
     * 返回真实的位置
     *
     * @param position position
     * @return 下标从0开始
     */
    private int toRealPosition(int position) {
        int realPosition = (position - 1) % mCount;
        if (realPosition < 0) {
            realPosition += mCount;
        }
        return realPosition;
    }

    public abstract View createBannerView(Context context, T url);

    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // 多余1个才需要滚动
            return mCount > 1 ? mCount + 2 : mCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            // 第0位给了最后一个Banner，从第1位开始
            T url;
            if (position == 0) {
                url = mImageUrls.get(mCount - 1);
            } else if (position == mCount + 1) {
                url = mImageUrls.get(0);
            } else {
                url = mImageUrls.get(position - 1);
            }

            View imageView = createBannerView(getContext(), url);
            container.addView(imageView);

            if (mOnBannerListener != null) {
                imageView.setOnClickListener(v -> mOnBannerListener.onBannerClick(toRealPosition(position)));
            }
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == SCROLL_STATE_DRAGGING) {
            if (mCurrentItem == 0) {
                mViewPager.setCurrentItem(mCount, false);
            } else if (mCurrentItem == mCount + 1) {
                mViewPager.setCurrentItem(1, false);
            }
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(toRealPosition(position));
        }
    }

    public Banner setOnBannerClickListener(OnBannerClickListener listener) {
        mOnBannerListener = listener;
        return this;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public interface OnBannerClickListener {
        void onBannerClick(int position);
    }
}
