package com.example.view;
/**
 * 关于我们界面、公司介绍
 * @author jacyayj
 * 
 */
import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
	
public class About_us  extends Activity{
	/**
	 * 初始化界面
	 */
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.aboutus);
	ViewUtils.inject(this);
	}
	/**
	 * 点击事件
	 * @param v 触发事件的控件
	 */
	@OnClick({R.id.aboutusback})
	public void onClick(View v) {
		if (v.getId()==R.id.aboutusback) {
			finish();
		}
	}
}
