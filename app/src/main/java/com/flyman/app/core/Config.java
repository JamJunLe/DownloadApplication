package com.flyman.app.core;

import android.os.Environment;

public interface Config {
    String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();//sd卡位置
    String SD_DOWNLOAD_PATH = SD_PATH + "/FastDownload";//默认的下载位置
    int DEFAULT_THREAD_COUNT = 4;//默认的下载线程数
    long UPDATE_UI_TIME_DELAY = 1000;//ui的刷新的时间 +1s
    String INTEGER = " INTEGER";//(INT INTEGER TINYINT SMALLINT MEDIUMINT BIGINT UNSIGNED BIGINT INT2 INT8)
    String TEXT = " TEXT";//CHARACTER(20) VARCHAR(255) VARYING CHARACTER(255) NCHAR(55) NATIVE CHARACTER(70) NVARCHAR(100) TEXT CLOB
    String NUMERIC = " NUMERIC";//NUMERIC DECIMAL(10,5) BOOLEAN DATE DATETIME
    String REAL = " REAL";//REAL DOUBLE DOUBLE PRECISION FLOAT
    String NONE = " NONE";//BLOB
    String ID_PRIMARY_KEY = "_id integer primary key";//BLOB
}
