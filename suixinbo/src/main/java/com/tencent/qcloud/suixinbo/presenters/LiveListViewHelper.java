package com.tencent.qcloud.suixinbo.presenters;


import android.os.AsyncTask;

import com.tencent.qcloud.suixinbo.model.RoomInfoJson;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveListView;

import java.util.ArrayList;

/**
 * 直播列表页Presenter
 */
public class LiveListViewHelper extends Presenter {

    private final static String TAG = "LiveListViewHelper";

    private LiveListView mLiveListView;
    private GetRoomListTask mGetRoomListTask;

    public LiveListViewHelper(LiveListView view) {
        mLiveListView = view;
    }


    public void getPageData() {
        mGetRoomListTask = new GetRoomListTask();
        mGetRoomListTask.execute(0, 20);
    }


    @Override
    public void onDestory() {
    }




    /**
     * 获取后台数据接口
     */
    class GetRoomListTask extends AsyncTask<Integer, Integer, ArrayList<RoomInfoJson>> {

        @Override
        protected ArrayList<RoomInfoJson> doInBackground(Integer... params) {
            return UserServerHelper.getInstance().getRoomList();
        }

        @Override
        protected void onPostExecute(ArrayList<RoomInfoJson> result) {
            if(null != result) {
                if (mLiveListView != null)
                    mLiveListView.showRoomList(result);
            }
        }
    }

}
