package com.flyman.app.downloadapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DownloadDbManager {
    private SQLiteDatabase mSQLiteDatabase;
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    private DownloadDbManager() {
    }

    public synchronized SQLiteDatabase openDatabase(Context mContext) {
        if(mAtomicInteger.incrementAndGet() == 1)
        {
            //打开SQLiteDatabase连接
            try {
                mSQLiteDatabase = DownloadDbHelper.init(mContext).getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
                mSQLiteDatabase = DownloadDbHelper.init(mContext).getReadableDatabase();
            }
        }
        return mSQLiteDatabase;
    }

    public synchronized void closeDatabase() {
        if(mAtomicInteger.decrementAndGet() == 0)//创建全新SQLiteDatabase连接
        {
            if(mSQLiteDatabase!= null&&mSQLiteDatabase.isOpen()==false)
            {
                mSQLiteDatabase.close();
            }
        }
    }

    public static DownloadDbManager getInstance() {
        return DownloadDbMangerHolder.mDownloadDbManger;
    }

    static class DownloadDbMangerHolder {
        private static final DownloadDbManager mDownloadDbManger = new DownloadDbManager();
    }
}
