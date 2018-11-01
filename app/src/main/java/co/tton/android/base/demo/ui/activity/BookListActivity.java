package co.tton.android.base.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import co.tton.android.base.app.activity.BaseListActivity;
import co.tton.android.base.app.presenter.LoadMorePresenter;
import co.tton.android.base.demo.api.BookBean;
import co.tton.android.base.view.BaseQuickAdapter;

public class BookListActivity extends BaseListActivity<BookBean> {

    public static void goTo(Context context) {
        Intent intent = new Intent(context, BookListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BaseQuickAdapter<BookBean> initAdapter() {
        return null;
    }

    @Override
    protected LoadMorePresenter<BookBean> initLoadMoreComponent() {
        return null;
    }
}
