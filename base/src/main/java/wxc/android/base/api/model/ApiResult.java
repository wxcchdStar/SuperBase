package wxc.android.base.api.model;

import com.google.gson.annotations.SerializedName;

public class ApiResult<T> {

    @SerializedName("code")
    public int mCode;

    @SerializedName("message")
    public String mMessage;

    @SerializedName("result")
    public T mData;

    public boolean isOk() {
        return mCode == 200;
    }
}
