package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.QavsdkApplication;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LogoutView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.LogConstants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * 登录的数据处理类
 */
public class LoginHelper extends Presenter {
    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private LoginView mLoginView;
    private LogoutView mLogoutView;
    private int RoomId = -1;

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
        ILiveSDK.getInstance().initSdk(QavsdkApplication.getContext(), Constants.SDK_APPID, Constants.ACCOUNT_TYPE);
        ILiveLoginManager.getInstance().iLiveLogin(identify, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + identify + LogConstants.DIV + "request room id");
                getMyRoomNum();
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
     * 退出imsdk
     * <p>
     * 退出成功会调用退出AVSDK
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
                SxbLog.e(TAG, "IMLogout fail ：" +module + "|" + errCode + " msg " + errMsg);
            }
        });
    }

    /**
     * 登录TLS账号系统
     *
     * @param id
     * @param password
     */
    public void tlsLogin(String id, String password) {
        int ret = InitBusinessHelper.getmLoginHelper().TLSPwdLogin(id, password.getBytes(), new TLSPwdLoginListener() {
            @Override
            public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {//获取用户信息
//                Toast.makeText(mContext, "TLS login succ ! " + tlsUserInfo.identifier, Toast.LENGTH_SHORT).show();
//                SxbLog.i(TAG, "TLS OnPwdLoginSuccess " + tlsUserInfo.identifier);
                String userSig = InitBusinessHelper.getmLoginHelper().getUserSig(tlsUserInfo.identifier);
                MySelfInfo.getInstance().setId(tlsUserInfo.identifier);
                MySelfInfo.getInstance().setUserSig(userSig);
                imLogin(tlsUserInfo.identifier, userSig);
            }

            @Override
            public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {

            }

            @Override
            public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {

            }

            @Override
            public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
                SxbLog.e(TAG, "OnPwdLoginFail " + tlsErrInfo.Msg);
                Toast.makeText(mContext, "OnPwdLoginFail：\n" + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
                SxbLog.e(TAG, "OnPwdLoginTimeout " + tlsErrInfo.Msg);
                Toast.makeText(mContext, "OnPwdLoginTimeout：\n" + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }
        });
        if (ret != -1001) {
            Toast.makeText(mContext, "input invalid !", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 在TLS模块注册一个账号
     *
     * @param id
     * @param psw
     */
    public void tlsRegister(final String id, final String psw) {
        int ret = InitBusinessHelper.getmAccountHelper().TLSStrAccReg(id, psw, new TLSStrAccRegListener() {
            @Override
            public void OnStrAccRegSuccess(TLSUserInfo tlsUserInfo) {
                Toast.makeText(mContext, tlsUserInfo.identifier + " register a user succ !  ", Toast.LENGTH_SHORT).show();
                //继续登录流程
                tlsLogin(id, psw);
            }

            @Override
            public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
                Toast.makeText(mContext, " register a user fail ! " + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
                Toast.makeText(mContext, " register timeout ! " + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }
        });
        if (ret != -1001) {
            Toast.makeText(mContext, "input invalid !", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 向用户服务器获取自己房间号
     */
    private void getMyRoomNum() {
        if (MySelfInfo.getInstance().getMyRoomNum() == -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OKhttpHelper.getInstance().getMyRoomId(mContext);
                }
            }).start();
        }else{
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
