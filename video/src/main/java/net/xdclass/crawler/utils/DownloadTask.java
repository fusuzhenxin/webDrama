package net.xdclass.crawler.utils;

import net.xdclass.video.conf.DownloadProgressManager;

public class DownloadTask {
    private int lastProgress = -1; // 将 lastProgress 作为实例变量

    public void updateProgress(long downloadedSize, long totalSize, String taskId) {
        // 计算进度并发送到前端
        double progress = (double) downloadedSize / totalSize * 100;
        int progressInt = (int) Math.round(progress); // 将进度四舍五入并转换为整数

        if (progressInt != lastProgress) {
            DownloadProgressManager.setProgress(taskId, progressInt);
            System.out.println(progressInt);
            lastProgress = progressInt; // 更新上次发送的进度值
        }
    }
}
