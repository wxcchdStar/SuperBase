package co.tton.android.base.demo.api;

import android.content.Context;

import java.io.File;

import co.tton.android.base.api.ApiResultException;
import co.tton.android.base.demo.R;
import co.tton.android.base.view.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApiUtils {

    public static MultipartBody.Part convertToPartBody(String key, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), requestFile);
    }

    public static void toastError(Context context, String message, Throwable e) {
        if (e != null && e.getCause() instanceof ApiResultException) {
            if (message == null) {
                String str = ((ApiResultException) e.getCause()).mMessage;
                ToastUtils.showShort(context, str);
            } else {
                ToastUtils.showShort(context, message);
            }
        } else {
            ToastUtils.showShort(context, context.getString(R.string.common_connection_error));
        }
    }

    public static void toastSuccess(Context context, String message) {
        ToastUtils.showShort(context, message);
    }

}
