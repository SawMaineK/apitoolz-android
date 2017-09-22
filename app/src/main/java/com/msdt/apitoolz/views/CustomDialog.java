package com.msdt.apitoolz.views;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;
import com.msdt.apitoolz.R;

/**
 * Created by SMK on 9/23/2016.
 */
public class CustomDialog extends Dialog {
    private final Context mContext;
    private IconicsImageView dialog_close;
    private TextView dialog_title;
    private TextView dialog_message;
    private Button dialog_yes;
    private OnClickPositiveListener onPositiveListener;
    private OnCloseListener onCloseListener;
    private IconicsImageView dialog_icon;
    public static final int Success = 1;
    public static final int Error = 0;

    public CustomDialog(Context context) {
        super(context);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        init();
    }

    private void init(){
        this.setContentView(R.layout.dialog_custom);
        dialog_close = (IconicsImageView) findViewById(R.id.dialog_close);
        dialog_icon = (IconicsImageView) findViewById(R.id.dialog_icon);
        dialog_title = (TextView) findViewById(R.id.dialog_title);
        dialog_message = (TextView) findViewById(R.id.dialog_message);
        dialog_yes = (Button) findViewById(R.id.dialog_yes);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onCloseListener != null){
                    onCloseListener.onClick();
                }
            }
        });

        dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onPositiveListener != null){
                    onPositiveListener.onClick();
                }
            }
        });

    }

    @Override
    public void setTitle(CharSequence title) {
        dialog_title.setText(title);
        super.setTitle(title);
    }

    public void setMessageType(int type) {
        if(type == Success){
            dialog_icon.setIcon("cmd_check");
        }
        if(type == Error){
            dialog_icon.setIcon("cmd_alert");
        }
    }

    public void setMessage(String message){
        dialog_message.setText(message);
    }

    public void setOnClickPositiveListener(String yes, OnClickPositiveListener listener){
        dialog_yes.setText(yes);
        this.onPositiveListener = listener;
    }
    public interface OnClickPositiveListener{
        void onClick();
    }

    public void setOnCloseListener(OnCloseListener listener){
        this.onCloseListener = listener;
    }

    public interface OnCloseListener{
        void onClick();
    }
}
