package com.tencent.qcloud.suixinbo.model;

/**
 * Gson 辅助类
 */
public class LiveInfoJson {
    private int createTime;
    private String title;
    private String cover;
    private LBS lbs;
    private HOST host;
    private int admireCount;
    private String chatRoomId;
    private int avRoomId;
    private int timeSpan;
    private int watchCount;



    public int getAvRoomId() {
        return avRoomId;
    }

    public void setAvRoomId(int avRoomId) {
        this.avRoomId = avRoomId;
    }


    public String getCover() {
        return cover;
    }

    public HOST getHost() {
        return host;
    }

    public int getAdmireCount() {
        return admireCount;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public int getTimeSpan() {
        return timeSpan;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getCreateTime() {

        return createTime;
    }

    public String getTitle() {
        return title;
    }

    public LBS getLbs() {
        return lbs;
    }


    @Override
    public String toString() {
        return "LiveInfoJson{" +
                "createTime=" + createTime +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", lbs=" + lbs +
                ", host=" + host +
                ", admireCount=" + admireCount +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", timeSpan=" + timeSpan +
                ", watchCount=" + watchCount +
                '}';
    }

    public class LBS {
        private double longitude;
        private double latitue;
        private String address;

        public double getLongitude() {
            return longitude;
        }

        public double getLatitue() {
            return latitue;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "LBS{" +
                    "longitude=" + longitude +
                    ", latitue=" + latitue +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public class HOST {
        private String uid;
        private String avatar;
        private String username;


        public String getUid() {
            return uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "HOST{" +
                    "uid='" + uid + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

}
