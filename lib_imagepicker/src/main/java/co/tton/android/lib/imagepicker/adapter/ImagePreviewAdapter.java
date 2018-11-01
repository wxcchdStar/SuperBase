package co.tton.android.lib.imagepicker.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.tton.android.base.manager.ImageLoader;
import uk.co.senab.photoview.PhotoView;

public class ImagePreviewAdapter extends PagerAdapter {

    private List<String> mImageList;

    public ImagePreviewAdapter(ArrayList<String> picList) {
        mImageList = picList;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView imagePv = new PhotoView(container.getContext());
        container.addView(imagePv, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ImageLoader.get().loadPreview(imagePv, mImageList.get(position));
        return imagePv;
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
