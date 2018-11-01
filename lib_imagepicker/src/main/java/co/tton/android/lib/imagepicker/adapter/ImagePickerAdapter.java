package co.tton.android.lib.imagepicker.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import co.tton.android.base.manager.ImageLoader;
import co.tton.android.base.manager.RxBus;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.base.view.ToastUtils;
import co.tton.android.lib.imagepicker.ImagePreview;
import co.tton.android.lib.imagepicker.R;
import co.tton.android.lib.imagepicker.model.ImageFolder;

public class ImagePickerAdapter extends BaseQuickAdapter<String> {

    private Activity mActivity;
    private ArrayList<String> mSelectedImageList;
    private boolean mShowPreview;
    private int mMaxSelectedCount;

    public ImagePickerAdapter(Activity activity, ArrayList<String> pickerImageList, boolean showPreview, int maxSelectedCount) {
        super(activity);
        mActivity = activity;
        mSelectedImageList = pickerImageList;
        mShowPreview = showPreview;
        mMaxSelectedCount = maxSelectedCount;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_picker_image;
    }

    @Override
    protected void initViews(QuickHolder holder, final String data, final int position) {
        final ImageView selectIv = holder.findViewById(R.id.iv_image_select);
        ImageView imageIv = holder.findViewById(R.id.iv_image);
        // 展示图片
        if (ImageFolder.PATH_TAKE_PHOTO.equals(data)) {
            imageIv.setScaleType(ImageView.ScaleType.CENTER);
            imageIv.setImageResource(R.drawable.ic_take_photo);
            selectIv.setVisibility(View.GONE);
        } else {
            ImageLoader.get().load(imageIv, data, co.tton.android.base.R.drawable.img_default);

            selectIv.setVisibility(View.VISIBLE);
            selectIv.setSelected(mSelectedImageList.contains(data));
        }
        // 点击图片
        holder.findViewById(R.id.rl_item_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageFolder.PATH_TAKE_PHOTO.equals(data)) {
                    //拍照
                    UpdatePreviewCountEvent updateCountEvent = new UpdatePreviewCountEvent();
                    updateCountEvent.mTakePhoto = true;
                    RxBus.get().post(updateCountEvent);
                } else {
                    if (mShowPreview) {
                        //预览
                        int currentIndex = position; //预览当前位置
                        ArrayList<String> previewImageList = new ArrayList<>(); //预览图片列表
                        int startIndex = 0; //循环获取预览列表的起始位置
                        if (ImageFolder.PATH_TAKE_PHOTO.equals(data)) {
                            //当前文件夹是全部图片，预览排除拍照的图标
                            startIndex = 1;
                            currentIndex--;
                        }
                        while (startIndex < getData().size()) {
                            previewImageList.add(getData().get(startIndex));
                            startIndex++;
                        }
                        ImagePreview.getInstance()
                                .setAllImage(previewImageList)
                                .setMaxSelectedCount(mMaxSelectedCount)
                                .setCurrentItem(currentIndex)
                                .setSelectedImage(mSelectedImageList)
                                .start(mActivity);
                    }
                }
            }
        });
        //选择或取消选择
        selectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = selectIv.isSelected();
                selectIv.setSelected(!selected);
                if (!selected) {
                    if (!mSelectedImageList.contains(data)) {
                        if (mSelectedImageList.size() >= mMaxSelectedCount) {
                            ToastUtils.showLong(mActivity, mActivity.getString(R.string.image_selected_max_count, mMaxSelectedCount));
                            selectIv.setSelected(false);
                            return;
                        }
                        mSelectedImageList.add(data);
                    }
                } else {
                    mSelectedImageList.remove(data);
                }
                if (mShowPreview) {
                    //可以预览，更新文本
                    UpdatePreviewCountEvent updateCountEvent = new UpdatePreviewCountEvent();
                    updateCountEvent.mPreviewCount = mSelectedImageList.size();
                    updateCountEvent.mTakePhoto = false;
                    RxBus.get().post(updateCountEvent);
                }
            }
        });
    }

    public ArrayList<String> getSelectedImageList() {
        return mSelectedImageList;
    }

    public void setSelectedImageList(ArrayList<String> selectedImageList) {
        mSelectedImageList = selectedImageList;
        notifyDataSetChanged();
    }

    public class UpdatePreviewCountEvent {
        public boolean mTakePhoto;
        public int mPreviewCount;
    }
}
