package com.tencent.qcloud.suixinbo.presenters.viewinface;


/**
 * 定位回调
 */
public interface LocationView extends MvpView{

    void onLocationChanged(int code, double lat1, double long1, String location);

}
