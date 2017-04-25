package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;

import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.qcloud.suixinbo.model.MemberInfo;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.MembersDialogView;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 16/5/24.
 */
public class GetMemberListHelper extends Presenter {
    private Context mContext;
    private MembersDialogView mMembersDialogView;
    private static final String TAG = GetMemberListHelper.class.getSimpleName();
    private ArrayList<MemberInfo> mDialogMembers = new ArrayList<MemberInfo>();

    public GetMemberListHelper(Context context, MembersDialogView dialogView) {
        mContext = context;
        mMembersDialogView = dialogView;
    }

    public void getMemberList() {
        TIMGroupManager.getInstance().getGroupMembers("" + MySelfInfo.getInstance().getMyRoomNum(), new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                SxbLog.i(TAG, "get MemberList ");
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                SxbLog.i(TAG, "get MemberList ");
                getMemberListInfo(timGroupMemberInfos);

            }
        });
    }


    /**
     * 拉取成员列表信息
     *
     * @param timGroupMemberInfos
     */
    private void getMemberListInfo(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        mDialogMembers.clear();
        for (TIMGroupMemberInfo item : timGroupMemberInfos) {
            if (item.getUser().equals(MySelfInfo.getInstance().getId())) {
                continue;
            }
            MemberInfo member = new MemberInfo();
            member.setUserId(item.getUser());
            //if (QavsdkControl.getInstance().containIdView(item.getUser())) {
            AVVideoView avVideoView = ILiveRoomManager.getInstance().getRoomView().getUserAvVideoView(item.getUser(),  AVView.VIDEO_SRC_TYPE_CAMERA);
            if (null != avVideoView && avVideoView.isRendering()){
                member.setIsOnVideoChat(true);
            }
            mDialogMembers.add(member);

        }

        if (null != mMembersDialogView)
            mMembersDialogView.showMembersList(mDialogMembers);
    }


    @Override
    public void onDestory() {
        mMembersDialogView =null;
        mContext = null;
    }
}
