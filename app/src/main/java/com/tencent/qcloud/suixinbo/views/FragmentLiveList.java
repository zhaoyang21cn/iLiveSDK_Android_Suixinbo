package com.tencent.qcloud.suixinbo.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.TIMUserProfile;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.adapters.RoomShowAdapter;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.model.RoomInfoJson;
import com.tencent.qcloud.suixinbo.presenters.LiveListViewHelper;
import com.tencent.qcloud.suixinbo.presenters.ProfileInfoHelper;
import com.tencent.qcloud.suixinbo.presenters.UserServerHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveListView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.views.customviews.BaseFragmentActivity;
import com.tencent.qcloud.suixinbo.views.customviews.RadioGroupDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 直播列表页面
 */
public class FragmentLiveList extends Fragment implements View.OnClickListener, LiveListView, SwipeRefreshLayout.OnRefreshListener, ProfileView {
    private static final String TAG = "FragmentLiveList";
    private ListView mLiveList;
//    private ArrayList<LiveInfoJson> liveList = new ArrayList<LiveInfoJson>();
    private ArrayList<RoomInfoJson> roomList = new ArrayList<RoomInfoJson>();
    private RoomShowAdapter roomShowAdapter;
    private ProfileInfoHelper mUserInfoHelper;
    private LiveListViewHelper mLiveListViewHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int reqCode = 0;

    public FragmentLiveList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLiveListViewHelper = new LiveListViewHelper(this);
        mUserInfoHelper = new ProfileInfoHelper(this);
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);
        mLiveList = (ListView) view.findViewById(R.id.live_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),getResources().getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        roomShowAdapter =new RoomShowAdapter(getActivity(), R.layout.item_liveshow, roomList);
        mLiveList.setAdapter(roomShowAdapter);
        mLiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                RoomInfoJson item = roomList.get(i);
                //如果是自己
                if (item.getHostId().equals(MySelfInfo.getInstance().getId())) {
                    Intent intent = new Intent(getActivity(), LiveActivity.class);
                    MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                    MySelfInfo.getInstance().setJoinRoomWay(true);
                    CurLiveInfo.setHostID(item.getHostId());
                    CurLiveInfo.setHostName(MySelfInfo.getInstance().getId());
                    CurLiveInfo.setHostAvator("");
                    CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                    CurLiveInfo.setMembers(item.getInfo().getMemsize()); // 添加自己
                    CurLiveInfo.setAdmires(item.getInfo().getThumbup());
//                    CurLiveInfo.setAddress(item.getLbs().getAddress());
                    startActivity(intent);
                }else{
                    MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(item.getHostId());
                    CurLiveInfo.setHostName(item.getHostId());
                    CurLiveInfo.setHostAvator(item.getFaceUrl());
                    CurLiveInfo.setRoomNum(item.getInfo().getRoomnum());
                    CurLiveInfo.setMembers(item.getInfo().getMemsize()); // 添加自己
                    CurLiveInfo.setAdmires(item.getInfo().getThumbup());
//                    CurLiveInfo.setAddress(item.getLbs().getAddress());
					Intent intent = new Intent(getActivity(), LiveActivity.class);
            		startActivity(intent);
                }
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
        if (null != mLiveListViewHelper)
            mLiveListViewHelper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void showRoomList(UserServerHelper.RequestBackInfo info , ArrayList<RoomInfoJson> roomlist) {
        if(info.getErrorCode()!=0){
            Toast.makeText(getContext(), "error "+info.getErrorCode()+" info " +info.getErrorInfo(), Toast.LENGTH_SHORT).show();
            if (Constants.ERR_TOKEN_EXPIRED == info.getErrorCode()){    // token过期
                ((BaseFragmentActivity)getActivity()).requiredLogin();
            }
            return ;
        }

        mSwipeRefreshLayout.setRefreshing(false);
        roomList.clear();
        ArrayList<String> userList = new ArrayList<>();
        if (null != roomlist) {
            for (RoomInfoJson item : roomlist) {
                roomList.add(item);
                if (!userList.contains(item.getHostId()))
                    userList.add(item.getHostId());
            }
        }
        mUserInfoHelper.getUsersInfo(++reqCode, userList);
        roomShowAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        mLiveListViewHelper.getPageData();
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {

    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {
        if (requestCode == reqCode){
            HashMap<String, TIMUserProfile> map = new HashMap<>();
            for (TIMUserProfile profile : profiles){
                map.put(profile.getIdentifier(), profile);
            }
            for (RoomInfoJson json : roomList){
                if (map.containsKey(json.getHostId())){
                    json.setFaceUrl(map.get(json.getHostId()).getFaceUrl());
                }
            }
            roomShowAdapter.notifyDataSetChanged();
        }
    }
}
