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
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 测速对话框
 */
public class SpeedTestDialog {

    private final String TAG = "SpeedTestDialog";

    private ProgressDialog pd;
    NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private Context context;
    private List<PingResult> results = new ArrayList<>();
    private List<ServerInfo> totalServer = new ArrayList<>();
    private List<ServerInfo> doneServer = new ArrayList<>();
    private final int MSG_START = 1;
    private final int MSG_PROGRESS =2;
    private final int MSG_END = 3;
    private final int MSG_STOP = 4;
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
                    pd.dismiss();
                    StringBuilder resultStr = new StringBuilder();
                    for (PingResult result : results){
                        resultStr.append(serverInfoToString(result.getServer()));
                        resultStr.append(context.getString(R.string.ping_time) + result.getUseTime() + "ms ");
                        resultStr.append(context.getString(R.string.ping_miss) + percentFormat.format((double)(result.getTotalPkg() - result.getReceivePkg())/(double)result.getTotalPkg()) + "\n");
                    }
                    new AlertDialog.Builder(context).setMessage(resultStr.toString()).show();
                    break;
                case MSG_STOP:
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
        TIMAvManager.getInstance().requestSpeedTest(new TIMPingCallBack() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "ping failed. code: " + code + " desc: " + desc);
            }

            @Override
            public void onSuccess(PingResult t) {
                Log.d(TAG, "end test " + t.getServer().ip + " avg timeuse:" + t.getUseTime());
                results.add(t);

            }

            @Override
            public void onProgress(ServerInfo serverInfo, int totalPkg, int currentPkg) {
                boolean hasDone = false;
                for (ServerInfo info : doneServer) {
                    if (serverInfo.ip.equals(info.ip)) {
                        hasDone = true;
                        break;
                    }
                }
                if (!hasDone) {
                    doneServer.add(serverInfo);
                }
                Message message = new Message();
                message.what = MSG_PROGRESS;
                Bundle bundle = new Bundle();

                bundle.putString("msg", serverInfoToString(serverInfo) + "(" + doneServer.size() + "/" + totalServer.size() + ")\n" + context.getString(R.string.ping_progress) + " " + currentPkg + "/" + totalPkg);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onStart(List<ServerInfo> serverInfoList) {
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
            public void onFinish() {
                Message message = new Message();
                message.what = MSG_END;
                handler.sendMessage(message);
            }
        });
    }

    public void stop(){
        SxbLog.d(TAG, "stop speed test");
        TIMAvManager.getInstance().requestSpeedTestStop();
        Message message = new Message();
        message.what = MSG_STOP;
        handler.sendMessage(message);
    }

    public List<PingResult> getResults() {
        return results;
    }

    private String serverInfoToString(ServerInfo info){
        StringBuilder str = new StringBuilder();
        switch (info.idc){
            case SH:
                str.append(context.getString(R.string.ping_SH));
                break;
            case SZ:
                str.append(context.getString(R.string.ping_SZ));
                break;
            case CD:
                str.append(context.getString(R.string.ping_CD));
                break;
            case TJ:
                str.append(context.getString(R.string.ping_TJ));
                break;
            case NJ:
                str.append(context.getString(R.string.ping_NJ));
                break;
            case HZ:
                str.append(context.getString(R.string.ping_HZ));
                break;
            case GZ:
                str.append(context.getString(R.string.ping_GZ));
                break;

        }
        str.append(" ");
        switch (info.isp){
            case TEL:
                str.append(context.getString(R.string.ping_TEL));
                break;
            case CNC:
                str.append(context.getString(R.string.ping_CNC));
                break;
            case CMCC:
                str.append(context.getString(R.string.ping_CMCC));
                break;
        }
        return str.toString();

    }
}
