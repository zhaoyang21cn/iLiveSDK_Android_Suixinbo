package com.tencent.qcloud.suixinbo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xkazerzhang on 2016/12/22.
 */
public class RecordInfo {
    private String strUser;
    private String strCover;
    private String strVideoId;
    private String playUrl;

    public RecordInfo(JSONObject jsonRecord) throws JSONException{
        strUser = jsonRecord.optString("uid");
        strCover = jsonRecord.optString("cover");
        strVideoId = jsonRecord.optString("videoId");
        JSONArray urls = jsonRecord.getJSONArray("playurl");
        if (null != urls && urls.length()>0){
            playUrl = urls.getString(0);
        }
    }

    public String getStrUser() {
        return strUser;
    }

    public String getStrCover() {
        return strCover;
    }

    public String getStrVideoId() {
        return strVideoId;
    }

    public String getPlayUrl() {
        return playUrl;
    }
}
