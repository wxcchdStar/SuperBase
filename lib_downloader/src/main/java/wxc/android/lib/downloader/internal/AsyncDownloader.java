package wxc.android.lib.downloader.internal;

import android.content.Context;
import android.os.Process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;
import wxc.android.lib.downloader.DownloadException;
import wxc.android.lib.downloader.DownloadTask;
import wxc.android.lib.downloader.IDownloader;

public class AsyncDownloader implements IDownloader, Runnable {
    // 反馈下载进度的时间间隔
    private final static long TIME_STEP = 1000; // ms
    // 读写缓冲区大小
    private final static int BUFFER_SIZE = 8 * 1024; // 8k

    private Context mContext;
    private DownloadTask mDownloadTask;
    private volatile boolean mCancel;

    // 计算下载时间
    private long mConsumeTime;

    public AsyncDownloader(Context ctx, DownloadTask task) {
        mContext = ctx.getApplicationContext();
        mDownloadTask = task;
    }

    @Override
    public void startDownload() {
        Timber.d( "startDownload：%s", mDownloadTask.mDownloadUrl);
        DownloadThreadPool.executeTask(this);
    }

    @Override
    public synchronized void cancelDownload() {
        mCancel = true;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            onPreDownload();
            if (mCancel) return;
            onDownloading();
            if (mCancel) return;
            onPostDownload();
        } catch (Exception e) {
            if (e instanceof DownloadException) {
                Timber.w( "下载失败，原因：" + ((DownloadException) e).mCode + ", " + e.getMessage());
                mDownloadTask.mState = DownloadTask.STATE_FAILED;
                mDownloadTask.notifyListener();
            }
        } finally {
            // 在下载任务结束之前取消了下载任务
            if (mCancel && mDownloadTask.mState != DownloadTask.STATE_FINISH
                    && mDownloadTask.mState != DownloadTask.STATE_FAILED) {
                mDownloadTask.mState = DownloadTask.STATE_CANCEL;
                mDownloadTask.notifyListener();
            }
        }
    }

    private void onPreDownload() {
        mConsumeTime = System.currentTimeMillis();
        mDownloadTask.mState = DownloadTask.STATE_START;
        mDownloadTask.notifyListener();
        Timber.d("准备下载：%s", mDownloadTask.mDownloadUrl);
    }

    private void onDownloading() throws DownloadException {
        // 如果网络不可用, 则通知结果直接返回
        if (!DownloadUtils.isConnected(mContext)) {
            throw new DownloadException(DownloadException.CODE_NETWORK_UNAVAILABLE, null);
        }

        if (mCancel) return; // 检测任务是否取消

        URL downloadUrl;
        try {
            downloadUrl = new URL(mDownloadTask.mDownloadUrl);
        } catch (MalformedURLException e) {
            throw new DownloadException(DownloadException.CODE_URL_UNAVAILABLE, "Url: " + mDownloadTask.mDownloadUrl);
        }

        if (mCancel) return; // 检测任务是否取消

        long startPosition = 0;
        String tempSavePath = mDownloadTask.mSavePath + DownloadConst.TEMP_FILE_SUFFIX;
        File downloadFile = new File(tempSavePath);
        // 不支持断点续传, 则删除已有文件
        if (!mDownloadTask.mIsBreakpointSupported) {
            if (downloadFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                downloadFile.delete();
            }
        }

        if (mCancel) return; // 检测任务是否取消

        if (downloadFile.exists()) { // 文件存在，断点续传
            startPosition = downloadFile.length();
        } else { // 文件不存在
            // 创建目录
            File parentFile = downloadFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                synchronized (AsyncDownloader.class) {
                    boolean mkDirsSuccess = parentFile.mkdirs();
                    if (!mkDirsSuccess) { // 创建目录失败
                        Timber.w("创建目录失败，%s", parentFile.getPath());
                        // 创建目录失败，不是存储空间不足就是没有写权限
                        if (!DownloadUtils.isStorageEnough(parentFile.getPath(), 0)) {
                            throw new DownloadException(DownloadException.CODE_STORAGE_NOT_ENOUGH, "file size: " + mDownloadTask.mFileSize);
                        } else {
                            throw new DownloadException(DownloadException.CODE_FILE_SYSTEM_READ_ONLY, null);
                        }
                    }
                }
            }
            // 创建文件
            try {
                boolean isSuccess = downloadFile.createNewFile();
                if (!isSuccess) {
                    throw new IOException("File create failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Timber.w(downloadFile.getAbsolutePath() + "-文件创建失败，原因：" + e.getLocalizedMessage());
                // 文件创建失败，不是存储空间不足就是没有写权限
                if (!DownloadUtils.isStorageEnough(downloadFile.getParent(), mDownloadTask.mFileSize)) {
                    throw new DownloadException(DownloadException.CODE_STORAGE_NOT_ENOUGH, "file size: " + mDownloadTask.mFileSize);
                } else {
                    throw new DownloadException(DownloadException.CODE_FILE_SYSTEM_READ_ONLY, null);
                }
            }
        }

        if (mCancel) return; // 检测任务是否取消

        // 下载文件
        RandomAccessFile raf = null;
        HttpURLConnection connection = null;
        try {
            raf = new RandomAccessFile(downloadFile, "rw");
            raf.seek(startPosition);

            connection = openConnection(downloadUrl);
            if (startPosition > 0) { // 断点续传
                connection.addRequestProperty("Range", "bytes=" + startPosition + "-");
            }

            if (mCancel) return; // 检测任务是否取消

            // 正在连接
            mDownloadTask.mState = DownloadTask.STATE_CONNECTING;
            mDownloadTask.notifyListener();
            int retry = 0;
            boolean isConnected = false;
            while (!isConnected) {
                try {
                    connection.connect();
                    isConnected = true;
                } catch (IOException e) {
                    // 链接超时
                    retry++;
                    Timber.d("连接超时，第%d次尝试，URL:%s", retry, mDownloadTask.mDownloadUrl);
                    if (retry == DownloadConst.DEFAULT_MAX_RETRY_COUNT) {
                        throw new DownloadException(DownloadException.CODE_CONNECTION_TIMEOUT, null);
                    }
                }

                if (mCancel) return; // 检测任务是否取消
            }

            // 下载中
            int status = connection.getResponseCode();
            if (status < HttpURLConnection.HTTP_BAD_REQUEST) { // 小于400认为是正常
                checkRedirect(downloadUrl, connection);

                Timber.d( "连接成功，开始下载数据：%s", mDownloadTask.mDownloadUrl);
                mDownloadTask.mDownloadedSize = startPosition;
                mDownloadTask.mState = DownloadTask.STATE_DOWNLOADING;

                InputStream in = connection.getInputStream();
                if (mDownloadTask.mFileSize <= 0) {
                    mDownloadTask.mFileSize = connection.getContentLength();
                    if (startPosition > 0) {
                        mDownloadTask.mFileSize += startPosition;
                    }
                    mDownloadTask.notifyListener();
                }

                if (mCancel) return; // 检测任务是否取消

                byte[] buffer = new byte[BUFFER_SIZE];
                int rlen;
                long stepTime = System.currentTimeMillis();
                while ((rlen = in.read(buffer)) != -1) {
                    raf.write(buffer, 0, rlen);
                    mDownloadTask.mDownloadedSize += rlen;

                    if (System.currentTimeMillis() - stepTime > TIME_STEP) {
                        stepTime = System.currentTimeMillis();
                        mDownloadTask.mProgress = (int) (100 * mDownloadTask.mDownloadedSize / mDownloadTask.mFileSize);
                        mDownloadTask.notifyListener();
                        Timber.d("下载中，进度：%s", mDownloadTask.mProgress);
                    }

//                    Thread.sleep(1000);
                    if (mCancel) return; // 检测任务是否取消
                }

                if (mCancel) return; // 检测任务是否取消

                if (mDownloadTask.mDownloadedSize >= mDownloadTask.mFileSize) {
                    if (mCancel) return; // 检测任务是否取消

                    if (downloadFile.renameTo(new File(mDownloadTask.mSavePath))) {
                        mDownloadTask.mState = DownloadTask.STATE_FINISH;
                        mDownloadTask.notifyListener();
                    } else {
                        throw new DownloadException(DownloadException.CODE_NOT_DETERMINED, "File rename failed: " + mDownloadTask.mSavePath);
                    }
                }
            } else if (status == 416) { // 响应是416, SC_REQUESTED_RANGE_NOT_SATISFIABLE
                if (mCancel) return; // 检测任务是否取消
                if (downloadFile.renameTo(new File(mDownloadTask.mSavePath))) {
                    mDownloadTask.mState = DownloadTask.STATE_FINISH;
                    mDownloadTask.notifyListener();
                } else {
                    throw new DownloadException(DownloadException.CODE_NOT_DETERMINED, "File rename failed: " + downloadFile.getAbsolutePath());
                }
            } else {
                Timber.w( "HTTP错误：%s", status);
                throw new DownloadException(status, "HTTP ERROR: " + status);
            }
        } catch (Exception e) {
            // FileNotFoundException IOException
            throw new DownloadException(DownloadException.CODE_NOT_DETERMINED, e.getLocalizedMessage());
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void onPostDownload() {
        mDownloadTask.mState = DownloadTask.STATE_FINISH;
        mDownloadTask.notifyListener();

        mConsumeTime = System.currentTimeMillis() - mConsumeTime;
        Timber.d( "下载完成时间：" + (mConsumeTime / 1000.0f) + "s");
    }

    private void checkRedirect(URL downloadUrl, HttpURLConnection connection) throws DownloadException {
        if (!downloadUrl.getHost().equals(connection.getURL().getHost())) {
            throw new DownloadException(DownloadException.CODE_CONNECTION_REDIRECT, "URL redirected");
        }
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(DownloadConst.CONNECTION_TIMEOUT);
        connection.setReadTimeout(DownloadConst.READ_TIMEOUT);
        // 默认：Connection：keep-alive
        // 默认：Accept-Encoding：gzip
        return connection;
    }
}
