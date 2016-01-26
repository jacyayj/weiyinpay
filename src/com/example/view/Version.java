
package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
	/**
	 * 版本更新页面
	 * @author jacyayj
	 *
	 */
public class Version  extends Activity{
	/**
	 * 页面初始化
	 */
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.version);
	ViewUtils.inject(this);
	}
/**
 * 页面点击事件
 * @param v 触发点击事件的控件
 */
	@OnClick({R.id.version_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.version_back : finish();
			
			break;

		default:
			break;
		}
	}
}
