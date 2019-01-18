package wxc.android.lib.downloader;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import wxc.android.lib.downloader.internal.DownloadUtils;

public class DownloadTask {
    // 下载状态列表
    public static final int STATE_NULL = 0;  // 没有状态
    public static final int STATE_WAIT = 1;  // 等待下载
    public static final int STATE_START = 2;  // 开始下载
    public static final int STATE_CONNECTING = 3;  // 正在连接
    public static final int STATE_DOWNLOADING = 4;  // 下载中
    public static final int STATE_FINISH = 5;  // 下载完成
    public static final int STATE_FAILED = 6;  // 下载失败
    public static final int STATE_CANCEL = 7;  // 下载取消

    // 下载文件ID
    public int mId;
    /**
     * The type of files directory to return. May be {@code null}
     * for the root of the files directory or one of the following
     * constants for a subdirectory:
     * {@link android.os.Environment#DIRECTORY_MUSIC},
     * {@link android.os.Environment#DIRECTORY_PODCASTS},
     * {@link android.os.Environment#DIRECTORY_RINGTONES},
     * {@link android.os.Environment#DIRECTORY_ALARMS},
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     * {@link android.os.Environment#DIRECTORY_PICTURES}, or
     * {@link android.os.Environment#DIRECTORY_MOVIES}.
     */
    public String mType;
    // 下载名称
    public String mName;
    // 下载链接
    public String mDownloadUrl;
    // 保存路径
    public String mSavePath;
    // 下载进度
    public volatile int mProgress;
    // 已下载大小
    public volatile long mDownloadedSize;
    // 文件大小
    public long mFileSize;
    // 下载状态
    public volatile int mState = STATE_NULL;
    // 下载时间
    public long mDownloadTime;
    // 是否支持断电续传
    public boolean mIsBreakpointSupported;
    // 是否显示下载通知
    public boolean mHasNotification;
    // 下载监听器
    private List<WeakReference<DownloadListener>> mDownloadListeners;

    public DownloadTask() {
        mDownloadTime = System.currentTimeMillis();
        mDownloadListeners = new ArrayList<>();
    }

    public String generateSavePath(Context context, String type, String url) {
        return DownloadUtils.getStorageDir(context, type) + generateFileName(url);
    }

    public String generateFileName(String url) {
        int index = url.lastIndexOf("/.");
        if (index > -1) {
            String suffix = url.substring(index, url.length());
            return url.hashCode() + "." + suffix;
        }
        return String.valueOf(url.hashCode());
    }

    public synchronized void addDownloadListener(DownloadListener listener) {
        if (listener != null) {
            for (WeakReference<DownloadListener> listenerRef : mDownloadListeners) {
                DownloadListener aListener = listenerRef.get();
                if (aListener != null && aListener == listener) {
                    return;
                }
            }
            mDownloadListeners.add(new WeakReference<>(listener));
            Log.d("DownloadTask", "addDownloadListener: " + listener);
        }
    }

    public synchronized void removeDownloadListener(DownloadListener listener) {
        if (listener != null) {
            for (WeakReference<DownloadListener> listenerRef : mDownloadListeners) {
                DownloadListener aListener = listenerRef.get();
                if (aListener != null && aListener == listener) {
                    Log.d("DownloadTask", "removeDownloadListener: " + listener);
                    mDownloadListeners.remove(listenerRef);
                    return;
                }
            }
        }
    }

    public synchronized void clearListener() {
        mDownloadListeners.clear();
    }

    public synchronized void notifyListener() {
        if (mDownloadListeners.size() > 0) {
            for (WeakReference<DownloadListener> listenerRef : mDownloadListeners) {
                DownloadListener aListener = listenerRef.get();
                if (aListener != null) {
                    Log.d("DownloadTask", "notifyListener: " + aListener);
                    aListener.onDownload(this);
                }
            }
        }
    }

}
