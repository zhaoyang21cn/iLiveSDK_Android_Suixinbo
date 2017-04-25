package com.tencent.qcloud.suixinbo.presenters;

import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMUserProfile;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVText;

import java.util.List;
import java.util.Observable;

/**
 * 消息观察者
 */
public class MessageEvent extends Observable implements ILVLiveConfig.ILVLiveMsgListener {
    public final static int MSGTYPE_TEXT = 0;
    public final static int MSGTYPE_CMD = 1;
    public final static int MSGTYPE_OTHER= 2;

    private volatile static MessageEvent instance;

    public class SxbMsgInfo{
        int msgType;
        Object data;
        String senderId;
        TIMUserProfile profile;

        public SxbMsgInfo(int type, String id, TIMUserProfile user, Object obj){
            msgType = type;
            senderId = id;
            profile = user;
            data = obj;

        }
    }

    private MessageEvent(){}

    public static MessageEvent getInstance(){
        if (null == instance){
            synchronized (MessageEvent.class){
                if (null == instance){
                    instance = new MessageEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
        setChanged();
        notifyObservers(new SxbMsgInfo(MSGTYPE_TEXT, SenderId, userProfile, text));
    }

    @Override
    public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
        setChanged();
        notifyObservers(new SxbMsgInfo(MSGTYPE_CMD, id, userProfile, cmd));
    }

    @Override
    public void onNewOtherMsg(TIMMessage message) {
        setChanged();
        notifyObservers(new SxbMsgInfo(MSGTYPE_OTHER, message.getSender(), message.getSenderProfile(), message));
    }
}
