package co.tton.android.base.api;

import com.google.gson.annotations.SerializedName;

public class ApiResult<T> {

    @SerializedName("code")
    public int mCode;

    @SerializedName("message")
    public String mMessage;

    @SerializedName("data")
    public T mData;

    public boolean isOk() {
        return mCode == 0;
    }
}
