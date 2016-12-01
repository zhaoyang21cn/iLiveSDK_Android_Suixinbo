package com.tencent.qcloud.suixinbo.presenters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Base64;

import com.tencent.qcloud.suixinbo.model.LiveInfoJson;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    public static final String REGISTER = "http://182.254.234.225/sxb/index.php?svc=account&cmd=regist";
    public static final String LOGIN = "http://182.254.234.225/sxb/index.php?svc=account&cmd=login";
    public static final String LOGOUT = "http://182.254.234.225/sxb/index.php?svc=account&cmd=logout";
    public static final String APPLY_CREATE_ROOM ="http://182.254.234.225/sxb/index.php?svc=live&cmd=create";
    public static final String REPORT_ROOM_INFO ="http://182.254.234.225/sxb/index.php?svc=live&cmd=reportroom";
    public static final String HEART_BEAT ="http://182.254.234.225/sxb/index.php?svc=live&cmd=heartbeat";
    public static final String STOP_ILIVE ="http://182.254.234.225/sxb/index.php?svc=live&cmd=exitroom";



    public static final String GET_MYROOMID = "http://182.254.234.225/sxb/index.php?svc=user_av_room&cmd=get";
    public static final String NEW_ROOM_INFO = "http://182.254.234.225/sxb/index.php?svc=live&cmd=start";
    public static final String STOP_ROOM = "http://182.254.234.225/sxb/index.php?svc=live&cmd=end";
    public static final String GET_LIVELIST = "http://182.254.234.225/sxb/index.php?svc=live&cmd=list";
    public static final String SEND_HEARTBEAT = "http://182.254.234.225/sxb/index.php?svc=live&cmd=host_heartbeat";
    public static final String GET_COS_SIG = "http://182.254.234.225/sxb/index.php?svc=cos&cmd=get_sign";


    private String token = ""; //后续使用唯一标示
    private String Sig = ""; //登录唯一标示
//    private int avRoom;
//    private String groupID;

    class ResquestResult {

        int errorCode;
        String errorInfo;

        ResquestResult(int code, String bad) {
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


    public String getToken() {
        return token;
    }

    public String getSig() {
        return Sig;
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
    public ResquestResult registerId(String id, String password) {
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

            return new ResquestResult(code, errorInfo);
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
    public ResquestResult loginId(String id, String password) {
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
                MySelfInfo.getInstance().setSign(Sig);
                MySelfInfo.getInstance().setToken(token);
            }
            return new ResquestResult(code, errorInfo);
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
    public ResquestResult logoutId(String id) {
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
            return new ResquestResult(code, errorInfo);

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
    public ResquestResult applyCreateRoom() {
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
                String groupID = data.getString("groupid");
            }
            return new ResquestResult(code, errorInfo);

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
    public ResquestResult reportRoomInfo(String inputJson) {
        try {

            String res = post(REPORT_ROOM_INFO, inputJson);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new ResquestResult(code, errorInfo);
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
    public ResquestResult heartBeater(String role) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("role", role);
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomid", MySelfInfo.getInstance().getMyRoomNum());
            String json = jasonPacket.toString();
            String res = post(HEART_BEAT, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new ResquestResult(code, errorInfo);
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
    public ResquestResult getRoomList(String role) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("role", role);
            jasonPacket.put("type", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomid", MySelfInfo.getInstance().getMyRoomNum());
            String json = jasonPacket.toString();
            String res = post(HEART_BEAT, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new ResquestResult(code, errorInfo);
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
    public ResquestResult notifyCloseLive() {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("type", "live");
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
            String json = jasonPacket.toString();
            String res = post(STOP_ILIVE, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new ResquestResult(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }











































































    /**
     * 同步Server 新创建房间信息
     */
    public int notifyServerNewLiveInfo(JSONObject reg) {

        try {

            String res = post(NEW_ROOM_INFO, reg.toString());
            SxbLog.i(TAG, "notifyServer live start  liveinfo: " + res);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            if (code == 0) {
                return code;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 同步Server 关闭房间信息
     */
    public LiveInfoJson notifyServerLiveStop(String id) {
        try {
            JSONObject stopLive = new JSONObject();
            stopLive.put("uid", id);
            stopLive.put("watchCount", 1000);
            stopLive.put("admireCount", 0);
            stopLive.put("timeSpan", 200);
            String json = stopLive.toString();
            String res = post(STOP_ROOM, json);
            SxbLog.i(TAG, "notifyServer live stop  liveinfo: " + res);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");
                JSONObject record = data.getJSONObject("record");
                String recordS = record.toString();
                Gson gson = new GsonBuilder().create();
                LiveInfoJson result = gson.fromJson(recordS, LiveInfoJson.class);
                return result;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取自己的房间
     */
    public void getMyRoomId(final Context context) {
        try {
            JSONObject myId = new JSONObject();
            myId.put("uid", MySelfInfo.getInstance().getId());
            String response = UserServerHelper.getInstance().post(GET_MYROOMID, myId.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                int id = data.getInt("avRoomId");
                MySelfInfo.getInstance().setMyRoomNum(id);
                MySelfInfo.getInstance().writeToCache(context.getApplicationContext());
            } else {
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取直播列表
     *
     * @param page     页数
     * @param pagesize 每页个数
     * @return 返回直播列表
     */
    public ArrayList<LiveInfoJson> getLiveList(int page, int pagesize) {
        try {
            JSONObject req = new JSONObject();
            req.put("pageIndex", page);
            req.put("pageSize", pagesize);
            req.put("appid", Constants.SDK_APPID);
            String response = UserServerHelper.getInstance().post(GET_LIVELIST, req.toString());

            SxbLog.i(TAG, "getLiveList " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                JSONArray record = data.getJSONArray("recordList");
                Type listType = new TypeToken<ArrayList<LiveInfoJson>>() {
                }.getType();
                ArrayList<LiveInfoJson> result = new Gson().fromJson(record.toString(), listType);
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public void sendHeartBeat(String userid, int watchCount, int admireCount, int timeSpan) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", userid);
            req.put("watchCount", watchCount);
            req.put("admireCount", admireCount);
            req.put("timeSpan", timeSpan);
            String response = UserServerHelper.getInstance().post(SEND_HEARTBEAT, req.toString());

            SxbLog.i(TAG, "sendHeartBeat " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "sendHeartBeat is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    /**
     * 密码加密
     */
    public static String encryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 加密
            return new String(Base64.encode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
