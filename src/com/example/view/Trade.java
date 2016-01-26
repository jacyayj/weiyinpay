package com.example.view;

import com.example.cz.R;
import com.example.modle.TradeModle;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
	/**
	 * �����������ҳ��
	 * @author jacyayj
	 *
	 */
public class Trade extends Activity {
	
	@ViewInject(R.id.trade_ordernumber)
	private TextView number = null;
	@ViewInject(R.id.trade_pay)
	private TextView pay = null;
	@ViewInject(R.id.trade_date)
	private TextView date = null;
	@ViewInject(R.id.trade_money)
	private TextView money = null;
	@ViewInject(R.id.trade_account)
	private TextView account = null;
	@ViewInject(R.id.trade_explain)
	private TextView explain = null;
	
	private TradeModle modle = null;
	private final String types[] = new String []{"�����豸","��Ƶ�豸"};
	private AlertDialog.Builder builder = null;
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
		loadSate();
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.trade_back,R.id.trade_ok})
	private void onClick(View v) {
			switch (v.getId()) {
			case R.id.trade_back : finish();
				break;
			case R.id.trade_ok : creatP();
				break;
			default:
				break;
			}
	}
	/**
	 * ѡ���豸����
	 */
	private void creatP() {
				if (builder == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(Trade.this);
					builder.setTitle("��ѡ�������豸����");
					builder.setItems(types, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								startActivity(new Intent().setClass(Trade.this, Bluetooth_payment.class));
								finish(); 
							}if (which == 1) {
								startActivity(new Intent().setClass(Trade.this, Seek.class));
								finish();
							}
							LocalDOM.getinstance().write(getBaseContext(),"user","from","pay");
						}
					}).create().show();
				}else {
					builder.show();
				}
	}
	/**
	 * ����ҳ��
	 */
	private void loadSate() {
		number.setText(modle.getOrdernumber());
		pay.setText(modle.getPhone());
		date.setText(modle.getDate());
		money.setText("��"+modle.getPrice());
		account.setText(AllUtils.hideName(modle.getName()));
		explain.setText(modle.getType());
	}
}
