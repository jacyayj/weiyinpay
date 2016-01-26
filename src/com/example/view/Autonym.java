package com.example.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.example.modle.USER;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
	/**
	 * �����ϴ��ɹ�ҳ�棬���û�չʾ���ϴ���ǰ��֤����Ϣ
	 * @author jacyayj
	 *
	 */
public class Autonym extends Activity {
	@ViewInject(R.id.autonym_name)
	private TextView name = null;
	@ViewInject(R.id.autonym_id)
	private TextView id = null;
	@ViewInject(R.id.autonym_phone)
	private TextView phone = null;
	@ViewInject(R.id.autonym_time)
	private TextView time = null;
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autonym);
		ViewUtils.inject(this);
		time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		name.setText(USER.USERNAME);
		id.setText(USER.USERID);
		phone.setText(USER.USERPHONE);
	}
	/**
	 * ҳ�����¼��Ĵ���
	 * @param v �����¼��Ŀؼ�
	 */
	@OnClick({R.id.autonym_ok,R.id.autonym_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.autonym_ok : startActivity(new Intent().setClass(Autonym.this, AttestDone.class));
			break;
		case R.id.autonym_back : onBackPressed();
		default:
			break;
		}
	}
	/**
	 * �����ֻ�back�����в���
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
