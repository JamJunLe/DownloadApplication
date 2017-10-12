package com.flyman.app.downloadapplication.util;

import android.content.Context;

import com.flyman.app.downloadapplication.bean.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtil {
    public static FileInfo createDownloadFile(Context context, FileInfo fileInfo)
    {
        RandomAccessFile raf = null;
        try {
            String defaultDownloadPath = SharedPreferencesUtil.getDownLoadPath(context);
            File defaultDownloadDirectory =  new File(defaultDownloadPath);//创建默认的下载路径
            LogUtils.e("默认的下载文件夹 ="+ defaultDownloadDirectory.getPath());
            if (defaultDownloadDirectory.exists() == false)
            {
                defaultDownloadDirectory.mkdirs();
            }
            File downloadFile = new File(defaultDownloadDirectory,fileInfo.getName());//创建需要下载的目标文件
            if (downloadFile.exists() == false)
            {
                downloadFile.createNewFile();
            }
            fileInfo.setSdPath(downloadFile.getPath());//设置目标文件的路径
            raf = new RandomAccessFile(downloadFile,"rwd");
            raf.setLength(fileInfo.getLength());//设定文件长度
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return fileInfo;
    }
}
