package wxc.android.lib.updateversion;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

public class UpdateVersionService extends IntentService {
    private static final String ARGS_DOWNLOAD_URL = "download_url";
    private static final String ARGS_VERSION_NAME = "version_name";

    private long mTaskId;
    private DownloadManager mDownloadManager;

    public static void startService(Context context, String downloadUrl, String versionName) {
        Intent intent = new Intent(context, UpdateVersionService.class);
        intent.putExtra(ARGS_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(ARGS_VERSION_NAME, versionName);
        context.startService(intent);
    }

    public UpdateVersionService() {
        super("update_version");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String versionName = intent.getStringExtra(ARGS_VERSION_NAME);
        String downloadUrl = intent.getStringExtra(ARGS_DOWNLOAD_URL);
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        String fileName = getDownloadFileName(versionName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setAllowedOverRoaming(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeTypeStr = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadUrl));
        request.setMimeType(mimeTypeStr);

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mTaskId = mDownloadManager.enqueue(request);

        registerReceiver(new UpdateVersionReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public String getDownloadFileName(String versionName) {
        return getPackageName() + "_" + versionName + ".apk";
    }

    public class UpdateVersionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mTaskId);

            Cursor cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToNext()) {
                int downloadStatue = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (downloadStatue) {
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    case DownloadManager.STATUS_PENDING:
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        String downloadPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        installApk(downloadPath);
                        unregisterReceiver(this);
                        stopSelf();
                        break;
                    case DownloadManager.STATUS_FAILED:
                        unregisterReceiver(this);
                        stopSelf();
                        break;
                    default:
                }
                cursor.close();
            }
        }

        private void installApk(String downloadPath) {
            File file = new File(downloadPath);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            if (downloadPath.startsWith("file://")) {
                intent.setDataAndType(Uri.parse(downloadPath),
                        "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
            }
            startActivity(intent);
        }
    }
}
