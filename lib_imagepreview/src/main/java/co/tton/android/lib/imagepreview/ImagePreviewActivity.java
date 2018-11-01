package co.tton.android.lib.imagepreview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.manager.ImageLoader;
import co.tton.android.base.utils.V;
import uk.co.senab.photoview.PhotoView;

public class ImagePreviewActivity extends BaseActivity {
    private static final String PIC_LIST = "list";
    private static final String ITEM_POSITION = "position";

    public static void goTo(Context context, ArrayList<String> picList, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putStringArrayListExtra(PIC_LIST, picList);
        intent.putExtra(ITEM_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, Color.BLACK, 0);

        ArrayList<String> picList = (getIntent().getStringArrayListExtra(PIC_LIST));
        if (picList == null) {
            picList = new ArrayList<>();
        }
        final int size = picList.size();

        int index = getIntent().getIntExtra(ITEM_POSITION, 0);

        final ImagePreviewIndicator indicator = V.f(this, R.id.indicator);
        if (size <= 1) {
            indicator.setVisibility(View.GONE);
        } else {
            indicator.setCount(size);
        }
        indicator.setSelection(index);

        ViewPager viewPager = V.f(this, R.id.vp_preview);
        PreviewAdapter adapter = new PreviewAdapter(picList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indicator.setSelection(position);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_preview;
    }

    private static class PreviewAdapter extends PagerAdapter {
        private List<String> mPictureList;

        PreviewAdapter(ArrayList<String> picList) {
            mPictureList = picList;
        }

        @Override
        public int getCount() {
            return mPictureList == null ? 0 : mPictureList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            String uri = mPictureList.get(position);
            ImageLoader.get().loadPreview(photoView, uri);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
