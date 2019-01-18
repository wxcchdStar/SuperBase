package co.tton.android.lib.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import co.tton.android.lib.imagepicker.activity.ImagePickerActivity;

public class ImagePicker {

    public static final int DEFAULT_MAX_SELECTED_IMAGE_COUNT = 100;
    public static final int DEFAULT_COLUMN_COUNT = 3;

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_VIDEO = "SHOW_VIDEO";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String EXTRA_GRID_COLUMN = "COLUMN";
    public final static String EXTRA_ORIGINAL_PHOTOS = "ORIGINAL_PHOTOS";
    public final static String EXTRA_SHOW_PREVIEW = "SHOW_PREVIEW";

    private Bundle mPickerOptionsBundle;
    private Intent mPickerIntent;

    public static ImagePicker getInstance() {
        return new ImagePicker();
    }

    private ImagePicker() {
        mPickerOptionsBundle = new Bundle();
        mPickerIntent = new Intent();
    }

    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    public void start(@NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(fragment.getContext()), requestCode);
    }

    public Intent getIntent(@NonNull Context context) {
        mPickerIntent.setClass(context, ImagePickerActivity.class);
        mPickerIntent.putExtras(mPickerOptionsBundle);
        return mPickerIntent;
    }

    public ImagePicker setMaxPickerImageCount(int photoCount) {
        mPickerOptionsBundle.putInt(EXTRA_MAX_COUNT, photoCount);
        return this;
    }

    public ImagePicker setGridColumnCount(int columnCount) {
        mPickerOptionsBundle.putInt(EXTRA_GRID_COLUMN, columnCount);
        return this;
    }

    public ImagePicker setShowGif(boolean showGif) {
        mPickerOptionsBundle.putBoolean(EXTRA_SHOW_GIF, showGif);
        return this;
    }

    public ImagePicker setShowVideo(boolean showVideo) {
        mPickerOptionsBundle.putBoolean(EXTRA_SHOW_VIDEO, showVideo);
        return this;
    }

    public ImagePicker setImageSelected(ArrayList<String> imagesUri) {
        mPickerOptionsBundle.putStringArrayList(EXTRA_ORIGINAL_PHOTOS, imagesUri);
        return this;
    }

    public ImagePicker setShowPreview(boolean showPreview) {
        mPickerOptionsBundle.putBoolean(EXTRA_SHOW_PREVIEW, showPreview);
        return this;
    }
}
