package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LogoutView;
import com.tencent.qcloud.suixinbo.utils.LogConstants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

/**
 * 登录的数据处理类
 */
public class LoginHelper extends Presenter {
    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private LoginView mLoginView;
    private LogoutView mLogoutView;

    public LoginHelper(Context context) {
        mContext = context;
    }

    public LoginHelper(Context context, LoginView loginView) {
        mContext = context;
        mLoginView = loginView;
    }

    public LoginHelper(Context context, LogoutView logoutView) {
        mContext = context;
        mLogoutView = logoutView;
    }


    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig  用户签名
     */
    public void imLogin(final String identify, String userSig) {
        //TODO 新方式登录ILiveSDK
        ILiveLoginManager.getInstance().iLiveLogin(identify, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + identify + LogConstants.DIV + "request room id");
//                getMyRoomNum();
                if (mLoginView != null)
                    mLoginView.loginSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + "tilvblogin failed:" + module + "|" + errCode + "|" + errMsg);
                if (mLoginView != null)
                    mLoginView.loginFail();
            }
        });
    }


    /**
     * 退出imsdk <p> 退出成功会调用退出AVSDK
     */
    public void imLogout() {
        //TODO 新方式登出ILiveSDK
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "IMLogout succ !");
                //清除本地缓存
                MySelfInfo.getInstance().clearCache(mContext);
                mLogoutView.logoutSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "IMLogout fail ：" + module + "|" + errCode + " msg " + errMsg);
            }
        });
    }

    /**
     * 登录TLS账号系统
     */
    public void tlsLogin(final String id, final String password) {
//        ILiveLoginManager.getInstance().tlsLogin(id, password, new ILiveCallBack<String>() {
//            @Override
//            public void onSuccess(String userSig) {
//                MySelfInfo.getInstance().setId(id);
//                MySelfInfo.getInstance().setUserSig(userSig);
//                imLogin(id, userSig);
//            }
//
//            @Override
//            public void onError(String module, int errCode, String errMsg) {
//                Toast.makeText(mContext, "OnPwdLoginFail|" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.ResquestResult result = UserServerHelper.getInstance().loginId(id, password);
                if (result != null && result.getErrorCode() == 0) {
                    MySelfInfo.getInstance().setId(id);
                    MySelfInfo.getInstance().setUserSig(UserServerHelper.getInstance().getSig());
                    MySelfInfo.getInstance().writeToCache(mContext);
//                    UserServerHelper.getInstance().logoutId(id);
                    imLogin(id, MySelfInfo.getInstance().getUserSig());
                }
            }
        }).start();

    }


    /**
     * 在TLS模块注册一个账号
     */
    public void tlsRegister(final String id, final String psw) {

//        ILiveLoginManager.getInstance().tlsRegister(id, psw, new ILiveCallBack() {
//            @Override
//            public void onSuccess(Object data) {
//                Toast.makeText(mContext, id + " register a user succ !  ", Toast.LENGTH_SHORT).show();
//                //继续登录流程
//                tlsLogin(id, psw);
//            }
//
//            @Override
//            public void onError(String module, int errCode, String errMsg) {
//                Toast.makeText(mContext, "tlsRegister->failed|"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.getInstance().registerId(id, psw);
            }
        }).start();
    }


    /**
     * 登出
     * @param id
     */
    public void tlsLogout(final String id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.ResquestResult result = UserServerHelper.getInstance().logoutId(id);
                if (result != null && result.getErrorCode() == 0) {

                }
            }
        }).start();
    }

    /**
     * 向用户服务器获取自己房间号
     */
    private void getMyRoomNum() {
        if (MySelfInfo.getInstance().getMyRoomNum() == -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserServerHelper.getInstance().getMyRoomId(mContext);
                }
            }).start();
        } else {
            SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "request room id"
                    + LogConstants.DIV + LogConstants.STATUS.SUCCEED + LogConstants.DIV + "get room id from local " + MySelfInfo.getInstance().getMyRoomNum());
        }
    }


    @Override
    public void onDestory() {
        mLoginView = null;
        mLogoutView = null;
        mContext = null;
    }
}
