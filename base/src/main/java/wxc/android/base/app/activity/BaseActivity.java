package wxc.android.base.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.gyf.barlibrary.ImmersionBar;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import wxc.android.base.R;
import wxc.android.base.app.presenter.BaseActivityPresenter;
import wxc.android.base.app.presenter.linker.ActivityLinker;
import wxc.android.base.utils.AndroidBug5497Workaround;
import wxc.android.base.utils.V;

public abstract class BaseActivity extends RxAppCompatActivity {

    protected Toolbar mToolbar;

    private final ActivityLinker mLinker = new ActivityLinker();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        // 解决软键盘遮挡布局的问题
        AndroidBug5497Workaround.assistActivity(this);
        // TODO 设置StatusBar颜色，根布局必须设置fitsSystemWindows
        ImmersionBar.with(this)
                .statusBarColor(R.color.colorPrimary)
                .init();
        // Toolbar
        initToolbar();
        // Delegate lifecycle
        mLinker.register(this);
        mLinker.onCreate(savedInstanceState);
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
        super.onDestroy();
        mLinker.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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

    protected abstract int getLayoutId();

    public void addPresenter(BaseActivityPresenter presenter) {
        mLinker.addActivityCallbacks(presenter);
    }

    public static boolean isAvailable(Activity activity) {
        return activity != null && !activity.isDestroyed() && !activity.isFinishing();
    }

}