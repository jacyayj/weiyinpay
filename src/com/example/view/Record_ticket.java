package com.example.view;

import com.example.cz.R;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
	/**
	 * 交易记录查看电子小票页面
	 * @author jacyayj
	 *
	 */
public class Record_ticket extends Activity{
	@ViewInject(R.id.record_ticket)
	private ImageView ticket = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null; 
	@SuppressLint("SdCardPath")
	private final  String path = "/sdcard/weiyin/ticket/";
	/**
	 * 初始化页面
	 */
	@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.record_ticket);
	ViewUtils.inject(this);
	Bitmap bt = BitmapFactory.decodeFile(path + USER.USERPHONE+"head.jpg");
	if (bt!=null) {
		ticket.setImageBitmap(AllUtils.toRoundBitmap(bt));
		 bt.recycle();
	}else {
		BitmapUtils bitmapUtils = new BitmapUtils(Record_ticket.this);
		bitmapUtils.display(ticket, URL+getIntent().getStringExtra("ticket"));
		System.out.println("url:"+getIntent().getStringExtra("ticket"));
	}
}	
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.r_ticket_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.r_ticket_back : finish();
			break;
		default:
			break;
		}
	}
}
