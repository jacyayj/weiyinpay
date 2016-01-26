package com.example.view;


import com.example.cz.R;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
	/**
	 * ���ÿ���֤״̬ҳ��
	 * @author jacyayj
	 *
	 */
public class UpGrade extends Activity{
	
	@ViewInject(R.id.upgrade_state) 
	private TextView state;
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgrade);
		ViewUtils.inject(this);
		if ("1".equals(getIntent().getStringExtra("grade"))) {
			state.setText("����֤");
		}
	}
	/**
	 * ҳ�����¼�
	 * @param arg0	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.upfrade_attest ,R.id.upgrade_back,R.id.upgrade_deal})
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.upfrade_attest : startActivity(new Intent(UpGrade.this,VisaAttest.class));
			break;
		case R.id.upgrade_back : finish();
		break;
		case R.id.upgrade_deal : 
			LocalDOM.getinstance().write(getBaseContext(), "user","from","upgrade");
			startActivity(new Intent(getBaseContext(), Deal.class));
		break;
		default:
			break;
		}
	}
}
