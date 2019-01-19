package wxc.android.base.rx;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;

public class RxBus {

    private static volatile RxBus sInstance;

    private final Relay<Object> mBus;

    private RxBus() {
        mBus = PublishRelay.create().toSerialized();
    }

    public static RxBus get() {
        if (sInstance == null) {
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    public void post(Object object) {
        if (object == null) return;

        mBus.accept(object);
    }

    public  <T> Observable<T> toObservable(final Class<T> type) {
        return mBus.ofType(type);
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }
}