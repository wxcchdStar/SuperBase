package wxc.android.lib.downloader.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadThreadPool {
    private static final ExecutorService DOWNLOAD_THREAD_POOL
            = Executors.newFixedThreadPool(DownloadConst.DEFAULT_MAX_TASK_COUNT);

    public static void executeTask(Runnable runnable) {
        DOWNLOAD_THREAD_POOL.execute(runnable);
    }
}
