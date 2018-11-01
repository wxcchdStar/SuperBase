package co.tton.android.lib.banner;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import co.tton.android.base.utils.V;
import rx.Subscription;
import rx.functions.Action1;

public abstract class BaseBannerPresenter implements Animation.AnimationListener, View.OnClickListener {
    private static final int FLIP_INTERVAL = 3000; // 3s

    private Context mContext;
    private View mContentView;
    private BannerFlipper mFlipper;
    private BannerIndicator mIndicator;

    // banner轮播动画
    private Animation mSlideInLeftAnim;
    private Animation mSlideOutRightAnim;
    private Animation mSlideInRightAnim;
    private Animation mSlideOutLeftAnim;

    private List<BannerBean> mBanners;

    public void initContentView(View view) {
        mContext = view.getContext();
        initPlayAnimations();
        initViews(view);
        initGesture();
        initBannerHeight();
    }

    // 初始化banner播放动画
    private void initPlayAnimations() {
        mSlideInLeftAnim = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        mSlideOutRightAnim = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
        mSlideInRightAnim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
        mSlideOutLeftAnim = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_left);
    }

    // 初始化banner views
    private void initViews(View view) {
        mContentView = V.f(view, R.id.fl_banner);
        mIndicator = V.f(mContentView, R.id.indicator);
        mFlipper = V.f(mContentView, R.id.vf_banner_player);
        mFlipper.setFlipInterval(FLIP_INTERVAL);
        mFlipper.setInAnimation(mSlideInRightAnim);
        mFlipper.setOutAnimation(mSlideOutLeftAnim);
        mSlideInRightAnim.setAnimationListener(this);
        mSlideInLeftAnim.setAnimationListener(this);
        mFlipper.setAutoStart(true);
    }

    // 初始化滑动banner手势
    private void initGesture() {
        final int pagingTouchSlop = ViewConfiguration.get(mContext).getScaledPagingTouchSlop();
        final GestureDetector detector = new GestureDetector(mContext,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (e1.getX() - e2.getX() > pagingTouchSlop) { // 左滑，上一页
                            mFlipper.setInAnimation(mSlideInRightAnim);
                            mFlipper.setOutAnimation(mSlideOutLeftAnim);
                            mFlipper.stopFlipping();
                            mFlipper.showNext();
                            mFlipper.startFlipping();
                        } else if (e2.getX() - e1.getX() > pagingTouchSlop) { // 右滑，下一页
                            mFlipper.setInAnimation(mSlideInLeftAnim);
                            mFlipper.setOutAnimation(mSlideOutRightAnim);
                            mFlipper.stopFlipping();
                            mFlipper.showPrevious();
                            mFlipper.startFlipping();
                        }
                        return true;
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        if (mFlipper != null) {
                            View currentView = mFlipper.getCurrentView();
                            if (currentView != null) {
                                onClick(currentView);
                            }
                        }
                        return true;
                    }
                });
        mFlipper.setDetector(detector);
    }

    private void initBannerHeight() {
        int[] measure = getBannerSize();
        mFlipper.getLayoutParams().height = measure[1];
        mContentView.getLayoutParams().height = measure[1];
        mContentView.requestLayout();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // do nothing
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // do nothing
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        int childIndex = mFlipper.getDisplayedChild();
        mIndicator.setSelection(childIndex);
        if (animation == mSlideInLeftAnim) {
            mFlipper.setInAnimation(mSlideInRightAnim);
            mFlipper.setOutAnimation(mSlideOutLeftAnim);
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mFlipper.getChildCount(); i++) {
            if (v == mFlipper.getChildAt(i)) {
                if (i >= mBanners.size()) return;
                BannerBean bean = mBanners.get(i);
                onBannerClick(bean);
                return;
            }
        }
    }

    protected abstract Subscription requestBanner(Action1<List<BannerBean>> success, Action1<Throwable> failed);

    protected abstract int[] getBannerSize();

    protected void onBannerClick(BannerBean bean) {
        // do nothing
    }

    protected abstract void setBanner(ViewGroup flipper, List<BannerBean> banners);

    protected Action1<List<BannerBean>> mSuccess = new Action1<List<BannerBean>>() {

        @Override
        public void call(List<BannerBean> banners) {
            mBanners = banners;

            if (banners != null && !banners.isEmpty()) {
                if (banners.size() <= 1) {
                    mIndicator.setVisibility(View.GONE);

                    mFlipper.stopFlipping();
                    mFlipper.setAutoStart(false);
                    mFlipper.setDetector(new GestureDetector(mContext,
                            new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onSingleTapUp(MotionEvent e) {
                                    if (mFlipper != null) {
                                        View currentView = mFlipper.getCurrentView();
                                        if (currentView != null) {
                                            onClick(currentView);
                                        }
                                    }
                                    return true;
                                }
                            }));
                } else {
                    mIndicator.setVisibility(View.VISIBLE);
                }
                mIndicator.setCount(banners.size());
                mIndicator.setSelection(0);
                mFlipper.removeAllViews();
                setBanner(mFlipper, banners);
            } else {
                mContentView.setVisibility(View.GONE);
            }
        }
    };

    protected Action1<Throwable> mFailed = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            mContentView.setVisibility(View.GONE);
            throwable.printStackTrace();
        }
    };

}
