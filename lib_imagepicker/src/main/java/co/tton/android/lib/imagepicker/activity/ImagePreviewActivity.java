package co.tton.android.lib.imagepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.utils.V;
import co.tton.android.base.view.ToastUtils;
import co.tton.android.lib.imagepicker.ImagePicker;
import co.tton.android.lib.imagepicker.ImagePreview;
import co.tton.android.lib.imagepicker.R;
import co.tton.android.lib.imagepicker.adapter.ImagePreviewAdapter;
import co.tton.android.lib.imagepicker.views.LargeImageViewPager;

public class ImagePreviewActivity extends BaseActivity {

    public static final int RESULT_DONE = 20;
    public static final int RESULT_BACK = 30;

    private ArrayList<String> mSelectedImageList; //选中图片列表
    private ArrayList<String> mImageList; //所有图片
    private int mMaxSelectedCount; //选中图片最大数量
    private int mCurrentItem; //当前预览位置
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedImageList = getIntent().getStringArrayListExtra(ImagePreview.EXTRA_SELECTED_IMAGE);
        if (mSelectedImageList == null) {
            mSelectedImageList = new ArrayList<>();
        }
        mMaxSelectedCount = getIntent().getIntExtra(ImagePicker.EXTRA_MAX_COUNT, ImagePicker.DEFAULT_MAX_SELECTED_IMAGE_COUNT);

        mImageList = getIntent().getStringArrayListExtra(ImagePreview.EXTRA_ALL_IMAGE);
        mCurrentItem = getIntent().getIntExtra(ImagePreview.EXTRA_CURRENT_ITEM, 0);
        setTitle(getString(R.string.image_preview_title, mCurrentItem + 1, mImageList.size()));

        final CheckBox selectCb = V.f(this, R.id.cb_picker_image_preview);
        if (mSelectedImageList.contains(mImageList.get(mCurrentItem))) {
            selectCb.setChecked(true);
        }
        selectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    if (!mSelectedImageList.contains(mImageList.get(mCurrentItem))) {
                        if (mSelectedImageList.size() >= mMaxSelectedCount) {
                            ToastUtils.showLong(ImagePreviewActivity.this, getString(R.string.image_selected_max_count, mMaxSelectedCount));
                            return;
                        }
                        mSelectedImageList.add(mImageList.get(mCurrentItem));
                    }
                } else {
                    mSelectedImageList.remove(mImageList.get(mCurrentItem));
                }
                mMenu.getItem(0).setTitle(getString(R.string.picker_image_done_count, mSelectedImageList.size(), mMaxSelectedCount));
            }
        });

        LargeImageViewPager viewPager = V.f(this, R.id.view_pager_image);
        ImagePreviewAdapter previewAdapter = new ImagePreviewAdapter(mImageList);
        viewPager.setAdapter(previewAdapter);
        viewPager.setCurrentItem(mCurrentItem);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
                setTitle(getString(R.string.image_preview_title, mCurrentItem + 1, mImageList.size()));
                if (mSelectedImageList.contains(mImageList.get(position))) {
                    selectCb.setChecked(true);
                } else {
                    selectCb.setChecked(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_image_done, menu);
        menu.getItem(0).setTitle(getString(R.string.picker_image_done_count, mSelectedImageList.size(), mMaxSelectedCount));
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_image;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_picker_image_done) {
            onDone(RESULT_DONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onDone(RESULT_BACK);
    }

    private void onDone(int resultCode) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ImagePreview.EXTRA_SELECTED_IMAGE, mSelectedImageList);
        setResult(resultCode, intent);
        finish();
    }
}
