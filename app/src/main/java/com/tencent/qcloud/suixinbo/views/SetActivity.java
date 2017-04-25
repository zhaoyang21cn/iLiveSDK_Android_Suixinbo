package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.TIMManager;
import com.tencent.av.sdk.AVContext;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.qalsdk.QALSDKManager;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;
import com.tencent.qcloud.suixinbo.views.customviews.CustomSwitch;
import com.tencent.qcloud.suixinbo.views.customviews.LineControllerView;
import com.tencent.qcloud.suixinbo.views.customviews.RadioGroupDialog;
import com.tencent.qcloud.suixinbo.views.customviews.TemplateTitle;

/**
 * 设置页面
 */
public class SetActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "SetActivity";
    private final String beautyTypes[] = new String[]{"ILiveSDK", "AVSDK"};
    private CustomSwitch csAnimator;
    private LineControllerView lcvLog;
    private LineControllerView lcvBeauty;
    private LineControllerView lcvVersion;
    private TemplateTitle ttHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView(){
        ttHead = (TemplateTitle)findViewById(R.id.tt_head);
        csAnimator = (CustomSwitch)findViewById(R.id.cs_animator);
        lcvLog = (LineControllerView)findViewById(R.id.lcv_set_log_level);
        lcvBeauty = (LineControllerView)findViewById(R.id.lcv_beauty_type);
        lcvVersion = (LineControllerView)findViewById(R.id.lcv_set_version);

        lcvLog.setContent(MySelfInfo.getInstance().getLogLevel().toString());
        lcvBeauty.setContent(beautyTypes[MySelfInfo.getInstance().getBeautyType()&0x1]);

        csAnimator.setChecked(MySelfInfo.getInstance().isbLiveAnimator(), false);

        ttHead.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changeLogLevel(){
        RadioGroupDialog voiceTypeDialog = new RadioGroupDialog(this, SxbLog.getStringValues());
        voiceTypeDialog.setTitle(R.string.str_dt_voice);
        voiceTypeDialog.setSelected(SxbLog.getLogLevel().ordinal());
        voiceTypeDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SxbLog.d(TAG, "changeLogLevel->onClick item:"+position);
                MySelfInfo.getInstance().setLogLevel(SxbLog.SxbLogLevel.values()[position]);
                SxbLog.setLogLevel(MySelfInfo.getInstance().getLogLevel());
                lcvLog.setContent(MySelfInfo.getInstance().getLogLevel().toString());
                MySelfInfo.getInstance().writeToCache(SetActivity.this);
            }
        });
        voiceTypeDialog.show();
    }

    private void changeBeautyType(){
        RadioGroupDialog beautyTypeDialog = new RadioGroupDialog(this, beautyTypes);
        beautyTypeDialog.setTitle(R.string.str_beauty_type);
        beautyTypeDialog.setSelected(MySelfInfo.getInstance().getBeautyType());
        beautyTypeDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SxbLog.d(TAG, "changeBeautyType->onClick item:"+position);
                if (0 == position){
                    Toast.makeText(SetActivity.this, getString(R.string.str_beauty_not_support), Toast.LENGTH_SHORT).show();
                    return;
                }

                MySelfInfo.getInstance().setBeautyType(position);
                MySelfInfo.getInstance().writeToCache(SetActivity.this);
                lcvBeauty.setContent(beautyTypes[MySelfInfo.getInstance().getBeautyType() & 0x1]);
            }
        });
        beautyTypeDialog.show();
    }

    private void showSDKVersion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("IM SDK: "+ TIMManager.getInstance().getVersion()+"\r\n"
            +"QAL SDK: "+ QALSDKManager.getInstance().getSdkVersion()+"\r\n"
            +"AV SDK: "+ AVContext.getVersion());
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.cs_animator:
            MySelfInfo.getInstance().setbLiveAnimator(!MySelfInfo.getInstance().isbLiveAnimator());
            MySelfInfo.getInstance().writeToCache(this);
            csAnimator.setChecked(MySelfInfo.getInstance().isbLiveAnimator(), true);
            break;
        case R.id.lcv_set_log_level:
            changeLogLevel();
            break;
        case R.id.lcv_beauty_type:
            changeBeautyType();
            break;
        case R.id.lcv_set_version:
            showSDKVersion();
            break;
        }
    }
}
