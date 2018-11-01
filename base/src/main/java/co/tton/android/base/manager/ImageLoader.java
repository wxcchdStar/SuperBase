package co.tton.android.base.manager;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import co.tton.android.base.R;

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

    public void load(ImageView view, String uri, int placeHolderId) {
        if (uri == null) return;

        Glide.with(view.getContext().getApplicationContext())
                .load(uri)
                .placeholder(placeHolderId)
                .centerCrop()
                .dontAnimate()
                .into(view);
    }

    public void loadPreview(ImageView view, String uri) {
        if (uri == null) return;

        Glide.with(view.getContext().getApplicationContext())
                .load(uri)
                .placeholder(R.drawable.img_default)
                .fitCenter()
                .dontAnimate()
                .into(view);
    }

//    public void loadWithoutSize(final ImageView view, final String uri) {
//        if (uri == null) return;
//
//        Context context = view.getContext().getApplicationContext();
//
//        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        view.getLayoutParams().height = ValueUtils.dpToPx(context, 200);
//        view.requestLayout();
//
//        Glide.with(context)
//                .load(uri)
//                .dontAnimate()
//                .transform(new BitmapTransformation(context) {
//                    @Override
//                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//                        return toTransform;
//                    }
//
//                    @Override
//                    public String getId() {
//                        return getClass().getName();
//                    }
//                })
//                .into(view);
//    }

//    private void load(final ImageView view, Bitmap toTransform) {
//        if (view.getMeasuredWidth() <= 0) return;
//
//        float radio = 1.0f * resource.getIntrinsicWidth() / resource.getIntrinsicHeight();
//        view.getLayoutParams().height = Math.round(view.getMeasuredWidth() / radio);
//        view.requestLayout();
//        view.setBackground(null);
//    }
}
