package com.example.view;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
	/**
	 * �������չʾ����
	 * @author jacyayj
	 *
	 */
public class Funt extends Activity {
	@ViewInject(R.id.funt)
	private ImageView funt = null;
	@ViewInject(R.id.funt_tv1)
	private TextView tv1 = null;
	@ViewInject(R.id.funt_tv2)
	private TextView tv2 = null;
	@ViewInject(R.id.funt_tv3)
	private TextView tv3 = null;
	/**
	 * ҳ���ʼ�������ݲ������û�չʾ�������
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.funt);
		ViewUtils.inject(this);
//		System.out.println(getIntent().getStringExtra("state"));
		if ("1".equals(getIntent().getStringExtra("state"))) {
			funt.setBackground(getResources().getDrawable(R.drawable.funt_3));
			tv1.setTextColor(0xff999999);
			tv3.setTextColor(0xff333333);
		}
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.funt_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.funt_back : 
			if ("drawmoney".equals(getIntent().getStringExtra("from"))) {
				onBackPressed();
			}else {
				finish();
			}
			break;

		default:
			break;
		}
	}
	/**
	 * �����ֻ���back�������в���
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent().setClass(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
