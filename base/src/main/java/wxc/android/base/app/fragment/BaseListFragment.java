package wxc.android.base.app.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxc.android.base.R;
import wxc.android.base.app.presenter.BaseLoadMorePresenter;
import wxc.android.base.utils.V;
import wxc.android.base.views.BaseQuickAdapter;
import wxc.android.base.views.CommonLayout;

public abstract class BaseListFragment<T> extends BaseLazyLoadFragment {

    protected CommonLayout mCommonLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter<T> mAdapter;
    protected BaseLoadMorePresenter<T> mLoadMoreComponent;

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common_layout, container, false);
        // 初始化通用布局
        mCommonLayout = V.f(rootView, R.id.common_layout);
        mCommonLayout.setContentLayoutId(getContentLayoutId());
        mCommonLayout.setOnErrorClickListener(v -> mLoadMoreComponent.reload());
        // 初始化分页加载列表
        mCommonLayout.showContent();
        View view = mCommonLayout.getContentView();
        mRecyclerView = V.f(view, R.id.common_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = initAdapter();
        setRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mLoadMoreComponent = initLoadMoreComponent();
        return rootView;
    }

    protected int getContentLayoutId() {
        return R.layout.common_list;
    }

    @Override
    public void initData() {
        setDataInitiated(true);
        mLoadMoreComponent.init(mCommonLayout, mRecyclerView, mAdapter);
    }

    @Override
    public void cancelInit() {

    }

    @Override
    public void onDestroyView() {
        mLoadMoreComponent.destroy();
        super.onDestroyView();
    }

    protected void setRecyclerView(RecyclerView recyclerView) {
        // set recyclerView config
    }

    protected abstract BaseQuickAdapter<T> initAdapter();

    protected abstract BaseLoadMorePresenter<T> initLoadMoreComponent();
}
