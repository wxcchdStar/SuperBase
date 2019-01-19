package wxc.android.base.api.progress;

import android.content.Context;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ApiProgressObserver<T> implements Observer<T>, ApiProgressDialogHandler.ProgressDismissListener {

    private Observer<T> mObserver;
    private ApiProgressDialogHandler mProgressDialogHandler;
    private Disposable mDisposable;

    public ApiProgressObserver(Context context, Observer<T> observer) {
        mObserver = observer;
        mProgressDialogHandler = new ApiProgressDialogHandler(context, this);
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        show();
        if (mObserver != null) {
            mObserver.onSubscribe(d);
        }
    }

    @Override
    public void onNext(T t) {
        if (mObserver != null) {
            mObserver.onNext(t);
        }
    }

    @Override
    public void onComplete() {
        dismiss();
        if (mObserver != null) {
            mObserver.onComplete();
        }
    }

    @Override
    public void onError(Throwable e) {
        dismiss();
        if (mObserver != null) {
            mObserver.onError(e);
        }
    }

    @Override
    public void onDismissProgress() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    private void show() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ApiProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismiss() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ApiProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }
}