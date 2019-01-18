package wxc.android.base.app.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import wxc.android.base.app.fragment.BaseFragment;
import wxc.android.base.manager.CompositeDisposableHelper;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragmentPresenter {

    protected BaseFragment mFragment;

    protected CompositeDisposableHelper mCompositeSubscriptionHelper;

    public void setFragment(BaseFragment fragment) {
        mFragment = fragment;
    }

    public void onAttach(Context context) {
    }

    public void onCreate(Bundle savedInstanceState) {
    }

    public void initContentView(View view) {
        mCompositeSubscriptionHelper = CompositeDisposableHelper.newInstance();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroyView() {
        mCompositeSubscriptionHelper.unDispose();
    }

    public void onDestroy() {
    }

    public void onDetach() {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void addDisposable(Disposable disposable) {
        mCompositeSubscriptionHelper.addDispose(disposable);
    }
}
