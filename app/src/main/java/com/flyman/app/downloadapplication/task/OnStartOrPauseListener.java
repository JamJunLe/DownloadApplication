package com.flyman.app.downloadapplication.task;

import com.flyman.app.downloadapplication.bean.FileInfo;

/**
 *  @ClassName OnStartOrPauseListener
 *  @description 当用户点击列表中的下载/暂停按钮时的监听
 *
 *  @author Flyman
 *  @date 2017-7-8 23:50
 */
public interface OnStartOrPauseListener {
    void start(FileInfo fileInfo,int position);
    void pause(FileInfo fileInfo,int position);
}
