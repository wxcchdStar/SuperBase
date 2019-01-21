package wxc.android.base.demo.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import wxc.android.base.app.activity.BaseActivity;
import wxc.android.base.demo.R;
import wxc.android.base.demo.ui.music.MusicRankingListActivity;
import wxc.android.base.utils.V;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        V.f(this, R.id.btn_api_demo).setOnClickListener(v -> MusicRankingListActivity.goTo(this));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}
