package com.tencent.qcloud.suixinbo.utils;

import android.util.Log;

import com.tencent.qcloud.suixinbo.model.MySelfInfo;

import java.util.Calendar;

/**
 * 日志输出
 */
public class SxbLog {
    public enum SxbLogLevel {
        OFF,
        ERROR,
        WARN,
        DEBUG,
        INFO
    }


    public static String ACTION_HOST_CREATE_ROOM = "clogs.host.createRoom";
    public static String ACTION_VIEWER_ENTER_ROOM = "clogs.viewer.enterRoom";
    public static String ACTION_VIEWER_QUIT_ROOM = "clogs.viewer.quitRoom";
    public static String ACTION_HOST_QUIT_ROOM = "clogs.host.quitRoom";
    public static String ACTION_VIEWER_SHOW = "clogs.viewer.upShow";
    public static String ACTION_VIEWER_UNSHOW = "clogs.viewer.unShow";
    public static String ACTION_HOST_KICK = "clogs.host.kick";


    static private SxbLogLevel level = SxbLogLevel.INFO;

    static public String[] getStringValues() {
        SxbLogLevel[] levels = SxbLogLevel.values();
        String[] stringValuse = new String[levels.length];
        for (int i = 0; i < levels.length; i++) {
            stringValuse[i] = levels[i].toString();
        }
        return stringValuse;
    }

    static public void setLogLevel(SxbLogLevel newLevel) {
        level = newLevel;
        w("Log", "change log level: " + newLevel);
    }

    public static void v(String strTag, String strInfo) {
        Log.v(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.INFO.ordinal()) {
            SxbLogImpl.writeLog("I", strTag, strInfo, null);
        }
    }

    public static void i(String strTag, String strInfo) {
        v(strTag, strInfo);
    }

    public static void d(String strTag, String strInfo) {
        Log.d(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.DEBUG.ordinal()) {
            SxbLogImpl.writeLog("D", strTag, strInfo, null);
        }
    }


    public static void w(String strTag, String strInfo) {
        Log.w(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.WARN.ordinal()) {
            SxbLogImpl.writeLog("W", strTag, strInfo, null);
        }
    }

    public static void e(String strTag, String strInfo) {
        Log.e(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.ERROR.ordinal()) {
            SxbLogImpl.writeLog("E", strTag, strInfo, null);
        }
    }

    public static void writeException(String strTag, String strInfo, Exception tr) {
        SxbLogImpl.writeLog("C", strTag, strInfo, tr);
    }

    public static String getTime() {

        long currentTimeMillis = System.currentTimeMillis();

        Log.v("Test", String.valueOf(currentTimeMillis));


        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(currentTimeMillis);

        String time = calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + ":" + calendar.get(Calendar.MILLISECOND);
        return time;
    }

    public static void standardEnterRoomLog(String TAG, String info, String success, String info2) {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info
                    + LogConstants.DIV + success + LogConstants.DIV + info2);
        } else {
            SxbLog.d(TAG, LogConstants.ACTION_VIEWER_ENTER_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info +
                    LogConstants.DIV + success + LogConstants.DIV + info2);
        }
    }


    public static void standardQuiteRoomLog(String TAG, String info, String success, String info2) {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            SxbLog.d(TAG, LogConstants.ACTION_HOST_QUIT_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info
                    + LogConstants.DIV + success + LogConstants.DIV + info2);
        } else {
            SxbLog.d(TAG, LogConstants.ACTION_VIEWER_QUIT_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info +
                    LogConstants.DIV + success + LogConstants.DIV + info2);
        }
    }

    /**
     * 上麦LOG
     * @param TAG
     * @param info
     * @param success
     * @param info2
     */
    public static void standardMemberShowLog(String TAG, String info, String success, String info2) {
            SxbLog.d(TAG, LogConstants.ACTION_VIEWER_UNSHOW + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info
                    + LogConstants.DIV +success + LogConstants.DIV + info2);
    }

    /**
     * 下麦LOG
     * @param TAG
     * @param info
     * @param success 成功与否
     * @param info2
     */
    public static void standardMemberUnShowLog(String TAG, String info, String success, String info2) {
        SxbLog.d(TAG, LogConstants.ACTION_VIEWER_UNSHOW + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info
                + LogConstants.DIV + success + LogConstants.DIV + info2);
    }


    public static void standardLog(String TAG, String type, String info, String success, String info2) {
        SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + info
                + LogConstants.DIV + success + LogConstants.DIV + info2);
    }

}
