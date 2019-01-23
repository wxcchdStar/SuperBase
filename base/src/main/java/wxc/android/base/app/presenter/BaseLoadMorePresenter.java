package wxc.android.base.app.presenter;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import wxc.android.base.R;
import wxc.android.base.views.BaseQuickAdapter;
import wxc.android.base.views.CommonLayout;

// TODO: 改造成不依赖CommonLayout
public abstract class BaseLoadMorePresenter<T> {

    protected int mPage;
    protected int mPageSize;

    private CommonLayout mCommonLayout;
    private BaseQuickAdapter<T> mAdapter;
    private BaseQuickAdapter.LoadMoreComponent<T> mLoadMoreComponent;

    private int mFirstPage;

    public BaseLoadMorePresenter() {
        this(1, 20); // 默认第一页从1开始，每页有20项。
    }

    public BaseLoadMorePresenter(int firstPage, int pageSize) {
        mFirstPage = firstPage;
        mPage = firstPage;
        mPageSize = pageSize;
    }

    public void init(CommonLayout commonLayout, RecyclerView recyclerView, BaseQuickAdapter<T> adapter) {
        mCommonLayout = commonLayout;
        mAdapter = adapter;

        mLoadMoreComponent = new BaseQuickAdapter.LoadMoreComponent<>();
        mLoadMoreComponent.install(recyclerView, mAdapter, R.layout.common_load_next);
        mLoadMoreComponent.setOnLoadNextListener(() -> requestNextPage(mSuccessAction, mFailedAction));

        mCommonLayout.setOnErrorClickListener(v -> reload());

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

    protected Consumer<List<T>> mSuccessAction = new Consumer<List<T>>() {
        @Override
        public void accept(List<T> list) {
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
            if (mPage == mFirstPage) {
                mCommonLayout.showError();
            } else {
                mLoadMoreComponent.onLoadMoreFailed();
            }
        }
    };

    protected abstract Disposable requestNextPage(Consumer<List<T>> success, Consumer<Throwable> failed);
}
