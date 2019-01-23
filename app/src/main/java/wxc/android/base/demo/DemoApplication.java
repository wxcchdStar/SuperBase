package wxc.android.base.demo;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

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
        // init
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }
}
