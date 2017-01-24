package com.tencent.qcloud.suixinbo.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xkazerzhang on 2016/12/22.
 */
public class RecordInfo {
    private String strName;
    private String strUser;
    private String strCreateTime;
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
        strName = jsonRecord.optString("name");
        if (strName.startsWith("sxb_")){    // 去掉手动录制的前缀
            strName = strName.substring(4);
        }

/*        String infos[] = filename.split("_");
        if (5 == infos.length){
            strUser = infos[1];
            strName = infos[2];
            strCreateTime = infos[3];
        }else{
            strName = filename;
        }*/
    }

    public String getStrName() {
        return strName;
    }

    public String getStrCreateTime() {
        return strCreateTime;
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
