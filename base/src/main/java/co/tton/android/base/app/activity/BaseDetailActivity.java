package co.tton.android.base.app.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import co.tton.android.base.R;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.CommonLayout;
import io.reactivex.disposables.Disposable;

public abstract class BaseDetailActivity extends BaseActivity {

    protected CommonLayout mCommonLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCommonLayout = V.f(this, R.id.common_layout);
        mCommonLayout.setContentLayoutId(getContentLayoutId());
        mCommonLayout.setOnErrorClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDetail();
            }
        });
        requestDetail();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_layout;
    }

    protected abstract int getContentLayoutId();

    private void requestDetail() {
        mCommonLayout.showLoading();
        addDisposable(getDetailRequest());
    }

    protected abstract Disposable getDetailRequest();
}
