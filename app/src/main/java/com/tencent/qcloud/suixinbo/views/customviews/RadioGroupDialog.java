package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tencent.qcloud.suixinbo.R;

import java.util.ArrayList;

/**
 * 通过单选对话框
 */
public class RadioGroupDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    private int mSelected = 0;
    private RadioGroup rgMain;
    private ArrayList<RadioButton> rbList;
    private onItemClickListener itemListener;

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public RadioGroupDialog(Context context, String[] menus) {
        super(context, R.style.common_dlg);
        rgMain = new RadioGroup(context);
        setContentView(rgMain);

        rgMain.setOrientation(LinearLayout.VERTICAL);
        rgMain.setOnCheckedChangeListener(this);
        initMenus(menus);
        //setSelected(mSelected);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int btnId) {
        if (null != itemListener){
            for (int i=0; i<rbList.size(); i++){
                if (rbList.get(i).getId() == btnId){
                    itemListener.onItemClick(i);
                    break;
                }
            }
        }
        dismiss();
    }

    public void setSelected(int selected){
        rgMain.clearCheck();
        if (selected < rbList.size())
            rbList.get(selected).setChecked(true);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        itemListener = listener;
    }

    private void initMenus(String[] menus){
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 10, 5, 10);
        rbList = new ArrayList<>();
        for (String menu : menus){
            RadioButton rbMenu = new RadioButton(getContext());
            rbMenu.setText(menu);
            rbList.add(rbMenu);
            rgMain.addView(rbMenu, layoutParams);
        }
    }
}
