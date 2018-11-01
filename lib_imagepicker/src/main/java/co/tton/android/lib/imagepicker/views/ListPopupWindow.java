package co.tton.android.lib.imagepicker.views;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.List;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.lib.imagepicker.R;
import co.tton.android.lib.imagepicker.adapter.PopupWindowAdapter;
import co.tton.android.lib.imagepicker.model.ImageFolder;

public class ListPopupWindow {

    private RecyclerView mFolderRv;

    private PopupWindow mPopupWindow;
    private Activity mActivity;
    private PopupWindowAdapter mFolderAdapter;

    public ListPopupWindow(BaseActivity activity) {
        mActivity = activity;
    }

    public void initPopupWindow() {
        View contentView = mActivity.getLayoutInflater().inflate(R.layout.popup_window, null);
        mFolderRv = (RecyclerView) contentView.findViewById(R.id.rv_image_folder);
        mFolderRv.setLayoutManager(new LinearLayoutManager(mActivity));
        mFolderRv.addItemDecoration(new GreyItemDecoration(mActivity));
        mFolderAdapter = new PopupWindowAdapter(mActivity);
        mFolderRv.setAdapter(mFolderAdapter);
        //初始化popupwindow
        mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }

    public void setPopupWindowData(List<ImageFolder> folderList) {
        if (mFolderAdapter != null) {
            mFolderAdapter.setCurrentSelectedFolder(folderList.get(0));
            mFolderAdapter.setDataDirectly(folderList);
            setPopupWindowHeight();
        }
    }

    private void setPopupWindowHeight() {
        View itemView = mActivity.getLayoutInflater().inflate(R.layout.item_popup_window, null);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        itemView.measure(w, h);
        int height = itemView.getMeasuredHeight();
        int totalHeight = mFolderAdapter.getItemCount() * height;

        DisplayMetrics displayMetrics = mActivity.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;

        if (1.0f * totalHeight / screenHeight > 1.0f * 3 / 4) {
            mPopupWindow.setHeight(Math.round(screenHeight * 1.0f * 3 / 4));
        } else {
            mPopupWindow.setHeight(totalHeight);
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void show(View view) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(view);
        }
    }
}
