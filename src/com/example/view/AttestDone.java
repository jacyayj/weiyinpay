package com.example.view;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.example.modle.USER;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
	/**
	 * 认证完成页面，向用户展示认证进度
	 * @author jacyayj
	 *
	 */
public class AttestDone extends Activity{
	@ViewInject(R.id.attest_done)
	private ImageView im = null;
	@ViewInject(value = R.id.attest_tv1)
	private TextView tv1 = null;
	@ViewInject(value = R.id.attest_tv2)
	private TextView tv2 = null;
	@ViewInject(value = R.id.attest_tv3)
	private TextView tv3 = null;
	/**
	 * 初始化页面，判断认证进度更新界面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attest_done);
		ViewUtils.inject(this);
		if ("3".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			im.setBackgroundResource(R.drawable.attest_done_3);
			tv1.setTextColor(0xff999999);
			tv3.setTextColor(0xff333333);
		}else if ("2".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			im.setBackgroundResource(R.drawable.attest_done_2);
			tv1.setTextColor(0xff999999);
			tv2.setTextColor(0xff333333);
		}
	}
	/**
	 * 页面点击事件
	 * @param v 触发事件的控件
	 */
	@OnClick({R.id.attest_done_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.attest_done_back : onBackPressed();
			
			break;

		default:
			break;
		}
	}
	/**
	 * 监听手机back键进行操作
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
