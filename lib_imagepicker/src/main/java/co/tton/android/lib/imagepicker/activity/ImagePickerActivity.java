package co.tton.android.lib.imagepicker.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.manager.RxBus;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.ToastUtils;
import co.tton.android.lib.imagepicker.ImagePicker;
import co.tton.android.lib.imagepicker.ImagePreview;
import co.tton.android.lib.imagepicker.R;
import co.tton.android.lib.imagepicker.adapter.ImagePickerAdapter;
import co.tton.android.lib.imagepicker.adapter.PopupWindowAdapter;
import co.tton.android.lib.imagepicker.model.ImageFolder;
import co.tton.android.lib.imagepicker.utils.FileUtils;
import co.tton.android.lib.imagepicker.utils.PhotoDirectoryLoader;
import co.tton.android.lib.imagepicker.views.GridItemDecoration;
import co.tton.android.lib.imagepicker.views.ListPopupWindow;
import rx.Observer;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.SIZE;

public class ImagePickerActivity extends BaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGS_TAKE_PHOTO_PATH = "image_path";

    private static final int REQ_TAKE_PHOTO = 11;

    private TextView mSelectedFolderTv;
    private TextView mPreviewTv;

    private boolean mShowVideo; //是否显示视频
    private boolean mShowGif; //是否显示gif
    private boolean mShowPreview; //是否预览图片
    private int mMaxSelectedCount; //选择图片最大数量
    private int mImageColumn; //RecycleView列数

    private ImagePickerAdapter mImagePickerAdapter;
    private ListPopupWindow mPopupWindow;
    private MenuItem mDoneMenu;

    private String mTakePhotoPath; //拍照图片地址
    private ArrayList<String> mSelectedImageList; //选中图片列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
            mTakePhotoPath = savedInstanceState.getString(ARGS_TAKE_PHOTO_PATH);
        } else {
            bundle = getIntent().getExtras();
        }
        mShowGif = bundle.getBoolean(ImagePicker.EXTRA_SHOW_GIF, false);
        mShowVideo = bundle.getBoolean(ImagePicker.EXTRA_SHOW_VIDEO, false);
        mMaxSelectedCount = bundle.getInt(ImagePicker.EXTRA_MAX_COUNT, ImagePicker.DEFAULT_MAX_SELECTED_IMAGE_COUNT);
        //初始化PopupWindow
        mPopupWindow = new ListPopupWindow(this);
        mPopupWindow.initPopupWindow();
        //初始化控件
        mSelectedFolderTv = V.f(this, R.id.tv_picker_image_sel_folder);
        mPreviewTv = V.f(this, R.id.tv_picker_image_preview);
        mSelectedFolderTv.setOnClickListener(this);
        mPreviewTv.setOnClickListener(this);
        //设置title
        if (mShowVideo) {
            setTitle(R.string.image_and_video);
            mSelectedFolderTv.setText(R.string.image_and_video);
        } else {
            setTitle(R.string.image);
            mSelectedFolderTv.setText(R.string.all_image);
        }
        //接收RxBus
        receiveRxBus();
        //是否有图片预览
        mShowPreview = bundle.getBoolean(ImagePicker.EXTRA_SHOW_PREVIEW, true);
        if (!mShowPreview) {
            mPreviewTv.setVisibility(View.GONE);
        }
        //获取已选中图片
        mSelectedImageList = bundle.getStringArrayList(ImagePicker.EXTRA_ORIGINAL_PHOTOS);
        if (mSelectedImageList == null) {
            mSelectedImageList = new ArrayList<>();
        }
        //图片列表
        mImageColumn = bundle.getInt(ImagePicker.EXTRA_GRID_COLUMN, ImagePicker.DEFAULT_COLUMN_COUNT);
        RecyclerView imageRv = V.f(this, R.id.rv_picker_image);
        imageRv.setLayoutManager(new GridLayoutManager(this, mImageColumn));
        imageRv.addItemDecoration(new GridItemDecoration(this));
        mImagePickerAdapter = new ImagePickerAdapter(this, mSelectedImageList, mShowPreview, mMaxSelectedCount);
        imageRv.setAdapter(mImagePickerAdapter);
        //加载图片
        getSupportLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picker_image;
    }

    private void receiveRxBus() {
        //更新图片列表
        addSubscription(RxBus.get().toObservable(PopupWindowAdapter.UpdateShowImageListEvent.class)
                .subscribe(new Observer<PopupWindowAdapter.UpdateShowImageListEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PopupWindowAdapter.UpdateShowImageListEvent event) {
                        if (event.mFolder == null) return;

                        mPopupWindow.dismiss();

                        mImagePickerAdapter.setDataDirectly(event.mFolder.mPhotoList);

                        mSelectedFolderTv.setText(event.mFolder.mName);
                    }
                }));
        //更新预览数量
        addSubscription(RxBus.get().toObservable(ImagePickerAdapter.UpdatePreviewCountEvent.class)
                .subscribe(new Observer<ImagePickerAdapter.UpdatePreviewCountEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ImagePickerAdapter.UpdatePreviewCountEvent updatePreviewCountEvent) {
                        if (updatePreviewCountEvent.mTakePhoto) {
                            //拍照
                            takePhoto();
                        } else {
                            //更新控件文本
                            setPreviewViewText(updatePreviewCountEvent.mPreviewCount);
                        }
                    }
                }));
    }

    private void takePhoto() {
        try {
            //调用系统相机
            String picDir = FileUtils.getStorageDir(this, Environment.DIRECTORY_DCIM);
            File photoFile = new File(picDir, System.currentTimeMillis() + ".jpg");
            mTakePhotoPath = photoFile.getPath();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(intent, REQ_TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_image_done, menu);
        mDoneMenu = menu.findItem(R.id.menu_picker_image_done);
        setPreviewViewText(mSelectedImageList.size());
        return super.onCreateOptionsMenu(menu);
    }

    private void setPreviewViewText(int previewCount) {
        mDoneMenu.setTitle(getString(R.string.picker_image_done_count, previewCount, mMaxSelectedCount));
        if (previewCount == 0) {
            mPreviewTv.setText(getString(R.string.image_preview));
        } else {
            mPreviewTv.setText(getString(R.string.image_preview_count, previewCount));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARGS_TAKE_PHOTO_PATH, mTakePhotoPath);
        outState.putBoolean(ImagePicker.EXTRA_SHOW_VIDEO, mShowVideo);
        outState.putBoolean(ImagePicker.EXTRA_SHOW_GIF, mShowGif);
        outState.putBoolean(ImagePicker.EXTRA_SHOW_PREVIEW, mShowPreview);
        outState.putInt(ImagePicker.EXTRA_MAX_COUNT, mMaxSelectedCount);
        outState.putInt(ImagePicker.EXTRA_GRID_COLUMN, mImageColumn);
        outState.putStringArrayList(ImagePicker.EXTRA_ORIGINAL_PHOTOS, mSelectedImageList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_picker_image_done) {
            onDone(mImagePickerAdapter.getSelectedImageList());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK) {
            //拍照返回
            if (data != null && data.getDataString() != null) return;
            insertImage(getContentResolver());
        } else if (requestCode == ImagePreview.REQUEST_CODE && resultCode == ImagePreviewActivity.RESULT_DONE) {
            //预览页点击完成
            onDone(data.getStringArrayListExtra(ImagePreview.EXTRA_SELECTED_IMAGE));
        } else if (requestCode == ImagePreview.REQUEST_CODE && resultCode == ImagePreviewActivity.RESULT_BACK) {
            //预览页点击返回键
            mImagePickerAdapter.setSelectedImageList(data.getStringArrayListExtra(ImagePreview.EXTRA_SELECTED_IMAGE));
        }
    }

    private void insertImage(ContentResolver cr) {
        File file = new File(mTakePhotoPath);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.TITLE, "");
        values.put(MediaStore.Images.Media.DESCRIPTION, "");
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.ImageColumns.SIZE, file.length());
        values.put(MediaStore.Images.ImageColumns.DATA, file.getPath());

        Uri url = null;
        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
            }
        }
    }

    private void onDone(ArrayList<String> selectedImageList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ImagePicker.EXTRA_ORIGINAL_PHOTOS, selectedImageList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_picker_image_sel_folder:
                //选择文件夹
                mPopupWindow.show(mSelectedFolderTv);
                break;
            case R.id.tv_picker_image_preview:
                //预览
                if (mImagePickerAdapter.getSelectedImageList().size() == 0) {
                    ToastUtils.showLong(this, R.string.select_image_preview);
                } else {
                    ImagePreview.getInstance()
                            .setAllImage(mImagePickerAdapter.getSelectedImageList())
                            .setMaxSelectedCount(mMaxSelectedCount)
                            .setSelectedImage(mImagePickerAdapter.getSelectedImageList())
                            .start(ImagePickerActivity.this);
                }
                break;
            default:
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new PhotoDirectoryLoader(this, args.getBoolean(ImagePicker.EXTRA_SHOW_GIF, false));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;

        HashMap<String, ImageFolder> folderMap = new HashMap<>();
        List<ImageFolder> folderList = new ArrayList<>();

        ImageFolder allFolder = new ImageFolder();
        allFolder.mId = ImageFolder.ID_ALL_FOLDER;
        allFolder.mName = getString(R.string.all_image);

        while (data.moveToNext()) {
            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            long size = data.getInt(data.getColumnIndexOrThrow(SIZE));

            if (size < 1 || !FileUtils.isExist(path)) continue;

            if (!folderMap.containsKey(bucketId)) {
                ImageFolder folder = new ImageFolder();
                folder.mId = bucketId;
                folder.mName = name;
                folder.mCoverPath = path;
                folderList.add(folder);
                folderMap.put(bucketId, folder);
            }
            folderMap.get(bucketId).addPhoto(path);
            allFolder.addPhoto(path);
        }
        if (allFolder.mPhotoList.size() > 0) {
            allFolder.mCoverPath = allFolder.mPhotoList.get(0);
        }
        allFolder.mPhotoList.add(0, ImageFolder.PATH_TAKE_PHOTO);

        folderList.add(0, allFolder);

        mImagePickerAdapter.clearData();

        mPopupWindow.setPopupWindowData(folderList);

        if (folderList.size() > 0) {
            ImageFolder folder = folderList.get(0);
            mImagePickerAdapter.addData(folder.mPhotoList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
