package wxc.android.base.app.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxc.android.base.R;
import wxc.android.base.utils.V;
import wxc.android.base.view.CommonLayout;

public abstract class BaseDetailFragment extends BaseLazyLoadFragment {

    protected CommonLayout mCommonLayout;

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mCommonLayout = V.f(view, R.id.common_layout);
        mCommonLayout.setContentLayoutId(getContentLayoutId());
        mCommonLayout.setOnErrorClickListener(v -> requestDetail());
        return view;
    }

    protected final int getLayoutId() {
        return R.layout.fragment_common_layout;
    }

    protected abstract int getContentLayoutId();

    @Override
    public void initData() {
        setDataInitiated(true);
        requestDetail();
    }

    @Override
    public void cancelInit() {

    }

    private void requestDetail() {
        mCommonLayout.showLoading();
        requestGetDetail();
    }

    protected abstract void requestGetDetail();
}
