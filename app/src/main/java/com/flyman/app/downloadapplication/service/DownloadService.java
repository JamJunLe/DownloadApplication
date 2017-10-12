package com.flyman.app.downloadapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.task.DownloadTaskManger;
import com.flyman.app.downloadapplication.util.IntentUtil;
import com.flyman.app.downloadapplication.util.LogUtils;

import static com.flyman.app.downloadapplication.util.IntentUtil.FLAG_PAUSE;
import static com.flyman.app.downloadapplication.util.IntentUtil.FLAG_START;


public class DownloadService extends Service {
    private DownloadTaskManger mDownloadTaskManger;

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.e("onCreate", "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle mBundle = intent.getExtras();
            FileInfo mFileInfo = (FileInfo) mBundle.getSerializable(IntentUtil.FILE_INFO);
            int position = mBundle.getInt(IntentUtil.FILE_INFO_POSITION);
            int status = intent.getFlags();
            switch (status) {
                case FLAG_START: {
                    mDownloadTaskManger = new DownloadTaskManger();
                    mDownloadTaskManger.start(mFileInfo, position);
                    LogUtils.i("FLAG_START ======= mFileInfo = " + mFileInfo.toString());
                    break;
                }
                case FLAG_PAUSE: {
                    if (mDownloadTaskManger != null) {
                        mDownloadTaskManger.pause();
                        LogUtils.i("FLAG_PAUSE =======");
                    }
                    break;
                }
                default: {

                }
            }
        }
        return START_NOT_STICKY;//出现异常不重启
//        return super.onStartCommand(intent,flags,startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
