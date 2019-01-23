package wxc.android.base.demo.ui.others;

import wxc.android.base.app.activity.BaseSplashActivity;
import wxc.android.base.demo.R;
import wxc.android.base.demo.ui.MainActivity;

public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void goToNext() {
        MainActivity.goTo(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }
}
