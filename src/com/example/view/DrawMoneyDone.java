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
	 * ���ɹ�ҳ�棬��ʾ�û����ɹ�
	 * @author jacyayj
	 *
	 */
public class DrawMoneyDone extends Activity{
	/**
	 * ҳ���ʼ��
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_money_done);
		ViewUtils.inject(this);
	}
	/**
	 * ҳ�����¼�
	 * @param v	 ��������¼��Ŀؼ�
	 */
	@OnClick({R.id.draw_back,R.id.draw_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.draw_ok : startActivity(new Intent().setClass(DrawMoneyDone.this, TradeT.class));
			break;
		case R.id.draw_back : onBackPressed();
			break;
		default:
			break;
		}
	}
	/**
	 * �����ֻ���back�������в���
	 */
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
