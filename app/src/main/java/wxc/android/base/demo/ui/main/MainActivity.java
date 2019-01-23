package wxc.android.base.demo.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import wxc.android.base.app.activity.BaseMainWithBottomBarActivity;
import wxc.android.base.demo.R;

public class MainActivity extends BaseMainWithBottomBarActivity {

    public static void goTo(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getViewPagerId() {
        return R.id.vp_main;
    }

    @Override
    protected int getBottomBarId() {
        return R.id.tab_main;
    }

    @Override
    protected void initBottomItems() {
        mBottomBar.addTab(mBottomBar.newTab().setText("Tab0"));
        mBottomBar.addTab(mBottomBar.newTab().setText("Tab1"));
        mBottomBar.addTab(mBottomBar.newTab().setText("Tab2"));
        mBottomBar.addTab(mBottomBar.newTab().setText("Tab3"));
    }

    @Override
    protected BaseMainPagerAdapter initPagerAdapter() {
        return new MainPagerAdapter(getSupportFragmentManager(), mBottomBar.getTabCount());
    }

    private static class MainPagerAdapter extends BaseMainPagerAdapter {
        public MainPagerAdapter(FragmentManager fm, int count) {
            super(fm, count);
        }

        @Override
        public Fragment getItem(int position) {
            return MainTabFragment.newInstance(position);
        }
    }

}
