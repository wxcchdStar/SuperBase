package co.tton.android.base.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import co.tton.android.base.R;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.ToastUtils;

public abstract class BaseMainWithBottomBarActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private static final String ARGS_CURRENT_POSITION = "current_position";

    protected ViewPager mViewPager;
    protected TabLayout mBottomBar;

    private long mExitTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBottomBar(savedInstanceState);
        initViewPager();
    }

    private void initViewPager() {
        mViewPager = V.f(this, getViewPagerId());
        mViewPager.setOffscreenPageLimit(mBottomBar.getTabCount());
        mViewPager.setAdapter(initPagerAdapter());
    }

    private void initBottomBar(Bundle savedInstanceState) {
        mBottomBar = V.f(this, getBottomBarId());
        initBottomItems();
        mBottomBar.addOnTabSelectedListener(this);
        // Activity重建时回复当前选择的页面
        if (savedInstanceState != null) {
            int currentPosition = savedInstanceState.getInt(ARGS_CURRENT_POSITION, 0);
            TabLayout.Tab currentTab = mBottomBar.getTabAt(currentPosition);
            if (currentTab != null) {
                currentTab.select();
            }
        }
    }

    protected abstract int getViewPagerId();

    protected abstract int getBottomBarId();

    protected abstract void initBottomItems();

    protected abstract BaseMainPagerAdapter initPagerAdapter();

    protected boolean isAllowTabSelected(int position) {
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (mViewPager != null) {
            int position = tab.getPosition();
            if (!isAllowTabSelected(position)) {
                int currentPosition = mViewPager.getCurrentItem();
                TabLayout.Tab lastTab = mBottomBar.getTabAt(currentPosition);
                if (lastTab != null) {
                    lastTab.select();
                }
                return;
            }
            mViewPager.setCurrentItem(position, false);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        mBottomBar.removeOnTabSelectedListener(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGS_CURRENT_POSITION, mBottomBar.getSelectedTabPosition());
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTimestamp > 3000) {
            mExitTimestamp = System.currentTimeMillis();
            ToastUtils.showShort(this, R.string.common_exit_app);
        } else {
            super.onBackPressed();
        }
    }

    protected static abstract class BaseMainPagerAdapter extends FragmentPagerAdapter {
        private int mCount;

        public BaseMainPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            mCount = count;
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}