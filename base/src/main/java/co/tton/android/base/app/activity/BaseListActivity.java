package co.tton.android.base.app.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import co.tton.android.base.R;
import co.tton.android.base.app.presenter.LoadMorePresenter;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.base.view.CommonLayout;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import wxc.android.logwriter.L;

public abstract class BaseListActivity<T> extends BaseActivity {

    protected CommonLayout mCommonLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter<T> mAdapter;
    protected LoadMorePresenter<T> mLoadMoreComponent;

    protected PublishSubject<String> mSubject;
    protected Subscription mSubscription;
    protected String mKeyword = "";
    protected SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        // 初始化搜索
        mSubject = PublishSubject.create();
        mSubject.ofType(String.class)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (mLoadMoreComponent.isInit()) {
                            if (mSubscription != null) {
                                mSubscription.unsubscribe();
                            }
                            mSubscription = mLoadMoreComponent.reload();
                        }
                    }
                });
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
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    L.e("onQueryTextSubmit: " + query);
                    onQueryTextChange(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    L.e("onQueryTextChange: " + newText);
                    if (TextUtils.isEmpty(newText)) {
                        newText = "";
                    }
                    mKeyword = newText;
                    mSubject.onNext(newText);
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected void setRecyclerView(RecyclerView recyclerView) {

    }

    protected abstract BaseQuickAdapter<T> initAdapter();

    protected abstract LoadMorePresenter<T> initLoadMoreComponent();
}
