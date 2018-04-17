package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.av.PingResult;
import com.tencent.av.ServerInfo;
import com.tencent.av.TIMAvManager;
import com.tencent.av.TIMPingCallBack;
import com.tencent.ilivesdk.tools.speedtest.ILiveServerInfo;
import com.tencent.ilivesdk.tools.speedtest.ILiveSpeedTestCallback;
import com.tencent.ilivesdk.tools.speedtest.ILiveSpeedTestManager;
import com.tencent.ilivesdk.tools.speedtest.ILiveSpeedTestRequestParam;
import com.tencent.ilivesdk.tools.speedtest.ILiveSpeedTestResult;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;

/**
 * 测速对话框
 */
public class SpeedTestDialog {

    private final String TAG = "SpeedTestDialog";

    private ProgressDialog pd;
    NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private Context context;
    private List<ILiveSpeedTestResult> testResults = new ArrayList<>();
    private List<ILiveServerInfo> totalServer = new ArrayList<>();
    private final int MSG_START = 1;
    private final int MSG_PROGRESS =2;
    private final int MSG_END = 3;
    private final int MSG_STOP = 4;

    private int count;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START:
                    pd = new ProgressDialog(context);
                    pd.setTitle(context.getString(R.string.ping_ing));
                    pd.setCancelable(false);
                    pd.setMessage(context.getString(R.string.ping_start));
                    pd.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.ping_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stop();
                        }
                    });
                    pd.show();
                    break;
                case MSG_PROGRESS:
                    String pdMsg = msg.getData().getString("msg");
                    pd.setMessage(pdMsg);
                    break;
                case MSG_END:
                    if (null != pd && pd.isShowing())
                        pd.dismiss();
                    StringBuilder resultStr = new StringBuilder();
                    for (ILiveSpeedTestResult result : testResults){
                        resultStr.append(result.getServerInfo().getAddress());
                        resultStr.append(context.getString(R.string.ping_time) + result.getAvgRtt() + "ms ");
                        resultStr.append(context.getString(R.string.ping_miss_up) + percentFormat.format((double)result.getUpLoss()/10000));
                        resultStr.append(context.getString(R.string.ping_miss_down) + percentFormat.format((double)result.getDownLoss()/10000) + "\n");
                    }
                    new AlertDialog.Builder(context).setMessage(resultStr.toString()).show();
                    break;
                case MSG_STOP:
                    if (null != pd && pd.isShowing())
                        pd.dismiss();
                    break;
            }
        }
    };

    public SpeedTestDialog(Context context){
        this.context = context;
        percentFormat.setMinimumFractionDigits(1);

    }

    public void start(){
        count = 0;
        testResults.clear();
        totalServer.clear();
        ILiveSpeedTestRequestParam param = new ILiveSpeedTestRequestParam();
        param.roomId = 0;
        param.callType = 1;
        ILiveSpeedTestManager.getInstance().requestSpeedTest(param, new ILiveSpeedTestCallback() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "ping failed. code: " + code + " desc: " + desc);
                Message message = new Message();
                message.what = MSG_STOP;
                handler.sendMessage(message);
            }

            @Override
            public void onStart(List<ILiveServerInfo> serverInfoList) {
                Log.d(TAG, "start test " + serverInfoList.size() + " ip");
                if (serverInfoList.size() > 0) {
                    totalServer.addAll(serverInfoList);
                    Message message = new Message();
                    message.what = MSG_START;
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(context, context.getString(R.string.ping_no_server), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(ILiveServerInfo serverInfo, int totalPkg, int pkgGap) {
                if (pkgGap != 0)
                {
                    count++;
                }
                Message message = new Message();
                message.what = MSG_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putString("msg", count + "/150");
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFinish(List<ILiveSpeedTestResult> results) {
                testResults.addAll(results);
                Message message = new Message();
                message.what = MSG_END;
                handler.sendMessage(message);
            }
        });

//        TIMAvManager.getInstance().requestSpeedTest(new TIMPingCallBack() {
//            @Override
//            public void onError(int code, String desc) {
//                Log.e(TAG, "ping failed. code: " + code + " desc: " + desc);
//            }
//
//            @Override
//            public void onSuccess(PingResult t) {
//                Log.d(TAG, "end test " + t.getServer().ip + " avg timeuse:" + t.getUseTime());
//                results.add(t);
//
//            }
//
//            @Override
//            public void onProgress(ServerInfo serverInfo, int totalPkg, int currentPkg) {
//                boolean hasDone = false;
//                for (ServerInfo info : doneServer) {
//                    if (serverInfo.ip.equals(info.ip)) {
//                        hasDone = true;
//                        break;
//                    }
//                }
//                if (!hasDone) {
//                    doneServer.add(serverInfo);
//                }
//                Message message = new Message();
//                message.what = MSG_PROGRESS;
//                Bundle bundle = new Bundle();
//
//                bundle.putString("msg", serverInfoToString(serverInfo) + "(" + doneServer.size() + "/" + totalServer.size() + ")\n" + context.getString(R.string.ping_progress) + " " + currentPkg + "/" + totalPkg);
//                message.setData(bundle);
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onStart(List<ServerInfo> serverInfoList) {
//                Log.d(TAG, "start test " + serverInfoList.size() + " ip");
//                if (serverInfoList.size() > 0) {
//                    totalServer.addAll(serverInfoList);
//                    Message message = new Message();
//                    message.what = MSG_START;
//                    handler.sendMessage(message);
//                } else {
//                    Toast.makeText(context, context.getString(R.string.ping_no_server), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFinish() {
//                Message message = new Message();
//                message.what = MSG_END;
//                handler.sendMessage(message);
//            }
//        });
    }

    public void stop(){
        SxbLog.d(TAG, "stop speed test");
        ILiveSpeedTestManager.getInstance().stopSpeedTest();
        Message message = new Message();
        message.what = MSG_STOP;
        handler.sendMessage(message);
    }



}
