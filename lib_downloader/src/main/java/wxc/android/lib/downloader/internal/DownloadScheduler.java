package wxc.android.lib.downloader.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wxc.android.lib.downloader.DownloadListener;
import wxc.android.lib.downloader.DownloadService;
import wxc.android.lib.downloader.DownloadTask;
import wxc.android.lib.downloader.IDownloadNotification;

public class DownloadScheduler implements DownloadListener {
    private Context mContext;

    // 下载队列
    private List<String> mInitTasks;
    private List<String> mCurrentTasks;
    private List<String> mWaitingTasks;
    private Map<String, DownloadTask> mTaskMap;
    private Map<String, AsyncDownloader> mDownloaderMap;

    // 下载通知
    private IDownloadNotification mNotificationManager;

    // 下载服务
    private DownloadService.DownloadBinder mDownloadBinder;
    private volatile boolean mIsServiceConnected;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("DownloadScheduler", "onServiceConnected");

            mIsServiceConnected = true;
            mDownloadBinder = (DownloadService.DownloadBinder) service;
            pollingDownloadTasks();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("DownloadScheduler", "onServiceDisconnected");

            mIsServiceConnected = false;
            mDownloadBinder = null;
        }
    };

    // 关闭下载服务帮助类
    private Handler mHandler;

    private Runnable mUnbindServiceRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (DownloadScheduler.this) {
                if (mIsServiceConnected) {
                    Log.d("DownloaderScheduler", "stop download service");
                    try {
                        mContext.unbindService(mConnection);
                        Intent intent = new Intent(mContext, DownloadService.class);
                        mContext.stopService(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public DownloadScheduler(Context ctx) {
        mContext = ctx.getApplicationContext();
        mInitTasks = Collections.synchronizedList(new ArrayList<String>(DownloadConst.DEFAULT_MAX_TASK_COUNT));
        mCurrentTasks = Collections.synchronizedList(new ArrayList<String>(DownloadConst.DEFAULT_MAX_TASK_COUNT));
        mWaitingTasks = Collections.synchronizedList(new ArrayList<String>());
        mTaskMap = new HashMap<>();
        mDownloaderMap = new HashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public boolean startDownload(DownloadTask task) {
        if (task == null) return false;

        synchronized (this) {
            String taskId = getTaskId(task.mType, task.mId);
            if (!mTaskMap.containsKey(taskId)) {
                mHandler.removeCallbacks(mUnbindServiceRunnable);

                mTaskMap.put(taskId, task);
                task.addDownloadListener(this);

                if (mCurrentTasks.size() < DownloadConst.DEFAULT_MAX_TASK_COUNT
                        && mInitTasks.size() < DownloadConst.DEFAULT_MAX_TASK_COUNT) {
                    mInitTasks.add(taskId);
                } else {
                    mWaitingTasks.add(taskId);
                    task.mState = DownloadTask.STATE_WAIT;
                    task.notifyListener();
                }

                Log.d("DownloadScheduler", "service connected: " + mIsServiceConnected);
                if (!mIsServiceConnected) {
                    doBindService();
                } else {
                    pollingDownloadTasks();
                }
                return true;
            } else {
                Log.w("DownloadScheduler", "Task(" + task.mId + ") existed! >>> " + task.mDownloadUrl);
                return false;
            }
        }
    }

    // 取消下载任务，但不会删除下载缓存文件
    public void cancelDownload(String type, int id) {
        if (type == null || id == 0) return;

        synchronized (this) {
            String taskId = type + "|" + id;
            if (mTaskMap.containsKey(taskId)) {
                // 从任务列表中移除
                mTaskMap.remove(taskId);
                mInitTasks.remove(taskId);
                mWaitingTasks.remove(taskId);
                mCurrentTasks.remove(taskId);
                // 取消任务下载
                AsyncDownloader downloader = mDownloaderMap.remove(taskId);
                if (downloader != null) {
                    downloader.cancelDownload();
                }
            }
        }
    }

    // 方法是运行在UI线程中
    private synchronized void pollingDownloadTasks() {
        Log.d("DownloadScheduler", "pollingDownloadTasks run at " + Thread.currentThread());
        Log.d("DownloadScheduler", "pre polling, cur mSize: " + mCurrentTasks.size() + ", wait mSize: " + mWaitingTasks.size());

        while (mCurrentTasks.size() < DownloadConst.DEFAULT_MAX_TASK_COUNT && !mInitTasks.isEmpty()) {
            executeDownload(mInitTasks.remove(0));
        }

        while (mCurrentTasks.size() < DownloadConst.DEFAULT_MAX_TASK_COUNT && !mWaitingTasks.isEmpty()) {
            String taskId = mWaitingTasks.remove(0);
            executeDownload(taskId);
        }
    }

    private synchronized void executeDownload(String taskId) {
        mCurrentTasks.add(taskId);

        DownloadTask curTask = mTaskMap.get(taskId);
        if (curTask != null) {
            AsyncDownloader downloader = new AsyncDownloader(mContext, curTask);
            mDownloaderMap.put(taskId, downloader);
            mDownloadBinder.startDownload(downloader);
        } else {
            mTaskMap.remove(taskId);
        }
    }

    @Override
    public void onDownload(DownloadTask task) {
        if (task.mHasNotification && mNotificationManager != null) {
            mNotificationManager.update(task);
        }
        if (task.mState == DownloadTask.STATE_CANCEL) { // 下载取消
            String taskId = getTaskId(task.mType, task.mId);
            synchronized (this) {
                mCurrentTasks.remove(taskId);
                mTaskMap.remove(taskId);
                Log.d("DownloadScheduler", "移除Task:" + taskId);
                if (mTaskMap.isEmpty()) {
                    doUnbindService();
                } else {
                    pollingDownloadTasks();
                }
            }
        }
    }

    private synchronized void doBindService() {
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.startService(intent);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private synchronized void doUnbindService() {
        mHandler.removeCallbacks(mUnbindServiceRunnable);
        mHandler.postDelayed(mUnbindServiceRunnable, DownloadConst.SERVICE_LEFT_TIME);
    }

    public synchronized DownloadTask getDownloadTask(String type, int id) {
        return mTaskMap.get(getTaskId(type, id));
    }

    private static String getTaskId(String type, int id) {
        return type + "|" + id;
    }

    public void setNotificationManager(IDownloadNotification notificationManager) {
        mNotificationManager = notificationManager;
    }
}
