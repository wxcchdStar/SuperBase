package co.tton.android.base.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.tton.android.base.R;
import co.tton.android.base.app.presenter.LoadMorePresenter;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.base.view.CommonLayout;

public abstract class BaseListFragment<T> extends BaseLazyLoadFragment {

    protected CommonLayout mCommonLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter<T> mAdapter;
    protected LoadMorePresenter<T> mLoadMoreComponent;

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common_layout, container, false);
        // 初始化通用布局
        mCommonLayout = V.f(rootView, R.id.common_layout);
        mCommonLayout.setContentLayoutId(getContentLayoutId());
        mCommonLayout.setOnErrorClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMoreComponent.reload();
            }
        });
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

    }

    protected abstract BaseQuickAdapter<T> initAdapter();

    protected abstract LoadMorePresenter<T> initLoadMoreComponent();
}
