package co.tton.android.base.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import java.util.List;

import co.tton.android.base.api.ApiObserver;
import co.tton.android.base.api.ApiTransformer;
import co.tton.android.base.api.progress.ProgressObserver;
import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.demo.api.Api;
import co.tton.android.base.demo.api.BookBean;
import co.tton.android.base.utils.V;
import co.tton.android.lib.imagepreview.ImagePreviewActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        V.f(this, R.id.btn_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        V.f(this, R.id.btn_image_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreviewActivity.goTo(getApplicationContext(), null, 0);
            }
        });

        Api.get().getBooks("123")
                .compose(new ApiTransformer<List<BookBean>>())
                .subscribe(new ProgressObserver<>(this, new ApiObserver<List<BookBean>>() {
                    @Override
                    public void onNext(List<BookBean> bookBeans) {
                        super.onNext(bookBeans);
                    }
                }));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}
