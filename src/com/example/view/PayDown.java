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
	 * ����ɹ�ҳ��
	 * @author jacyayj
	 *
	 */
public class PayDown extends Activity {
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_down);
		ViewUtils.inject(this);
	}
	/**
	 * �����ֻ�back�����в���
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
	/**
	 * ҳ�����¼�
	 * @param v ��������¼��Ŀؼ�
	 */
	@OnClick({R.id.paydown_ok,R.id.paydown_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.paydown_back : onBackPressed();
			break;
		case R.id.paydown_ok : startActivity(new Intent().setClass(PayDown.this, PayTrade.class));
			break;
		default:
			break;
		}
	}
}
