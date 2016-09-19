package com.tencent.qcloud.suixinbo.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.LoginHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;
import com.tencent.qcloud.suixinbo.utils.SxbLog;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录类
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginView {
    TextView mBtnLogin, mBtnRegister;
    EditText mPassWord, mUserName;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginHelper mLoginHeloper;
    private final int REQUEST_PHONE_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SxbLog.i(TAG, "LoginActivity onCreate");
        mLoginHeloper = new LoginHelper(this, this);
        checkPermission();
        //获取个人数据本地缓存
        MySelfInfo.getInstance().getCache(getApplicationContext());
        if (needLogin() == true) {//本地没有账户需要登录
            initView();
        } else {
            //有账户登录直接IM登录
            SxbLog.i(TAG, "LoginActivity onCreate");
            mLoginHeloper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }
    }

    @Override
    protected void onDestroy() {
        mLoginHeloper.onDestory();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerNewUser) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if (view.getId() == R.id.btn_login) {//登录账号系统TLS
            if (mUserName.getText().equals("")) {
                Toast.makeText(LoginActivity.this, "name can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mPassWord.getText().equals("")) {
                Toast.makeText(LoginActivity.this, "password can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
//            tlsLogin(mUserName.getText().toString(),mPassWord.getText().toString());
            mLoginHeloper.tlsLogin(mUserName.getText().toString(), mPassWord.getText().toString());
        }
    }

    private void initView() {
        setContentView(R.layout.activity_independent_login);
        mBtnLogin = (TextView) findViewById(R.id.btn_login);
        mUserName = (EditText) findViewById(R.id.username);
        mPassWord = (EditText) findViewById(R.id.password);
        mBtnRegister = (TextView) findViewById(R.id.registerNewUser);
        mBtnRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }


    /**
     * 判断是否需要登录
     *
     * @return true 代表需要重新登录
     */
    public boolean needLogin() {
        if (MySelfInfo.getInstance().getId() != null) {
            return false;//有账号不需要登录
        } else {
            return true;//需要登录
        }

    }


    /**
     * 直接跳转主界面
     */
    private void jumpIntoHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void loginSucc() {
        Toast.makeText(LoginActivity.this, "" + MySelfInfo.getInstance().getId() + " login ", Toast.LENGTH_SHORT).show();
        jumpIntoHomeActivity();
    }

    @Override
    public void loginFail() {
        initView();
    }

    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }
}
