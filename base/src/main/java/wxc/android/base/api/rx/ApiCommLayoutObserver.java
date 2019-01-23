package wxc.android.base.api.rx;

import io.reactivex.disposables.Disposable;
import wxc.android.base.views.CommonLayout;

public class ApiCommLayoutObserver<T> extends ApiObserver<T> {

    private CommonLayout mCommonLayout;

    public ApiCommLayoutObserver(CommonLayout commonLayout) {
        super();
        mCommonLayout = commonLayout;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCommonLayout.showLoading();
        super.onSubscribe(d);
    }

    @Override
    public void onError(Throwable e) {
        mCommonLayout.showError();
        super.onError(e);
    }
}
