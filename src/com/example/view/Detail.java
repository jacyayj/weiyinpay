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
	 * ��������ҳ�棬���û�չʾ�������׵���������
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
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		ViewUtils.inject(this);
		setList();
	}
	//�ն˱��    terminalno   
	//������      orderno 
	// ����       cardno 
	//����ʱ��    datetime 
	//����״̬    transstat   
	//���׽��    amount   
	//��������    transtype
	//�������     cardtype  
	//������Ŀ      transitem 
	//���ݿ������    fid  
	//�̻����    mfrchantno
	/**
	 * ��ʼ��������Ϣ�б�
	 */
	private void setList() {
		list = new ArrayList<Map<String,Object>>();
		contents = getIntent().getStringArrayListExtra("pram");
		temp = getIntent().getStringExtra("temp");
		if ("pay".equals(temp)) {
			lables = new String[]{"������","��������","�տ��˺�","����ʱ��","���","�����˻�","֧����ʽ","����"};
			look.setText("�鿴����СƱ");
		}else if ("cash".equals(temp)) {
			lables = new String[]{"��������","����˺�","���ת�뿨��","���","����ʱ��","֧����ʽ"};
			look.setText("�鿴�ʽ�̬");
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
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
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
