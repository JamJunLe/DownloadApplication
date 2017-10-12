package com.flyman.app.downloadapplication.task;

import android.os.AsyncTask;

import com.flyman.app.downloadapplication.app.DownloadApplication;
import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.db.dao.FileInfoDao;
import com.flyman.app.downloadapplication.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_DELETE;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_INSERT;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_QUERY_ALL;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_QUERY_DOWNLOADED;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_QUERY_DOWNLOADING;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_UPDATE;

public class FileInfoTask extends AsyncTask<FileInfo, FileInfo, List<FileInfo>> {
    private int flag;
    private TaskCallback mTaskCallback;
    private FileInfoDao mFileInfoDao;

    public FileInfoTask(int flag, TaskCallback taskCallback) {
        this.flag = flag;
        mTaskCallback = taskCallback;
        mFileInfoDao = new FileInfoDao(DownloadApplication.getContext());
    }

    public FileInfoTask(int flag) {
        this(flag, null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mTaskCallback != null) {
            mTaskCallback.onTaskPre(flag);
        }

    }

    @Override
    protected List<FileInfo> doInBackground(FileInfo... fileInfo) {
        FileInfo mFileInfo = fileInfo[0];
        switch (flag) {
            case FLAG_QUERY_ALL: {
                return mFileInfoDao.query(mFileInfo, FLAG_QUERY_ALL);
            }
            case FLAG_QUERY_DOWNLOADING: {
                return mFileInfoDao.query(mFileInfo, FLAG_QUERY_DOWNLOADING);
            }
            case FLAG_QUERY_DOWNLOADED: {
                return mFileInfoDao.query(mFileInfo, FLAG_QUERY_DOWNLOADED);
            }
            case FLAG_DELETE: {
                break;
            }
            case FLAG_UPDATE: {
                return mFileInfoDao.update(mFileInfo) == true ? new ArrayList<FileInfo>() : null;
            }
            case FLAG_INSERT: {
                return mFileInfoDao.insert(mFileInfo) == true ? new ArrayList<FileInfo>() : null;
            }
            default: {
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(FileInfo... values) {
        super.onProgressUpdate(values);
        if (mTaskCallback != null) {
            mTaskCallback.onUpdateTask(flag, values);
        }

    }

    @Override
    protected void onPostExecute(List<FileInfo> fileInfoList) {
        super.onPostExecute(fileInfoList);
        LogUtils.e("onPostExecute "+fileInfoList ==null?"空的":"不为空");
        if (fileInfoList == null) {
            if (mTaskCallback != null) {
                mTaskCallback.onTaskFail(flag);
            }

        } else {
            if (mTaskCallback != null) {
                mTaskCallback.onTaskSuccess(flag, fileInfoList);
            }

        }

    }
}
