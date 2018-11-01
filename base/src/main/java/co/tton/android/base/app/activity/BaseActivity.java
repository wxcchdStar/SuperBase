package co.tton.android.base.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jaeger.library.StatusBarUtil;

import co.tton.android.base.R;
import co.tton.android.base.app.presenter.BaseActivityPresenter;
import co.tton.android.base.app.presenter.linker.ActivityLinker;
import co.tton.android.base.manager.CompositeSubscriptionHelper;
import co.tton.android.base.utils.V;
import co.tton.android.base.utils.ValueUtils;
import rx.Subscription;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    private boolean mDestroyed;

    private ActivityLinker mLinker = new ActivityLinker();

    private CompositeSubscriptionHelper mCompositeSubscriptionHelper;

    private boolean mIsHasStatusBarColor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mLinker.register(this);
        mLinker.onCreate(savedInstanceState);

        mCompositeSubscriptionHelper = CompositeSubscriptionHelper.newInstance();

        if (mIsHasStatusBarColor) {
            StatusBarUtil.setColor(this, ValueUtils.getColor(this, R.color.colorPrimary), 0);
        }

        initToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLinker.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLinker.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLinker.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLinker.onStop();
    }

    @Override
    protected void onDestroy() {
        mDestroyed = true;
        super.onDestroy();
        mLinker.onDestroy();
        mCompositeSubscriptionHelper.unsubscribe();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLinker.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            if (mLinker.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLinker.onActivityResult(requestCode, resultCode, data);
    }

    private void initToolbar() {
        mToolbar = V.f(this, R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public boolean isDestroyed() {
        return mDestroyed || isFinishing();
    }

    protected abstract int getLayoutId();

    protected void setIsHasStatusBarColor(boolean b) {
        mIsHasStatusBarColor = b;
    }

    public void addPresenter(BaseActivityPresenter presenter) {
        mLinker.addActivityCallbacks(presenter);
    }

    public void addSubscription(Subscription subscription) {
        mCompositeSubscriptionHelper.addSubscription(subscription);
    }

    public static boolean isAvailable(BaseActivity activity) {
        return activity != null && !activity.isDestroyed() && !activity.isFinishing();
    }

}