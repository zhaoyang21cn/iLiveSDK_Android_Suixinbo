package com.tencent.qcloud.suixinbo.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.QavsdkApplication;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.presenters.LoginHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;

/**
 * 注册账号类
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, LoginView {
    private EditText mUserName, mPassword, mRepassword;
    private TextView mBtnRegister;
    private ImageButton mBtnBack;
    QavsdkApplication mMyApplication;
    LoginHelper mLoginHeloper;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independent_register);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRepassword = (EditText) findViewById(R.id.repassword);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        mBtnBack = (ImageButton) findViewById(R.id.back);
        mBtnBack.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mMyApplication = (QavsdkApplication) getApplication();
        mLoginHeloper = new LoginHelper(this, this);
    }

    @Override
    protected void onDestroy() {
        mLoginHeloper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            String userId = mUserName.getText().toString();
            String userPW = mPassword.getText().toString();
            String userPW2 = mRepassword.getText().toString();


            if (userId.length() < 4 || userId.length() > 24) {
                Log.i(TAG, "onClick " + userId.length());
                Toast.makeText(RegisterActivity.this, "用户名不符合格式", Toast.LENGTH_SHORT).show();
                return;
            }


            if (userId.length() == 0 || userPW.length() == 0 || userPW2.length() == 0) {
                Toast.makeText(RegisterActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!userPW.equals(userPW2)) {
                Toast.makeText(RegisterActivity.this, "两次密码输入密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPW.length() < 8) {
                Toast.makeText(RegisterActivity.this, "密码的长度不能小于8个字符", Toast.LENGTH_SHORT).show();
                return;
            }

            //注册一个账号
            mLoginHeloper.tlsRegister(userId, mPassword.getText().toString());
        }
        if (view.getId() == R.id.back) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void loginSucc() {
        jumpIntoHomeActivity();
    }

    @Override
    public void loginFail() {

    }

    /**
     * 直接跳转主界面
     */
    private void jumpIntoHomeActivity() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
