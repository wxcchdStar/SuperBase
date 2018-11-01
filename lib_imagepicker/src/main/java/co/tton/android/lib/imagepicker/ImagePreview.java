package co.tton.android.lib.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import co.tton.android.lib.imagepicker.activity.ImagePreviewActivity;

public class ImagePreview {

    public final static int REQUEST_CODE = 666;

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_SELECTED_IMAGE = "selected_image";
    public final static String EXTRA_ALL_IMAGE = "image";

    private Bundle mPreviewOptionsBundle;
    private Intent mPreviewIntent;

    public static ImagePreview getInstance() {
        return new ImagePreview();
    }

    private ImagePreview() {
        mPreviewOptionsBundle = new Bundle();
        mPreviewIntent = new Intent();
    }

    public void start(@NonNull Activity activity) {
        activity.startActivityForResult(getIntent(activity), REQUEST_CODE);
    }

    public void start(@NonNull Context context, @NonNull android.support.v4.app.Fragment fragment) {
        fragment.startActivityForResult(getIntent(context), REQUEST_CODE);
    }

    public Intent getIntent(@NonNull Context context) {
        mPreviewIntent.setClass(context, ImagePreviewActivity.class);
        mPreviewIntent.putExtras(mPreviewOptionsBundle);
        return mPreviewIntent;
    }

    public ImagePreview setSelectedImage(ArrayList<String> selectedImage) {
        mPreviewOptionsBundle.putStringArrayList(EXTRA_SELECTED_IMAGE, selectedImage);
        return this;
    }

    public ImagePreview setAllImage(ArrayList<String> allImage) {
        mPreviewOptionsBundle.putStringArrayList(EXTRA_ALL_IMAGE, allImage);
        return this;
    }

    public ImagePreview setCurrentItem(int currentItem) {
        mPreviewOptionsBundle.putInt(EXTRA_CURRENT_ITEM, currentItem);
        return this;
    }

    public ImagePreview setMaxSelectedCount(int maxSelectedCount) {
        mPreviewOptionsBundle.putInt(ImagePicker.EXTRA_MAX_COUNT, maxSelectedCount);
        return this;
    }
}
