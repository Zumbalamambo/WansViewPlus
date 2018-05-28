package org.videolan.libvlc;

/**
 * Created by HW on 2016/8/4.
 */
public class VideoDownloadControler {
    static {
        System.loadLibrary("vlcjni");
    }

    public VideoDownloadControler() {
    }

    public native boolean start(int id, String downloadUrl, String downloadFilePath, double duration, int fps, VideoDownloadWatcher watcher);

    public native int stop(int id);

    public native VideoDownloadBean getStatus(int id);
}
