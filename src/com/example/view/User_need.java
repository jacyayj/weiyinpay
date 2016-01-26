package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
	
/**
 * 用户须知页面
 * @author jacyayj
 *
 */
public class User_need  extends Activity{
	@ViewInject(R.id.user_lin21)
	private LinearLayout li21 = null;
	@ViewInject(R.id.user_lin51)
	private LinearLayout li51 = null;
	@ViewInject(R.id.user_lin61)
	private LinearLayout li61 = null;
	private boolean isopen2 = false;
	private boolean isopen5 = false;
	private boolean isopen6 = false;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_need);
		ViewUtils.inject(this);
	}
	/**
	 * 页面点击事件
	 * @param v 触发点击事件的控件
	 */
	@OnClick({R.id.user_lin2,R.id.user_lin5,R.id.user_lin6,R.id.user_back})
	private void  onclick(View v){
		switch (v.getId()) {
		case R.id.user_back :finish();
			break;
		case R.id.user_lin2:
			if (isopen2) {
				li21.setVisibility(View.GONE);
				isopen2 = false;
			}else {
				isopen2 = true;
				li21.setVisibility(View.VISIBLE);
				li51.setVisibility(View.GONE);
				li61.setVisibility(View.GONE);
			}
			break;
		case R.id.user_lin5:
			if (isopen5) {
				li51.setVisibility(View.GONE);
				isopen5 = false;
			}else {
				isopen5 =true;
				li51.setVisibility(View.VISIBLE);
				li21.setVisibility(View.GONE);
				li61.setVisibility(View.GONE);
			}
			break;
		case R.id.user_lin6:
			if (isopen6) {
				li61.setVisibility(View.GONE);
				isopen6 = false;
			}else {
				isopen6 = true;
				li61.setVisibility(View.VISIBLE);
				li51.setVisibility(View.GONE);
				li21.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
  } 
}
