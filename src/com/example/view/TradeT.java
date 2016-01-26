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
	 * 提款订单详情页面
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
	 * 初始化页面
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
	 * 页面点击事件
	 * @param v	触发点击事件的控件
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
	 * 显示订单详情
	 */
	@SuppressLint("SimpleDateFormat")
	private void loadSate() {
		number.setText("手机号普通提款");
		draw.setText(USER.USERPHONE);
		date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		money.setText("￥"+modle.getPrice()+".00");
		id.setText(modle.getId());
		type.setText("账号支付");
	}
	/**
	 * 监听手机back键进行操作
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
