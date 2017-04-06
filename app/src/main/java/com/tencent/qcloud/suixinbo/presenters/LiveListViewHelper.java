package com.tencent.qcloud.suixinbo.presenters;


import android.os.AsyncTask;

import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveListView;

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
        mLiveListView = null;
    }




    /**
     * 获取后台数据接口
     */
    class GetRoomListTask extends AsyncTask<Integer, Integer, UserServerHelper.RequestBackInfo> {

        @Override
        protected UserServerHelper.RequestBackInfo doInBackground(Integer... params) {
            return UserServerHelper.getInstance().getRoomList();
        }

        @Override
        protected void onPostExecute(UserServerHelper.RequestBackInfo info) {
            if(null != info) {
                if (mLiveListView != null)
                    mLiveListView.showRoomList(info,UserServerHelper.getInstance().getRoomListData());
            }
        }
    }

}
