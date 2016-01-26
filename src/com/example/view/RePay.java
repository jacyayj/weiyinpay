/**
 * 
 */
package com.example.view;

import java.util.ArrayList;

import com.example.cz.R;
import com.example.modle.Bank;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import Adapter.VisaAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 信用卡还款页面
 * @author jacyayj
 */
public class RePay extends Activity {
	@ViewInject(R.id.visa_attest_listview)
	private ListView listView = null;
	@ViewInject(R.id.visa_drawer_layout)
	private DrawerLayout layout = null;
	@ViewInject(R.id.visa_drawer)
	private View drawer = null;
	@ViewInject(R.id.visa_deal_content)
	private ImageView deal = null;
	
	ArrayList<Bank> data = null;
	/**
	 * 页面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visa_attest);
		ViewUtils.inject(this);
		if ("repay".equals(getIntent().getStringExtra("from"))) {
			drawer.setBackgroundColor(Color.WHITE);
			deal.setImageResource(R.drawable.repay_konw);
		}
		setList();
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.visa_attest_back,R.id.visa_konw})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.visa_attest_back : finish();
			break;
		case R.id.visa_konw : layout.openDrawer(drawer);
			break;
		default:
			break;
		}
	}
	/**
	 * 初始化信用卡列表
	 */
	private void setList() {
		if (data == null) {
			data = new ArrayList<Bank>();
		}else {
			data.clear();
		}
		VisaAdapter adapter = new VisaAdapter(this);
		for(int i =0; i <3; i++) {  
			Bank item = new Bank("银行"+i,"00000000000000000"+i, "周杰伦"+i);
			data.add(item);
	    }
		listView.setAdapter(adapter);
		adapter.refresh(data);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startActivity(new Intent(RePay.this,RePayList.class));
			}
		});
	}
}
