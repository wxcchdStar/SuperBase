package wxc.android.base.demo.ui.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import wxc.android.base.api.rx.ApiTransformer;
import wxc.android.base.app.activity.BaseListActivity;
import wxc.android.base.app.presenter.BaseLoadMorePresenter;
import wxc.android.base.demo.R;
import wxc.android.base.demo.api.Api;
import wxc.android.base.demo.model.MusicRankingBean;
import wxc.android.base.image.ImageLoader;
import wxc.android.base.views.BaseQuickAdapter;

public class MusicRankingListActivity extends BaseListActivity<MusicRankingBean> {

    public static void goTo(Context context) {
        Intent intent = new Intent(context, MusicRankingListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_music_ranking);
    }

    @Override
    protected BaseQuickAdapter<MusicRankingBean> initAdapter() {
        return new MusicRankingListAdapter(this);
    }

    @Override
    protected BaseLoadMorePresenter<MusicRankingBean> initLoadMoreComponent() {
        // 使用Integer.MAX_VALUE展示单页数据
        return new BaseLoadMorePresenter<MusicRankingBean>(1, 3) {
            @Override
            protected Disposable requestNextPage(Consumer<List<MusicRankingBean>> success, Consumer<Throwable> failed) {
                return Api.get().getMusicRankings()
                        .compose(new ApiTransformer<>(MusicRankingListActivity.this))
                        .subscribe(success, failed);
            }
        };
    }

    private static class MusicRankingListAdapter extends BaseQuickAdapter<MusicRankingBean> {

        public MusicRankingListAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getLayoutId(int viewType) {
            return R.layout.item_music_ranking_list;
        }

        @Override
        protected void initViews(QuickHolder holder, MusicRankingBean data, int position) {
            ImageView avatarIv = holder.findViewById(R.id.iv_music_ranking_avatar);
            TextView titleTv = holder.findViewById(R.id.tv_music_ranking_title);
            TextView descTv = holder.findViewById(R.id.tv_music_ranking_desc);

            ImageLoader.get().load(avatarIv, data.pic_s192);
            titleTv.setText(data.name);
            descTv.setText(data.comment);

            holder.findViewById(R.id.ll_music_ranking_list)
                    .setOnClickListener(v -> MusicRankingDetailActivity.goTo(mContext, data.type));
        }
    }
}
