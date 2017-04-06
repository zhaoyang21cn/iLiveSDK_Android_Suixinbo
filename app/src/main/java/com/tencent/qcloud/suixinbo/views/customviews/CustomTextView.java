package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 自定义TextView控件以便正确计算多行文本的实际高度
 */
public class CustomTextView extends TextView {
    private float mWidth = 0;

    public CustomTextView(Context context) {
        this(context, null, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidth = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     *  修正实际宽度
     */
    public void fixViewWidth(float width){
        mWidth = width;
    }

    /**
     *  计算实际高度
     */
    private float getMaxLineHeight(String str){
        float totalHeight = 0.0f;

        float paddingLeft = ((LinearLayout)this.getParent()).getPaddingLeft();
        float paddingRight = (((LinearLayout) this.getParent()).getPaddingRight());
        // 计算有效宽度
        float width = mWidth - paddingLeft - paddingRight - getPaddingLeft() - getPaddingRight();

        // 计算文本行数
        int line = (int) Math.ceil((this.getPaint().measureText(str) / width));

        totalHeight = (this.getPaint().getFontMetrics().descent-this.getPaint().getFontMetrics().ascent)*line;

        return totalHeight;
    }

    /**
     *  重载onMeasure方法，以便父控件能够获取实际高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout();
        if (null != layout){
            int height = (int) Math.ceil(getMaxLineHeight(this.getText().toString()))
                    + getCompoundPaddingTop() + getCompoundPaddingBottom();
            int width = getMeasuredWidth();

            setMeasuredDimension(width, height);
        }
    }
}
