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
	 * 付款订单详情页面
	 * @author jacyayj
	 *
	 */
public class PayTrade extends Activity {
	private TextView tvs[] = null;
	private TradeModle modle = null;
	/**
	 * 页面初始化
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
	 * 页面点击事件
	 * @param v 触发点击事件的控件
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
	 * 初始化交易内容
	 */
	private void findView(){
		String data[] = new String[]{modle.getOrdernumber(),modle.getType(),USER.USERPHONE,
				modle.getDate(),modle.getPrice()+"元","10元",modle.getPhone(),"刷卡支付",modle.getId()};
		for (int i = 0; i < tvs.length; i++) {
			tvs[i] = (TextView) findViewById(R.id.payt_1+i);
			tvs[i].setText(data[i]);
		}
	}
	/**
	 * 监听手机的back键
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
