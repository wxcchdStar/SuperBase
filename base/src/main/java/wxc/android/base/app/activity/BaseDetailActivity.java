package wxc.android.base.app.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import wxc.android.base.R;
import wxc.android.base.utils.V;
import wxc.android.base.view.CommonLayout;

public abstract class BaseDetailActivity extends BaseActivity {

    protected CommonLayout mCommonLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCommonLayout = V.f(this, R.id.common_layout);
        mCommonLayout.setContentLayoutId(getContentLayoutId());
        mCommonLayout.setOnErrorClickListener(v -> requestDetail());
        requestDetail();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_layout;
    }

    protected abstract int getContentLayoutId();

    private void requestDetail() {
        mCommonLayout.showLoading();
        requestGetDetail();
    }

    protected abstract void requestGetDetail();
}
