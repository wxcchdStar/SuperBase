package wxc.android.base.api;

public class ApiResultException extends RuntimeException {

    public int mCode;
    public String mMessage;

    public ApiResultException(int code, String message) {
        super(message);
        mCode = code;
        mMessage = message;
    }

}
