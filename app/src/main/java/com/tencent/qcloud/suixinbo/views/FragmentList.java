package com.tencent.qcloud.suixinbo.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;

/**
 * Created by tencent on 2016/12/22.
 */
public class FragmentList extends Fragment implements TabHost.OnTabChangeListener {
    private FragmentTabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        tabHost = (FragmentTabHost)view.findViewById(R.id.tab_host);
        tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        tabHost.addTab(tabHost.newTabSpec("Live")
                        .setIndicator(getTabView(getString(R.string.live_titile))),
                FragmentLiveList.class,
                null);

        tabHost.addTab(tabHost.newTabSpec("Record")
                        .setIndicator(getTabView(getString(R.string.str_record))),
                FragmentRecordList.class,
                null);
        tabHost.setCurrentTabByTag("Live");
        tabHost.getTabWidget().setDividerDrawable(null);
        updateTab(tabHost);
        tabHost.setOnTabChangedListener(this);
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        tabHost.setCurrentTabByTag(tabId);
        updateTab(tabHost);
    }

    private View getTabView(String title) {
        View tabView = LayoutInflater.from(getContext()).inflate(R.layout.toptab_layout, null);
        TextView tabTextView = (TextView) tabView.findViewById(R.id.tab_text);
        tabTextView.setText(title);
        return tabView;
    }

    /**
     * 更新Tab标签的颜色，和字体的颜色
     * @param tabHost
     */
    private void updateTab(final TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);

            TextView tv = (TextView)view.findViewById(R.id.tab_text);
            tv.setTextSize(18);
            if (tabHost.getCurrentTab() == i) {//选中
                view.setBackgroundResource(0==i ? R.drawable.tab_left_select : R.drawable.tab_right_select);
                //tv.setBackgroundColor(Color.WHITE);
                tv.setTextColor(this.getResources().getColor(R.color.sxbtheme));
            } else {//不选中
                view.setBackgroundResource(0==i ? R.drawable.tab_left : R.drawable.tab_right);
                //tv.setBackgroundColor(this.getResources().getColor(R.color.tabBgColor));
                tv.setTextColor(Color.WHITE);
            }
        }
    }
}
