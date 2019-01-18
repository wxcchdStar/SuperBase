package wxc.android.lib.downloader;

import android.content.Context;

import wxc.android.lib.downloader.internal.DownloadScheduler;

public final class DownloadManager {

    private static volatile DownloadManager sInstance;

    public static DownloadManager getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (DownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new DownloadManager(ctx);
                }
            }
        }
        return sInstance;
    }

    private DownloadScheduler mScheduler;

    private DownloadManager(Context ctx) {
        mScheduler = new DownloadScheduler(ctx.getApplicationContext());
//        mScheduler.setNotificationManager(new IDownloadNotification() {
//            @Override
//            public void update(DownloadTask task) {
//
//            }
//        });
    }

    public DownloadTask getDownloadTask(String type, int id) {
        return mScheduler.getDownloadTask(type, id);
    }

    public boolean startDownload(DownloadTask task) {
        if (task == null) return false;
        return mScheduler.startDownload(task);
    }

    public void cancelDownload(String type, int id) {
        mScheduler.cancelDownload(type, id);
    }

}
