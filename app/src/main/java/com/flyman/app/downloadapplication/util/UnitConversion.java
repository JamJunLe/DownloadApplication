package com.flyman.app.downloadapplication.util;

public class UnitConversion {


    public static String humanReadableByteCount4File(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "");
        return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String humanReadableByteCount4File(long bytes) {
        return humanReadableByteCount4File(bytes, false);
    }

    /**
     * 每秒下载量(下载速度)
     *
     * @param
     * @return String
     */
    public static String humanReadableByteCount4Network(long bytes, long time) {
        int unit = 1024;
        time = time/1000;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "";
        return String.format("%.2f %sB", (bytes / Math.pow(unit, exp))/time, pre)+"/s";
    }

    /**
     * 每秒下载量(下载速度)
     *
     * @param bytes
     *            long
     * @return String
     */
    public static String humanReadableByteCount4Network(long bytes) {
        return humanReadableByteCount4Network(bytes, 1000);
    }

//    原始的函数方法
//    public static String humanReadableByteCount(long bytes, boolean si) {
//        int unit = si ? 1000 : 1024;
//        if (bytes < unit) return bytes + " B";
//        int exp = (int) (Math.log(bytes) / Math.log(unit));
//        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
//        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
//    }
//
//    public static String readableFileSize(long size) {
//        if(size <= 0) return "0";
//        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
//        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
//        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
//    }
}
