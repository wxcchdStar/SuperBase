package wxc.android.base.app.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import wxc.android.base.R;
import wxc.android.base.utils.V;
import wxc.android.base.views.ToastUtils;

public abstract class BaseMainWithBottomBarActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private static final int EXIT_TIPS_INTERVAL = 3000;

    private static final String ARGS_CURRENT_POSITION = "current_position";

    protected ViewPager mViewPager;
    protected TabLayout mBottomBar;

    private long mExitTimestamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBottomBar();
        initViewPager();
        // Activity重建时恢复当前选择的页面
        if (savedInstanceState != null) {
            int currentPosition = savedInstanceState.getInt(ARGS_CURRENT_POSITION, 0);
            TabLayout.Tab currentTab = mBottomBar.getTabAt(currentPosition);
            if (currentTab != null) {
                currentTab.select();
            }
        }
    }

    private void initViewPager() {
        mViewPager = V.f(this, getViewPagerId());
        mViewPager.setOffscreenPageLimit(mBottomBar.getTabCount());
        mViewPager.setAdapter(initPagerAdapter());
    }

    private void initBottomBar() {
        mBottomBar = V.f(this, getBottomBarId());
        initBottomItems();
        mBottomBar.addOnTabSelectedListener(this);
    }

    protected abstract int getViewPagerId();

    protected abstract int getBottomBarId();

    protected abstract void initBottomItems();

    protected abstract BaseMainPagerAdapter initPagerAdapter();

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (mViewPager != null) {
            int position = tab.getPosition();
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
        if (System.currentTimeMillis() - mExitTimestamp > EXIT_TIPS_INTERVAL) {
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