package com.flyman.app.downloadapplication.task;

public interface TaskCallback<T> {
    void onTaskPre(int flag);
    void onUpdateTask(int flag,T t);
    void onTaskSuccess(int flag, T t);
    void onTaskFail(int flag);
}
