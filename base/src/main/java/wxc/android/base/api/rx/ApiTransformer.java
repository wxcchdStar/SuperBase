package wxc.android.base.api.rx;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle3.components.support.RxFragment;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wxc.android.base.api.ApiResultException;
import wxc.android.base.api.model.ApiResult;
import wxc.android.logwriter.L;

public class ApiTransformer<T> implements ObservableTransformer<ApiResult<T>, T> {

    private LifecycleProvider<?> mLifecycleProvider;

    public ApiTransformer(LifecycleProvider<?> lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
    }

    @Override
    public ObservableSource<T> apply(Observable<ApiResult<T>> observable) {
        return observable
                .map(apiResult -> {
                    if (apiResult.isOk()) {
                        return apiResult.mData;
                    }
                    throw new ApiResultException(apiResult.mCode, apiResult.mMessage);
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    L.e(throwable);

                    if (throwable instanceof Exception) {
                        throw new RuntimeException(throwable);
                    }
                    return null;
                })
                .compose(getLifecycleTransformer());
    }

    @SuppressWarnings("unchecked")
    private LifecycleTransformer<T> getLifecycleTransformer() {
        if (mLifecycleProvider instanceof RxAppCompatActivity) {
            return RxLifecycleAndroid.bindActivity((Observable<ActivityEvent>) mLifecycleProvider.lifecycle());
        } else if (mLifecycleProvider instanceof RxFragment) {
            return RxLifecycleAndroid.bindFragment((Observable<FragmentEvent>) mLifecycleProvider.lifecycle());
        }
        throw new IllegalArgumentException("mLifecycleEventClass not available");
    }
}
