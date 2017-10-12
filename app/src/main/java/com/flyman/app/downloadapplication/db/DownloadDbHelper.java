package com.flyman.app.downloadapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.util.LogUtils;

import static com.flyman.app.core.Config.ID_PRIMARY_KEY;
import static com.flyman.app.core.Config.INTEGER;
import static com.flyman.app.core.Config.NUMERIC;
import static com.flyman.app.core.Config.TEXT;
import static com.flyman.app.downloadapplication.bean.FileInfo.FileInfoColumnName.finishedPercent;
import static com.flyman.app.downloadapplication.bean.FileInfo.FileInfoColumnName.isPartial;
import static com.flyman.app.downloadapplication.bean.FileInfo.FileInfoColumnName.name;
import static com.flyman.app.downloadapplication.bean.FileInfo.FileInfoColumnName.sdPath;
import static com.flyman.app.downloadapplication.bean.FileInfo.FileInfoColumnName.url;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.end;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.fileInfoId;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.finishLength;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.isFinished;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.length;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.start;
import static com.flyman.app.downloadapplication.bean.ThreadInfo.ThreadInfoColumnName.threadId;

public class DownloadDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "download.db";
    private static final int VERSION = 1;
    public static final String TABLE_THREAD_INFO = "ThreadInfo";
    public static final String TABLE_FILE_INFO = "FileInfo";
    private static volatile SQLiteOpenHelper mSqLiteOpenHelper;

    /**
     * 使用单例模式保证app内只存在唯一的SQLiteOpenHelper,使得只能获取唯一的SQLiteDatabase
     * 解决多线程并发的操作
     *
     * @param
     * @return nothing
     */
    private DownloadDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static SQLiteOpenHelper init(Context mContext) {
        if (mSqLiteOpenHelper == null) {
            synchronized (DownloadDbHelper.class) {
                if (mSqLiteOpenHelper == null) {
                    mSqLiteOpenHelper = new DownloadDbHelper(mContext);
                }
            }
        }
        return mSqLiteOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createFileInfoTable(sqLiteDatabase);
        createThreadInfoTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createThreadInfoTable(SQLiteDatabase sqLiteDatabase) {
        //create table if not exists ThreadInfo(fileInfoId integer primary key,threadId INTEGER,fileInfoId TEXT, length INTEGER,start INTEGER,end INTEGER, finishLength INTEGER,isFinished NUMERIC)
        String sql = "create table if not exists "
                + TABLE_THREAD_INFO + "("
                + ID_PRIMARY_KEY + ","
                + threadId + INTEGER + " not null" + ","
                + fileInfoId + TEXT + ","
                + length + INTEGER + ","
                + start + INTEGER + ","
                + end + INTEGER + ","
                + finishLength + INTEGER + ","
                + isFinished + NUMERIC
                + ")";
        LogUtils.e("创建线程数据库 sql = " + sql);
        sqLiteDatabase.execSQL(sql);
    }

    private void createFileInfoTable(SQLiteDatabase sqLiteDatabase) {
        //create table if not exists ThreadInfo(fileInfoId integer primary key,threadId INTEGER,fileInfoId TEXT, length INTEGER,start INTEGER,end INTEGER, finishLength INTEGER,isFinished NUMERIC)
        String sql = "create table if not exists "
                + TABLE_FILE_INFO + "("
                + ID_PRIMARY_KEY + ","
                + FileInfo.FileInfoColumnName.fileInfoId + TEXT + " not null" + ","
                + url + TEXT + ","
                + name + TEXT + ","
                + sdPath + TEXT + ","
                + length + INTEGER + ","
                + finishLength + INTEGER + ","
                + finishedPercent + INTEGER + ","
                + isFinished + NUMERIC + ","
                + isPartial + NUMERIC
                + ")";
        LogUtils.e("创建文件数据库 sql = " + sql);
        sqLiteDatabase.execSQL(sql);
    }
}
