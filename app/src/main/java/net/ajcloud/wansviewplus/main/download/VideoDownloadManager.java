package net.ajcloud.wansviewplus.main.download;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.SparseArray;

import net.ajcloud.wansviewplus.entity.camera.Camera;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.download.database.VideoDownloadDBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.videolan.libvlc.VideoDownloadBean;
import org.videolan.libvlc.VideoDownloadControler;
import org.videolan.libvlc.VideoDownloadWatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created
 */
public class VideoDownloadManager implements VideoDownloadWatcher {

    // 等待状态
    public static final int DOWNLOAD_RAW = 0;
    // 下载文件初始化状态
    public static final int DOWNLOAD_INIT = 1;
    // 下载文件状态
    public static final int DOWNLOAD_DOWNLOADING = 2;
    // 文件下载完成后转码的状态
    public static final int DOWNLOAD_CODING = 3;
    // 下载成功的状态
    public static final int DOWNLOAD_SUCCEED = 4;
    // 下载失败时返回的状态
    public static final int DOWNLOAD_FAILED = 5;
    // 主动停掉下载时返回的状态
    public static final int DOWNLOAD_TERMINATED = 6;
    // 主动查询下载状态时，未获取到匹配的状态
    public static final int DOWNLOAD_UNMATCHED = 7;
    // 下载的文件已过期或不存在
    public static final int DOWNLOAD_NOTFOUND = 8;
    // 正在下载时，退出app，再次进入时置为暂停状态
    public static final int DOWNLOAD_PAUSE = 9;

    public static final int STOP_SUCCEED = 0;
    public static final int STOP_FAILED = -1;

    // 下载完成，通知页面刷新
    public static final int REFRESH_LIST = 1;
    // 下载中，通知页面刷新
    public static final int REFRESH_ITEM = 2;

    private static VideoDownloadManager manager;
    private VideoDownloadControler controler;
    private VideoDownloadDBHelper helper;
    private final static Object syncObj = new Object();
    private ExecutorService pool;
    private LinkedBlockingQueue queue = new LinkedBlockingQueue<DownloadRunnable>();
    private SparseArray<VideoDownloadBean> list = new SparseArray<VideoDownloadBean>();
    private Handler uiHandler;

    public static final int MAX_THREAD_NUM = 1;
    public static final int MAX_NUM = 5;
    public static final String downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera" + File.separator;

    private VideoDownloadManager() {
        controler = new VideoDownloadControler();
        helper = new VideoDownloadDBHelper(MainApplication.getApplication());
        pool = new ThreadPoolExecutor(MAX_THREAD_NUM, MAX_THREAD_NUM,
                0L, TimeUnit.MILLISECONDS, queue);
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static VideoDownloadManager getInstance() {
        if (null == manager) {
            synchronized (syncObj) {
                manager = new VideoDownloadManager();
            }
        }
        return manager;
    }

    // 把要下载的文件，添加到下载队列
    public void download(VideoDownloadBean bean) {
        if (null == bean) {
            return;
        }
        bean.setDownloadState(DOWNLOAD_RAW);
        if (null != list.get(bean.getId())) {
            helper.update(bean);
        } else {
            list.put(bean.getId(), bean);
            helper.insert(bean);
        }

        if (bean.getType() == 0) {
            downloadPicFile(bean.getPicUrl(), bean.getPicFilePath());
            pool.execute(new DownloadRunnable(bean));
        } else if (bean.getType() == 1 || bean.getType() == 2) {
            copyPictureFile(bean);
            pool.execute(new DownloadTFCardVideoRunnable(bean));
        }
        if (null != uiHandler) {
            Message msg = uiHandler.obtainMessage();
            msg.what = REFRESH_ITEM;
            msg.obj = bean;
            uiHandler.sendMessage(msg);
        }
    }

    //先获取TF卡/NAS设备视频下载地址后在开始下载视频
    public void getTFCardVideoDownloadUrl(final VideoDownloadBean bean) {
        /*
        DevHost host = AppApplication.devHostPresenter.getDevHost(bean.getOid());
        if (null != host) {
            final String LocalStoreType;
            Camera camera = (Camera) host.getResideUserData();
            if (camera.getStorageSetting().getType().equals("0")) {
                LocalStoreType = "tf";
            } else {
                LocalStoreType = "nas";
            }
            if (Utils.isCameraInLan(bean.getOid(), camera.getCameraState().getGwmac()) &&
                    !TextUtils.isEmpty(camera.getCameraState().getLocalip())) {
                //局域网下载
                String tmpUrl = "http://" + camera.getCameraState().getLocalip() + ":52869/web/" + LocalStoreType + "/" + bean.getStream();
                if (!TextUtils.isEmpty(camera.getCameraState().getLocalstoragehost())) {
                    tmpUrl = camera.getCameraState().getLocalstoragehost() + "web/" + LocalStoreType + "/" + bean.getStream();
                }
                bean.setDownloadUrl(tmpUrl);
                downloadTFCardVideo(bean);
            } else {
                //
                synchronized (syncObj) {
                    HttpAdapterManger.getCameraRequest().getToken(AppApplication.devHostPresenter.getDevHost(bean.getOid()), 9, 3, -1,
                            new ZResponse(CameraRequest.GetStreamTokenUrl, new ResponseListener() {
                                @Override
                                public void onSuccess(String s, Object o) {
                                    CameraToken cameraToken = (CameraToken) o;
                                    String reqserver = cameraToken.getReqserver();
                                    if (reqserver.startsWith("http")) {
                                        bean.setDownloadUrl(reqserver + "/web/" + LocalStoreType + "/" + bean.getStream());
                                    } else {
                                        bean.setDownloadUrl("http://" + reqserver + "/web/" + LocalStoreType + "/" + bean.getStream());
                                    }
                                    synchronized (syncObj) {
                                        syncObj.notify();
                                    }
                                }

                                @Override
                                public void onError(String s, int i) {
                                    bean.setDownloadState(DOWNLOAD_FAILED);
                                    synchronized (syncObj) {
                                        syncObj.notify();
                                    }
                                }
                            }));
                    try {
                        syncObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    downloadTFCardVideo(bean);
                }
            }
        } else {
            bean.setDownloadState(DOWNLOAD_FAILED);
            downloadTFCardVideo(bean);
        }
        */
    }

    // 获取下载的状态
    public VideoDownloadBean getStatus(int id) {
        return controler.getStatus(id);
    }

    // 停止正在下载的文件
    public void stop(int id) {
        VideoDownloadBean data = list.get(id);
        if (null != data) {
            data.setDownloadState(DOWNLOAD_TERMINATED);
            if (data.getType() == 0) {
                controler.stop(id);
            } else {
                downloadState(data);
            }
        }
    }

    // 切换账号时停掉当前所有下载
    public void stopAll() {
        ArrayList<VideoDownloadBean> datas = helper.query();
        for (VideoDownloadBean data : datas) {
            if (data.getDownloadState() == DOWNLOAD_RAW || data.getDownloadState() == DOWNLOAD_INIT
                    || data.getDownloadState() == DOWNLOAD_DOWNLOADING || data.getDownloadState() == DOWNLOAD_CODING) {
                stop(data.getId());
            }
        }
    }

    // 下载状态的回调
    @Override
    public void downloadState(VideoDownloadBean bean) {
        VideoDownloadBean tmpData = list.get(bean.getId());
        if (null == tmpData) {
            return;
        }
        tmpData.setDownloadState(bean.getDownloadState());
        tmpData.setDownloadProgress(bean.getDownloadProgress());
        helper.update(tmpData);

        if (DOWNLOAD_SUCCEED == bean.getDownloadState()) {
            relevanceAlbum(tmpData.getDownloadFilePath());
        }

        if (null != uiHandler) {
            Message msg = uiHandler.obtainMessage();
            if (DOWNLOAD_SUCCEED == bean.getDownloadState()) {
                msg.what = REFRESH_LIST;
            } else {
                msg.what = REFRESH_ITEM;
            }
            msg.obj = bean;
            uiHandler.sendMessage(msg);
        }
    }
    //云存储下载
    class DownloadRunnable implements Runnable {

        VideoDownloadBean downloadObject;

        public DownloadRunnable(VideoDownloadBean bean) {
            downloadObject = bean;
        }

        @Override
        public void run() {
            if (DOWNLOAD_RAW == downloadObject.getDownloadState()) {
                controler.start(downloadObject.getId(), downloadObject.getDownloadUrl(),
                        downloadObject.getDownloadFilePath(),
                        downloadObject.getDuration(),
                        downloadObject.getFps(),
                        VideoDownloadManager.this);
            } else {
                downloadState(downloadObject);
            }
        }
    }

    // TF卡/NAS下载
    class DownloadTFCardVideoRunnable implements Runnable {

        VideoDownloadBean downloadObject;

        public DownloadTFCardVideoRunnable(VideoDownloadBean bean) {
            downloadObject = bean;
        }

        @Override
        public void run() {
            copyPictureFile(downloadObject);
            if (DOWNLOAD_RAW == downloadObject.getDownloadState()) {
                getTFCardVideoDownloadUrl(downloadObject);
            } else {
                downloadState(downloadObject);
            }
        }

    }

    private void downloadTFCardVideo(VideoDownloadBean downloadObject) {
        if (DOWNLOAD_RAW == downloadObject.getDownloadState()) {
            String downloadUrl = downloadObject.getDownloadUrl();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(downloadUrl);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                long totalCount = httpResponse.getEntity().getContentLength();
                StatusLine statusLine = httpResponse.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    File file = new File(downloadObject.getDownloadFilePath());
                    FileOutputStream outputStream = new FileOutputStream(file);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    byte b[] = new byte[4096];
                    int j;
                    long lastDownloadSize = 0;
                    long curDownloadSize = 0;
                    long onePersonSize = totalCount / 100;
                    downloadObject.setDownloadState(DOWNLOAD_DOWNLOADING);
                    while (downloadObject.getDownloadState() == DOWNLOAD_DOWNLOADING && (j = inputStream.read(b)) != -1) {
                        outputStream.write(b, 0, j);
                        curDownloadSize += j;
                        if (totalCount > 0 && (curDownloadSize - lastDownloadSize) >= onePersonSize) {
                            downloadObject.setDownloadProgress((int) (curDownloadSize * 100 / totalCount));
                            downloadState(downloadObject);
                            lastDownloadSize = curDownloadSize;
                        }
                    }

                    if (totalCount == curDownloadSize) {
                        downloadObject.setDownloadProgress(100);
                        downloadObject.setDownloadState(DOWNLOAD_SUCCEED);
                        downloadState(downloadObject);
                    } else {
                        downloadObject.setDownloadState(DOWNLOAD_FAILED);
                        downloadState(downloadObject);
                    }

                    outputStream.flush();
                    outputStream.close();
                } else {
                    downloadObject.setDownloadState(DOWNLOAD_FAILED);
                    downloadState(downloadObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
                downloadObject.setDownloadState(DOWNLOAD_FAILED);
                downloadState(downloadObject);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
        } else {
            downloadState(downloadObject);
        }
    }

    // 获取下载队列
    public SparseArray<VideoDownloadBean> getList() {
        return list;
    }

    // 初始化下载队列
    public void initDownloadList() {
        list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<VideoDownloadBean> datas = helper.query();
                for (VideoDownloadBean data : datas) {
                    if (data.getDownloadState() != DOWNLOAD_SUCCEED) {
                        data.setDownloadProgress(0);
                        deleteFile(data.getDownloadFilePath());
                        if (data.getDownloadState() == DOWNLOAD_RAW || data.getDownloadState() == DOWNLOAD_INIT
                                || data.getDownloadState() == DOWNLOAD_DOWNLOADING || data.getDownloadState() == DOWNLOAD_CODING) {
                            data.setDownloadState(DOWNLOAD_PAUSE);
                            helper.update(data);
                        }
                    }
                    list.put(data.getId(), data);
                }
            }
        }).start();
    }

    // 获取所有的下载列表
    private ArrayList<VideoDownloadBean> getDownloadList() {
        return helper.query();
    }

    // 从数据中删除该条数据，并删除下载文件
    public void delete(VideoDownloadBean bean) {
        if (null == bean) {
            return;
        }
        stop(bean.getId());
        list.remove(bean.getId());
        helper.delete(bean);
        deleteFile(bean, null);
    }

    // 从数据库中删除该list中的所有数据，并删除对应的下载文件
    public void delete(final ArrayList<VideoDownloadBean> mList) {
        if (null == mList || mList.isEmpty()) {
            return;
        }

        for (VideoDownloadBean bean : mList) {
            stop(bean.getId());
            list.remove(bean.getId());
        }
        helper.deleteList(mList);
        deleteFile(null, new ArrayList<VideoDownloadBean>(mList));
    }

    // 异步删除下载文件，避免同步删除文件造成ANR
    private void deleteFile(final VideoDownloadBean bean, final ArrayList<VideoDownloadBean> mList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != bean) {
                    VideoDownloadManager.this.deleteFile(bean.getDownloadFilePath());
                    VideoDownloadManager.this.deleteFile(bean.getPicFilePath());
                }

                if (null != mList) {
                    for (VideoDownloadBean bean : mList) {
                        VideoDownloadManager.this.deleteFile(bean.getDownloadFilePath());
                        VideoDownloadManager.this.deleteFile(bean.getPicFilePath());
                    }
                }
            }
        }).start();

    }

    private void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        File file = new File(filePath);
        if (null != file && file.exists()) {
            file.delete();
        }
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    // 判断要下载的文件是否已存在下载队列
    public boolean isExist(String stream) {
        ArrayList<VideoDownloadBean> dataList = getDownloadList();
        if (dataList.isEmpty()) {
            return false;
        }

        for (VideoDownloadBean value : dataList) {
            if (stream.equals(value.getStream())) {
                if (DOWNLOAD_FAILED == value.getDownloadState() || DOWNLOAD_TERMINATED == value.getDownloadState()
                        || DOWNLOAD_PAUSE == value.getDownloadState()) {
                    delete(value);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    // 获取当前正在下载的个数
    public int getDownloadingNum() {
        int downloadingNum = 0;
        ArrayList<VideoDownloadBean> dataList = getDownloadList();
        for (VideoDownloadBean bean : dataList) {
            if (VideoDownloadManager.DOWNLOAD_SUCCEED != bean.getDownloadState()) {
                downloadingNum++;
            }
        }
        return downloadingNum;
    }

    private void relevanceAlbum(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        MainApplication.getApplication().sendBroadcast(intent);
    }

    private boolean copyPictureFile(VideoDownloadBean bean) {
        try {
            String toFile = MainApplication.fileIO.getImageFileDirectory(bean.getOid());
            File fFile = new File(bean.getPicFilePath());
            if (fFile.exists()) {
                toFile += fFile.getName();
            } else {
                return false;
            }

            if (bean.getPicFilePath().equals(toFile)) {
                return true;
            }

            File dFile = new File(toFile);
            if (!dFile.exists()) {
                dFile.createNewFile();
            } else {
                dFile.delete();
                dFile.createNewFile();
            }
            FileInputStream fosfrom = new FileInputStream(fFile);
            FileOutputStream fosto = new FileOutputStream(dFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            bean.setPicFilePath(dFile.getAbsolutePath());
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private String getPlayUrl(VideoDownloadBean bean) {
        /*
        Map<String, String> params = new HashMap<>();
        params.put("sid", AppApplication.access_token);
        params.put("oid", bean.getOid());
        params.put("stream", bean.getStream());
        String url = AppApplication.getInstance().getServerInfo() != null ? AppApplication.getInstance().getServerInfo().getM3u8url() : null;

        return url + CameraRequest.GetTsInfo + "?" + ZTELib.getInstence().getBusinessQuery(CameraRequest.GetTsInfo,
                AppApplication.devHostPresenter.getDevHost(bean.getOid()).getAk(), params);*/
        return "";
    }

    public String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MainApplication.getApplication(), blockSize * availableBlocks);
    }

    private void downloadPicFile(final String downloadUrl, String filePath) {
        if (!TextUtils.isEmpty(downloadUrl) && !TextUtils.isEmpty(filePath)) {
            final File destDir = new File(filePath);
            if (destDir.exists()) {
                return;
            }

            final File finalDir = new File(filePath + "tmp");
            try {
                finalDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //创建一个URL对象
                    URL url = null;
                    try {
                        url = new URL(downloadUrl);
                        //创建一个HttpURLConnection
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        //获取一个InputStream
                        InputStream inputStream = urlConn.getInputStream();
                        //创建一个文件输出流
                        OutputStream output = new FileOutputStream(finalDir);
                        //建立缓冲
                        byte[] buffer = new byte[1 * 1024];
                        //Stream读取索引
                        int downloadNum = 0;
                        //将输入流写到文件当中
                        while ((downloadNum = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, downloadNum);
                        }
                        output.flush();
                        output.close();
                        inputStream.close();
                        finalDir.renameTo(destDir);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // 退出应用关闭线程池，释放资源
    public void exit() {
        pool.shutdown();
        manager = null;
    }
}
