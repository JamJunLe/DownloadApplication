package com.flyman.app.downloadapplication.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.flyman.app.downloadapplication.db.DownloadDbHelper.TABLE_FILE_INFO;

public class FileInfoDao extends BaseDaoImpl<FileInfo> {

    public FileInfoDao(Context context) {
        super(context);
    }

    @Override
    public List<FileInfo> query(FileInfo fileInfo, int flag) {
        // Cursor mCursor = mSQLiteDatabase.rawQuery("select * from " + TABLE_THREAD_INFO + " where fileInfoId= ?", new String[]{mFileInfoId});
        String sql;
        Cursor mCursor;
        List<FileInfo> results = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = openSQLiteDatabase();
        String[] whereArgs = null;
        switch (flag) {
            case IDao.FLAG_QUERY_ALL: {
                sql = "select * from " + TABLE_FILE_INFO;
                LogUtils.e("FLAG_QUERY_ALL sql = " + sql);
                break;
            }
            case IDao.FLAG_QUERY_SINGLE: {
                sql = "select * from " + TABLE_FILE_INFO + " where " + FileInfo.FileInfoColumnName.fileInfoId + "= ?";
                whereArgs = new String[]{fileInfo.getFileInfoId()};
                LogUtils.e("FLAG_QUERY_SINGLE sql = " + sql + " " + whereArgs.toString());
                break;
            }
            case IDao.FLAG_QUERY_DOWNLOADING: {
                sql = "select * from " + TABLE_FILE_INFO + " where " + FileInfo.FileInfoColumnName.isFinished + "= ?";
                whereArgs = new String[]{"0"};
                LogUtils.e("FLAG_QUERY_DOWNLOADING sql = " + sql + " " + whereArgs.toString());
                break;
            }
            case IDao.FLAG_QUERY_DOWNLOADED: {
                sql = "select * from " + TABLE_FILE_INFO + " where " + FileInfo.FileInfoColumnName.isFinished + "= ?";
                whereArgs = new String[]{"1"};
                LogUtils.e("FLAG_QUERY_DOWNLOADED sql = " + sql + " " + whereArgs.toString());
                break;
            }
            default: {
                sql = "select * from " + TABLE_FILE_INFO;
                LogUtils.e("default query sql = " + sql);
            }
        }
        try {
            //在这里执行多个数据库操作
            sqLiteDatabase.beginTransaction();
            mCursor = sqLiteDatabase.rawQuery(sql, whereArgs);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    FileInfo mFileInfo = new FileInfo();
                    mFileInfo.setFileInfoId(mCursor.getString(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.fileInfoId)));
                    mFileInfo.setName(mCursor.getString(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.name)));
                    mFileInfo.setSdPath(mCursor.getString(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.sdPath)));
                    mFileInfo.setUrl(mCursor.getString(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.url)));
                    mFileInfo.setLength(mCursor.getLong(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.length)));
                    mFileInfo.setFinishedLength(mCursor.getLong(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.finishLength)));
                    mFileInfo.setFinishedPercent(mCursor.getLong(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.finishedPercent)));
                    mFileInfo.setFinished(mCursor.getInt(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.isFinished)) == 0 ? false : true);
                    mFileInfo.setFinished(mCursor.getInt(mCursor.getColumnIndex(FileInfo.FileInfoColumnName.isPartial)) == 0 ? false : true);
                    results.add(mFileInfo);
                }
            }
            //执行过程中可能会抛出异常
            sqLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            LogUtils.a("查询出现异常 = " + sql);
            return null;
        } finally {
            //当所有操作执行完成后结束一个事务
            sqLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        return results;
    }

    @Override
    public boolean insert(FileInfo fileInfo) {
        //insert into ThreadInfo (fileInfoId,length,start,end,finishLength,isFinished) values('ithome_android_5.0',1080200,10802000,1080200,1080200,0)
        List<FileInfo> results = query(fileInfo, FLAG_QUERY_SINGLE);
        //插入时先查询改文件是否存在，如果存在则不进行插入操作
        if (results != null && results.size() != 0) {
            //改文件存在，不进行插入
            LogUtils.a("插入失败，改文件已经存在 FileInfoId = " + fileInfo.getFileInfoId());
            return false;
        }
        SQLiteDatabase sqLiteDatabase = openSQLiteDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            //在这里执行多个数据库操作
            String sql = "insert into "
                    + TABLE_FILE_INFO
                    + " ("
                    + FileInfo.FileInfoColumnName.fileInfoId + ","
                    + FileInfo.FileInfoColumnName.url + ","
                    + FileInfo.FileInfoColumnName.name + ","
                    + FileInfo.FileInfoColumnName.sdPath + ","
                    + FileInfo.FileInfoColumnName.length + ","
                    + FileInfo.FileInfoColumnName.finishLength + ","
                    + FileInfo.FileInfoColumnName.finishedPercent + ","
                    + FileInfo.FileInfoColumnName.isFinished + ","
                    + FileInfo.FileInfoColumnName.isPartial
                    + ")"
                    + " values(?,?,?,?,?,?,?,?,?)";
            LogUtils.e("insert sql = " + sql);
            Object[] args = {fileInfo.getFileInfoId(), fileInfo.getUrl(), fileInfo.getName(), fileInfo.getSdPath(), fileInfo.getLength(), fileInfo.getFinishedLength(), fileInfo.getFinishedPercent(), fileInfo.isFinished() == true ? 1 : 0, fileInfo.isPartial() == true ? 1 : 0};
            sqLiteDatabase.execSQL(sql, args);
            //执行过程中可能会抛出异常
            sqLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            return false;
        } finally {
            //当所有操作执行完成后结束一个事务
            sqLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        return true;
    }

    @Override
    public boolean update(FileInfo fileInfo) {
        //update ThreadInfo set start = 1000 ,end = 5000 where threadId = 0
        SQLiteDatabase sqLiteDatabase = openSQLiteDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            //在这里执行多个数据库操作
//            String sql = "update"
//                    + TABLE_FILE_INFO
//                    +" ("
//                    +FileInfo.FileInfoColumnName.fileInfoId+","
//                    +FileInfo.FileInfoColumnName.url+","
//                    +FileInfo.FileInfoColumnName.name+","
//                    +FileInfo.FileInfoColumnName.sdPath+","
//                    +FileInfo.FileInfoColumnName.length+","
//                    +FileInfo.FileInfoColumnName.finishLength+","
//                    +FileInfo.FileInfoColumnName.finishedPercent+","
//                    +FileInfo.FileInfoColumnName.isFinished
//                    +")"
//                    + " values(?,?,?,?,?,?,?,?)";
//            LogUtils.e("update fileInfo sql = " + sql);
//            Object[] args = {fileInfo.getFileInfoId(),fileInfo.getUrl(),fileInfo.getName(),fileInfo.getSdPath(),fileInfo.getLength(),fileInfo.getFinishedLength(),fileInfo.getFinishedPercent(),fileInfo.isFinished()==true?1:0};
            LogUtils.e("update", "update fileInfo "+fileInfo.toString());
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(FileInfo.FileInfoColumnName.fileInfoId, fileInfo.getFileInfoId());
            mContentValues.put(FileInfo.FileInfoColumnName.url, fileInfo.getUrl());
            mContentValues.put(FileInfo.FileInfoColumnName.name, fileInfo.getName());
            mContentValues.put(FileInfo.FileInfoColumnName.sdPath, fileInfo.getSdPath());
            mContentValues.put(FileInfo.FileInfoColumnName.length, fileInfo.getLength());
            mContentValues.put(FileInfo.FileInfoColumnName.finishLength, fileInfo.getFinishedLength());
            mContentValues.put(FileInfo.FileInfoColumnName.finishedPercent, fileInfo.getFinishedPercent());
            mContentValues.put(FileInfo.FileInfoColumnName.isFinished, fileInfo.isFinished() == true ? 1 : 0);
            mContentValues.put(FileInfo.FileInfoColumnName.isPartial, fileInfo.isPartial() == true ? 1 : 0);
            sqLiteDatabase.update(TABLE_FILE_INFO, mContentValues, FileInfo.FileInfoColumnName.fileInfoId + "= ?", new String[]{fileInfo.getFileInfoId()});
            //执行过程中可能会抛出异常
            sqLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            LogUtils.e("update", "更新文件失败");
            return false;
        } finally {
            //当所有操作执行完成后结束一个事务
            sqLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        LogUtils.e("update", "更新文件成功");
        return true;
    }
}
