package co.tton.android.base.manager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CompositeDisposableHelper {
    private CompositeDisposable mCompositeDisposable;

    public static CompositeDisposableHelper newInstance() {
        return new CompositeDisposableHelper();
    }

    private CompositeDisposableHelper() {

    }

    public void addDispose(Disposable disposable) {
        if (disposable == null) {
            return;
        }
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}
