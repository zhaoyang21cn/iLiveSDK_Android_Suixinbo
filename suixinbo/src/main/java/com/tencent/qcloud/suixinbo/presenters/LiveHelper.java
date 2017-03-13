package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.av.TIMAvManager;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRecordOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.MemberID;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.LogConstants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * 直播控制类
 */
public class LiveHelper extends Presenter implements ILiveRoomOption.onRoomDisconnectListener, Observer {
    private final String TAG = "LiveHelper";
    private LiveView mLiveView;
    public Context mContext;
    private boolean bCameraOn = false;
    private boolean bMicOn = false;
    private boolean flashLgihtStatus = false;
    private long streamChannelID;
    private ApplyCreateRoom createRoomProcess;


    //获取
    private GetMemberListTask mGetMemListTask;

    class GetMemberListTask extends AsyncTask<String, Integer, ArrayList<MemberID>> {

        @Override
        protected ArrayList<MemberID> doInBackground(String... strings) {
            //1上报成员
            UserServerHelper.getInstance().reportMe(MySelfInfo.getInstance().getIdStatus(), 0);
            //2 拉取成员列表
            return UserServerHelper.getInstance().getMemberList();
        }

        @Override
        protected void onPostExecute(ArrayList<MemberID> result) {
            if (mLiveView != null)
                mLiveView.refreshMember(result);

        }
    }


    class ApplyCreateRoom extends AsyncTask<String, Integer, UserServerHelper.RequestBackInfo> {

        @Override
        protected UserServerHelper.RequestBackInfo doInBackground(String... strings) {

            return UserServerHelper.getInstance().applyCreateRoom(); //获取后台
        }

        @Override
        protected void onPostExecute(UserServerHelper.RequestBackInfo result) {
            if (result != null && result.getErrorCode() == 0) {
                createRoom();
            } else {
                Log.i(TAG, "ApplyCreateRoom onPostExecute: " + result.getErrorInfo());
            }
        }
    }


    /**
     * 申请房间
     */
    private void startCreateRoom() {
        createRoomProcess = new ApplyCreateRoom(); //申请房间
        createRoomProcess.execute();

    }


    /**
     * 拉取成员
     */
    public void pullMemberList() {
        mGetMemListTask = new GetMemberListTask(); //拉取成员
        mGetMemListTask.execute();

    }

    /**
     * 上报房间
     */
    private void NotifyServerLiveTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.getInstance().notifyCloseLive();
            }
        }).start();

    }


    public LiveHelper(Context context, LiveView liveview) {
        mContext = context;
        mLiveView = liveview;
        MessageEvent.getInstance().addObserver(this);
    }

    @Override
    public void onDestory() {
        mLiveView = null;
        mContext = null;
        MessageEvent.getInstance().deleteObserver(this);
        ILVLiveManager.getInstance().quitRoom(null);
    }

    /**
     * 进入房间
     */
    public void startEnterRoom() {
        if (MySelfInfo.getInstance().isCreateRoom() == true) {
            startCreateRoom();
        } else {
            joinRoom();
        }
    }

    public void switchRoom(){
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(CurLiveInfo.getHostID())
                .autoCamera(false)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(Constants.NORMAL_MEMBER_ROLE)
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);
        ILVLiveManager.getInstance().switchRoom(CurLiveInfo.getRoomNum(), memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|switchRoom->join room sucess");
                mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|switchRoom->join room failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });
        SxbLog.i(TAG, "switchRoom startEnterRoom ");
    }


    public void startExitRoom() {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-SXB|quitRoom->success");
                CurLiveInfo.setCurrentRequestCount(0);
                //通知结束
                NotifyServerLiveTask();
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-SXB|quitRoom->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });

    }

//    public void perpareQuitRoom(boolean bPurpose) {
//        if (bPurpose) {
//            sendGroupCmd(Constants.AVIMCMD_EXITLIVE, "");
//        }
//        mLiveView.readyToQuit();
//    }

    /**
     * 发送信令
     */

    public int sendGroupCmd(int cmd, String param) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        return sendCmd(customCmd);
    }

    public int sendC2CCmd(final int cmd, String param, String destId) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendCmd(customCmd);
    }

    /**
     * 打开闪光灯
     */
    public void toggleFlashLight() {
        AVVideoCtrl videoCtrl = ILiveSDK.getInstance().getAvVideoCtrl();
        if (null == videoCtrl) {
            return;
        }

        final Object cam = videoCtrl.getCamera();
        if ((cam == null) || (!(cam instanceof Camera))) {
            return;
        }
        final Camera.Parameters camParam = ((Camera) cam).getParameters();
        if (null == camParam) {
            return;
        }

        Object camHandler = videoCtrl.getCameraHandler();
        if ((camHandler == null) || (!(camHandler instanceof Handler))) {
            return;
        }

        //对摄像头的操作放在摄像头线程
        if (flashLgihtStatus == false) {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = true;
                    } catch (RuntimeException e) {
                        SxbLog.d("setParameters", "RuntimeException");
                    }
                }
            });
        } else {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = false;
                    } catch (RuntimeException e) {
                        SxbLog.d("setParameters", "RuntimeException");
                    }

                }
            });
        }
    }

    public void startRecord(ILiveRecordOption option) {
        ILiveRoomManager.getInstance().startRecordVideo(option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "start record success ");
                mLiveView.startRecordCallback(true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "start record error " + errCode + "  " + errMsg);
                mLiveView.startRecordCallback(false);
            }
        });
    }

    public void stopRecord() {
        ILiveRoomManager.getInstance().stopRecordVideo(new ILiveCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                SxbLog.d(TAG, "stopRecord->success");
                for (String url : data) {
                    SxbLog.d(TAG, "stopRecord->url:" + url);
                }
                mLiveView.stopRecordCallback(true, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "stopRecord->failed:" + module + "|" + errCode + "|" + errMsg);
                mLiveView.stopRecordCallback(false, null);
            }
        });
    }

    public void startPush(ILivePushOption option) {
        ILiveRoomManager.getInstance().startPushStream(option, new ILiveCallBack<TIMAvManager.StreamRes>() {
            @Override
            public void onSuccess(TIMAvManager.StreamRes data) {
                List<TIMAvManager.LiveUrl> liveUrls = data.getUrls();
                streamChannelID = data.getChnlId();
                mLiveView.pushStreamSucc(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "url error " + errCode + " : " + errMsg);
                Toast.makeText(mContext, "start stream error,try again " + errCode + " : " + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void stopPush() {
        ILiveRoomManager.getInstance().stopPushStream(streamChannelID, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.e(TAG, "stopPush->success");
                mLiveView.stopStreamSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "stopPush->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }


    @Override
    public void onRoomDisconnect(int errCode, String errMsg) {
        if (null != mLiveView) {
            mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        List<TIMMessage> list = (List<TIMMessage>) o;
        parseIMMessage(list);
    }


    /**
     * 上报房间信息
     */
    public void notifyNewRoomInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject liveInfo = null;
                try {
                    liveInfo = new JSONObject();
                    liveInfo.put("token", MySelfInfo.getInstance().getToken());

                    JSONObject room = new JSONObject();
                    if (TextUtils.isEmpty(CurLiveInfo.getTitle())) {
                        room.put("title", mContext.getString(R.string.text_live_default_title));
                    } else {
                        room.put("title", CurLiveInfo.getTitle());
                    }
                    room.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
                    room.put("type", "live");
                    room.put("groupid", "" + CurLiveInfo.getRoomNum());
                    room.put("cover", CurLiveInfo.getCoverurl());
                    room.put("appid", Constants.SDK_APPID);
                    room.put("device", 1);
                    room.put("videotype", 0);
                    liveInfo.put("room", room);

                    JSONObject lbs = new JSONObject();
                    lbs.put("longitude", CurLiveInfo.getLong1());
                    lbs.put("latitude", CurLiveInfo.getLat1());
                    lbs.put("address", CurLiveInfo.getAddress());
                    liveInfo.put("lbs", lbs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (liveInfo != null) {
                    SxbLog.standardEnterRoomLog(TAG, "upload room info to serve", "", "room id " + liveInfo.toString());
                    UserServerHelper.getInstance().reporNewtRoomInfo(liveInfo.toString());
                }

            }
        }).start();
    }

    /**
     * 上报录制信息
     */
    public void notifyNewRecordInfo(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject recordInfo = null;
                try {
                    recordInfo = new JSONObject();
                    recordInfo.put("token", MySelfInfo.getInstance().getToken());
                    recordInfo.put("roomnum", CurLiveInfo.getRoomNum());
                    recordInfo.put("uid", MySelfInfo.getInstance().getId());
                    recordInfo.put("name", name);
                    recordInfo.put("type", 0);
                    recordInfo.put("cover", CurLiveInfo.getCoverurl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (recordInfo != null) {
                    SxbLog.standardEnterRoomLog(TAG, "upload record info to serve", "", "record info " + recordInfo.toString());
                    UserServerHelper.getInstance().reporNewtRecordInfo(recordInfo.toString());
                }

            }
        }).start();
    }

    public void toggleCamera() {
        bCameraOn = !bCameraOn;
        SxbLog.d(TAG, "toggleCamera->change camera:" + bCameraOn);
        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), bCameraOn);
    }

    public void toggleMic() {
        bMicOn = !bMicOn;
        SxbLog.d(TAG, "toggleMic->change mic:" + bMicOn);
        ILiveRoomManager.getInstance().enableMic(bMicOn);
    }

    public boolean isMicOn() {
        return bMicOn;
    }

    public void upMemberVideo() {
        if (!ILiveRoomManager.getInstance().isEnterRoom()) {
            SxbLog.e(TAG, "upMemberVideo->with not in room");
        }
        ILVLiveManager.getInstance().upToVideoMember(Constants.VIDEO_MEMBER_ROLE, true, true, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.d(TAG, "upToVideoMember->success");
                bMicOn = true;
                bCameraOn = true;
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "upToVideoMember->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    public void downMemberVideo() {
        if (!ILiveRoomManager.getInstance().isEnterRoom()) {
            SxbLog.e(TAG, "downMemberVideo->with not in room");
        }
        ILVLiveManager.getInstance().downToNorMember(Constants.NORMAL_MEMBER_ROLE, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                bMicOn = false;
                bCameraOn = false;
                SxbLog.e(TAG, "downMemberVideo->onSuccess");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "downMemberVideo->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }


    private void createRoom() {
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(MySelfInfo.getInstance().getId())
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(Constants.HOST_ROLE)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);
        ILVLiveManager.getInstance().createRoom(MySelfInfo.getInstance().getMyRoomNum(), hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-SXB|startEnterRoom->create room sucess");
                bCameraOn = true;
                bMicOn = true;
                mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
                notifyNewRoomInfo();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-SXB|startEnterRoom->create room failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });
    }


    private void joinRoom() {
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(CurLiveInfo.getHostID())
                .autoCamera(false)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(Constants.NORMAL_MEMBER_ROLE)
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);
        ILVLiveManager.getInstance().joinRoom(CurLiveInfo.getRoomNum(), memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|startEnterRoom->join room sucess");
                mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|startEnterRoom->join room failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });
        SxbLog.i(TAG, "joinLiveRoom startEnterRoom ");
    }

    private int sendCmd(final ILVCustomCmd cmd) {
        return ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "sendCmd->success:" + cmd.getCmd() + "|" + cmd.getParam());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
//                Toast.makeText(mContext, "sendCmd->failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 解析消息回调
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {
        List<TIMMessage> tlist = list;

        for (int i = tlist.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = tlist.get(i);

            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();

                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                        mLiveView.hostLeave("host", null);
                    }

                }
                //定制消息
                if (type == TIMElemType.Custom) {
                    String id, nickname;
                    if (currMsg.getSenderProfile() != null) {
                        id = currMsg.getSenderProfile().getIdentifier();
                        nickname = currMsg.getSenderProfile().getNickName();
                    } else {
                        id = sendId;
                        nickname = sendId;
                    }
                    handleCustomMsg(elem, id, nickname);
                    continue;
                }

                //其他群消息过滤

                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null)
                    if (!CurLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
                        continue;
                    }

                //最后处理文本消息
                if (type == TIMElemType.Text) {
                    if (currMsg.isSelf()) {
                        handleTextMessage(elem, MySelfInfo.getInstance().getNickName());
                    } else {
                        String nickname;
                        if (currMsg.getSenderProfile() != null && (!currMsg.getSenderProfile().getNickName().equals(""))) {
                            nickname = currMsg.getSenderProfile().getNickName();
                        } else {
                            nickname = sendId;
                        }
                        handleTextMessage(elem, nickname);
                    }
                }
            }
        }
    }

    /**
     * 处理文本消息解析
     */
    private void handleTextMessage(TIMElem elem, String name) {
        TIMTextElem textElem = (TIMTextElem) elem;

        mLiveView.refreshText(textElem.getText(), name);
    }


    /**
     * 处理定制消息 赞 关注 取消关注
     */
    private void handleCustomMsg(TIMElem elem, String identifier, String nickname) {
        try {
            if (null == mLiveView) {
                return;
            }
            String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");
            SxbLog.i(TAG, "cumstom msg  " + customText);

            JSONTokener jsonParser = new JSONTokener(customText);
            // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
            // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = json.getInt(Constants.CMD_KEY);
            switch (action) {
                case Constants.AVIMCMD_MUlTI_HOST_INVITE:
                    SxbLog.d(TAG, LogConstants.ACTION_VIEWER_SHOW + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "receive invite message" +
                            LogConstants.DIV + "id " + identifier);
                    mLiveView.showInviteDialog();
                    break;
                case Constants.AVIMCMD_MUlTI_JOIN:
                    SxbLog.i(TAG, "handleCustomMsg " + identifier);
                    mLiveView.cancelInviteView(identifier);
                    break;
                case Constants.AVIMCMD_MUlTI_REFUSE:
                    mLiveView.cancelInviteView(identifier);
                    Toast.makeText(mContext, identifier + " refuse !", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.AVIMCMD_PRAISE:
                    mLiveView.refreshThumbUp();
                    break;
                case Constants.AVIMCMD_ENTERLIVE:
                    if (mLiveView != null)
                        mLiveView.memberJoin(identifier, nickname);
                    break;

                case Constants.AVIMCMD_MULTI_CANCEL_INTERACT://主播关闭摄像头命令
                    //如果是自己关闭Camera和Mic
                    String closeId = json.getString(Constants.CMD_PARAM);
                    if (closeId.equals(MySelfInfo.getInstance().getId())) {//是自己
                        //TODO 被动下麦 下麦 下麦
                        downMemberVideo();
                    }
                    //其他人关闭小窗口
                    ILiveRoomManager.getInstance().getRoomView().closeUserView(closeId, AVView.VIDEO_SRC_TYPE_CAMERA,true);
                    mLiveView.hideInviteDialog();
                    mLiveView.refreshUI(closeId);
                    break;
                case Constants.AVIMCMD_MULTI_HOST_CANCELINVITE:
                    mLiveView.hideInviteDialog();
                    break;
                case Constants.AVIMCMD_MULTI_HOST_CONTROLL_CAMERA:
//                    toggleCamera();
                    switchCamera();
                    break;
                case Constants.AVIMCMD_MULTI_HOST_CONTROLL_MIC:
                    toggleMic();
                    break;
                case Constants.AVIMCMD_EXITLIVE:
                    startExitRoom();
                    break;
                case Constants.AVIMCMD_HOST_BACK:
                    mLiveView.hostBack(identifier, nickname);

                default:
                    break;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            // 异常处理代码
        }
    }

    public void switchCamera() {
        if (ILiveRoomManager.getInstance().getCurCameraId() == ILiveConstants.NONE_CAMERA) return;
        if (ILiveRoomManager.getInstance().getCurCameraId() == ILiveConstants.FRONT_CAMERA) {
            ILiveRoomManager.getInstance().switchCamera(ILiveConstants.BACK_CAMERA);
        } else {
            ILiveRoomManager.getInstance().switchCamera(ILiveConstants.FRONT_CAMERA);
        }

    }
}
