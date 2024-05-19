package net.xdclass.video.conf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DownloadProgressManager {
    private static final ConcurrentMap<String, Double> progressMap = new ConcurrentHashMap<>();

    public static void setProgress(String taskId, double progress) {
        progressMap.put(taskId, progress);
    }

    public static Double getProgress(String taskId) {
        return progressMap.get(taskId);
    }

    public static void removeProgress(String taskId) {
        progressMap.remove(taskId);
    }
}
