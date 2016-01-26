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
	 * ��Ϣҳ�����ҳ��
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
	 * ��ʼ��ҳ��
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
	 * ҳ�����¼�
	 * @param v �����¼��Ŀؼ�
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
