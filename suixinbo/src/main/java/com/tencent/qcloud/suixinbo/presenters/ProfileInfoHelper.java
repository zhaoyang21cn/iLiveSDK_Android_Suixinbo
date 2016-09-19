package com.tencent.qcloud.suixinbo.presenters;

import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.util.List;

/**
 * 用户资料获取
 */
public class ProfileInfoHelper {
    private String TAG = getClass().getName();
    private ProfileView mView;

    public ProfileInfoHelper(ProfileView view){
        mView = view;
    }

    public void getMyProfile(){
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                SxbLog.w(TAG, "getMyProfile->error:"+i+","+s);
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                mView.updateProfileInfo(timUserProfile);
            }
        });
    }

    public void setMyNickName(String nickName){
        TIMFriendshipManager.getInstance().setNickName(nickName, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                SxbLog.w(TAG, "setNickName->error:" + i + "," + s);
            }

            @Override
            public void onSuccess() {
                getMyProfile();
            }
        });
    }

    public void setMySign(String sign){
        TIMFriendshipManager.getInstance().setSelfSignature(sign, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                SxbLog.w(TAG, "setSelfSignature->error:" + i + "," + s);
            }

            @Override
            public void onSuccess() {
                getMyProfile();
            }
        });
    }

    public void setMyAvator(String url){
        TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                SxbLog.w(TAG, "setMyAvator->error:" + i + "," + s);
            }

            @Override
            public void onSuccess() {
                getMyProfile();
            }
        });
    }

    public void getUsersInfo(final int requestCode, List<String> users){
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                SxbLog.w(TAG, "getUsersInfo->error:" + i + "," + s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> profiles) {
                mView.updateUserInfo(requestCode, profiles);
            }
        });
    }
}
