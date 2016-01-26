package com.example.modle;
 
 
import com.example.cz.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
 
public class myProgrssDialog extends Dialog {
    @SuppressWarnings("unused")
	private Context context;
    private static myProgrssDialog customProgressDialog = null;
    public myProgrssDialog(Context context){
        super(context);
        this.context = context;
    }
    public myProgrssDialog(Context context, int theme) {
        super(context, theme);
    }
    public static myProgrssDialog createDialog(Context context){
        customProgressDialog = new myProgrssDialog(context,R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.loading);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        customProgressDialog.setCancelable(false);
        return customProgressDialog;
    }
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
        if (customProgressDialog == null){
            return;
        }
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
    /**
     * [Summary]
     * setTitile 标题
     * @param strTitle
     * @return
     */
    public myProgrssDialog setTitile(String strTitle){
        return customProgressDialog;
    }
    /**
     * [Summary]
     * setMessage 提示内容
     * @param strMessage
     * @return
     */
    public myProgrssDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }
        return customProgressDialog;
    }
}
