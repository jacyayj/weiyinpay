package com.example.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
	/**
	 * 交易详情页面，向用户展示本单交易的想想内容
	 * @author jacyayj
	 *
	 */
public class Detail extends Activity {
	@ViewInject(R.id.detail_look)
	private Button look = null;
	@ViewInject(R.id.detail_list)
	private ListView listView = null;
	
	private List<Map<String, Object>> list = null;
	private Map<String, Object> map = null;
	private String lables[] = null;
	private ArrayList<String> contents = null;
	private String temp = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		ViewUtils.inject(this);
		setList();
	}
	//终端编号    terminalno   
	//订单号      orderno 
	// 卡号       cardno 
	//日期时间    datetime 
	//交易状态    transstat   
	//交易金额    amount   
	//交易类型    transtype
	//卡号类别     cardtype  
	//交易项目      transitem 
	//数据库表主键    fid  
	//商户编号    mfrchantno
	/**
	 * 初始化交易信息列表
	 */
	private void setList() {
		list = new ArrayList<Map<String,Object>>();
		contents = getIntent().getStringArrayListExtra("pram");
		temp = getIntent().getStringExtra("temp");
		if ("pay".equals(temp)) {
			lables = new String[]{"订单号","交易名称","收款账号","交易时间","金额","付款账户","支付方式","卡号"};
			look.setText("查看电子小票");
		}else if ("cash".equals(temp)) {
			lables = new String[]{"交易名称","提款账号","提款转入卡号","金额","交易时间","支付方式"};
			look.setText("查看资金动态");
		}
		for (int i = 0; i < lables.length; i++) {
			map = new HashMap<String, Object>();
			map.put("lable", lables[i]);
			map.put("content", contents.get(i));
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), list, R.layout.detail_item,
		new String[]{"lable","content"}, new int[]{R.id.detail_item1,R.id.detail_item2});
		listView.setAdapter(adapter);
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.detail_back,R.id.detail_look})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.detail_back : finish();
			break;
		case R.id.detail_look : 
			Intent intent = new Intent();
			if ("pay".equals(temp)) {
				intent.setClass(Detail.this, Record_ticket.class);
				intent.putExtra("orderno",contents.get(0));
				intent.putExtra("ticket",contents.get(8));
				startActivity(intent);
			}else {
				intent.setClass(Detail.this, Funt.class);
				intent.putExtra("state",contents.get(6));
				intent.putExtra("from","detail");
				System.out.println(contents.get(6));
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
}
