package wxc.android.base.api.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public abstract class ApiObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {
        Timber.d(e);
    }

    @Override
    public void onComplete() {

    }
}
