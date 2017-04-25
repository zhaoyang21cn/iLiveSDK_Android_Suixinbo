package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;

import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.av.extra.effect.AVVideoEffect;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.CrashHandler;



/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusinessHelper {
    private static String TAG = "InitBusinessHelper";

    private InitBusinessHelper() {
    }
    private static String appVer = "1.0";


    /**
     * 初始化App
     */
    public static void initApp(final Context context) {
        //初始化avsdk imsdk
        TIMManager.getInstance().disableBeaconReport();
        MySelfInfo.getInstance().getCache(context);
        switch(MySelfInfo.getInstance().getLogLevel()){
        case OFF:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.OFF);
            break;
        case WARN:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.WARN);
            break;
        case DEBUG:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.DEBUG);
            break;
        case INFO:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.INFO);
            break;
        default:
            break;
        }

        // 初始化ILiveSDK
        ILiveSDK.getInstance().initSdk(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE);
        // 初始化直播模块
        ILVLiveConfig liveConfig = new ILVLiveConfig();
        liveConfig.setLiveMsgListener(MessageEvent.getInstance());
        ILVLiveManager.getInstance().init(liveConfig);

        // 初始化P图模块
        AVVideoEffect.getInstance(context).init(context, "ptusdk_suixinbo.licence");

//        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
//            @Override
//            public void onForceOffline(int error, String message) {
//                switch (error){
//                case ILiveConstants.ERR_KICK_OUT:
//                    SxbLog.w(TAG, "onForceOffline->entered!");
//                    SxbLog.d(TAG, LogConstants.ACTION_HOST_KICK + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "on force off line");
//                    Toast.makeText(context, context.getString(R.string.tip_force_offline), Toast.LENGTH_SHORT).show();
//                    MySelfInfo.getInstance().clearCache(context);
//                    context.sendBroadcast(new Intent(Constants.BD_EXIT_APP));
//                    break;
//                case ILiveConstants.ERR_EXPIRE:
//                    SxbLog.w(TAG, "onUserSigExpired->entered!");
//                    Toast.makeText(context, "onUserSigExpired|"+message, Toast.LENGTH_SHORT).show();
//                    context.sendBroadcast(new Intent(Constants.BD_EXIT_APP));
//                    break;
//                }
//            }
//        });

        //初始化CrashReport系统
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
    }
}
