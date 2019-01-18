package co.tton.android.base.app.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import co.tton.android.base.R;
import co.tton.android.base.app.presenter.LoadMorePresenter;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.base.view.CommonLayout;

public abstract class BaseListActivity<T> extends BaseActivity {

    protected CommonLayout mCommonLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter<T> mAdapter;
    protected LoadMorePresenter<T> mLoadMoreComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化通用布局
        mCommonLayout = V.f(this, R.id.common_layout);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = initAdapter();
        setRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mLoadMoreComponent = initLoadMoreComponent();
        mLoadMoreComponent.init(mCommonLayout, mRecyclerView, mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_layout;
    }

    protected int getContentLayoutId() {
        return R.layout.common_list;
    }

    @Override
    protected void onDestroy() {
        mLoadMoreComponent.destroy();
        super.onDestroy();
    }

    protected void setRecyclerView(RecyclerView recyclerView) {
        // set recyclerView config
    }

    protected abstract BaseQuickAdapter<T> initAdapter();

    protected abstract LoadMorePresenter<T> initLoadMoreComponent();
}
