package com.example.view;

import com.example.cz.R;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
	/**
	 * ��һ��ʵ����֤ҳ��
	 * @author jacyayj
	 *
	 */
public class Fist extends Activity {
	
	@ViewInject(R.id.attest_box)
	private CheckBox box = null;
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fist);
		ViewUtils.inject(this);
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.fist_back,R.id.fist_ok,R.id.attest_deal})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.attest_deal :
			startActivity(new Intent().setClass(getBaseContext(), Deal.class));
			LocalDOM.getinstance().write(getBaseContext(), "user", "from", "fist");
			break;
		case R.id.fist_back : finish();
			break;
		case R.id.fist_ok : 
			if (box.isChecked()) {
				startActivity(new Intent().setClass(Fist.this,Sign.class));
				LocalDOM.getinstance().write(getBaseContext(), "user", "from", "deal");
			}else {
				AllUtils.toast(getBaseContext(), "���Ķ�Э�鲢ͬ������");
			}
			break;
		default:
			break;
		}
	}
}
