package wxc.android.lib.downloader.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DownloadUtils {

    /**
     * @param context 上下文
     * @param type    The type of files directory to return. May be {@code null}
     *                for the root of the files directory or one of the following
     *                constants for a subdirectory:
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.
     * @return 存储路径
     */
    public static String getStorageDir(Context context, String type) {
        String path;
        if (isSdCardExist()) {
            File file = context.getExternalFilesDir(type);
            if (file == null) {
                path = "/Android/data/" + context.getPackageName() + "/files/";
            } else {
                path = file.getPath();
            }
        } else {
            path = context.getFilesDir().getPath();
        }
        mkdirs(path);
        if (path.charAt(path.length() - 1) != '/') {
            path += File.separator;
        }
        return path;
    }

    /*
     * 创建目录
     */
    private static void mkdirs(String dirPath) {
        File storageFile = new File(dirPath);
        if (storageFile.isDirectory() && !storageFile.exists()) {
            storageFile.mkdirs();
        }
    }

    /**
     * 判断网络是否已连接
     */
    public static boolean isConnected(Context ctx) {
        boolean result = false;
        if (ctx != null) {
            try {
                ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        result = true;
                    }
                }
            } catch (NoSuchFieldError e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断指定目录是否有足够的存储空间
     */
    @SuppressLint("ObsoleteSdkInt")
    public static boolean isStorageEnough(String dirPath, long neededSize) {
        if (dirPath == null) return false;

        StatFs statFs = new StatFs(dirPath);
        long blockSize;
        long availableBlocks;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSize();
            availableBlocks = statFs.getAvailableBlocks();
        } else {
            blockSize = statFs.getBlockSizeLong();
            availableBlocks = statFs.getAvailableBlocksLong();
        }
        return availableBlocks * blockSize > neededSize;
    }

    /**
     * 判断SD卡是否存在
     */
    public static boolean isSdCardExist() {
        // Check if media is mounted or storage is built-in
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }
}
