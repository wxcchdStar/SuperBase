package co.tton.android.lib.imagepicker.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.tton.android.base.manager.ImageLoader;
import co.tton.android.base.manager.RxBus;
import co.tton.android.base.view.BaseQuickAdapter;
import co.tton.android.lib.imagepicker.R;
import co.tton.android.lib.imagepicker.model.ImageFolder;

public class PopupWindowAdapter extends BaseQuickAdapter<ImageFolder> {

    private ImageFolder mCurrentSelectedFolder;

    public PopupWindowAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_popup_window;
    }

    @Override
    protected void initViews(QuickHolder holder, final ImageFolder data, int position) {
        ImageView folderImageIv = holder.findViewById(R.id.iv_cover_image);
        TextView folderNameTv = holder.findViewById(R.id.tv_folder_name);
        TextView folderSizeTv = holder.findViewById(R.id.tv_image_count);
        TextView folderSelTv = holder.findViewById(R.id.tv_select_folder);

        ImageLoader.get().load(folderImageIv, data.mCoverPath, co.tton.android.base.R.drawable.img_default);
        folderNameTv.setText(data.mName);
        folderSizeTv.setText(String.valueOf(data.mPhotoList.size()));
        if (mCurrentSelectedFolder.mId.equals(data.mId)) {
            folderSelTv.setBackgroundResource(R.drawable.ic_folder_selected);
        } else {
            folderSelTv.setBackgroundResource(R.drawable.ic_folder_unselected);
        }

        LinearLayout itemLL = holder.findViewById(R.id.ll_item_folder);
        itemLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择文件夹
                mCurrentSelectedFolder = data;
                notifyDataSetChanged();
                UpdateShowImageListEvent updateImageEvent = new UpdateShowImageListEvent();
                updateImageEvent.mFolder = mCurrentSelectedFolder;
                RxBus.get().post(updateImageEvent);
            }
        });
    }

    public void setCurrentSelectedFolder(ImageFolder currentSelectedFolder) {
        mCurrentSelectedFolder = currentSelectedFolder;
    }

    public static class UpdateShowImageListEvent {
        public ImageFolder mFolder;
    }
}
