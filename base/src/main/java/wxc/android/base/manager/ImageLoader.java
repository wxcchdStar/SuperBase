package wxc.android.base.manager;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import wxc.android.base.R;

public class ImageLoader {

    private static ImageLoader sInstance;

    public static ImageLoader get() {
        if (sInstance == null) {
            sInstance = new ImageLoader();
        }
        return sInstance;
    }

    private ImageLoader() {

    }

    public void load(ImageView view, String uri) {
        if (view == null || uri == null) return;

        view.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(view)
                .load(uri)
                .apply(getDefaultOptions())
                .into(view);
    }

    public void loadPreview(ImageView view, String uri) {
        if (view == null || uri == null) return;

        view.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(view)
                .load(uri)
                .apply(getOverrideOptions(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).centerInside())
                .into(view);
    }

    private static RequestOptions getDefaultOptions() {
        return new RequestOptions()
                .placeholder(R.drawable.img_default)
                .error(R.drawable.img_default)
                .fallback(R.drawable.img_default)
                .centerCrop();
    }

    private static RequestOptions getOverrideOptions(int width, int height) {
        return getDefaultOptions().override(width, height);
    }

    private static RequestOptions getRoundOptions(int radius) {
        return getDefaultOptions()
                .transforms(new CenterCrop(), new RoundedCorners(radius));
    }

}
