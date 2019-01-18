package wxc.android.lib.downloader.internal;

public class DownloadConst {
    /**
     * 下载临时文件后缀名
     */
    public static final String TEMP_FILE_SUFFIX = ".tmp";

    /**
     * 连接超时时间
     */
    public static final int CONNECTION_TIMEOUT = 60 * 1000; // 60s

    /**
     * 从socket读数据时发生阻塞的超时时间
     */
    public static final int READ_TIMEOUT = 60 * 1000; // 60s

    /**
     * 默认的连接重试最大次数
     */
    public static final int DEFAULT_MAX_RETRY_COUNT = 5;

    /**
     * 默认的最大下载任务数
     */
    public static final int DEFAULT_MAX_TASK_COUNT = 2;

    /**
     * 下载Service在没有下载任务时存活的时间
     */
    public static final int SERVICE_LEFT_TIME = 15 * 1000; // 15s

}
