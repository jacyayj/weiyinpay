package com.example.view;

import com.example.cz.R;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
	/**
	 * 第一次实名认证页面
	 * @author jacyayj
	 *
	 */
public class Fist extends Activity {
	
	@ViewInject(R.id.attest_box)
	private CheckBox box = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fist);
		ViewUtils.inject(this);
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.fist_back,R.id.fist_ok,R.id.attest_deal})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.attest_deal :
			startActivity(new Intent().setClass(getBaseContext(), Deal.class));
			LocalDOM.getinstance().write(getBaseContext(), "user", "from", "fist");
			break;
		case R.id.fist_back : finish();
			break;
		case R.id.fist_ok : 
			if (box.isChecked()) {
				startActivity(new Intent().setClass(Fist.this,Sign.class));
				LocalDOM.getinstance().write(getBaseContext(), "user", "from", "deal");
			}else {
				AllUtils.toast(getBaseContext(), "请阅读协议并同意后继续");
			}
			break;
		default:
			break;
		}
	}
}
