package com.example.view;

import com.example.cz.R;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
	/**
	 * �����ÿ�ˢ������
	 * @author jacyayj
	 *
	 */
public class VisaSlot extends Activity {
	@ViewInject(R.id.visa_slot_tv1)
	TextView tv1 = null;
	@ViewInject(R.id.visa_slot_tv2)
	TextView tv2 = null;
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visa_slot);
		ViewUtils.inject(this);
		setTextColor();
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.visa_slot_next,R.id.visa_slot_back})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.visa_slot_next : 
			LocalDOM.getinstance().write(getBaseContext(), "user","from","visa");
			toSlot();
			break;
		case R.id.visa_slot_back : finish();
			break;
		default:
			break;
		}
	}
	/**
	 * ����������ɫ
	 */
	private void setTextColor() {
		String str1 = (String) tv1.getText();
        int start = str1.indexOf("�����˵����ÿ�");
        int end = start+"�����˵����ÿ�".length();
        SpannableStringBuilder style=new SpannableStringBuilder(str1); 
        style.setSpan(new ForegroundColorSpan(Color.rgb(82, 165, 243)),start,end,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
        tv1.setText(style);
		String str2 = (String) tv2.getText();
        int start2 = str2.indexOf("��Ա�ѱ�׼����ա����ÿ���֤������׼��");
        int end2 = start2+"��Ա�ѱ�׼����ա����ÿ���֤������׼��".length();
        SpannableStringBuilder style2=new SpannableStringBuilder(str2); 
        style2.setSpan(new ForegroundColorSpan(Color.rgb(82, 165, 243)),start2,end2,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
        tv2.setText(style2);
	}
	/**
	 * ѡ�����ÿ�ˢ����ʽ����������Ƶ��
	 */
	private void toSlot() {
		final String types[] = new String []{"�����豸","��Ƶ�豸"};
		AlertDialog.Builder builder = new AlertDialog.Builder(VisaSlot.this);
		builder.setTitle("��ѡ�������豸����");
		builder.setItems(types, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					startActivity(new Intent().setClass(VisaSlot.this, Bluetooth_payment.class));
				}if (which == 1) {
					startActivity(new Intent().setClass(VisaSlot.this, Seek.class));
				}
			}
		}).create().show();
	}
}
