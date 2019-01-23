package wxc.android.base.demo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import wxc.android.base.app.activity.BaseActivity;
import wxc.android.base.demo.R;
import wxc.android.base.demo.ui.music.MusicRankingListActivity;

public class MainActivity extends BaseActivity {

    public static void goTo(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_api_demo).setOnClickListener(v ->  MusicRankingListActivity.goTo(this));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}
