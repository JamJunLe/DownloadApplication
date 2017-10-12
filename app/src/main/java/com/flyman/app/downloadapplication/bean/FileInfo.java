package com.flyman.app.downloadapplication.bean;

import java.io.Serializable;
import java.util.List;

public class FileInfo implements Serializable {
    private String fileInfoId;
    private String url;
    private String name;
    private String sdPath;//sd卡位置
    private long length;//文件大小
    private long finishedLength;//文件已经下载的大小
    private long finishedPercent;//文件已经下载的百分比
    private long instantaneousLength;//瞬时下载量
    private boolean isPartial;
    private boolean isFinished;
    private List<ThreadInfo> ThreadInfos;//线程数

    public String getFileInfoId() {
        return fileInfoId;
    }

    public void setFileInfoId(String fileInfoId) {
        this.fileInfoId = fileInfoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSdPath() {
        return sdPath;
    }

    public void setSdPath(String sdPath) {
        this.sdPath = sdPath;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinishedLength() {
        return finishedLength;
    }

    public void setFinishedLength(long finishedLength) {
        this.finishedLength = finishedLength;
    }

    public long getFinishedPercent() {
        return finishedPercent;
    }

    public void setFinishedPercent(long finishedPercent) {
        this.finishedPercent = finishedPercent;
    }

    public long getInstantaneousLength() {
        return instantaneousLength;
    }

    public void setInstantaneousLength(long instantaneousLength) {
        this.instantaneousLength = instantaneousLength;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public List<ThreadInfo> getThreadInfos() {
        return ThreadInfos;
    }

    public void setThreadInfos(List<ThreadInfo> threadInfos) {
        ThreadInfos = threadInfos;
    }


    public interface FileInfoColumnName {
        String fileInfoId = "fileInfoId";
        String url = "url";
        String name = "name";
        String sdPath = "sdPath";
        String length = "length";
        String finishLength = "finishLength";
        String finishedPercent = "finishedPercent";
        String isFinished = "isFinished";
        String isPartial = "isPartial";
    }

    public boolean isPartial() {
        return isPartial;
    }

    public void setPartial(boolean partial) {
        isPartial = partial;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileInfoId='" + fileInfoId + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", sdPath='" + sdPath + '\'' +
                ", length=" + length +
                ", finishedLength=" + finishedLength +
                ", finishedPercent=" + finishedPercent +
                ", instantaneousLength=" + instantaneousLength +
                ", isPartial=" + isPartial +
                ", isFinished=" + isFinished +
                ", ThreadInfos=" + ThreadInfos +
                '}';
    }
}
