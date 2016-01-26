package com.example.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.example.modle.TradeModle;
import com.example.modle.USER;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
	/**
	 * ��������ҳ��
	 * @author jacyayj
	 *
	 */
public class TradeT extends Activity {
	
	@ViewInject(R.id.t_name)
	private TextView number = null;
	@ViewInject(R.id.t_draw)
	private TextView draw = null;
	@ViewInject(R.id.t_spdate)
	private TextView date = null;
	@ViewInject(R.id.t_money)
	private TextView money = null;
	@ViewInject(R.id.t_id)
	private TextView id = null;
	@ViewInject(R.id.t_type)
	private TextView type = null;
	
	private TradeModle modle = null;
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_t);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
		loadSate();
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.t_back,R.id.t_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.t_back : onBackPressed();
			break;
		case R.id.t_ok : startActivity(new Intent(TradeT.this, Funt.class).putExtra("from","drawmoney"));
			break;
		default:
			break;
		}
	}
	/**
	 * ��ʾ��������
	 */
	@SuppressLint("SimpleDateFormat")
	private void loadSate() {
		number.setText("�ֻ�����ͨ���");
		draw.setText(USER.USERPHONE);
		date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		money.setText("��"+modle.getPrice()+".00");
		id.setText(modle.getId());
		type.setText("�˺�֧��");
	}
	/**
	 * �����ֻ�back�����в���
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
