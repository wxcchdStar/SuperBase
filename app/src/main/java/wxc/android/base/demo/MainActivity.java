package wxc.android.base.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import wxc.android.base.app.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}
