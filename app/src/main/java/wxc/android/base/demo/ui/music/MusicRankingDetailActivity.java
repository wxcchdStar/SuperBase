package wxc.android.base.demo.ui.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.Nullable;
import wxc.android.base.api.rx.ApiCommLayoutObserver;
import wxc.android.base.api.rx.ApiTransformer;
import wxc.android.base.app.activity.BaseDetailActivity;
import wxc.android.base.demo.R;
import wxc.android.base.demo.api.Api;
import wxc.android.base.demo.api.ApiUtils;
import wxc.android.base.demo.model.MusicRankingDetailBean;
import wxc.android.base.manager.ImageLoader;

public class MusicRankingDetailActivity extends BaseDetailActivity {
    public static final String ARGS_TYPE = "args_type";

    public static void goTo(Context context, int type) {
        Intent intent = new Intent(context, MusicRankingDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(ARGS_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_music_ranking_detail);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.content_music_ranking_detail;
    }

    @Override
    protected void requestGetDetail() {
        int type = getIntent().getIntExtra(ARGS_TYPE, -1);
        Api.get().getMusicRankingDetail(type)
                .compose(new ApiTransformer<>(this))
                .subscribe(new ApiCommLayoutObserver<List<MusicRankingDetailBean>>(mCommonLayout) {

                    @Override
                    public void onNext(List<MusicRankingDetailBean> beanList) {
                        if (beanList != null && !beanList.isEmpty()) {
                            mCommonLayout.showContent();
                            initContent(beanList);
                        } else {
                            mCommonLayout.showEmpty();
                        }
                        ApiUtils.toastSuccess(getApplicationContext(), "Success!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ApiUtils.toastError(getApplicationContext(), null, e);
                    }
                });
    }

    private void initContent(List<MusicRankingDetailBean> beanList) {
        ImageView avatarIv = findViewById(R.id.iv_music_ranking_detail_avatar);
        TextView titleTv = findViewById(R.id.tv_music_ranking_detail_title);
        TextView descTv = findViewById(R.id.tv_music_ranking_detail_desc);

        MusicRankingDetailBean bean = beanList.get(0);
        ImageLoader.get().loadPreview(avatarIv, bean.pic_premium);
        titleTv.setText(bean.album_title);
        descTv.setText(bean.artist_name);
    }
}
