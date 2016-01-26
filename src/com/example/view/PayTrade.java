package com.example.view;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.example.modle.TradeModle;
import com.example.modle.USER;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
	/**
	 * ���������ҳ��
	 * @author jacyayj
	 *
	 */
public class PayTrade extends Activity {
	private TextView tvs[] = null;
	private TradeModle modle = null;
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paytrade);
		ViewUtils.inject(this);
		tvs = new TextView[9];
		modle = TradeModle.getInstance();
		findView();
	}
	/**
	 * ҳ�����¼�
	 * @param v ��������¼��Ŀؼ�
	 */
	@OnClick({R.id.payt_back,R.id.payt_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.payt_ok : startActivity(new Intent(PayTrade.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		case R.id.payt_back : onBackPressed();
		default:
			break;
		}
	}
	/**
	 * ��ʼ����������
	 */
	private void findView(){
		String data[] = new String[]{modle.getOrdernumber(),modle.getType(),USER.USERPHONE,
				modle.getDate(),modle.getPrice()+"Ԫ","10Ԫ",modle.getPhone(),"ˢ��֧��",modle.getId()};
		for (int i = 0; i < tvs.length; i++) {
			tvs[i] = (TextView) findViewById(R.id.payt_1+i);
			tvs[i].setText(data[i]);
		}
	}
	/**
	 * �����ֻ���back��
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
