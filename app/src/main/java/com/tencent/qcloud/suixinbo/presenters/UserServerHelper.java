package com.tencent.qcloud.suixinbo.presenters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.util.Log;

import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.MemberID;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.model.RecordInfo;
import com.tencent.qcloud.suixinbo.model.RoomInfoJson;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 网络请求类
 */
public class UserServerHelper {
    private static final String TAG = UserServerHelper.class.getSimpleName();
    private static UserServerHelper instance = null;

    public static final String SERVER = "https://sxb.qcloud.com/sxb_new/index.php?";

    public static final String REGISTER             = SERVER+"svc=account&cmd=regist";
    public static final String LOGIN                = SERVER+"svc=account&cmd=login";
    public static final String LOGOUT               = SERVER+"svc=account&cmd=logout";
    public static final String APPLY_CREATE_ROOM    = SERVER+"svc=live&cmd=create";
    public static final String REPORT_ROOM_INFO     = SERVER+"svc=live&cmd=reportroom";
    public static final String HEART_BEAT           = SERVER+"svc=live&cmd=heartbeat";
    public static final String STOP_ILIVE           = SERVER+"svc=live&cmd=exitroom";
    public static final String GET_ROOMLIST         = SERVER+"svc=live&cmd=roomlist";
    public static final String REPORT_ME            = SERVER+"svc=live&cmd=reportmemid";
    public static final String GET_MEMLIST          = SERVER+"svc=live&cmd=roomidlist";
    public static final String REPORT_RECORD        = SERVER+"svc=live&cmd=reportrecord";
    public static final String GET_REOCORDLIST      = SERVER+"svc=live&cmd=recordlist";
    public static final String GET_PLAYERLIST       = SERVER+"svc=live&cmd=livestreamlist";
    public static final String GET_ROOM_PLAYURL     = SERVER+"svc=live&cmd=getroomplayurl";
    public static final String GET_COS_SIG          = SERVER+"svc=cos&cmd=get_sign";
    public static final String GET_LINK_SIG         = SERVER+"svc=live&cmd=linksig";


    private String token = ""; //后续使用唯一标示
    private String Sig = ""; //登录唯一标示

    public class RequestBackInfo {

        int errorCode;
        String errorInfo;

        RequestBackInfo(int code, String bad) {
            errorCode = code;
            errorInfo = bad;
        }

        public int getErrorCode() {
            return errorCode;
        }


        public String getErrorInfo() {
            return errorInfo;
        }

    }


    public static UserServerHelper getInstance() {
        if (instance == null) {
            instance = new UserServerHelper();
        }
        return instance;
    }



    private ArrayList<RoomInfoJson> roomList;

    public ArrayList<RoomInfoJson> getRoomListData(){
        return roomList;
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();


    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return "";
        }
    }


    /**
     * 注册ID （独立方式）
     */
    public RequestBackInfo registerId(String id, String password) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("id", id);
            jasonPacket.put("pwd", password);
            String json = jasonPacket.toString();
            String res = post(REGISTER, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");

            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登录ID （独立方式）
     */
    public RequestBackInfo loginId(String id, String password) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("id", id);
            jasonPacket.put("pwd", password);
            String json = jasonPacket.toString();
            String res = post(LOGIN, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");

                Sig = data.getString("userSig");
                token = data.getString("token");
                MySelfInfo.getInstance().setId(id);
                MySelfInfo.getInstance().setUserSig(Sig);
                MySelfInfo.getInstance().setToken(token);

            }
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登出ID （独立方式）
     */
    public RequestBackInfo logoutId(String id) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("id", id);
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            String json = jasonPacket.toString();
            String res = post(LOGOUT, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 申请创建房间
     */
    public RequestBackInfo applyCreateRoom() {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("type", "live");
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            String json = jasonPacket.toString();
            String res = post(APPLY_CREATE_ROOM, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");
                int avRoom = data.getInt("roomnum");
                MySelfInfo.getInstance().setMyRoomNum(avRoom);
                CurLiveInfo.setRoomNum(avRoom);
                String groupID = data.getString("groupid");
            }
            return new RequestBackInfo(code, errorInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 上报房间信息
     */
    public RequestBackInfo reporNewtRoomInfo(String inputJson) {
        try {

            String res = post(REPORT_ROOM_INFO, inputJson);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 上报录制信息
     */
    public RequestBackInfo reporNewtRecordInfo(String inputJson) {
        try {
            Log.v(TAG, "reporNewtRecordInfo->"+inputJson);
            String res = post(REPORT_RECORD, inputJson);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            RequestBackInfo ret =  new RequestBackInfo(code, errorInfo);
            Log.v(TAG, "reporNewtRecordInfo->rsp:"+ret.errorCode+"|"+ret.getErrorInfo());
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 心跳上报
     */
    public RequestBackInfo heartBeater (int role) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("role", role);
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
            jasonPacket.put("thumbup",CurLiveInfo.getAdmires());
            String json = jasonPacket.toString();
            String res = post(HEART_BEAT, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取房间列表
     */
    public RequestBackInfo getRoomList() {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("type", "live");
            jasonPacket.put("index", 0);
            jasonPacket.put("size", 20);
            jasonPacket.put("appid", Constants.SDK_APPID);
            String json = jasonPacket.toString();
            String res = post(GET_ROOMLIST, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            if(code ==0){
                JSONObject data = response.getJSONObject("data");
                JSONArray record = data.getJSONArray("rooms");
                Type listType = new TypeToken<ArrayList<RoomInfoJson>>() {}.getType();
                roomList= new Gson().fromJson(record.toString(), listType);
            }
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }






    /**
     * 通知UserServer结束房间
     */
    public RequestBackInfo notifyCloseLive() {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
            jasonPacket.put("type", "live");
            String json = jasonPacket.toString();
            String res = post(STOP_ILIVE, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 上报成员
     */
    public RequestBackInfo reportMe(int role, int action) {
        try {
            JSONObject jasonPacket = new JSONObject();

            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", CurLiveInfo.getRoomNum());
            jasonPacket.put("id", MySelfInfo.getInstance().getId());
            jasonPacket.put("role", role);
            jasonPacket.put("operate", action);

            String json = jasonPacket.toString();
            String res = post(REPORT_ME, json);
            ILiveLog.i(TAG,"reportMe "+role+" action " + action);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取房间内成员
     */
    public ArrayList<MemberID> getMemberList() {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", CurLiveInfo.getRoomNum());
            jasonPacket.put("index",0);
            jasonPacket.put("size", 40);

            String json = jasonPacket.toString();
            String res = post(GET_MEMLIST, json);
               JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            if(code ==0){
                JSONObject data = response.getJSONObject("data");
                JSONArray record = data.getJSONArray("idlist");
                Type listType = new TypeToken<ArrayList<MemberID>>() {}.getType();
                ArrayList<MemberID> result = new Gson().fromJson(record.toString(), listType);
                ILiveLog.i(TAG,"size"+result.size());
                return result;
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//
//    /**
//     * 上报录制视频URL
//     */
//    public RequestBackInfo reportRecord(String videoid,String videoUrl,int type,String cover) {
//        try {
//            JSONObject jasonPacket = new JSONObject();
//            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
//            jasonPacket.put("videoid", videoid);
//            jasonPacket.put("playurl",videoUrl);
//            jasonPacket.put("type", type);
//            jasonPacket.put("cover",cover);
//
//            String json = jasonPacket.toString();
//            String res = post(REPORT_RECORD, json);
//            JSONTokener jsonParser = new JSONTokener(res);
//            JSONObject response = (JSONObject) jsonParser.nextValue();
//            int code = response.getInt("errorCode");
//            String errorInfo = response.getString("errorInfo");
//            return new RequestBackInfo(code, errorInfo);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//
//
//
//    /**
//     * 获取点播列表
//     */
//    public RequestBackInfo getRecordList() {
//        try {
//            JSONObject jasonPacket = new JSONObject();
//            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
//            jasonPacket.put("type", 0);
//            jasonPacket.put("index",0);
//            jasonPacket.put("size", 10);
//            String json = jasonPacket.toString();
//            String res = post(GET_REOCORDLIST, json);
//            JSONTokener jsonParser = new JSONTokener(res);
//            JSONObject response = (JSONObject) jsonParser.nextValue();
//            int code = response.getInt("errorCode");
//            String errorInfo = response.getString("errorInfo");
//            return new RequestBackInfo(code, errorInfo);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



    /**
     * 拉取录制列表
     */
    public ArrayList<RecordInfo> getRecordList (int page,int size) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
			jasonPacket.put("type", Constants.VOD_MODE);
            jasonPacket.put("index", page);
            jasonPacket.put("size",size);
            String json = jasonPacket.toString();
            Log.v(TAG, "getRecordList->request: "+json);
            String res = post(GET_REOCORDLIST, json);
            Log.v(TAG, "getRecordList->ret: "+res);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            if(code ==0){
                JSONObject data = response.getJSONObject("data");
                JSONArray record = data.getJSONArray("videos");
                ArrayList<RecordInfo> recList = new ArrayList<>();
                for (int i=0; i<record.length(); i++){
                    recList.add(new RecordInfo(record.getJSONObject(i)));
                }
                ILiveLog.i(TAG,"size"+recList.size());
                return recList;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取播放列表
     */
    public RequestBackInfo getPlayUrlList (int page, int size) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("index", page);
            jasonPacket.put("size",size);
            String json = jasonPacket.toString();
            String res = post(GET_PLAYERLIST, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 获取房间回放地址
     */
    public RequestBackInfo getRoomPlayUrl (int room) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", room);
            String json = jasonPacket.toString();
            String res = post(GET_ROOM_PLAYURL, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");
                String address = data.getString("address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCosSig() {
        try {
            String response = UserServerHelper.getInstance().post(GET_COS_SIG, "");
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                String sign = data.getString("sign");
                return sign;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getGetLinkSig(String id, String roomid) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("id", id);
            jasonPacket.put("roomnum", Integer.valueOf(roomid));
            String json = jasonPacket.toString();
            String response = post(GET_LINK_SIG, json);
            SxbLog.d(TAG, "getGetLinkSig->rsp:"+response);

            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                String sign = data.getString("linksig");
                return sign;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
