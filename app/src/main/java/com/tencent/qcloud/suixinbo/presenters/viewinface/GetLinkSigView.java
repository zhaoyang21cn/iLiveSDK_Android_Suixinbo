package com.tencent.qcloud.suixinbo.presenters.viewinface;

/**
 * Created by xkazerzhang on 2017/4/13.
 */
public interface GetLinkSigView {
    void onGetSignRsp(String id, String roomnum, String sign);
}
