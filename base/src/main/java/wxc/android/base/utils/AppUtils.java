package wxc.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

public class AppUtils {

    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName(Context context) {
        String versionName = "0.0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            versionName = pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static void installApk(Context context, File file) {
        if (file == null) return;

        Intent intent = new Intent();
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void uninstallApp(Context context, String packageName) {
        try {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openUrl(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null) return false;

        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void callPhone(Context context, String phone) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(Intent.createChooser(intent, ""));
    }

}
