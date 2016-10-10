package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;


/**
 * 提示对话框
 */
public class NotifyDialog extends DialogFragment {

    String tag = "notifyDialog";
    private String title;
    DialogInterface.OnClickListener okListener;
    DialogInterface.OnClickListener cancelListener;

    public void show(String title, FragmentManager fm, DialogInterface.OnClickListener listener1, DialogInterface.OnClickListener listener2){
        this.title = title;
        okListener = listener1;
        cancelListener = listener2;
        setCancelable(false);
        try{
            show(fm, tag);
        }catch (Exception e){
            Log.e(tag, "show notify dialog error, activity has been destroyed");
        }

    }

    public void show(String title, FragmentManager fm, DialogInterface.OnClickListener listener1){
        this.title = title;
        okListener = listener1;
        setCancelable(false);
        try{
            show(fm, tag);
        }catch (Exception e){
            Log.e(tag, "show notify dialog error, activity has been destroyed");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(title)
                .setPositiveButton("ok", okListener)
                .setNegativeButton("cancel", cancelListener == null ? new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                } : cancelListener);
        return builder.create();
    }
}
