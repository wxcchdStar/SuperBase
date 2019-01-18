package wxc.android.base.api;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wxc.android.logwriter.L;

public class ApiTransformer<T> implements ObservableTransformer<ApiResult<T>, T> {

    @Override
    public ObservableSource<T> apply(Observable<ApiResult<T>> observable) {
        return observable
                .map(new Function<ApiResult<T>, T>() {
                    @Override
                    public T apply(ApiResult<T> apiResult) {
                        if (apiResult.isOk()) {
                            return apiResult.mData;
                        }
                        throw new ApiResultException(apiResult.mCode, apiResult.mMessage);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, T>() {
                    @Override
                    public T apply(Throwable throwable) {
                        L.e(throwable);

                        if (throwable instanceof Exception) {
                            throw new RuntimeException(throwable);
                        }
                        return null;
                    }
                });
    }
}
