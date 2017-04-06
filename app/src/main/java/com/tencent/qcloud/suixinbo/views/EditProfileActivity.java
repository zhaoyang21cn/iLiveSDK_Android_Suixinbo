package com.tencent.qcloud.suixinbo.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.ProfileInfoHelper;
import com.tencent.qcloud.suixinbo.presenters.UploadHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.UploadView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.GlideCircleTransform;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.utils.UIUtils;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;
import com.tencent.qcloud.suixinbo.views.customviews.LineControllerView;
import com.tencent.qcloud.suixinbo.views.customviews.TemplateTitle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人编辑页面
 */
public class EditProfileActivity extends BaseActivity implements View.OnClickListener, ProfileView, UploadView{
    private final static int REQ_EDIT_NICKNAME = 0x100;
    private final static int REQ_EDIT_SIGN  = 0x200;

    private static final int CROP_CHOOSE = 10;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;

    private UploadHelper mUploadHelper;
    private ProfileInfoHelper mProfileInfoHelper;
    private String TAG = "EditProfileActivity";
    private ImageView ivIcon;
    private TemplateTitle ttEdit;
    private LineControllerView lcvNickName;
    private LineControllerView lcvSign;
    private boolean bPermission = false;

    private Uri iconUrl, iconCrop;

    private void updateView(){
        lcvNickName.setContent(MySelfInfo.getInstance().getNickName());
        lcvSign.setContent(MySelfInfo.getInstance().getSign());
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            ivIcon.setImageBitmap(cirBitMap);
        }else{
            SxbLog.d(TAG, "profile avator: " + MySelfInfo.getInstance().getAvatar());
            RequestManager req = Glide.with(this);
            req.load(MySelfInfo.getInstance().getAvatar()).transform(new GlideCircleTransform(this)).into(ivIcon);
        }
    }

    private void initView(){
        ttEdit = (TemplateTitle) findViewById(R.id.tt_edit);
        ivIcon = (ImageView) findViewById(R.id.iv_ep_icon);
        lcvNickName = (LineControllerView) findViewById(R.id.lcv_ep_nickname);
        lcvSign = (LineControllerView) findViewById(R.id.lcv_ep_sign);

        ttEdit.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();

        mProfileInfoHelper = new ProfileInfoHelper(this);
        mUploadHelper = new UploadHelper(this, this);

        bPermission = checkCropPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUploadHelper.onDestory();
    }

    private Uri createCoverUri(String type) {
        String filename = MySelfInfo.getInstance().getId()+ type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return UIUtils.getUriFromFile(this, outputImage);
    }

    private boolean checkCropPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_PHONE_STATE)){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0){
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }


    /**
     * 获取图片资源
     *
     * @param type
     */
    private void getPicFrom(int type) {
        if (!bPermission){
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                iconUrl = createCoverUri("_icon");
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, iconUrl);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                iconUrl = createCoverUri("_select_icon");
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    /**
     * 图片选择对话框
     */
    private void showPhotoDialog() {
        final Dialog pickDialog = new Dialog(this, R.style.floag_dialog);
        pickDialog.setContentView(R.layout.pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = pickDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int)(display.getWidth()); //设置宽度

        pickDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) pickDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) pickDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) pickDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                pickDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                pickDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
            }
        });

        pickDialog.show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_ep_icon) {
            showPhotoDialog();

        } else if (i == R.id.lcv_ep_nickname) {
            EditActivity.navToEdit(this, getString(R.string.profile_nickname), MySelfInfo.getInstance().getNickName(), REQ_EDIT_NICKNAME);

        } else if (i == R.id.lcv_ep_sign) {
            EditActivity.navToEdit(this, getString(R.string.profile_sign), MySelfInfo.getInstance().getSign(), REQ_EDIT_SIGN);

        } else {
        }
    }

    public void startPhotoZoom(Uri uri) {
        iconCrop = createCoverUri("_icon_crop");

        revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 300);
        intent.putExtra("aspectY", 300);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconCrop);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            SxbLog.e(TAG, "onActivityResult->failed for request: " + requestCode + "/" + resultCode);
            return;
        }
        switch (requestCode){
        case REQ_EDIT_NICKNAME:
            mProfileInfoHelper.setMyNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            break;
        case REQ_EDIT_SIGN:
            mProfileInfoHelper.setMySign(data.getStringExtra(EditActivity.RETURN_EXTRA));
            break;
        case CAPTURE_IMAGE_CAMERA:
            startPhotoZoom(iconUrl);
            break;
        case IMAGE_STORE:
            String path = UIUtils.getPath(this, data.getData());
            if (null != path){
                SxbLog.e(TAG, "startPhotoZoom->path:" + path);
                File file = new File(path);
                startPhotoZoom(UIUtils.getUriFromFile(this, file));
            }
            break;
        case CROP_CHOOSE:
            mUploadHelper.uploadCover(iconCrop.getPath());
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults){
                    if (ret != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {
        if (TextUtils.isEmpty(profile.getNickName())){
            MySelfInfo.getInstance().setNickName(profile.getIdentifier());
        }else{
            MySelfInfo.getInstance().setNickName(profile.getNickName());
        }
        MySelfInfo.getInstance().setSign(profile.getSelfSignature());
        MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
        updateView();
    }

    @Override
    public void updateUserInfo(int reqid, List<TIMUserProfile> profiles) {
    }

    @Override
    public void onUploadProcess(int percent) {}

    @Override
    public void onUploadResult(int code, String url) {
        if (0 == code) {
            mProfileInfoHelper.setMyAvator(url);
        }else{
            SxbLog.w(TAG, "onUploadResult->failed: "+code);
        }
    }
}
