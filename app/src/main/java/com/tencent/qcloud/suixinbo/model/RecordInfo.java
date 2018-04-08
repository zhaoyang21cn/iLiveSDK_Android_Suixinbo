package com.tencent.qcloud.suixinbo.model;


import com.tencent.qcloud.suixinbo.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xkazerzhang on 2016/12/22.
 */
public class RecordInfo {
    private String strName;     // name 直播名称
    private String strUser;         // uid 主播id
    private String strCreateTime;   //   创建时间
    private String strCover;        // cover 封面
    private String strVideoId;
    private String playUrl;         // playurl 播放地址
    private String strSize;         // 文件大小
    private String strDuration;       // 录制时长
    private String strFaceUrl="";      // 头像

    public RecordInfo(JSONObject jsonRecord) throws JSONException{
        strUser = jsonRecord.optString("uid");
        strCover = jsonRecord.optString("cover");
        strVideoId = jsonRecord.optString("videoId");
        JSONArray urls = jsonRecord.getJSONArray("playurl");
        if (null != urls && urls.length()>0){
            playUrl = urls.getString(0);
        }
        strName = jsonRecord.optString("name");
        long uSec = jsonRecord.optLong("createTime", 0);
        if (0 != uSec){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strCreateTime = formatter.format(new Date(uSec*1000));
        }else {
            String info[] = strName.split("_");
            if ("sxb".equals(info[0])) {     //手动录制
                strName = info[2];
                strCreateTime = info[info.length - 2];
            }
        }
        strSize = UIUtils.getFormatSize(jsonRecord.optInt("fileSize", 0));
        strDuration = UIUtils.getFormatSec(jsonRecord.optInt("duration", 0));


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

    public String getStrSize() {
        return strSize;
    }

    public String getStrDuration() {
        return strDuration;
    }

    public String getStrFaceUrl() {
        return strFaceUrl;
    }

    public void setStrFaceUrl(String strFaceUrl) {
        this.strFaceUrl = strFaceUrl;
    }
}
