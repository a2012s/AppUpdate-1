package com.open.hule.library.downloadmanager;

import android.app.DownloadManager;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.open.hule.library.utils.UpdateManager;

import java.lang.ref.WeakReference;

/**
 * @author hule
 * @date 2019/7/15 16:43
 * description:下载监听handler
 */
public class DownloadHandler extends Handler {

    private final WeakReference<UpdateManager> wrfUpdateManager;

    public DownloadHandler(UpdateManager updateManager) {
        wrfUpdateManager = new WeakReference<>(updateManager);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case DownloadManager.STATUS_PAUSED:
                // 暂停
                wrfUpdateManager.get().setState(DownloadManager.STATUS_PAUSED);
                break;
            case DownloadManager.STATUS_PENDING:
                // 开始
                wrfUpdateManager.get().setState(DownloadManager.STATUS_PENDING);
                break;
            case DownloadManager.STATUS_RUNNING:
                // 下载中
                wrfUpdateManager.get().setState(DownloadManager.STATUS_RUNNING);
                if (wrfUpdateManager.get() != null) {
                    wrfUpdateManager.get().setProgress(msg.arg1);
                }
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                wrfUpdateManager.get().setState(DownloadManager.STATUS_SUCCESSFUL);
                if (wrfUpdateManager.get() != null) {
                    wrfUpdateManager.get().setProgress(100);
                    wrfUpdateManager.get().unregisterContentObserver();
                }
                if (wrfUpdateManager.get() != null) {
                    wrfUpdateManager.get().installApp(wrfUpdateManager.get().getDownloadFile());
                }
                break;
            case DownloadManager.STATUS_FAILED:
                wrfUpdateManager.get().setState(DownloadManager.STATUS_FAILED);
                // 下载失败，清除本次的下载任务
                if (wrfUpdateManager.get() != null) {
                    wrfUpdateManager.get().clearCurrentTask();
                    wrfUpdateManager.get().unregisterContentObserver();
                    wrfUpdateManager.get().showFail();
                }
                break;
            default:
                break;
        }
    }
}
