package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveMemStatusLisenter;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveConfig;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveManager;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.LiveInfoJson;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.EnterQuiteRoomView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.LogConstants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 进出房间Presenter
 */
public class EnterLiveHelper extends Presenter implements ILiveMemStatusLisenter {
    private EnterQuiteRoomView mStepInOutView;
    private Context mContext;
    private static final String TAG = EnterLiveHelper.class.getSimpleName();
    private ArrayList<String> video_ids = new ArrayList<String>();

    private static final int TYPE_MEMBER_CHANGE_IN = 1;//进入房间事件。
    private static final int TYPE_MEMBER_CHANGE_OUT = 2;//退出房间事件。
    private static final int TYPE_MEMBER_CHANGE_HAS_CAMERA_VIDEO = 3;//有发摄像头视频事件。
    private static final int TYPE_MEMBER_CHANGE_NO_CAMERA_VIDEO = 4;//无发摄像头视频事件。
    private static final int TYPE_MEMBER_CHANGE_HAS_AUDIO = 5;//有发语音事件。
    private static final int TYPE_MEMBER_CHANGE_NO_AUDIO = 6;//无发语音事件。
    private static final int TYPE_MEMBER_CHANGE_HAS_SCREEN_VIDEO = 7;//有发屏幕视频事件。
    private static final int TYPE_MEMBER_CHANGE_NO_SCREEN_VIDEO = 8;//无发屏幕视频事件。


    public EnterLiveHelper(Context context, EnterQuiteRoomView view) {
        mContext = context;
        mStepInOutView = view;
    }


    /**
     * 进入一个直播房间流程
     */
    public void startEnterRoom() {
        //TODO 配置房间
        ILVLiveConfig liveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(liveConfig);
        if (MySelfInfo.getInstance().isCreateRoom() == true) {
            //TODO 新方式创建房间
            ILiveRoomOption hostOption = new ILiveRoomOption(MySelfInfo.getInstance().getId()).
                    controlRole("Host")
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)
                    .setRoomMemberStatusLisenter(this)
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);
            ILVLiveManager.getInstance().createRoom(MySelfInfo.getInstance().getMyRoomNum(), hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom->create room sucess");
                    mStepInOutView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom->create room failed:" + module + "|" + errCode + "|" + errMsg);
                    if (null != mStepInOutView) {
                        mStepInOutView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                    }
                }
            });
            //createLive();
        } else {
            //TODO 新方式加入房间
            ILiveRoomOption memberOption = new ILiveRoomOption(CurLiveInfo.getHostID())
                    .autoCamera(false)
                    .controlRole("NormalMember")
                    .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                    .autoMic(false);
            ILVLiveManager.getInstance().joinRoom(CurLiveInfo.getRoomNum(), memberOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom->join room sucess");
                    mStepInOutView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    ILiveLog.d(TAG, "ILVB-DBG|startEnterRoom->join room failed:" + module + "|" + errCode + "|" + errMsg);
                    if (null != mStepInOutView) {
                        mStepInOutView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                    }
                }
            });
            SxbLog.i(TAG, "joinLiveRoom startEnterRoom ");
            //joinLive(CurLiveInfo.getRoomNum());
        }

    }


    /**
     * 1_5上传房间信息
     */
    public void notifyServerCreateRoom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject liveInfo = null;
                try {
                    liveInfo = new JSONObject();
                    if (TextUtils.isEmpty(CurLiveInfo.getTitle())) {
                        liveInfo.put("title", mContext.getString(R.string.text_live_default_title));
                    } else {
                        liveInfo.put("title", CurLiveInfo.getTitle());
                    }
                    liveInfo.put("cover", CurLiveInfo.getCoverurl());
                    liveInfo.put("chatRoomId", CurLiveInfo.getChatRoomId());
                    liveInfo.put("avRoomId", CurLiveInfo.getRoomNum());
                    JSONObject hostinfo = new JSONObject();
                    hostinfo.put("uid", MySelfInfo.getInstance().getId());
                    hostinfo.put("avatar", MySelfInfo.getInstance().getAvatar());
                    hostinfo.put("username", MySelfInfo.getInstance().getNickName());
                    liveInfo.put("host", hostinfo);
                    JSONObject lbs = new JSONObject();
                    lbs.put("longitude", CurLiveInfo.getLong1());
                    lbs.put("latitude", CurLiveInfo.getLat1());
                    lbs.put("address", CurLiveInfo.getAddress());
                    liveInfo.put("lbs", lbs);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (liveInfo != null) {
                    SxbLog.standardEnterRoomLog(TAG, "upload room info to serve", "", "room id " + CurLiveInfo.getRoomNum());
                    OKhttpHelper.getInstance().notifyServerNewLiveInfo(liveInfo);
                }

            }
        }).start();


    }


    /**
     * 退出房间
     */
    public void quiteLive() {
        //退出IM房间

        //退出AV房间
        //quiteAVRoom();
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-DBG|quitRoom->success");
                CurLiveInfo.setCurrentRequestCount(0);
                //通知结束
                notifyServerLiveEnd();
                if (null != mStepInOutView) {
                    mStepInOutView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-DBG|quitRoom->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mStepInOutView) {
                    mStepInOutView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });

    }

    private NotifyServerLiveEnd liveEndTask;

    /**
     * 通知用户UserServer房间
     */
    private void notifyServerLiveEnd() {
        liveEndTask = new NotifyServerLiveEnd();
        liveEndTask.execute(MySelfInfo.getInstance().getId());
    }

    @Override
    public void onDestory() {
        mStepInOutView = null;
        mContext = null;
    }

    class NotifyServerLiveEnd extends AsyncTask<String, Integer, LiveInfoJson> {

        @Override
        protected LiveInfoJson doInBackground(String... strings) {
            return OKhttpHelper.getInstance().notifyServerLiveStop(strings[0]);
        }

        @Override
        protected void onPostExecute(LiveInfoJson result) {
        }
    }


    @Override
    public boolean onEndpointsUpdateInfo(int eventid, String[] updateList) {
        SxbLog.d(TAG, "ILVB-DBG|onEndpointsUpdateInfo. eventid = " + eventid + "/" + mStepInOutView);
        if (null == mStepInOutView) {
            return false;
        }

        switch (eventid) {
            case TYPE_MEMBER_CHANGE_IN:
                SxbLog.i(TAG, "stepin id  " + updateList.length);
                mStepInOutView.memberJoinLive(updateList);

                break;
            case TYPE_MEMBER_CHANGE_HAS_CAMERA_VIDEO:
                video_ids.clear();
                for (String id : updateList) {
                    video_ids.add(id);
                    SxbLog.i(TAG, "camera id " + id);
                }
                Intent intent = new Intent(Constants.ACTION_CAMERA_OPEN_IN_LIVE);
                intent.putStringArrayListExtra("ids", video_ids);
                mContext.sendBroadcast(intent);
                break;
            case TYPE_MEMBER_CHANGE_NO_CAMERA_VIDEO: {

                ArrayList<String> close_ids = new ArrayList<String>();
                String ids = "";
                for (String id : updateList) {
                    close_ids.add(id);
                    ids = ids + " " + id;

                }
                SxbLog.standardMemberShowLog(TAG, "close camera callback", "" + LogConstants.STATUS.SUCCEED, "close ids " + ids);

                Intent closeintent = new Intent(Constants.ACTION_CAMERA_CLOSE_IN_LIVE);
                closeintent.putStringArrayListExtra("ids", close_ids);
                mContext.sendBroadcast(closeintent);
            }
            break;
            case TYPE_MEMBER_CHANGE_HAS_AUDIO:
                break;

            case TYPE_MEMBER_CHANGE_OUT:
                mStepInOutView.memberQuiteLive(updateList);
                break;
            default:
                break;
        }

        return false;
    }
}