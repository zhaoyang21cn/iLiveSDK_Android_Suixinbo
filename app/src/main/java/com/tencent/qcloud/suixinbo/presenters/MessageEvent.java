package com.tencent.qcloud.suixinbo.presenters;

import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;

import java.util.List;
import java.util.Observable;

/**
 * Created by xkazerzhang on 2016/11/16.
 */
public class MessageEvent extends Observable implements TIMMessageListener {
    private volatile static MessageEvent instance;

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
    public boolean onNewMessages(List<TIMMessage> list) {
        setChanged();
        notifyObservers(list);
        return false;
    }
}
