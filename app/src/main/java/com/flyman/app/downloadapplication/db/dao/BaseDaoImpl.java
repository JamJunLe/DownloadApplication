package com.flyman.app.downloadapplication.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.flyman.app.downloadapplication.db.DownloadDbManager;

import java.util.List;

public class BaseDaoImpl<T> implements IDao<T> {
    protected Context mContext;

    public BaseDaoImpl(Context context) {
        mContext = context;
    }
    protected SQLiteDatabase openSQLiteDatabase()
    {
        return DownloadDbManager.getInstance().openDatabase(mContext);
    }
    protected void closeSQLiteDatabase()
    {
        DownloadDbManager.getInstance().closeDatabase();
    }


    @Override
    public boolean insert(T t) {
        return false;
    }

    @Override
    public boolean delete(T t) {
        return false;
    }

    @Override
    public boolean update(T t) {
        return false;
    }

    @Override
    public List<T> query(T t) {
        return null;
    }

    @Override
    public List<T> query(T t, int flag) {
        return null;
    }

}
