package co.tton.android.base.api;

public class ApiResultException extends RuntimeException {

    public String mCode;
    public String mMessage;

    public ApiResultException(String code, String message) {
        super(message);
        mCode = code;
        mMessage = message;
    }

}
