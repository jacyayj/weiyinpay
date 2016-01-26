package com.example.view;

import com.example.cz.R;
import com.example.modle.TradeModle;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
	/**
	 * ���ÿ���Ϣҳ��
	 * @author jacyayj
	 *
	 */
public class VisaAttest1 extends Activity{
	@ViewInject(R.id.visa_cardno)
	private TextView cardno = null;
	@ViewInject(R.id.visa_carddate)
	private EditText carddate = null;
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visa_attest1);
		ViewUtils.inject(this);
		cardno.setText(TradeModle.getInstance().getId());
		carddate.setText(TradeModle.getInstance().getDate());
	}
	/**
	 * ҳ�����¼�
	 * @param arg0	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.visa_attest1_next,R.id.visa_attest1_back})
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.visa_attest1_next : startActivity(new Intent(getBaseContext(), VisaAttest2.class));
			break;
		case R.id.visa_attest1_back : finish();
			break;
		default:
			break;
		}
	}
}
