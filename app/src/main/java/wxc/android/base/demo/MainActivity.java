package wxc.android.base.demo;

import android.os.Bundle;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.List;

import androidx.annotation.Nullable;
import wxc.android.base.api.ApiObserver;
import wxc.android.base.api.ApiTransformer;
import wxc.android.base.api.progress.ProgressObserver;
import wxc.android.base.app.activity.BaseActivity;
import wxc.android.base.demo.api.Api;
import wxc.android.base.demo.api.BookBean;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Api.get().getBooks("123")
                .compose(new ApiTransformer<>(this))
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new ProgressObserver<>(this, new ApiObserver<List<BookBean>>() {
                    @Override
                    public void onNext(List<BookBean> bookBeans) {

                    }
                }));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}
