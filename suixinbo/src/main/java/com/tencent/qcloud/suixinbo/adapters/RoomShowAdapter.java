package com.tencent.qcloud.suixinbo.adapters;

import android.app.Activity;
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
import com.tencent.qcloud.suixinbo.model.RoomInfoJson;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.utils.UIUtils;

import java.util.ArrayList;


/**
 * 直播列表的Adapter
 */
public class RoomShowAdapter extends ArrayAdapter<RoomInfoJson> {
    private static String TAG = "LiveShowAdapter";
    private int resourceId;
    private Activity mActivity;
    private class ViewHolder{
        TextView tvTitle;
        TextView tvHost;
        TextView tvMembers;
        TextView tvAdmires;
        TextView tvLbs;
        ImageView ivCover;
        ImageView ivAvatar;
    }

    public RoomShowAdapter(Activity activity, int resource, ArrayList<RoomInfoJson> objects) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_liveshow, null);

            holder = new ViewHolder();
            holder.ivCover = (ImageView) convertView.findViewById(R.id.cover);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.live_title);
            holder.tvHost = (TextView) convertView.findViewById(R.id.host_name);
            holder.tvMembers = (TextView) convertView.findViewById(R.id.live_members);
            holder.tvAdmires = (TextView) convertView.findViewById(R.id.praises);
            holder.tvLbs = (TextView) convertView.findViewById(R.id.live_lbs);
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.avatar);

            convertView.setTag(holder);
        }

        RoomInfoJson data = getItem(position);
        if (!TextUtils.isEmpty(data.getInfo().getCover())){
            SxbLog.d(TAG, "load cover: " + data.getInfo().getCover());
            RequestManager req = Glide.with(mActivity);
            req.load(data.getInfo().getCover()).into(holder.ivCover); //获取网络图片
//            holder.ivCover.setImageResource(R.drawable.cover_background);
        }else{
            holder.ivCover.setImageResource(R.drawable.cover_background);
        }

        if (null == data.getHostId() || TextUtils.isEmpty("")){
            // 显示默认图片
            Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            holder.ivAvatar.setImageBitmap(cirBitMap);
        }else{
//            RequestManager req = Glide.with(mActivity);
//            req.load(data.getHost().getAvatar()).transform(new GlideCircleTransform(mActivity)).into(holder.ivAvatar);
        }

        holder.tvTitle.setText(UIUtils.getLimitString(data.getInfo().getTitle(), 10));
        if (!TextUtils.isEmpty("")){
//            holder.tvHost.setText("@" + UIUtils.getLimitString(data.getHost().getUsername(), 10));
        }else{
            holder.tvHost.setText("@" + UIUtils.getLimitString(data.getHostId(), 10));
        }
        holder.tvMembers.setText(""+data.getInfo().getMemsize());
        holder.tvAdmires.setText(""+data.getInfo().getThumbup());
//        if (!TextUtils.isEmpty(data.getLbs().getAddress())) {
//            holder.tvLbs.setText(UIUtils.getLimitString(data.getLbs().getAddress(), 9));
//        }else{
//            holder.tvLbs.setText(getContext().getString(R.string.live_unknown));
//        }

        return convertView;
    }
}
