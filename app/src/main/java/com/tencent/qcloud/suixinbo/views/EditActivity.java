package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;
import com.tencent.qcloud.suixinbo.views.customviews.TemplateTitle;

/**
 * 修改文本页面
 */
public class EditActivity extends BaseActivity {


    public final static String RETURN_EXTRA = "result";
    private static String defaultString;
    private EditText input;
    private static int lenLimit;

    /**
     * 启动修改文本界面
     *
     * @param context fragment context
     * @param title 界面标题
     * @param defaultStr 默认文案
     * @param reqCode 请求码，用于识别返回结果
     */
    public static void navToEdit(Fragment context, String title, String defaultStr, int reqCode){
        Intent intent = new Intent(context.getActivity(), EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
    }


    /**
     * 启动修改文本界面
     *
     * @param context activity context
     * @param title 界面标题
     * @param defaultStr 默认文案
     * @param reqCode 请求码，用于识别返回结果
     */
    public static void navToEdit(Activity context, String title, String defaultStr, int reqCode){
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
    }


    /**
     * 启动修改文本界面
     *
     * @param context fragment context
     * @param title 界面标题
     * @param defaultStr 默认文案
     * @param reqCode 请求码，用于识别返回结果
     * @param limit 输入长度限制
     */
    public static void navToEdit(Fragment context, String title, String defaultStr, int reqCode,int limit){
        Intent intent = new Intent(context.getActivity(), EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
        lenLimit = limit;
    }


    /**
     * 启动修改文本界面
     *
     * @param context activity context
     * @param title 界面标题
     * @param defaultStr 默认文案
     * @param reqCode 请求码，用于识别返回结果
     * @param limit 输入长度限制
     */
    public static void navToEdit(Activity context, String title, String defaultStr, int reqCode, int limit){
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
        lenLimit = limit;
    }

    private void cancelEdit(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelEdit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getIntent().getStringExtra("title");
        input = (EditText) findViewById(R.id.editContent);
        if (defaultString != null){
            input.setText(defaultString);
            input.setSelection(defaultString.length());
        }
        if (lenLimit != 0){
            input.setFilters( new InputFilter[] { new InputFilter.LengthFilter(lenLimit) } );
        }
        TemplateTitle title = (TemplateTitle) findViewById(R.id.ttHead);
        title.setTitle(getIntent().getStringExtra("title"));
        title.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(RETURN_EXTRA, input.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        title.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEdit();
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        defaultString = null;
        lenLimit = 0;
    }
}
