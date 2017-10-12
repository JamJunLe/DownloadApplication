package com.flyman.app.downloadapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.flyman.app.core.Config;

public class SharedPreferencesUtil {
    public static final String DOWNLOAD_PATH="DOWNLOAD_PATH";
    public static final String THREAD_COUNT="THREAD_COUNT";//默认的线程
    public static void saveDownLoadPath(Context context,String downLoadPath)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DOWNLOAD_PATH, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(DOWNLOAD_PATH,downLoadPath);
        mEditor.commit();
    }

    public static String getDownLoadPath(Context context)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DOWNLOAD_PATH, Activity.MODE_PRIVATE);
        String path = mSharedPreferences.getString(DOWNLOAD_PATH, Config.SD_DOWNLOAD_PATH);
        return path;
    }
    /**
     *  保存线程数
     *
     *  @return nothing
     *  @param  context
     */
    public static void saveThreadCount(Context context, int threadCount)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(THREAD_COUNT, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(THREAD_COUNT,threadCount);
        mEditor.commit();
    }

    public static int getThreadCount(Context context)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(THREAD_COUNT, Activity.MODE_PRIVATE);
        int threadCount = mSharedPreferences.getInt(THREAD_COUNT, Config.DEFAULT_THREAD_COUNT);
        return threadCount;
    }
}
