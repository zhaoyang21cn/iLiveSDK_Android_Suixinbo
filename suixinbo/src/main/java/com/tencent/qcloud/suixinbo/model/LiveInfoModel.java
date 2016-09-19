package com.tencent.qcloud.suixinbo.model;

/**
 *  直播item
 */
public class LiveInfoModel {
    private String title;
    private String cover;
    private int watchcount;
    private int admireCount;
    private String hostName;
    private int hostUid;
    private String hostAvatar;
    private String position;


    public int getAdmireCount() {
        return admireCount;
    }

    public void setAdmireCount(int admireCount) {
        this.admireCount = admireCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getWatchcount() {
        return watchcount;
    }

    public void setWatchcount(int watchcount) {
        this.watchcount = watchcount;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    public int getHostUid() {
        return hostUid;
    }

    public void setHostUid(int hostUid) {
        this.hostUid = hostUid;
    }


    public String getHostAvatar() {
        return hostAvatar;
    }

    public void setHostAvatar(String hostAvatar) {
        this.hostAvatar = hostAvatar;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }




//    public LiveInfoModel(PLULiveListProto.LiveItem item){
//        title = item.getTitle();
//        cover = item.getCover();
//        timespan = item.getTimeSpan();
//        watchcount = item.getWatchcount();
//        watchtimespan = item.getWatchtimespan();
//        admireCount = item.getAdmireCount();
//        hostName = item.getHost().getUsername();
//        chatRoomId = item.getChatRoomId();
//        hostUid = item.getHost().getUid();
//        avatar = item.getHost().getAvatar();
//        lbs = new LbsModel();
//        lbs.setLatitude(item.getLbs().getLatitude());
//        lbs.setLongitude(item.getLbs().getLongitude());
//        lbs.setAddress(item.getLbs().getAddress());
//        device = item.getDevice();
//        objId = item.getObjId();
//    }


}
