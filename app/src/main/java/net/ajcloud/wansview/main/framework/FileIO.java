package net.ajcloud.wansview.main.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {
    InputStream readAsset(String fileName) throws IOException;

    InputStream readFile(String fileName) throws IOException;

    OutputStream writeFile(String fileName) throws IOException;

    String getAudioFileDirectory(String camera_id);

    String getVideoFileDirectory(String camera_id);

    String getVideoDownloadFileDirectory(String camera_id);

    String getImageFileDirectory(String camera_id);

    String getAlarmImageDirectory();

    String getLibraryFileDirectory();

    String getDownloadAppDirectory();

    String getRealTimePictureDirectory(String camera_id);

    String getCacheDir();
    String getRsyncDownloadpath(String camera_id);

    String getTempFilePath(String camera_id);

    String getBackupThumbnailPath(String mac);
    String getBackupRootPath();

}
