package co.tton.android.base.api;

import com.google.gson.annotations.SerializedName;

public class ApiResult<T> {

    @SerializedName("issuccess")
    public boolean mStatus;

    @SerializedName("message")
    public String mMessage;

    @SerializedName("books")
    public T mData;

    @SerializedName("datacount")
    public int mDataCount;

    public boolean isOk() {
        return mStatus;
    }

}
