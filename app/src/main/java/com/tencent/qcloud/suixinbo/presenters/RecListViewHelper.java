package com.tencent.qcloud.suixinbo.presenters;

import android.os.AsyncTask;

import com.tencent.qcloud.suixinbo.model.RecordInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.RecListView;

import java.util.ArrayList;


/**
 * Created by xkazerzhang on 2016/12/22.
 */
public class RecListViewHelper {
    private final static String TAG = "RecListViewHelper";

    private RecListView recView;
    private GetRecordList mTask = null;

    public RecListViewHelper(RecListView view){
        recView = view;
    }

    class GetRecordList extends AsyncTask<Integer, Integer, ArrayList<RecordInfo>> {

        @Override
        protected ArrayList<RecordInfo> doInBackground(Integer... params) {
            return UserServerHelper.getInstance().getRecordList(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<RecordInfo> result) {
            if (recView != null)
                recView.onUpdateRecordList(result);
            mTask = null;
        }
    }


    public void refresh(){
        if (null == mTask){
            mTask = new GetRecordList();
            mTask.execute(1, 15);
        }
    }
}
