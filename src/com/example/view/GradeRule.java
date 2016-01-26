package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
	/**
	 * VIP等级规则，向用户展示VIP的升级与提款额度规则
	 * @author jacyayj
	 *
	 */
public class GradeRule extends Activity {
	/**
	 * 页面初始化
	 */
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO 自动生成的方法存根
	super.onCreate(savedInstanceState);
	setContentView(R.layout.grade_rule);
	ViewUtils.inject(this);
}
/**
 * 页面点击事件
 * @param v	触发点击事件的控件
 */
@OnClick({R.id.rule_back})
private void onClick(View v) {
	switch (v.getId()) {
	case R.id.rule_back : finish();
		
		break;

	default:
		break;
	}
}
}
