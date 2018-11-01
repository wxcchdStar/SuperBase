package co.tton.android.lib.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.manager.ImageLoader;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.base.view.SquareImageView;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_CODE = 233;

    private MainAdapter mMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView imageRv = V.f(this, R.id.rv_image);
        imageRv.setLayoutManager(new GridLayoutManager(this, 4));
        mMainAdapter = new MainAdapter(this);
        imageRv.setAdapter(mMainAdapter);

        V.f(this, R.id.tv_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.getInstance()
                        .setGridColumnCount(3)  //可选，默认3列
                        .setShowGif(false)  //可选，默认false
                        .setShowVideo(false)  //可选，默认false，暂不支持视频
                        .setMaxPickerImageCount(9)  //可选，默认最大数量是100
                        .setImageSelected((ArrayList<String>) mMainAdapter.getData())  //可选，可根据需求选择
                        .start(MainActivity.this, REQUEST_CODE);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == REQUEST_CODE) {
            //返回的ResultCode：ImagePicker.REQUEST_CODE
            mMainAdapter.setData(data.getStringArrayListExtra(ImagePicker.EXTRA_ORIGINAL_PHOTOS));
        }
    }

    private static class MainAdapter extends BaseQuickAdapter<String> {

        public MainAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getLayoutId(int viewType) {
            return R.layout.item_picker_image;
        }

        @Override
        protected void initViews(QuickHolder holder, String data, int position) {
            ImageView selectIv = holder.findViewById(R.id.iv_image_select);
            selectIv.setVisibility(View.GONE);

            ImageView imageIv = holder.findViewById(R.id.iv_image);
            ImageLoader.get().load(imageIv, data, 0);
        }
    }
}
