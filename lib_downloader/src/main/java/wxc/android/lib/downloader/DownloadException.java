package wxc.android.lib.downloader;

public class DownloadException extends Exception {

    /**
     * 下载异常类型： URL无效
     */
    public static final int CODE_URL_UNAVAILABLE = 101;

    /**
     * 下载异常类型： 链接已被重定向
     */
    public static final int CODE_CONNECTION_REDIRECT = 102;

    /**
     * 下载异常类型： 网络不可用/没有网络
     */
    public static final int CODE_NETWORK_UNAVAILABLE = 103;

    /**
     * 下载异常类型： 网络链接超时
     */
    public static final int CODE_CONNECTION_TIMEOUT = 104;

    /**
     * 下载异常类型： 网址不可达
     */
    public static final int CODE_NETWORK_UNREACHABLE = 105;

    /**
     * 下载异常类型： 不可判断
     */
    public static final int CODE_NOT_DETERMINED = 106;

    /**
     * 下载异常类型: 存储空间不足
     */
    public static final int CODE_STORAGE_NOT_ENOUGH = 107;

    /**
     * 下载异常类型： 文件系统只读
     */
    public static final int CODE_FILE_SYSTEM_READ_ONLY = 108;

    public int mCode;

    public DownloadException(int code, String detailMessage) {
        super(detailMessage);
        mCode = code;
    }
}
