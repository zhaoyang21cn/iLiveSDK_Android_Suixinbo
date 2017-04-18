package com.tencent.qcloud.suixinbo.views.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;


/**
 * 标题控件
 */
public class TemplateTitle extends RelativeLayout {

    private String titleText;
    private boolean canBack;
    private String backText;
    private String moreText;

    private TextView tvReturn;
    private TextView tvTitle;
    private TextView tvMore;


    public TemplateTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TemplateTitle, 0, 0);
        try {
            titleText = ta.getString(R.styleable.TemplateTitle_titleText);
            canBack = ta.getBoolean(R.styleable.TemplateTitle_canBack, true);
            backText = ta.getString(R.styleable.TemplateTitle_backText);
            moreText = ta.getString(R.styleable.TemplateTitle_moreText);
            setUpView();
        } finally {
            ta.recycle();
        }
    }

    private void setUpView(){
        tvReturn = (TextView)findViewById(R.id.menu_return);
        tvTitle = (TextView)findViewById(R.id.title);
        tvMore = (TextView)findViewById(R.id.menu_more);

        if (!canBack){
            tvReturn.setVisibility(View.GONE);
        }

        tvReturn.setText(backText);
        tvMore.setText(moreText);
        tvTitle.setText(titleText);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        titleText = title;
        tvTitle.setText(title);
    }

    /**
     * 设置扩展消息
     * @param title
     */
    public void setMoreText(String title){
        moreText = title;
        tvMore.setText(title);
    }

    /**
     * 设置返回文案
     * @param strReturn
     */
    public void setReturnText(String strReturn){
        backText = strReturn;
        tvReturn.setText(strReturn);
    }

    /**
     * 设置返回消息事件
     * @param listener
     */
    public void setReturnListener(View.OnClickListener listener){
        tvReturn.setOnClickListener(listener);
    }

    /**
     * 设置扩展事件
     * @param listener
     */
    public void setMoreListener(View.OnClickListener listener){
        if (!TextUtils.isEmpty(moreText)) {
            tvMore.setOnClickListener(listener);
        }
    }
}
