package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

	/**
	 * 手机充值页面
	 * @author jacyayj
	 *
	 */
public class Telphone extends Activity {
	@ViewInject(R.id.telphone_1)
	private CheckBox box1 = null;
	@ViewInject(R.id.telphone_2)
	private CheckBox box2 = null;
	@ViewInject(R.id.telphone_3)
	private CheckBox box3 = null;
	@ViewInject(R.id.telphone_4)
	private CheckBox box4 = null;
	@ViewInject(R.id.telphone_5)
	private CheckBox box5 = null;
	@ViewInject(R.id.telphone_6)
	private CheckBox box6 = null;
	
	String content = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.telphone);
		ViewUtils.inject(this);
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.telphone_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.telphone_back : finish();
			break;
		default:
			break;
		}
	}
	/**
	 * checkbox的状态监控
	 * @param buttonView 不同金额的checkbox
	 * @param isChecked	选中状态
	 */
	@OnCompoundButtonCheckedChange({R.id.telphone_1,R.id.telphone_2,R.id.telphone_3,R.id.telphone_4,R.id.telphone_5,R.id.telphone_6})
	private void onCheckedChange(CheckBox buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.telphone_1 :
				box1.setBackgroundResource(R.drawable.tephone_btn_b);
				box2.setChecked(false);
				box3.setChecked(false);
				box4.setChecked(false);
				box5.setChecked(false);
				box6.setChecked(false);
				box1.setTextColor(0xffffffff);
				content = "10";
				break;
			case R.id.telphone_2 :
				box2.setBackgroundResource(R.drawable.tephone_btn_b);
				box1.setChecked(false);
				box3.setChecked(false);
				box4.setChecked(false);
				box5.setChecked(false);
				box6.setChecked(false);
				box2.setTextColor(0xffffffff);
				break;
			case R.id.telphone_3 :
				box3.setBackgroundResource(R.drawable.tephone_btn_b);
				box2.setChecked(false);
				box1.setChecked(false);
				box4.setChecked(false);
				box5.setChecked(false);
				box6.setChecked(false);
				box3.setTextColor(0xffffffff);
				break;
			case R.id.telphone_4 :
				box4.setBackgroundResource(R.drawable.tephone_btn_b);
				box2.setChecked(false);
				box3.setChecked(false);
				box1.setChecked(false);
				box5.setChecked(false);
				box6.setChecked(false);
				box4.setTextColor(0xffffffff);
				break;
			case R.id.telphone_5 :
				box5.setBackgroundResource(R.drawable.tephone_btn_b);
				box2.setChecked(false);
				box3.setChecked(false);
				box4.setChecked(false);
				box1.setChecked(false);
				box6.setChecked(false);
				box5.setTextColor(0xffffffff);
				break;
			case R.id.telphone_6 :
				box6.setBackgroundResource(R.drawable.tephone_btn_b);
				box2.setChecked(false);
				box3.setChecked(false);
				box4.setChecked(false);
				box5.setChecked(false);
				box1.setChecked(false);
				box6.setTextColor(0xffffffff);
				break;
			default:
				break;
			}
		}else {
			switch (buttonView.getId()) {
			case R.id.telphone_1 :
				box1.setBackgroundResource(R.drawable.tephone_btn_a);
				box1.setTextColor(0xff333333);
				break;
			case R.id.telphone_2 :
				box2.setBackgroundResource(R.drawable.tephone_btn_a);
				box2.setTextColor(0xff333333);
				break;
			case R.id.telphone_3 :
				box3.setBackgroundResource(R.drawable.tephone_btn_a);
				box3.setTextColor(0xff333333);
				break;
			case R.id.telphone_4 :
				box4.setBackgroundResource(R.drawable.tephone_btn_a);
				box4.setTextColor(0xff333333);
				break;
			case R.id.telphone_5 :
				box5.setBackgroundResource(R.drawable.tephone_btn_a);
				box5.setTextColor(0xff333333);
				break;
			case R.id.telphone_6 :
				box6.setBackgroundResource(R.drawable.tephone_btn_a);
				box6.setTextColor(0xff333333);
				break;
			default:
				break;
			}
		}
	}
}
