package co.tton.android.base.app.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.manager.CompositeDisposableHelper;
import io.reactivex.disposables.Disposable;

public class BaseActivityPresenter {

    protected BaseActivity mActivity;

    private CompositeDisposableHelper mCompositeSubscriptionHelper;

    public void setActivity(BaseActivity activity) {
        mActivity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        mCompositeSubscriptionHelper = CompositeDisposableHelper.newInstance();
    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {
        mCompositeSubscriptionHelper.unDispose();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void addDisposable(Disposable disposable) {
        mCompositeSubscriptionHelper.addDispose(disposable);
    }
}