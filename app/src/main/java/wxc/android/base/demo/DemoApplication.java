package wxc.android.base.demo;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;
import wxc.android.base.app.activity.CommonWebViewActivity;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            // init Timber
            Timber.plant(new Timber.DebugTree());
            // init BlockCanary
            BlockCanary.install(this, new BlockCanaryContext()).start();
        }
        // init LeakCanary
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
        // init
        DoraemonKit.install(this);
        DoraemonKit.setWebDoorCallback(s -> CommonWebViewActivity.goTo(DemoApplication.this, s));
    }

}
