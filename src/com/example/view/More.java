package com.example.view;

import com.example.cz.R;
import com.example.untils.AllUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
	/**
	 * ����ҳ��
	 * @author jacyayj
	 *
	 */
public class More extends Activity {
	/**
	 *��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_page);
		ViewUtils.inject(this);
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.more_back,R.id.more_page_li1,R.id.more_page_li2,R.id.more_page_li3,R.id.more_page_li4,R.id.more_page_li5,R.id.more_page_li6})
	private void onClick(View v){
		switch (v.getId()) {
		case R.id.more_back : finish();
			break;
		case R.id.more_page_li1 : startActivity(new Intent(getBaseContext(),User_need.class));
		break;
		case R.id.more_page_li2 : startActivity(new Intent(getBaseContext(),About_us.class));
		break;
		case R.id.more_page_li3 : 
			AlertDialog.Builder builder=new AlertDialog.Builder(More.this);	
			builder.setTitle("����ͷ�����");
			builder.setMessage("86659129");
			builder.setNegativeButton("ȷ��", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				 Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+"86659129".trim()));
		         startActivity(intent);
				}
	          });
			builder.setPositiveButton("ȡ��", new  AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();
		break;
		case R.id.more_page_li4 : startActivity(new Intent(getBaseContext(),Applayagent.class));
		break;
		case R.id.more_page_li5 : AllUtils.shard(More.this);
		break;
		case R.id.more_page_li6 : startActivity(new Intent(getBaseContext(),Version.class));
		break;
		default:
			break;
		}
	}
}
