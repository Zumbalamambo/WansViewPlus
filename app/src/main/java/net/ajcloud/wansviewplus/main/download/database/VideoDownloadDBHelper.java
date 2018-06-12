package net.ajcloud.wansviewplus.main.download.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import net.ajcloud.wansviewplus.main.application.MainApplication;

import org.videolan.libvlc.VideoDownloadBean;

import java.util.ArrayList;

/**
 * Created by HW on 2016/8/10.
 */
public class VideoDownloadDBHelper extends SQLiteOpenHelper {

    private final static String _ID = "id";
    // 视频id编号
    private final static String ID = "Uid";
    // 视频文件名称
    private final static String NAME = "name";
    // 视频文件图片地址
    private final static String IMAGE_URL = "imageUrl";
    // 视频文件下载地址
    private final static String VIDEO_URL = "videoUrl";
    // 视频文件保存地址
    private final static String FILE_PATH = "filePath";
    // 视频当前下载状态
    private final static String STATE = "state";
    // 视频下载进度
    private final static String PROGRESS = "progress";
    // 视频时长
    private final static String DURATION = "duration";
    // 视频开始时间
    private final static String START_TIME = "startTime";
    // 视频文件唯一标示，用于向服务器重新获取url时使用
    private final static String STREAM = "stream";
    // 视频对应的设备oid
    private final static String OID = "oid";
    // 用户名
    private final static String RESERVE1 = "reserve1";
    // 图片文件下载地址
    private final static String RESERVE2 = "reserve2";
    private final static String RESERVE3 = "reserve3";
    private final static String RESERVE4 = "reserve4";
    private final static String RESERVE5 = "reserve5";
    // 视频来源
    private final static String RESERVE6 = "reserve6";
    private final static String RESERVE7 = "reserve7";
    private final static String RESERVE8 = "reserve8";

    private final static String DATABASE_NAME = "videodownload";
    private final static int DATABASE_VERSION = 1;

    public VideoDownloadDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + "(" +
                _ID + " INTEGER primary key autoincrement," +
                ID + " INTEGER," +
                NAME + " text," +
                IMAGE_URL + " text," +
                VIDEO_URL + " text," +
                FILE_PATH + " text," +
                STATE + " INTEGER," +
                PROGRESS + " INTEGER," +
                DURATION + " REAL," +
                START_TIME + " text," +
                STREAM + " text," +
                OID + " text," +
                RESERVE1 + " text," +
                RESERVE2 + " text," +
                RESERVE3 + " text," +
                RESERVE4 + " text," +
                RESERVE5 + " text," +
                RESERVE6 + " INTEGER," +
                RESERVE7 + " INTEGER," +
                RESERVE8 + " INTEGER" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public ArrayList<VideoDownloadBean> query() {
        ArrayList<VideoDownloadBean> lists = new ArrayList<VideoDownloadBean>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_NAME, null, null, null, null, null, null);
        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(RESERVE1));
                if (true/*MainApplication.UserName.equals(username)*/) {
                    VideoDownloadBean data = new VideoDownloadBean();
                    data.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                    data.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                    data.setStartTime(cursor.getString(cursor.getColumnIndex(START_TIME)));
                    data.setDuration(cursor.getDouble(cursor.getColumnIndex(DURATION)));
                    data.setPicUrl(cursor.getString(cursor.getColumnIndex(IMAGE_URL)));
                    data.setDownloadUrl(cursor.getString(cursor.getColumnIndex(VIDEO_URL)));
                    data.setDownloadFilePath(cursor.getString(cursor.getColumnIndex(FILE_PATH)));
                    data.setDownloadProgress(cursor.getInt(cursor.getColumnIndex(PROGRESS)));
                    data.setDownloadState(cursor.getInt(cursor.getColumnIndex(STATE)));
                    data.setStream(cursor.getString(cursor.getColumnIndex(STREAM)));
                    data.setOid(cursor.getString(cursor.getColumnIndex(OID)));
                    data.setUserName(username);
                    data.setPicFilePath(cursor.getString(cursor.getColumnIndex(RESERVE2)));
                    data.setType(cursor.getInt(cursor.getColumnIndex(RESERVE6)));
                    data.setFps(cursor.getInt(cursor.getColumnIndex(RESERVE7)));
                    lists.add(data);
                }
            }
        }
        return lists;
    }


    public void insert(VideoDownloadBean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_NAME, null, convertObject(value));
    }

    public void insertList(ArrayList<VideoDownloadBean> value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (VideoDownloadBean bean : value) {
            db.insert(DATABASE_NAME, null, convertObject(bean));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update(VideoDownloadBean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DATABASE_NAME, convertObject(value), ID + "=?", new String[]{String.valueOf(value.getId())});
    }

    public void delete(VideoDownloadBean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_NAME, ID + "=?", new String[]{String.valueOf(value.getId())});
    }

    public void deleteList(ArrayList<VideoDownloadBean> value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (VideoDownloadBean bean : value) {
            db.delete(DATABASE_NAME, ID + "=?", new String[]{String.valueOf(bean.getId())});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private ContentValues convertObject(VideoDownloadBean value) {
        ContentValues data = new ContentValues();
        data.put(ID, value.getId());
        data.put(NAME, value.getName());
        data.put(START_TIME, value.getStartTime());
        data.put(DURATION, value.getDuration());
        data.put(IMAGE_URL, value.getPicUrl());
        data.put(VIDEO_URL, value.getDownloadUrl());
        data.put(FILE_PATH, value.getDownloadFilePath());
        data.put(PROGRESS, value.getDownloadProgress());
        data.put(STATE, value.getDownloadState());
        data.put(STREAM, value.getStream());
        data.put(OID, value.getOid());
        data.put(RESERVE1, value.getUserName());
        data.put(RESERVE2, value.getPicFilePath());
        data.put(RESERVE6, value.getType());
        data.put(RESERVE7, value.getFps());
        return data;
    }
}
