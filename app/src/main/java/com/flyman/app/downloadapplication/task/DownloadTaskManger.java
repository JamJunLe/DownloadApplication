package com.flyman.app.downloadapplication.task;

import com.flyman.app.downloadapplication.app.DownloadApplication;
import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.util.LogUtils;

public class DownloadTaskManger {
    private PreDownloadTask mPreDownloadTask;
    private DownLoadTask mDownLoadTask;

    public void start(FileInfo mFileInfo, final int position) {
        mPreDownloadTask = new PreDownloadTask(new TaskCallback<FileInfo>() {
            @Override
            public void onTaskPre(int flag) {

            }

            @Override
            public void onUpdateTask(int flag, FileInfo fileInfo) {

            }

            @Override
            public void onTaskSuccess(int flag, FileInfo fileInfo) {
                if (fileInfo != null) {
                    mDownLoadTask = new DownLoadTask(DownloadApplication.getContext(),fileInfo,position);
                    mDownLoadTask.start();
                }
            }
            @Override
            public void onTaskFail(int flag) {
                LogUtils.e("PreDownloadTask fail");
            }
        });
        mPreDownloadTask.execute(mFileInfo);
    }

    public void pause() {
        if (mDownLoadTask != null) {
            mDownLoadTask.pause();
        }
    }

}
