package net.xdclass.video.crawler.listener;

public interface DownloadListener {

    void start();

    void process(String downloadUrl, int finished, int sum, float percent);

    void speed(String speedPerSecond);

    void end();

}
