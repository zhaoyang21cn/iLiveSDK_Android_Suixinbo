package com.tencent.tdemovideocall;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 帐户管理(演示独立模式下的帐号注册与登录)
 */
public class AccountMgr {
    private final static String TAG = "AccountMgr";

    private final static String REGIST_PATH = "http://182.254.234.225:8085/regist";
    private final static String LOGIN_PATH = "http://182.254.234.225:8085/login";

    public interface RequestCallBack{
        void onResult(int error, String response);
    }

    private int iReqId = 1;
    private final SparseArray<RequestCallBack> mapRequest = new SparseArray<>();
    private Handler hMsgHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            RequestCallBack callBack = null;
            synchronized (mapRequest){
                callBack = mapRequest.get(msg.what);
                mapRequest.remove(msg.what);
            }
            if (null != callBack){
                Log.v(TAG, "onResult->requestId:"+msg.what+", code:"+msg.arg1+", info:"+msg.obj);
                callBack.onResult(msg.arg1, (String)msg.obj);
            }
        }
    };


    private int getRequestId(){
        return iReqId++;
    }

    /**
     * 获取HTTP的Get请求返回值(阻塞)
     * @param strAction
     * @return
     * @throws Exception
     */
    public static String getHttpGetRsp(String strAction) throws Exception{
        Log.v(TAG, "getHttpGetRsp->request: \n" + strAction);
        URL _url = new URL(strAction.replace(" ", "%20"));
        HttpURLConnection _conn = (HttpURLConnection)_url.openConnection();
        _conn.setDoInput(true);
        //_conn.setDoOutput(true);
        _conn.setConnectTimeout(1000 * 5);
        _conn.setReadTimeout(1000 * 10);
        _conn.setRequestMethod("GET");

        int _rspCode = _conn.getResponseCode();
        if (_rspCode == 200){
            InputStreamReader _in = new InputStreamReader(_conn.getInputStream());
            BufferedReader _inReader = new BufferedReader(_in);
            StringBuffer _strBuf = new StringBuffer();
            String _line = null;
            while (null != (_line = _inReader.readLine())){
                _strBuf.append(_line+"\n");
            }

            _inReader.close();
            _in.close();
            _conn.disconnect();
            Log.v(TAG, "getHttpGetRsp->response info: " + _strBuf.toString());
            return _strBuf.toString();
        }else{
            Log.v(TAG, "getHttpGetRsp->response code: " + _rspCode);
        }

        return null;
    }

    private void doBackGetRequest(final int reqId, final String request){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = reqId;
                try {
                    String rsp = getHttpGetRsp(request);
                    if (TextUtils.isEmpty(rsp)){
                        msg.arg1 = 1;
                    }else{
                        msg.arg1 = 0;
                        msg.obj = rsp;
                    }
                }catch (Exception e){
                    msg.arg1 = 2;
                    msg.obj = e.toString();
                }
                hMsgHandler.sendMessage(msg);
            }
        });
        thread.start();
    }

    public int regist(final String account, final String password, RequestCallBack callBack){
        final int reqId = getRequestId();
        synchronized (mapRequest){
            mapRequest.put(reqId, callBack);
        }
        doBackGetRequest(reqId, REGIST_PATH + "?account=" + account + "&password=" + password);
        return reqId;
    }

    public int login(final String account, final String password, RequestCallBack callBack){
        final int reqId = getRequestId();
        synchronized (mapRequest){
            mapRequest.put(reqId, callBack);
        }
        doBackGetRequest(reqId, LOGIN_PATH + "?account=" + account + "&password=" + password);
        return reqId;
    }
}
