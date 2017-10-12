package com.flyman.app.downloadapplication.db.dao;

import java.util.List;

public interface IDao<T> {
    int FLAG_QUERY_ALL = 0;//查询所有
    int FLAG_QUERY_SINGLE = 1;//查询单一文件
    int FLAG_QUERY_DOWNLOADING = 2;//查询正在下载
    int FLAG_QUERY_DOWNLOADED = 3;//查询已经下载
    int FLAG_DELETE = 4;
    int FLAG_UPDATE = 5;
    int FLAG_INSERT = 6;
    boolean insert(T t);
    boolean delete(T t);
    boolean update(T t);
    List<T> query(T t);
    List<T> query(T t,int flag);
}
