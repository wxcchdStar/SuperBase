package co.tton.android.lib.imagepicker.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class FileUtils {

    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) return false;

        File f = new File(path);
        return f.exists();
    }

    public static boolean isSdCardExist() {
        // Check if media is mounted or storage is built-in
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }

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

    private static void mkdirs(String dirPath) {
        File storageFile = new File(dirPath);
        if (storageFile.isDirectory() && !storageFile.exists()) {
            storageFile.mkdirs();
        }
    }

}
