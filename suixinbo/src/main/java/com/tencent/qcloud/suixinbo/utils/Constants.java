package com.tencent.qcloud.suixinbo.utils;


import com.tencent.av.sdk.AVRoomMulti;

/**
 * 静态函数
 */
public class Constants {
    public static final String BD_EXIT_APP = "bd_sxb_exit";

    public static final String USER_INFO = "user_info";

    public static final String USER_ID = "user_id";

    public static final String USER_SIG = "user_sig";

    public static final String USER_NICK = "user_nick";

    public static final String USER_SIGN = "user_sign";

    public static final String USER_AVATAR = "user_avatar";

    public static final String USER_ROOM_NUM = "user_room_num";

    public static final String LIVE_ANIMATOR = "live_animator";
    public static final String LOG_LEVEL = "log_level";

//    //
//    public static final int ACCOUNT_TYPE = 792;
//    //    //sdk appid 由腾讯分配
//    public static final int SDK_APPID = 1400001533;

    public static final int SDK_APPID = 1400001692;

    public static final int ACCOUNT_TYPE = 884;

    public static final String ID_STATUS = "id_status";

    public static final int HOST = 1;

    public static final int MEMBER = 0;

    public static final int VIDEO_VIEW_MAX = 4;

    public static final int LOCATION_PERMISSION_REQ_CODE = 1;
    public static final int WRITE_PERMISSION_REQ_CODE = 2;


    public static final String APPLY_CHATROOM = "申请加入";

    public static final int IS_ALREADY_MEMBER = 10013;

    public static final int TEXT_TYPE = 0;
    public static final int MEMBER_ENTER = 1;
    public static final int MEMBER_EXIT = 2;
    public static final int HOST_LEAVE = 3;
    public static final int HOST_BACK = 4;

    public static final String ROOT_DIR = "/sdcard/Suixinbo/";


    public static final int AVIMCMD_MULTI = 0x800;             // 多人互动消息类型

    public static final int AVIMCMD_MUlTI_HOST_INVITE = AVIMCMD_MULTI + 1;         // 多人主播发送邀请消息, C2C消息
    public static final int AVIMCMD_MULTI_CANCEL_INTERACT = AVIMCMD_MUlTI_HOST_INVITE + 1;       // 断开互动，Group消息，带断开者的imUsreid参数
    public static final int AVIMCMD_MUlTI_JOIN = AVIMCMD_MULTI_CANCEL_INTERACT + 1;       // 多人互动方收到AVIMCMD_Multi_Host_Invite多人邀请后，同意，C2C消息
    public static final int AVIMCMD_MUlTI_REFUSE = AVIMCMD_MUlTI_JOIN + 1;      // 多人互动方收到AVIMCMD_Multi_Invite多人邀请后，拒绝，C2C消息

    public static final int AVIMCMD_Multi_Host_EnableInteractMic = AVIMCMD_MUlTI_REFUSE + 1;  // 主播打开互动者Mic，C2C消息
    public static final int AVIMCMD_Multi_Host_DisableInteractMic = AVIMCMD_Multi_Host_EnableInteractMic + 1;// 主播关闭互动者Mic，C2C消息
    public static final int AVIMCMD_Multi_Host_EnableInteractCamera = AVIMCMD_Multi_Host_DisableInteractMic + 1; // 主播打开互动者Camera，C2C消息
    public static final int AVIMCMD_Multi_Host_DisableInteractCamera = AVIMCMD_Multi_Host_EnableInteractCamera + 1; // 主播打开互动者Camera，C2C消息
    public static final int AVIMCMD_MULTI_HOST_CANCELINVITE = AVIMCMD_Multi_Host_DisableInteractCamera + 1;
    public static final int AVIMCMD_MULTI_HOST_CONTROLL_CAMERA = AVIMCMD_MULTI_HOST_CANCELINVITE + 1;
    public static final int AVIMCMD_MULTI_HOST_CONTROLL_MIC = AVIMCMD_MULTI_HOST_CONTROLL_CAMERA + 1;

    public static final int AVIMCMD_Text = -1;         // 普通的聊天消息

    public static final int AVIMCMD_None = AVIMCMD_Text + 1;               // 无事件

    // 以下事件为TCAdapter内部处理的通用事件
    public static final int AVIMCMD_EnterLive = AVIMCMD_None + 1;          // 用户加入直播, Group消息  1
    public static final int AVIMCMD_ExitLive = AVIMCMD_EnterLive + 1;         // 用户退出直播, Group消息  2
    public static final int AVIMCMD_Praise = AVIMCMD_ExitLive + 1;           // 点赞消息, Demo中使用Group消息  3
    public static final int AVIMCMD_Host_Leave = AVIMCMD_Praise + 1;         // 主播离开, Group消息 ： 4
    public static final int AVIMCMD_Host_Back = AVIMCMD_Host_Leave + 1;         // 主播回来, Demo中使用Group消息 ： 5

    public static final String CMD_KEY = "userAction";
    public static final String CMD_PARAM = "actionParam";


    public static final long HOST_AUTH = AVRoomMulti.AUTH_BITS_DEFAULT;//权限位；TODO：默认值是拥有所有权限。
    public static final long VIDEO_MEMBER_AUTH = AVRoomMulti.AUTH_BITS_DEFAULT;//权限位；TODO：默认值是拥有所有权限。
    public static final long NORMAL_MEMBER_AUTH = AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO;


    public static final String HOST_ROLE = "Host";
    public static final String VIDEO_MEMBER_ROLE = "VideoMember";
    public static final String NORMAL_MEMBER_ROLE = "NormalMember";
}
