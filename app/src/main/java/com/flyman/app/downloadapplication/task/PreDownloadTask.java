package com.flyman.app.downloadapplication.task;

import android.os.AsyncTask;

import com.flyman.app.downloadapplication.app.DownloadApplication;
import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.bean.ThreadInfo;
import com.flyman.app.downloadapplication.db.dao.FileInfoDao;
import com.flyman.app.downloadapplication.db.dao.ThreadInfoDao;
import com.flyman.app.downloadapplication.util.FileUtil;
import com.flyman.app.downloadapplication.util.LogUtils;
import com.flyman.app.downloadapplication.util.SharedPreferencesUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Flyman
 * @ClassName PreDownloadTask
 * @description 下载之前一些初始化配置
 * @date 2017-7-9 1:19
 */
public class PreDownloadTask extends AsyncTask<FileInfo, Void, FileInfo> {
    private ThreadInfoDao dao;
    private static final int TIME_OUT = 5000;//链接超时
    private static final String REQUEST_METHOD = "GET";//链接方式
    private TaskCallback mTaskCallback;

    public PreDownloadTask(TaskCallback taskCallback) {
        dao = new ThreadInfoDao(DownloadApplication.getContext());
        mTaskCallback = taskCallback;
    }

    @Override
    protected FileInfo doInBackground(FileInfo... fileInfos) {
        FileInfo mFileInfo = fileInfos[0];
//        LogUtils.e("PreDownloadTask", "doInBackground" + mFileInfo.toString());
        HttpURLConnection mHttpURLConnection = null;
        boolean isPartial;
        try {
            if (mFileInfo.getLength() == 0) {
                //说明文件的信息不完整，需要通过网络获取文件的大小
                int length = -1;
                URL mUrl = new URL(mFileInfo.getUrl());
                mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
                mHttpURLConnection.setConnectTimeout(TIME_OUT);
                mHttpURLConnection.setRequestMethod(REQUEST_METHOD);
                if (mHttpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = mHttpURLConnection.getContentLength();
                }
                URL isPartialUrl = new URL(mFileInfo.getUrl());
                mHttpURLConnection = (HttpURLConnection) isPartialUrl.openConnection();
                mHttpURLConnection.setConnectTimeout(TIME_OUT);
                mHttpURLConnection.setRequestMethod(REQUEST_METHOD);
                mHttpURLConnection.setRequestProperty("Range", "bytes=0" + "-" + length);
                if (mHttpURLConnection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    isPartial = true;
                } else {
                    isPartial = false;
                    LogUtils.a("PreDownloadTask", "不支持分段下载");
                }
                //链接有问题 无法获取资源
                if (length < 0) {
                    LogUtils.a("PreDownloadTask", "网络文件有问题");
                    return null;
                }
                mFileInfo.setLength(length);
                mFileInfo.setPartial(isPartial);
                mFileInfo = FileUtil.createDownloadFile(DownloadApplication.getContext(), mFileInfo);
                //数据库中更新文件信息
                new FileInfoDao(DownloadApplication.getContext()).update(mFileInfo);
//                mHandler.sendMessage(mHandler.obtainMessage(FLAG_INSERT, mFileInfo));
            }
            //创建文件信息
            if (mFileInfo.getSdPath() == null || mFileInfo.equals("")) {
                mFileInfo = FileUtil.createDownloadFile(DownloadApplication.getContext(), mFileInfo);
            }
            //获取文件的下载线程信息
            if (mFileInfo.getThreadInfos() == null || mFileInfo.getThreadInfos().size() == 0) {
                mFileInfo.setThreadInfos(getThreadInfoList(mFileInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mHttpURLConnection != null) {
                    mHttpURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return mFileInfo;
    }

    @Override
    protected void onPostExecute(FileInfo fileInfo) {
        super.onPostExecute(fileInfo);
        if (mTaskCallback != null) {
            mTaskCallback.onTaskSuccess(1, fileInfo);
        }
    }


    /**
     * 获取下载线程信息
     *
     * @param
     * @return List<ThreadInfo>
     */
    private List<ThreadInfo> getThreadInfoList(FileInfo mFileInfo) {
        dao = new ThreadInfoDao(DownloadApplication.getContext());
        //创建一个ThreadInfo，用以查询该文件的线程信息是否存在
        ThreadInfo queryThreadInfo = new ThreadInfo();
        queryThreadInfo.setFileInfoId(mFileInfo.getFileInfoId());
        //查询
        List<ThreadInfo> mThreadInfos = (dao.query(queryThreadInfo));
        // 如果数据库没有数据,则开始自主构建
        if (mThreadInfos == null || mThreadInfos.size() == 0) {
            LogUtils.e("getThreadInfoList", "数据库中并没有该文件的线程信息，自主的构建");
            mThreadInfos = new ArrayList<>();
            int threadCount = SharedPreferencesUtil.getThreadCount(DownloadApplication.getContext());
            //不支持分段下载
            if (mFileInfo.isPartial() == false) {
                threadCount = 1;
            }
            threadCount = 1;
            long length = mFileInfo.getLength();//总文件的大小
            long avgLength = length / threadCount;//平均长度
            LogUtils.e("getThreadInfoList", "线程数 = " + threadCount +" 每条线程的平均长度 =" + avgLength);
            for (int i = 0; i < threadCount; i++) {
                ThreadInfo mThreadInfo = new ThreadInfo();
                mThreadInfo.setThreadId(i);
                mThreadInfo.setFinished(false);
                mThreadInfo.setFileInfoId(mFileInfo.getFileInfoId());
                mThreadInfo.setLength(avgLength);
                mThreadInfo.setFinishLength(0);
                mThreadInfo.setStart(avgLength * i);
                if (i == (threadCount - 1))//最后一段
                {
                    mThreadInfo.setEnd(length);
                } else {
                    mThreadInfo.setEnd(avgLength * (i + 1) - 1);
                }
                mThreadInfos.add(mThreadInfo);
                dao.insert(mThreadInfo);//向数据库插入数据
            }

        } else {
            LogUtils.e("getThreadInfoList", "线程信息数据库中存在 ==========");
        }
        LogUtils.e("getThreadInfoList", "最终获取的线程信息为 "+ mThreadInfos.toString());
        return mThreadInfos;
    }


}
