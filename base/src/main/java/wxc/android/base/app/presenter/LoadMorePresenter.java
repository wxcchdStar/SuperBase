package wxc.android.base.app.presenter;

import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import wxc.android.base.R;
import wxc.android.base.view.BaseQuickAdapter;
import wxc.android.base.view.CommonLayout;

// TODO: 改造成不依赖CommonLayout
public abstract class LoadMorePresenter<T> {

    protected int mPage;
    protected int mPageSize;

    private CommonLayout mCommonLayout;
    private BaseQuickAdapter<T> mAdapter;
    private BaseQuickAdapter.LoadMoreComponent<T> mLoadMoreComponent;

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

        mLoadMoreComponent = new BaseQuickAdapter.LoadMoreComponent<>();
        mLoadMoreComponent.install(recyclerView, mAdapter, R.layout.common_load_next);
        mLoadMoreComponent.setOnLoadNextListener(new BaseQuickAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestNextPage(mSuccessAction, mFailedAction);
            }
        });

        mCommonLayout.setOnErrorClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });

        mInit = true;
        reload();
    }

    public void reload() {
        mPage = mFirstPage;
        mCommonLayout.showLoading();
        requestNextPage(mSuccessAction, mFailedAction);
    }

    public void destroy() {
        if (mLoadMoreComponent != null) {
            mLoadMoreComponent.uninstall();
        }
    }

    public boolean isInit() {
        return mInit;
    }

    protected Consumer<List<T>> mSuccessAction = new Consumer<List<T>>() {
        @Override
        public void accept(List<T> list) {
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

    protected Consumer<Throwable> mFailedAction = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable t) {
            mInit = true;
            if (mPage == mFirstPage) {
                mCommonLayout.showError();
            } else {
                mLoadMoreComponent.onLoadMoreFailed();
            }
        }
    };

    protected abstract Disposable requestNextPage(Consumer<List<T>> success, Consumer<Throwable> failed);
}
