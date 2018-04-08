package com.tencent.qcloud.suixinbo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.RecordInfo;
import com.tencent.qcloud.suixinbo.utils.GlideCircleTransform;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.utils.UIUtils;
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
        ImageView ivIcon;
        ImageView ivAvatar;
        TextView tvName;
        TextView tvUser;
        TextView tvTime;
        TextView tvSize;
        TextView tvDuration;
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
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.cover);
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);

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

        if (!TextUtils.isEmpty(data.getStrCover())){
            SxbLog.d(TAG, "load cover: " + data.getStrCover());
            RequestManager req = Glide.with(mActivity);
            req.load(data.getStrCover()).into(holder.ivIcon);
        }else{
            holder.ivIcon.setImageResource(R.drawable.default_background);
        }

        holder.tvName.setText(data.getStrName());
        if (!TextUtils.isEmpty(data.getStrUser()))
            holder.tvUser.setText("@"+data.getStrUser());
        if (!TextUtils.isEmpty(data.getStrCreateTime()))
            holder.tvTime.setText(data.getStrCreateTime());
        if (!TextUtils.isEmpty(data.getStrSize()))
            holder.tvSize.setText(data.getStrSize());
        if (!TextUtils.isEmpty(data.getStrDuration()))
            holder.tvDuration.setText(data.getStrDuration());
        if (TextUtils.isEmpty(data.getStrFaceUrl())){
            // 显示默认图片
            Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            holder.ivAvatar.setImageBitmap(cirBitMap);
        }else{
            SxbLog.d(TAG, "load face: " + data.getStrFaceUrl());
            RequestManager req = Glide.with(mActivity);
            req.load(data.getStrFaceUrl()).transform(new GlideCircleTransform(mActivity)).into(holder.ivAvatar);
        }
        return convertView;
    }
}
