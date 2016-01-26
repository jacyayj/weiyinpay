package com.example.view;

import java.util.List;

import com.example.cz.R;
import com.example.modle.TerminalModle;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
	/**
	 * �ն˹���ҳ��
	 * @author jacyayj
	 *
	 */
public class Terminal extends Activity {
	
	private final String METHOED = "appPosAction_showAll.shtml";
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	@ViewInject(R.id.terminal_item)
	private TextView posno = null;
	
	private String content = null;
	private String timetemp = null;
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terminal);
		ViewUtils.inject(this);
		loadData();
	}
	/**
	 * ҳ�����¼�
	 * @param v ��������¼��Ŀؼ�
	 */
	@OnClick({R.id.terminal_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.terminal_back : finish();
			break;
		default:
			break;
		}
	}
	/**
	 * �ӷ�������ȡ�󶨵ĵ�POS���ն˺�
	 */
	private void loadData() {
			timetemp = System.currentTimeMillis()+AllUtils.getRandom();
			HttpUtils httpUtils = new HttpUtils(15000);
	        RequestParams params = new RequestParams();
	        
	        params.addBodyParameter("myToken",USER.USERTOKEN);
	        params.addBodyParameter("timestamp",timetemp);
	        params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
	        httpUtils.send(HttpMethod.POST, URL+METHOED, params,new RequestCallBack<String>() {
	        	@Override
	        	public void onStart() {
	        		AllUtils.startProgressDialog(Terminal.this, "������");
	        		super.onStart();
	        	}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), arg1);
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
				  AllUtils.stopProgressDialog();
				  System.out.println(arg0.result);
					List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","data"});
					content = temp.get(0);
					if ("1001".equals(content)) {
						Toast.makeText(getBaseContext(),"����ʧ��", Toast.LENGTH_SHORT).show();
					}else if ("1002".equals(content)) {
						Toast.makeText(getBaseContext(), "��½״̬���������µ�¼", Toast.LENGTH_SHORT).show();
					}else if ("1000".equals(content)) {
						content = temp.get(1);
					}
						if(content.length()!=0){
							try {
								Gson gson = new Gson();
							    JsonParser parser = new JsonParser();
							    JsonArray Jarray = parser.parse(content).getAsJsonArray();
							    for(JsonElement obj : Jarray ){
							    	TerminalModle cse = gson.fromJson( obj , TerminalModle.class);
							    	posno.setText(cse.getFsn());
							    }
							} catch (Exception e) {
								AllUtils.toast(getBaseContext(), arg0.result);
							}
					    }
						else {
							Toast.makeText(getBaseContext(), "����δ����κ��ն�", Toast.LENGTH_SHORT).show();
						}
				}
			});
	}
}
