package com.tencent.qcloud.suixinbo.presenters;

import android.os.AsyncTask;

import com.tencent.qcloud.suixinbo.presenters.viewinface.GetLinkSigView;

/**
 * Created by xkazerzhang on 2017/4/13.
 */
public class GetLinkSignHelper {
    private static String TAG = "GetLinkSignHelper";
    private GetLinkSigView linkView;
    private GetSignTask mTask = null;

    class GetSignTask extends AsyncTask<String, Integer, String> {
        String id, roomnum;
        @Override
        protected String doInBackground(String... params) {
            id = params[0];
            roomnum = params[1];
            return UserServerHelper.getInstance().getGetLinkSig(id, roomnum);
        }

        @Override
        protected void onPostExecute(String sign) {
            if (linkView != null)
                linkView.onGetSignRsp(id, roomnum, sign);
            mTask = null;
        }
    }

    public GetLinkSignHelper(GetLinkSigView view){
        linkView = view;
    }

    public void getLinkSign(String id, String roomnum){
        if (null == mTask){
            mTask = new GetSignTask();
            mTask.execute(id, roomnum);
        }
    }
}
