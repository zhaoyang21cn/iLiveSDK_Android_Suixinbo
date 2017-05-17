package com.tencent.qcloud.suixinbo.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.BuildConfig;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.LocationHelper;
import com.tencent.qcloud.suixinbo.presenters.UploadHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LocationView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.UploadView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.utils.UIUtils;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;
import com.tencent.qcloud.suixinbo.views.customviews.CustomSwitch;
import com.tencent.qcloud.suixinbo.views.customviews.LineControllerView;
import com.tencent.qcloud.suixinbo.views.customviews.RadioGroupDialog;
import com.tencent.qcloud.suixinbo.views.customviews.SpeedTestDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播发布类
 */
public class PublishLiveActivity extends BaseActivity implements View.OnClickListener, LocationView, UploadView {
    private UploadHelper mPublishLivePresenter;
    private LocationHelper mLocationHelper;
    private TextView BtnBack, BtnPublish;
    private Dialog mPicChsDialog;
    private ImageView cover;
    private Uri fileUri, cropUri;
    private TextView tvPicTip;
    private TextView tvLBS;
    private TextView tvTitle;
    private CustomSwitch btnLBS;
    private LineControllerView lcvRole;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final String TAG = PublishLiveActivity.class.getSimpleName();

    private static final int CROP_CHOOSE = 10;
    private boolean bUploading = false;
    private boolean bPermission = false;
    private int uploadPercent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_publish);
        mPublishLivePresenter = new UploadHelper(this, this);
        mLocationHelper = new LocationHelper(this);
        tvTitle = (TextView) findViewById(R.id.live_title);
        BtnBack = (TextView) findViewById(R.id.btn_cancel);
        tvPicTip = (TextView) findViewById(R.id.tv_pic_tip);
        BtnPublish = (TextView) findViewById(R.id.btn_publish);
        cover = (ImageView) findViewById(R.id.cover);
        tvLBS = (TextView) findViewById(R.id.address);
        btnLBS = (CustomSwitch) findViewById(R.id.btn_lbs);
        lcvRole = (LineControllerView)findViewById(R.id.lcv_role);
        cover.setOnClickListener(this);
        BtnBack.setOnClickListener(this);
        BtnPublish.setOnClickListener(this);
        btnLBS.setOnClickListener(this);
        lcvRole.setOnClickListener(this);

        lcvRole.setContent(getRoleShow(CurLiveInfo.getCurRole()));

        initPhotoDialog();
        initRoleDialog();
        // 提前更新sig
        mPublishLivePresenter.updateSig();

        bPermission = checkPublishPermission();
    }

    @Override
    protected void onDestroy() {
        mPublishLivePresenter.onDestory();
        mLocationHelper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            finish();

        } else if (i == R.id.btn_publish) {
            if (bUploading) {
                Toast.makeText(this, getString(R.string.publish_wait_uploading) + " " + uploadPercent + "%", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, LiveActivity.class);
                MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                MySelfInfo.getInstance().setJoinRoomWay(true);
                CurLiveInfo.setTitle(tvTitle.getText().toString().isEmpty() ? "直播间" : tvTitle.getText().toString());
                CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
                CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                startActivity(intent);
                SxbLog.i(TAG, "PerformanceTest  publish Live     " + SxbLog.getTime());
                this.finish();
            }

        } else if (i == R.id.cover) {
            mPicChsDialog.show();

        } else if (i == R.id.btn_lbs) {
            if (btnLBS.getChecked()) {
                btnLBS.setChecked(false, true);
                tvLBS.setText(R.string.text_live_close_lbs);
            } else {
                btnLBS.setChecked(true, true);
                tvLBS.setText(R.string.text_live_location);
                if (mLocationHelper.checkLocationPermission()) {
                    if (!mLocationHelper.getMyLocation(getApplicationContext(), this)) {
                        tvLBS.setText(getString(R.string.text_live_lbs_fail));
                        btnLBS.setChecked(false, false);
                    }
                }
            }

        } else if (i == R.id.speed_test) {
            new SpeedTestDialog(this).start();
        } else if (i == R.id.lcv_role){
            if (roleDialog != null) roleDialog.show();
        }
    }

    // 角色对话框
    private RadioGroupDialog roleDialog;
    private void initRoleDialog() {
        final String[] roles = new String[]{ "高清(960*540,25fps)","标清(640*368,20fps)", "流畅(640*368,15fps)"};
        final String[] values = new String[]{Constants.HD_ROLE, Constants.SD_ROLE, Constants.LD_ROLE};
        roleDialog = new RadioGroupDialog(this, roles);
        roleDialog.setTitle(R.string.str_dt_change_role);
        if (CurLiveInfo.getCurRole().equals(Constants.SD_ROLE)){
            roleDialog.setSelected(1);
        }else if (CurLiveInfo.getCurRole().equals(Constants.LD_ROLE)){
            roleDialog.setSelected(2);
        }else {
            roleDialog.setSelected(0);
        }
        roleDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SxbLog.d(TAG, "initRoleDialog->onClick item:"+position);
                CurLiveInfo.setCurRole(values[position]);
                lcvRole.setContent(getRoleShow(CurLiveInfo.getCurRole()));
            }
        });
    }

    private String getRoleShow(String role){
        if (role.equals(Constants.HD_ROLE)){
            return getString(R.string.str_dt_hd);
        }else if (role.equals(Constants.SD_ROLE)){
            return getString(R.string.str_dt_sd);
        }else{
            return getString(R.string.str_dt_ld);
        }
    }

    /**
     * 图片选择对话框
     */
    private void initPhotoDialog() {
        mPicChsDialog = new Dialog(this, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mPicChsDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
            }
        });
    }


    /**
     * 获取图片资源
     *
     * @param type
     */
    private void getPicFrom(int type) {
        if (!bPermission) {
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUri("", false);
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                fileUri = createCoverUri("_select", false);
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(PublishLiveActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(PublishLiveActivity.this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(PublishLiveActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(PublishLiveActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(PublishLiveActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    private Uri createCoverUri(String type, boolean bCrop) {
        String filename = MySelfInfo.getInstance().getId() + type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
/*        if (ContextCompat.checkSelfPermission(PublishLiveActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PublishLiveActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }*/
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bCrop) {
            return Uri.fromFile(outputImage);
        }else {
            return UIUtils.getUriFromFile(this, outputImage);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_CAMERA:
                    startPhotoZoom(fileUri);
                    break;
                case IMAGE_STORE:
                    String path = UIUtils.getPath(this, data.getData());
                    if (null != path) {
                        SxbLog.d(TAG, "startPhotoZoom->path:" + path);
                        File file = new File(path);
                        startPhotoZoom(UIUtils.getUriFromFile(this, file));
                    }
                    break;
                case CROP_CHOOSE:
                    tvPicTip.setVisibility(View.GONE);
                    cover.setImageBitmap(null);
                    cover.setImageURI(cropUri);
                    bUploading = true;
                    mPublishLivePresenter.uploadCover(cropUri.getPath());
                    break;

            }
        }

    }

    public void startPhotoZoom(Uri uri) {
        cropUri = createCoverUri("_crop", true);

        Intent intent = new Intent("com.android.camera.action.CROP");
        /* 这句要记得写：这是申请权限，之前因为没有添加这个，打开裁剪页面时，一直提示“无法修改低于50*50像素的图片”，
      开始还以为是图片的问题呢，结果发现是因为没有添加FLAG_GRANT_READ_URI_PERMISSION。
      如果关联了源码，点开FileProvider的getUriForFile()看看（下面有），注释就写着需要添加权限。
      */
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 500);
        intent.putExtra("aspectY", 309);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 309);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    @Override
    public void onLocationChanged(int code, double lat1, double long1, String location) {
        if (btnLBS.getChecked()) {
            if (0 == code) {
                tvLBS.setText(location);
                CurLiveInfo.setLat1(lat1);
                CurLiveInfo.setLong1(long1);
                CurLiveInfo.setAddress(location);
            } else {
                tvLBS.setText(getString(R.string.text_live_lbs_fail));
            }
        } else {
            CurLiveInfo.setLat1(0);
            CurLiveInfo.setLong1(0);
            CurLiveInfo.setAddress("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mLocationHelper.getMyLocation(getApplicationContext(), this)) {
                        tvLBS.setText(getString(R.string.text_live_lbs_fail));
                        btnLBS.setChecked(false, false);
                    }
                }
                break;
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int i=0; i<grantResults.length; i++){
                     if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        SxbLog.d(TAG, "request permission failed: "+permissions[i]);
                     }else{
                         SxbLog.d(TAG, "request permission success: "+permissions[i]);
                     }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onUploadResult(int code, String url) {
        if (0 == code) {
            CurLiveInfo.setCoverurl(url);
            Toast.makeText(this, getString(R.string.publish_upload_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.publish_upload_cover_failed)+"|"+code+"|"+url, Toast.LENGTH_SHORT).show();
        }
        bUploading = false;
    }

    @Override
    public void onUploadProcess(int percent) {
        uploadPercent = percent;
    }
}
