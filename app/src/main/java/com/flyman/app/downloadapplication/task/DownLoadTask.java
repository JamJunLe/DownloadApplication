package com.flyman.app.downloadapplication.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.flyman.app.core.Config;
import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.bean.ThreadInfo;
import com.flyman.app.downloadapplication.db.dao.FileInfoDao;
import com.flyman.app.downloadapplication.db.dao.ThreadInfoDao;
import com.flyman.app.downloadapplication.util.Arith;
import com.flyman.app.downloadapplication.util.IntentUtil;
import com.flyman.app.downloadapplication.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 下载任务类,用以控制下载的开始,结束,删除
 */
public class DownLoadTask {
    private long instantaneousLength = 0;//单位时间下载长度
    private FileInfo mFileInfo;
    private Intent mIntent;//发送广播,用以更新UI
    private Timer mTimer;
    private List<ThreadInfo> mThreadInfos;
    private boolean isPause = false;
    int size = 0;//下载用的线程信息
    private ThreadInfoDao mThreadInfoDao;
    private FileInfoDao mFileInfoDaoIml;
    private int position;
    private long fileInfoOriginalLength;//文件原有的已下载长度
    private long instantaneousLengthLastTime = 0;//记录瞬时速度的上一次值，以便下次计算
    private long threadInfoOriginalTotalLength;//所有线程原有的已下载长度
    private Context context;

    public DownLoadTask(Context context, FileInfo mFileInfo, int position) {
        this.context = context;
        this.mFileInfo = mFileInfo;
        this.position = position;
        mThreadInfoDao = new ThreadInfoDao(context);
        mFileInfoDaoIml = new FileInfoDao(context);
        mThreadInfos = mFileInfo.getThreadInfos();
        size = mFileInfo.getThreadInfos().size();
        fileInfoOriginalLength = mFileInfo.getFinishedLength();//文件原有的已下载长度
        threadInfoOriginalTotalLength = getCurrentThreadTotalFinishedLength();//所有线程原有的已下载长度
        mIntent = new Intent();
        mTimer = new Timer();
    }

    public void start() {
        LogUtils.i("DownLoadTask", "进入DownLoadTask的文件信息 = " + mFileInfo.getThreadInfos().toString());
        LogUtils.i("DownLoadTask", "文件原有的已下载长度 = " + fileInfoOriginalLength
                + " 所有线程原有的已下载长度 = " + threadInfoOriginalTotalLength +
                " 文件原有的已下载长度等于所有线程原有的已下载长度？ = " + (threadInfoOriginalTotalLength == fileInfoOriginalLength ? "相等" : "不相等"));
        for (int i = 0; i < size; i++) {
            //开启多线程下载
            new DownLoadThread(mThreadInfos.get(i)).start();
        }
        mTimer.schedule(mTask, 0, Config.UPDATE_UI_TIME_DELAY);
    }

    public void pause() {
        isPause = true;
    }

    class DownLoadThread extends Thread {
        private ThreadInfo mThreadInfo;
        private long currentThreadInfoOriginalLength;//线程原有的已下载长度

        public DownLoadThread(ThreadInfo threadInfo) {
            this.mThreadInfo = threadInfo;
            currentThreadInfoOriginalLength = threadInfo.getFinishLength();
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                URL url = new URL(mFileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                //设置下载位置
                long start = mThreadInfo.getStart();
                long end = mThreadInfo.getEnd();

                if (mFileInfo.isPartial() == true) {
                    connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
                }
                //设置文件写入位置
                File file = new File(mFileInfo.getSdPath());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(mThreadInfo.getStart());
                //开始下载
                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    //读取数据
                    is = connection.getInputStream();
                    long length = mThreadInfo.getLength() - currentThreadInfoOriginalLength;
                    LogUtils.e("DownLoadThread", "目标长度 = " + length + "  下载范围" + start + "  -  " + end + "  end - start  =  " + (end - start));
                    byte[] buffer = new byte[1024 * 4];
                    int len;
                    long finishedLength = 0;//已完成的长度
                    long time = System.currentTimeMillis();
                    while ((len = is.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        finishedLength = finishedLength + len;
                        mThreadInfo.setFinishLength(finishedLength);
                        if (System.currentTimeMillis() - time > 1000) {//减少UI负载
                            time = System.currentTimeMillis();
                            //把下载进度发送广播给Activity
                            mHandler.sendMessage(mHandler.obtainMessage());
                        }
                        if (finishedLength == length) {
                            LogUtils.e("DownLoadThread", "下载完成,删除线程程信息");
                            mThreadInfo.setFinished(true);
                            mThreadInfoDao.delete(mThreadInfo);
                            mHandler.sendMessage(mHandler.obtainMessage());//通知立刻更新
                            return;
                        }
                        if (isPause == true) {
                            ThreadInfo newThreadInfo = mThreadInfo.myClone();//克隆新对象更新数据库
                            newThreadInfo.setFinished(false);
                            newThreadInfo.setStart(mThreadInfo.getFinishLength() + currentThreadInfoOriginalLength);
                            newThreadInfo.setFinishLength(mThreadInfo.getFinishLength() + currentThreadInfoOriginalLength);
                            LogUtils.e("DownLoadThread", "下载暂停,更新线程信息 保存的新线程 =" + newThreadInfo.toString());
                            mThreadInfoDao.update(newThreadInfo);
                            mHandler.sendMessage(mHandler.obtainMessage());//通知立刻更新
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (raf != null && connection != null && is != null) {
                    try {
                        is.close();
                        raf.close();
                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    //每隔一秒更新UI
    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendMessage(mHandler.obtainMessage());
        }
    };
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            long instantaneousLengthTmp = getInstantaneousLength();//瞬时速度
            long totalFinishedLength = getTotalFinishedLength();//文件总的下载量
            long threadTotalFinishedLength = getThreadTotalFinishedLength();//所有线程的总下载量
            int currentFinishedPercent = (int) (totalFinishedLength * 100 / mFileInfo.getLength());//下载的百分比
            mFileInfo.setFinishedPercent(currentFinishedPercent);
            mFileInfo.setFinishedLength(totalFinishedLength);
            mFileInfo.setInstantaneousLength(instantaneousLengthTmp);
            LogUtils.i("========= UI更新 ==================================================================");
            LogUtils.e("DownLoadThread", "totalFinishedLength =" + totalFinishedLength
                    + " threadTotalFinishedLength = " + threadTotalFinishedLength
                    + " instantaneousLength =" + instantaneousLengthTmp
                    + " currentFinishedPercent =" + currentFinishedPercent);
            //下载完成时
            if (totalFinishedLength == mFileInfo.getLength()) {
                LogUtils.e("DownLoadThread", "下载完成,更新文件信息");
                mFileInfo.setFinished(true);
                mFileInfo.setFinishedPercent(100);
                mFileInfoDaoIml.update(mFileInfo);
                mTimer.cancel();//结束UI更新
            }
            //暂停
            if (isPause == true) {
                LogUtils.e("DownLoadThread", "暂停下载,更新文件信息");
                mFileInfo.setFinished(false);
                mFileInfo.setThreadInfos(mThreadInfos);
                mFileInfo.setInstantaneousLength(0);
                mFileInfoDaoIml.update(mFileInfo);
                mTimer.cancel();

            }
            updateList(mFileInfo);//发广播更新列表
            instantaneousLength = instantaneousLengthLastTime;//用上次记录的值替换当前,不这么做会出错
            return false;
        }
    });

    private void updateList(FileInfo fileInfo) {
        mIntent.setAction(IntentUtil.ACTION_UPDATE);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(IntentUtil.FILE_INFO, fileInfo);
        mBundle.putInt(IntentUtil.FILE_INFO_POSITION, position);
        mIntent.putExtras(mBundle);
        context.sendBroadcast(mIntent);
    }

    /**
     * 获得瞬时下载的进度(用以计算下载速度)
     *
     * @param
     * @return nothing
     */
    private long getInstantaneousLength() {
        long current = getCurrentThreadTotalFinishedLength();
        instantaneousLength = current - instantaneousLength;
        instantaneousLengthLastTime = current;//记录本次的瞬时值
        return (long) Arith.div(instantaneousLength, (Arith.div(Config.UPDATE_UI_TIME_DELAY, 1000)));
    }


    /**
     * 每条线程的下载量累加(加上原始的已经下载量)
     *
     * @param
     * @return nothing
     */
    private long getThreadTotalFinishedLength() {
        return getCurrentThreadTotalFinishedLength() + threadInfoOriginalTotalLength;
    }

    /**
     * 当前所有线程的下载量
     *
     * @param
     * @return nothing
     */
    private long getCurrentThreadTotalFinishedLength() {
        long currentTotalFinishedLength = 0;
        for (int i = 0; i < size; i++) {
            ThreadInfo mThreadInfo = mThreadInfos.get(i);
            currentTotalFinishedLength = currentTotalFinishedLength + mThreadInfo.getFinishLength();
        }
        return currentTotalFinishedLength;
    }

    /**
     * 获得文件总的下载量
     *
     * @param
     * @return nothing
     */
    private long getTotalFinishedLength() {
        return fileInfoOriginalLength + getThreadTotalFinishedLength();
    }



}
