package com.example.view;
/**
 * �������ǽ��桢��˾����
 * @author jacyayj
 * 
 */
import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
	
public class About_us  extends Activity{
	/**
	 * ��ʼ������
	 */
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.aboutus);
	ViewUtils.inject(this);
	}
	/**
	 * ����¼�
	 * @param v �����¼��Ŀؼ�
	 */
	@OnClick({R.id.aboutusback})
	public void onClick(View v) {
		if (v.getId()==R.id.aboutusback) {
			finish();
		}
	}
}
