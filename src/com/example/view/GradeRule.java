package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
	/**
	 * VIP�ȼ��������û�չʾVIP������������ȹ���
	 * @author jacyayj
	 *
	 */
public class GradeRule extends Activity {
	/**
	 * ҳ���ʼ��
	 */
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO �Զ����ɵķ������
	super.onCreate(savedInstanceState);
	setContentView(R.layout.grade_rule);
	ViewUtils.inject(this);
}
/**
 * ҳ�����¼�
 * @param v	��������¼��Ŀؼ�
 */
@OnClick({R.id.rule_back})
private void onClick(View v) {
	switch (v.getId()) {
	case R.id.rule_back : finish();
		
		break;

	default:
		break;
	}
}
}
