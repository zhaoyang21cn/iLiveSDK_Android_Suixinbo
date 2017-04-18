package com.tencent.qcloud.suixinbo.model;

public class MemberInfo {

    private String userId = "";
    private String userName = "";
    private String avatar = "";
    private boolean isOnVideoChat = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnVideoChat() {
        return isOnVideoChat;
    }

    public void setIsOnVideoChat(boolean isOnVideoChat) {
        this.isOnVideoChat = isOnVideoChat;
    }
}