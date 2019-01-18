package co.tton.android.base.api.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;


public class ProgressDialogHandler extends Handler {
    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private ProgressDialog mDialog;

    private Context mContext;
    private boolean mCancelable;
    private ProgressDismissListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressDismissListener listener) {
        this(context, listener, false);
    }

    public ProgressDialogHandler(Context context, ProgressDismissListener listener, boolean cancelable) {
        mContext = context;
        mProgressCancelListener = listener;
        mCancelable = cancelable;
    }

    private void initProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(mCancelable);

            if (mCancelable) {
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mProgressCancelListener.onDismissProgress();
                    }
                });
            }

            try {
                if (!mDialog.isShowing()) {
                    mDialog.show();
                }
            } catch (Exception e) {
                // fix: BadTokenException, Unable to add window -- token android.os.BinderProxy@44fbe5f is not valid;
                // is your activity running?
                e.printStackTrace();
            }
        }
    }

    private void dismissProgressDialog() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
                // fix: IllegalArgumentException, not attached to window manager
                e.printStackTrace();
            }
            mDialog = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

    public interface ProgressDismissListener {
        void onDismissProgress();
    }
}