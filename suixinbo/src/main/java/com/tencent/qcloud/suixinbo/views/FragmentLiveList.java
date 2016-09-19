package com.tencent.qcloud.suixinbo.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.adapters.LiveShowAdapter;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.LiveInfoJson;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.LiveListViewHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveListView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.util.ArrayList;


/**
 * 直播列表页面
 */
public class FragmentLiveList extends Fragment implements View.OnClickListener, LiveListView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "FragmentLiveList";
    private ListView mLiveList;
    private ArrayList<LiveInfoJson> liveList = new ArrayList<LiveInfoJson>();
    private LiveShowAdapter adapter;
    private LiveListViewHelper mLiveListViewHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FragmentLiveList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLiveListViewHelper = new LiveListViewHelper(this);
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);
        mLiveList = (ListView) view.findViewById(R.id.live_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        adapter = new LiveShowAdapter(getActivity(), R.layout.item_liveshow, liveList);
        mLiveList.setAdapter(adapter);

        mLiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                LiveInfoJson item = liveList.get(i);
                //如果是自己
                if (item.getHost().getUid().equals(MySelfInfo.getInstance().getId())) {
                    Intent intent = new Intent(getActivity(), LiveActivity.class);
                    intent.putExtra(Constants.ID_STATUS, Constants.HOST);
                    MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(item.getHost().getUid());
                    CurLiveInfo.setHostName(item.getHost().getUsername());
                    CurLiveInfo.setHostAvator(item.getHost().getAvatar());
                    CurLiveInfo.setRoomNum(item.getAvRoomId());
                    CurLiveInfo.setMembers(item.getWatchCount() + 1); // 添加自己
                    CurLiveInfo.setAdmires(item.getAdmireCount());
                    CurLiveInfo.setAddress(item.getLbs().getAddress());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LiveActivity.class);
                    intent.putExtra(Constants.ID_STATUS, Constants.MEMBER);
                    MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(item.getHost().getUid());
                    CurLiveInfo.setHostName(item.getHost().getUsername());
                    CurLiveInfo.setHostAvator(item.getHost().getAvatar());
                    CurLiveInfo.setRoomNum(item.getAvRoomId());
                    CurLiveInfo.setMembers(item.getWatchCount() + 1); // 添加自己
                    CurLiveInfo.setAdmires(item.getAdmireCount());
                    CurLiveInfo.setAddress(item.getLbs().getAddress());
                    startActivity(intent);
                }

                SxbLog.i(TAG, "PerformanceTest  join Live     " + SxbLog.getTime());
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        mLiveListViewHelper.getPageData();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mLiveListViewHelper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
    }


    @Override
    public void showFirstPage(ArrayList<LiveInfoJson> result) {
        mSwipeRefreshLayout.setRefreshing(false);
        liveList.clear();
        if (null != result) {
            for (LiveInfoJson item : result) {
                liveList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mLiveListViewHelper.getPageData();
    }
}
