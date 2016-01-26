package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
	/**
	 * 消息页面二级页面
	 * @author jacyayj
	 */
public class News2 extends Activity {
	@ViewInject(R.id.news2_title)
	private TextView title = null;
	@ViewInject(R.id.news2_time)
	private TextView time = null;
	@ViewInject(R.id.news2_content)
	private TextView content = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news2);
		ViewUtils.inject(this);
		title.setText(getIntent().getStringExtra("title"));
		time.setText(getIntent().getStringExtra("time"));
		content.setText(getIntent().getStringExtra("content"));
	}
	/**
	 * 页面点击事件
	 * @param v 触发事件的控件
	 */
	@OnClick({R.id.news2_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.news2_back : finish();
			break;
		default:
			break;
		}
	}
}
