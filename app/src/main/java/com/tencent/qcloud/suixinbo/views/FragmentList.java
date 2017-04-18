package com.tencent.qcloud.suixinbo.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        tabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

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
//        updateTab(tabHost);
        tabHost.setOnTabChangedListener(this);
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        tabHost.setCurrentTabByTag(tabId);
//        updateTab(tabHost);
    }

    /**
     * 更新Tab标签的颜色，和字体的颜色
     * @param tabHost
     */
    private void updateTab(final TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            //view.setPadding(0, 0, 0, 20);
            view.setBackgroundColor(getResources().getColor(R.color.btn_red_hover));
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(18);
            if (tabHost.getCurrentTab() == i) {//选中
                tv.setTextColor(Color.WHITE);
            } else {//不选中
                tv.setTextColor(Color.GRAY);
            }
        }
    }

    private View getTabView(String title) {
        View tabView = LayoutInflater.from(getContext()).inflate(R.layout.toptab_layout, null);
        ImageView tabImageView = (ImageView) tabView.findViewById(R.id.tab_image);
        tabImageView.setImageResource(R.drawable.tap_line);
        TextView tabTextView = (TextView) tabView.findViewById(R.id.tab_text);
        tabTextView.setText(title);
        return tabView;
    }
}
