package com.example.view;

import com.example.cz.R;
import com.example.untils.AllUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
	
/**
 * 找回密码页面二
 * @author jacyayj
 *
 */
public class Find2 extends Activity {
	@ViewInject(R.id.find2_pwd)
	private EditText pwd = null;
	@ViewInject(R.id.find2_pwd2)
	private EditText pwd2 = null;
	@ViewInject(R.id.findclear_2)
	private ImageView clear1 = null;
	@ViewInject(R.id.findclear_3)
	private ImageView clear2 = null;
	@ViewInject(R.id.find_strength)
	private ImageView change_s = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private String Phone = "";
	private String Pwd = "";
	private String Pwd2 = "";
	private String concent = null;
	private String timetemp = null;
	
	private int cnt = 0;
	private int cnt1 = 0;
	private int cnt2 = 0;
	private int cnt3 = 0;
	
	private final String METHOD = "pwdServerAction_savePwd.shtml";
	/**
	 * 页面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_layout2);
		ViewUtils.inject(this);
		clickEvent();
	}
	/**
	 * 页面点击事件
	 * @param v 触发点击事件的的控件
	 */
	@OnClick({R.id.find2_ok,R.id.find2_back,R.id.findclear_2,R.id.findclear_3})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.find2_ok : send();
			break;
		case R.id.find2_back : finish();	
			break;
		case R.id.findclear_2 : pwd.setText("");
			break;
		case R.id.findclear_3 : pwd2.setText("");
			break; 
		default:
			break;
		}
	}
	/**
	 * 根据输入框的内容判断清除按钮是否显示和密码强度
	 */
	private void clickEvent() {
		pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>=6 && s!="") {
					clear1.setVisibility(View.VISIBLE);
					for (int i = 0; i < s.length(); i++) {
						char temp = s.charAt(i);
						if (temp >= '0' && temp <='9') {
							cnt1 = 1;
							break;
						}else {
							cnt1 = 0;
						}
					}
					for (int i = 0; i < s.length(); i++) {
						char temp = s.charAt(i);
						if (temp >= 33 && temp <= 47 || temp >= 58 && temp <= 64 || temp >= 123 && temp <= 126 || temp >= 91 && temp <= 96) {
							cnt2 = 1;
							break;
						}else {
							cnt2 = 0;
						}
					}
					for (int i = 0; i < s.length(); i++) {
						char temp = s.charAt(i);
						if (temp >= 65 && temp <= 90 || temp >= 97 && temp <= 122) {
							cnt3 = 1;
							break;
						}else {
							cnt3 = 0;
						}
					}
					cnt = cnt1+cnt2+cnt3;
					if (cnt == 1) {
						change_s.setImageResource(R.drawable.ruo);
					}if (cnt == 2) {
						change_s.setImageResource(R.drawable.zhong);
					}if (cnt == 3) {
						change_s.setImageResource(R.drawable.qiang);
					}
				}else {
					clear1.setVisibility(View.INVISIBLE);
					change_s.setImageResource(R.drawable.wu);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		pwd2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0) {
					clear2.setVisibility(View.VISIBLE);
				}else {
					clear2.setVisibility(View.INVISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
	/**
	 * 向服务器发送用户输入的信息，并根据服务器的返回给予用户提示
	 */
	private void send() {
		Pwd = pwd.getText().toString();
		Pwd2 = pwd2.getText().toString();
		Phone = getIntent().getStringExtra("findphone");
		if (Pwd.equals(Pwd2)) {
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();
				HttpUtils httpUtils = new HttpUtils(15000);
	            RequestParams params = new RequestParams();
	            params.addBodyParameter("loginPwd",Pwd);
	            params.addBodyParameter("mobilePhone",Phone);
	            params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("hashCode",AllUtils.Md5(Pwd+Phone+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
	            httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(Find2.this, "请求中");
					};
	            	@Override
					public void onFailure(HttpException arg0, String arg1) {
	            		AllUtils.stopProgressDialog();
	            		AllUtils.toast(getBaseContext(), arg1);
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						System.out.println(arg0.result);
						try {
							concent = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
						} catch (Exception e) {
							AllUtils.toast(getBaseContext(), arg0.result);
						}
						if ("1000".equals(concent)) {
							AlertDialog.Builder dialog = new AlertDialog.Builder(Find2.this);
							dialog.setTitle("提示");
							dialog.setMessage("密码找回成功！请尝试重新登陆");
							dialog.setPositiveButton("确定", new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									startActivity(new Intent().setClass(Find2.this, LoginReg.class));
									finish();
								}
							});
							dialog.create().show();
						}else if ("1001".equals(concent)) {
							AllUtils.toast(getBaseContext(),"电弧号码不能为空");
						}else if ("1002".equals(concent)) {
							AllUtils.toast(getBaseContext(),"新密码不能为空");
						}else if ("1003".equals(concent)) {
							AllUtils.toast(getBaseContext(),"客服端出错");
						}else if ("1005".equals(concent)) {
							AllUtils.toast(getBaseContext(),"登陆状态出错请重新登录");
						}else if ("1006".equals(concent)) {
							AllUtils.toast(getBaseContext(),"修改失败");
						}else {
							Toast.makeText(Find2.this, arg0.result, Toast.LENGTH_SHORT).show();
						}
					}
				} );
		}else {
			Toast.makeText(Find2.this, "密码设置错误", Toast.LENGTH_SHORT).show();
		}
	}
}