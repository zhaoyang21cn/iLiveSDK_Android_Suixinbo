package com.tencent.qcloud.suixinbo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tencent.bugly.imsdk.crashreport.CrashReport;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.RecordInfo;
import com.tencent.qcloud.suixinbo.views.ActivityReplay;

import java.util.ArrayList;


/**
 * 点播列表的Adapter
 */
public class RecordAdapter extends ArrayAdapter<RecordInfo> {
    private static String TAG = "RecordAdapter";
    private int resourceId;
    private Activity mActivity;
    private class ViewHolder{
        TextView tvUser;
        TextView tvCover;
        TextView tvVideoId;
        TextView tvUrl;
    }

    public RecordAdapter(Activity activity, int resource, ArrayList<RecordInfo> objects) {
        super(activity, resource, objects);
        resourceId = resource;
        mActivity = activity;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder)convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            holder = new ViewHolder();
            holder.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
            holder.tvCover = (TextView) convertView.findViewById(R.id.tv_cover);
            holder.tvVideoId = (TextView) convertView.findViewById(R.id.tv_videoid);
            holder.tvUrl = (TextView) convertView.findViewById(R.id.tv_url);

            convertView.setTag(holder);
        }

        final RecordInfo data = getItem(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurLiveInfo.setRecordInfo(data);
                mActivity.startActivity(new Intent(mActivity.getApplicationContext(), ActivityReplay.class));
            }
        });

        if (!TextUtils.isEmpty(data.getStrUser()))
            holder.tvUser.setText(data.getStrUser());
        if (!TextUtils.isEmpty(data.getStrCover()))
            holder.tvCover.setText( data.getStrCover());
        holder.tvVideoId.setText(data.getStrVideoId());
        holder.tvUrl.setText(data.getPlayUrl());

        return convertView;
    }
}
