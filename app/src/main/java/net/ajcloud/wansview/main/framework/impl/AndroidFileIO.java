package net.ajcloud.wansview.main.framework.impl;

import android.content.res.AssetManager;
import android.os.Environment;

import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.main.framework.FileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AndroidFileIO implements FileIO {
    AssetManager assets;
    String storagePath;


    public AndroidFileIO(AssetManager assets) {
        this.assets = assets;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            this.storagePath = MainApplication.getApplication().getApplicationContext().getFilesDir().getAbsolutePath()
                    + File.separator;
        else {
            this.storagePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + MainApplication.getApplication().getApplicationContext().getPackageName()
                    + File.separator;
        }
    }

    @Override
    public InputStream readAsset(String fileName) throws IOException {
        return assets.open(fileName);
    }

    @Override
    public InputStream readFile(String fileName) throws IOException {
        return new FileInputStream(storagePath + fileName);
    }

    @Override
    public OutputStream writeFile(String fileName) throws IOException {
        return new FileOutputStream(storagePath + fileName);
    }

    @Override
    public String getAudioFileDirectory(String camera_id) {
        String path = storagePath + "Audio" + File.separator + camera_id + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getVideoFileDirectory(String camera_id) {
        String path = storagePath + "Video" + File.separator + camera_id;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getVideoDownloadFileDirectory(String camera_id) {
        String path = storagePath + "Video" + File.separator + camera_id + File.separator + "download";
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getImageFileDirectory(String camera_id) {
        String path = storagePath + "Image" + File.separator + camera_id + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getAlarmImageDirectory() {
        String path = storagePath + "AlarmImage" + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getLibraryFileDirectory() {
        String path = storagePath + "Library" + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getDownloadAppDirectory() {
        String path = storagePath + "Download" + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getRealTimePictureDirectory(String camera_id) {
        String path = storagePath + "RealTimePicture" + File.separator + camera_id;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getCacheDir() {
        File sdCache = MainApplication.getApplication().getApplicationContext().getExternalCacheDir();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || sdCache == null)
            return MainApplication.getApplication().getApplicationContext().getCacheDir().toString();
        else {
            return sdCache.toString() + File.separator;
        }
    }

    @Override
    public String getRsyncDownloadpath(String camera_id) {
        String path = storagePath + "LocalStore" + File.separator + camera_id;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getTempFilePath(String camera_id) {
        String path = storagePath + "temp" + File.separator + camera_id;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getBackupThumbnailPath(String mac) {
        String path = storagePath + ".AlbumThumbnail" + File.separator + mac + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    @Override
    public String getBackupRootPath() {
        String path = storagePath + ".AlbumThumbnail" + File.separator;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }
}
