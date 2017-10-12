package com.flyman.app.downloadapplication.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flyman.app.downloadapplication.bean.ThreadInfo;
import com.flyman.app.downloadapplication.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.flyman.app.downloadapplication.db.DownloadDbHelper.TABLE_THREAD_INFO;

public class ThreadInfoDao extends BaseDaoImpl<ThreadInfo> {

    public ThreadInfoDao(Context mContext) {
        super(mContext);
    }

    @Override
    public List<ThreadInfo> query(ThreadInfo threadInfo) {
        //select * from ThreadInfo where fileInfoId = 'ithome_android_5.0'
        List<ThreadInfo> mThreadInfos = null;
        SQLiteDatabase mSQLiteDatabase = openSQLiteDatabase();
        try {
            mSQLiteDatabase = openSQLiteDatabase();
            mSQLiteDatabase.beginTransaction();
            //在这里执行多个数据库操作
            String sql = "select * from "
                    + TABLE_THREAD_INFO
                    + " where fileInfoId='"
                    + threadInfo.getFileInfoId()
                    + "' and "
                    + ThreadInfo.ThreadInfoColumnName.isFinished +"="
                    + 0;
            LogUtils.e("查询未完成的线程信息 sql = " + sql);
            Cursor mCursor = mSQLiteDatabase.rawQuery(sql, null);
            if (mCursor == null) {
                return null;
            }
            if (mCursor != null) {
                mThreadInfos = new ArrayList<>();
                while (mCursor.moveToNext()) {
                    ThreadInfo mThreadInfo = new ThreadInfo();
                    mThreadInfo.setThreadId(mCursor.getInt(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.threadId)));
                    mThreadInfo.setFileInfoId(mCursor.getString(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.fileInfoId)));
                    mThreadInfo.setFinishLength(mCursor.getLong(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.finishLength)));
                    mThreadInfo.setLength(mCursor.getLong(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.length)));
                    mThreadInfo.setStart(mCursor.getLong(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.start)));
                    mThreadInfo.setEnd(mCursor.getLong(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.end)));
                    mThreadInfo.setFinished(mCursor.getInt(mCursor.getColumnIndex(ThreadInfo.ThreadInfoColumnName.isFinished)) == 0 ? false : true);
                    mThreadInfos.add(mThreadInfo);
                }
            }
            //执行过程中可能会抛出异常
            mSQLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            return null;
        } finally {
            //当所有操作执行完成后结束一个事务
            mSQLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        return mThreadInfos;
    }

    @Override
    synchronized public boolean insert(ThreadInfo threadInfo) {
        SQLiteDatabase mSQLiteDatabase = openSQLiteDatabase();
        //insert into ThreadInfo (fileInfoId,length,start,end,finishLength,isFinished) values('ithome_android_5.0',1080200,10802000,1080200,1080200,0)
        try {
            //在这里执行多个数据库操作
            mSQLiteDatabase.beginTransaction();
            //执行过程中可能会抛出异常
            String sql = "insert into " + TABLE_THREAD_INFO + "(threadId,fileInfoId,length,start,end,finishLength,isFinished) values(?,?,?,?,?,?,?)";
            Object[] args = new Object[]{threadInfo.getThreadId(), threadInfo.getFileInfoId(), threadInfo.getLength(), threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinishLength(), threadInfo.isFinished() == true ? 0 : 1};
            mSQLiteDatabase.execSQL(sql, args);
            LogUtils.e("插入数据 sql = " + sql);
            mSQLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            return false;
        } finally {
            //当所有操作执行完成后结束一个事务
            mSQLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        return false;
    }


    @Override
    synchronized public boolean update(ThreadInfo threadInfo) {
        //update ThreadInfo set start = 1000 ,end = 5000 where threadId = 0
        SQLiteDatabase mSQLiteDatabase = openSQLiteDatabase();
        String sql;
        try {
            mSQLiteDatabase.beginTransaction();
            //在这里执行多个数据库操作
            sql = "update "
                    + TABLE_THREAD_INFO
                    + " set  " + ThreadInfo.ThreadInfoColumnName.start + "="
                    + threadInfo.getStart()
                    + ","
                    + ThreadInfo.ThreadInfoColumnName.isFinished + "="
                    + (threadInfo.isFinished()==true?1:0)
                    + ","
                    + ThreadInfo.ThreadInfoColumnName.finishLength
                    + "="
                    + +threadInfo.getFinishLength()
                    + " where threadId="
                    + threadInfo.getThreadId();
            LogUtils.e("保存线程信息 sql = " + sql+" 具体的对象为 ="+threadInfo.toString());
            mSQLiteDatabase.execSQL(sql);
            mSQLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            LogUtils.a("更新线程信息失败 ThreadId = " + threadInfo.getThreadId());
            return false;
        } finally {
            //当所有操作执行完成后结束一个事务
            mSQLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        return false;
    }

    @Override
    synchronized public boolean delete(ThreadInfo threadInfo) {
        //delete from ThreadInfo where fileInfoId = 'https://d.ruanmei.com/app/ithome/ithome_android_5.0.apk'
        SQLiteDatabase mSQLiteDatabase = openSQLiteDatabase();
        try {
            mSQLiteDatabase.beginTransaction();
            //在这里执行多个数据库操作
            String sql = "delete from " + TABLE_THREAD_INFO + " where "
                    + ThreadInfo.ThreadInfoColumnName.threadId
                    + " ="
                    + threadInfo.getThreadId();
            LogUtils.e("删除 sql = " + sql);
            mSQLiteDatabase.execSQL(sql);
            mSQLiteDatabase.setTransactionSuccessful();
            //在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        } catch (Exception e) {
            e.printStackTrace();
            closeSQLiteDatabase();
            return false;
        } finally {
            //当所有操作执行完成后结束一个事务
            mSQLiteDatabase.endTransaction();
            closeSQLiteDatabase();
        }
        return true;
    }
}
