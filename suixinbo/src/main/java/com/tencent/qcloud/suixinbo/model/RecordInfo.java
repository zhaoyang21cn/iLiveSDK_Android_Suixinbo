package com.tencent.qcloud.suixinbo.model;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by xkazerzhang on 2016/12/22.
 */
public class RecordInfo {
    private String strName;
    private String strUser;
    private String strDuring;
    private String strFileId;
    private String strCreateTime;
    private String status;
    private String playUrl;

    public RecordInfo(JSONObject jsonRecord) throws Exception{
        String filename = jsonRecord.optString("fileName");
        strFileId = jsonRecord.optString("fileId");
        status = jsonRecord.optString("status");
        strDuring = jsonRecord.optString("duration");
        JSONArray jsonPlaySets = jsonRecord.optJSONArray("playSet");
        if (null != jsonPlaySets){
            for (int i=0; i<jsonPlaySets.length(); i++){
                JSONObject jsonPlay = jsonPlaySets.getJSONObject(i);
                playUrl = jsonPlay.optString("url");
                if (TextUtils.isEmpty(playUrl))
                    break;
            }
        }

        String infos[] = filename.split("_");
        if (5 == infos.length){
            strUser = infos[1];
            strName = infos[2];
            strCreateTime = infos[3];
        }else{
            strName = filename;
        }
    }

    public String getStrName() {
        return strName;
    }

    public String getStrUser() {
        return strUser;
    }

    public String getStrDuring() {
        return strDuring;
    }

    public String getStrFileId() {
        return strFileId;
    }

    public String getStrCreateTime() {
        return strCreateTime;
    }

    public String getStatus() {
        return status;
    }

    public String getPlayUrl() {
        return playUrl;
    }
}
