package wxc.android.base.demo;

import android.app.Application;

import timber.log.Timber;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // init Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
