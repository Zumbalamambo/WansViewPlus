package net.ajcloud.wansview.main.download.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created
 */
public class VideoDownloadContentProvider extends ContentProvider {

    private VideoDownloadDBHelper helper;
    private static final String DATABASE_NAME = "VideoDownload";
    private static final int DATABASE_VERSON = 1;

    @Override
    public boolean onCreate() {
//        helper = new VideoDownloadDBHelper(getContext(), DATABASE_NAME, DATABASE_VERSON);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query("video", null, null, null, null, null, null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.insert("video", null, values);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete("video", null, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.update("video", values, null, null);
        return 0;
    }
}
