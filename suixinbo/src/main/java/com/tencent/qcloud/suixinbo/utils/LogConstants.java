package com.tencent.qcloud.suixinbo.utils;

/**
 * 日志相关常量
 */
public class LogConstants {

    public static String DIV = "|";

    public static String ACTION_HOST_CREATE_ROOM = "clogs.host.createRoom";
    public static String ACTION_VIEWER_ENTER_ROOM = "clogs.viewer.enterRoom";
    public static String ACTION_VIEWER_QUIT_ROOM = "clogs.viewer.quitRoom";
    public static String ACTION_VIEWER_SHOW = "clogs.viewer.upShow";
    public static String ACTION_VIEWER_UNSHOW = "clogs.viewer.unShow";
    public static String ACTION_HOST_QUIT_ROOM = "clogs.host.quitRoom";
    public static String ACTION_HOST_KICK = "clogs.host.kick";


    public enum STATUS{
        SUCCEED,
        FAILED,
    }


}
