package com.tencent.tdemofm;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于开放聊天室的频道列表(基于IM聊天室的房间管理)
 */
public class DiscussSer {
    private final static String TAG = "DiscussSer";
    private String mGroupId;

    public interface onInitListener{
        void onInitComplete(int result);
    }

    public interface onGetStatusList{
        void onQueryComplete(List<String> list);
    }

    public DiscussSer(String strGroupId, final onInitListener listener){
        mGroupId = strGroupId;

        TIMGroupManager.getInstance().applyJoinGroup(mGroupId, "Join Discuss", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "applyJoinGroup->failed:"+i+"|"+s);
                if (10015 == i){    // 聊天室不存在
                    createDiscuss(listener);
                }else if (10013 == i){  // 已加入聊天室
                    listener.onInitComplete(ILiveConstants.NO_ERR);
                }
            }

            @Override
            public void onSuccess() {
                Log.v(TAG, "applyJoinGroup->success");
                listener.onInitComplete(ILiveConstants.NO_ERR);
            }
        });
    }

    private void createDiscuss(final onInitListener listener){
        TIMGroupManager.getInstance().createGroup("ChatRoom",
                new ArrayList<String>(),
                "DiscussSer-" + mGroupId,
                mGroupId,
                new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "CreateGroup->failed:"+i+"|"+s);
                        listener.onInitComplete(i);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "CreateGroup->success id:"+s+"/"+mGroupId);
                        listener.onInitComplete(ILiveConstants.NO_ERR);
                    }
                });
    }

    public void quaryStatus(final onGetStatusList listener){
        TIMGroupManager.getInstance().getGroupMembers(mGroupId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "getGroupMembers->failed id:"+i+"|"+s);
                listener.onQueryComplete(null);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                List<String> list = new ArrayList<String>();
                for (TIMGroupMemberInfo info : timGroupMemberInfos){
                    if (!TextUtils.isEmpty(info.getNameCard()) && !list.contains(info.getNameCard())){
                        list.add(info.getNameCard());
                    }
                }
                listener.onQueryComplete(list);
            }
        });
    }

    public void modifyStatus(final String status){
        TIMGroupManager.getInstance().modifyGroupMemberInfoSetNameCard(mGroupId, ILiveLoginManager.getInstance().getMyUserId(),
                status, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "modifyStatus->modify name card failed:"+i+"|"+s);
                    }

                    @Override
                    public void onSuccess() {
                        Log.v(TAG, "modifyStatus->modify status:"+status);
                    }
                });
    }
}
