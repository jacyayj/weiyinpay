
package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
	/**
	 * �汾����ҳ��
	 * @author jacyayj
	 *
	 */
public class Version  extends Activity{
	/**
	 * ҳ���ʼ��
	 */
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.version);
	ViewUtils.inject(this);
	}
/**
 * ҳ�����¼�
 * @param v ��������¼��Ŀؼ�
 */
	@OnClick({R.id.version_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.version_back : finish();
			
			break;

		default:
			break;
		}
	}
}
