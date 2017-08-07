package com.tencent.qcloud.suixinbo.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.adapters.RecordAdapter;
import com.tencent.qcloud.suixinbo.model.RecordInfo;
import com.tencent.qcloud.suixinbo.presenters.ProfileInfoHelper;
import com.tencent.qcloud.suixinbo.presenters.RecListViewHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.RecListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xkazerzhang on 2016/12/22.
 */
public class FragmentRecordList extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecListView, ProfileView {
    private ListView mLiveList;
    private EditText etKey, etSize;
    private RecListViewHelper recHelper;
    private ProfileInfoHelper mUserInfoHelper;
    private RecordAdapter adapterRecord;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<RecordInfo> recList;

    private AlertDialog mWaitDialog;
    private int reqCode = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        mLiveList = (ListView) view.findViewById(R.id.record_list);
        etKey = (EditText)view.findViewById(R.id.et_search_key);
        etSize = (EditText)view.findViewById(R.id.et_search_size);
        etKey.setText(ILiveLoginManager.getInstance().getMyUserId());
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        recHelper = new RecListViewHelper(this);
        mUserInfoHelper = new ProfileInfoHelper(this);
        freshRecordList();
        return view;
    }

    @Override
    public void onRefresh() {
        freshRecordList();
    }

    @Override
    public void onUpdateRecordList(ArrayList<RecordInfo> list) {
        dimissWaitDialog();
        mSwipeRefreshLayout.setRefreshing(false);
        recList = list;
        adapterRecord = new RecordAdapter(getActivity(), R.layout.item_record, recList);
        ArrayList<String> userList = new ArrayList<>();
        if (list != null) {
            for (RecordInfo item : list) {
                if (!userList.contains(item.getStrUser()))
                    userList.add(item.getStrUser());
            }
            mUserInfoHelper.getUsersInfo(++reqCode, userList);
            mLiveList.setAdapter(adapterRecord);
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {

    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {
        if (requestCode == reqCode && null != recList){
            HashMap<String, TIMUserProfile> map = new HashMap<>();
            for (TIMUserProfile profile : profiles){
                map.put(profile.getIdentifier(), profile);
            }
            for (RecordInfo info : recList){
                if (map.containsKey(info.getStrUser())){
                    info.setStrFaceUrl(map.get(info.getStrUser()).getFaceUrl());
                }
            }
            adapterRecord.notifyDataSetChanged();
        }
    }

    private void freshRecordList() {
        int size = 0;
        try {
            size = Integer.valueOf(etSize.getText().toString());
        }catch (Exception e){
        }
        if (size <= 0 || size > 30){     // 列表不能超过30
            size = 15;
        }
        showWaitDlalog(getString(R.string.str_loading_record));
        recHelper.refresh(etKey.getText().toString(), size);
    }

    private void showWaitDlalog(String strMsg) {
        if (null == mWaitDialog) {
            mWaitDialog = new AlertDialog.Builder(getActivity())
                    .setMessage(strMsg)
                    .create();
            mWaitDialog.setCanceledOnTouchOutside(false);
            mWaitDialog.show();
        }else{
            mWaitDialog.setMessage(strMsg);
            mWaitDialog.show();
        }
    }

    private void dimissWaitDialog() {
        if (null != mWaitDialog) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }
}
