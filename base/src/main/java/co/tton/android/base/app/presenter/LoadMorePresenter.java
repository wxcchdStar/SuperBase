package co.tton.android.base.app.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import co.tton.android.base.R;
import co.tton.android.base.manager.CompositeSubscriptionHelper;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.base.view.CommonLayout;
import rx.Subscription;
import rx.functions.Action1;

// TODO: 改造成不依赖CommonLayout
public abstract class LoadMorePresenter<T> {

    protected int mPage;
    protected int mPageSize;

    private CommonLayout mCommonLayout;
    private BaseQuickAdapter<T> mAdapter;
    private BaseQuickAdapter.LoadMoreComponent<T> mLoadMoreComponent;

    private CompositeSubscriptionHelper mCompositeSubscriptionHelper;

    private boolean mInit;

    private int mFirstPage;

    public LoadMorePresenter() {
        this(1, 20); // 默认第一页从1开始，每页有20项。
    }

    public LoadMorePresenter(int firstPage, int pageSize) {
        mFirstPage = firstPage;
        mPage = firstPage;
        mPageSize = pageSize;
    }

    public void init(CommonLayout commonLayout, RecyclerView recyclerView, BaseQuickAdapter<T> adapter) {
        mCommonLayout = commonLayout;
        mAdapter = adapter;
        mCompositeSubscriptionHelper = CompositeSubscriptionHelper.newInstance();

        mLoadMoreComponent = new BaseQuickAdapter.LoadMoreComponent<>();
        mLoadMoreComponent.install(recyclerView, mAdapter, R.layout.common_load_next);
        mLoadMoreComponent.setOnLoadNextListener(new BaseQuickAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                addSubscription(requestNextPage(mSuccessAction, mFailedAction));
            }
        });

        mCommonLayout.setOnErrorClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonLayout.showLoading();
                addSubscription(requestNextPage(mSuccessAction, mFailedAction));
            }
        });

        mInit = true;
        reload();
    }

    public Subscription reload() {
        mPage = mFirstPage;
        mCommonLayout.showLoading();

        Subscription subscription = requestNextPage(mSuccessAction, mFailedAction);
        addSubscription(subscription);
        return subscription;
    }

    public void destroy() {
        if (mCompositeSubscriptionHelper != null) {
            mCompositeSubscriptionHelper.unsubscribe();
        }
        if (mLoadMoreComponent != null) {
            mLoadMoreComponent.uninstall();
        }
    }

    private void addSubscription(Subscription subscription) {
        mCompositeSubscriptionHelper.addSubscription(subscription);
    }

    public boolean isInit() {
        return mInit;
    }

    protected Action1<List<T>> mSuccessAction = new Action1<List<T>>() {
        @Override
        public void call(List<T> list) {
            mInit = true;
            if (mPage == mFirstPage) {
                if (list != null && !list.isEmpty()) {
                    mAdapter.setDataDirectly(list);
                    if (list.size() < mPageSize) {
                        mLoadMoreComponent.onNoMore();
                    } else {
                        mLoadMoreComponent.onLoadMoreSuccess();
                    }
                    mCommonLayout.showContent();
                } else {
                    if (mAdapter.getHeaderViewCount() > 0) {
                        mCommonLayout.showContent();
                    } else {
                        mCommonLayout.showEmpty();
                    }
                }
            } else {
                mAdapter.addData(list);
                if (list == null || list.size() < mPageSize) {
                    mLoadMoreComponent.onNoMore();
                } else {
                    mLoadMoreComponent.onLoadMoreSuccess();
                }
            }
            mPage++;
        }
    };

    protected Action1<Throwable> mFailedAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable t) {
            mInit = true;
            if (mPage == mFirstPage) {
                mCommonLayout.showError();
            } else {
                mLoadMoreComponent.onLoadMoreFailed();
            }
        }
    };

    protected abstract Subscription requestNextPage(Action1<List<T>> success, Action1<Throwable> failed);
}
