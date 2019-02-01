package wxc.android.lib.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import timber.log.Timber;

public class DownloadService extends Service {

    private final DownloadBinder mBinder = new DownloadBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service一直运行，直到明确停止它
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder {

        public void startDownload(IDownloader downloader) {
            if (downloader != null) {
                downloader.startDownload();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d( "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d( "onDestroy");
    }
}
