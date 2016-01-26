package com.example.view;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
	/**
	 * 提款成功页面，提示用户提款成功
	 * @author jacyayj
	 *
	 */
public class DrawMoneyDone extends Activity{
	/**
	 * 页面初始化
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_money_done);
		ViewUtils.inject(this);
	}
	/**
	 * 页面点击事件
	 * @param v	 触发点击事件的控件
	 */
	@OnClick({R.id.draw_back,R.id.draw_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.draw_ok : startActivity(new Intent().setClass(DrawMoneyDone.this, TradeT.class));
			break;
		case R.id.draw_back : onBackPressed();
			break;
		default:
			break;
		}
	}
	/**
	 * 监听手机的back键并进行操作
	 */
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
