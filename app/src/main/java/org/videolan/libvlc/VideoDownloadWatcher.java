package org.videolan.libvlc;

/**
 * Created by HW on 2016/8/4.
 */
public interface VideoDownloadWatcher {
    void downloadState(VideoDownloadBean bean);
}
