package com.flyman.app.downloadapplication.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 线程对象
 */
public class ThreadInfo implements Serializable, Cloneable {
    private static final long serialVersionUID = 369285298572941L;  //最好是显式声明ID(用以克隆对象)
    private int threadId;
    private String fileInfoId;
    private long length;
    private long start;
    private long end;
    private long finishLength;//完成长度
    private boolean isFinished;

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getFileInfoId() {
        return fileInfoId;
    }

    public void setFileInfoId(String fileInfoId) {
        this.fileInfoId = fileInfoId;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinishLength() {
        return finishLength;
    }

    public void setFinishLength(long finishLength) {
        this.finishLength = finishLength;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public interface ThreadInfoColumnName {
        String threadId = "threadId";
        String fileInfoId = "fileInfoId";
        String length = "length";
        String start = "start";
        String end = "end";
        String finishLength = "finishLength";
        String isFinished = "isFinished";
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "threadId=" + threadId +
                ", fileInfoId='" + fileInfoId + '\'' +
                ", length=" + length +
                ", start=" + start +
                ", end=" + end +
                ", finishLength=" + finishLength +
                ", isFinished=" + isFinished +
                '}';
    }

    public ThreadInfo myClone() {
        ThreadInfo mThreadInfo = null;
        try {
            // 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            // 将流序列化成对象
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            mThreadInfo = (ThreadInfo) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mThreadInfo;
    }
}
