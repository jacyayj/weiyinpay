package com.example.view;

import java.util.List;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

	/**
	 * 注册完成页面
	 * @author jacyayj
	 *
	 */
public class RegDone extends Activity {
	
	private final String METHOD = "loginAction_login.shtml";
	
	@ViewInject(R.id.reg_done_user)
	private TextView user = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private List<String> result = null;
	private String content = null;
	private String timetemp = null;
	private String phone = null;
	private String pwd = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_done);
		ViewUtils.inject(this);
		loadData();
	}
	/**
	 * 页面点击事件
	 * @param v 触发点击事件的控件
	 */
	@OnClick({R.id.reg_done_back,R.id.reg_done_ok,R.id.reg_done_no})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_done_back : onBackPressed();
			break;
		case R.id.reg_done_ok : 
			startActivity(new Intent(RegDone.this, Fist.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			break;
		case R.id.reg_done_no :
			startActivity(new Intent(RegDone.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			break;
		default:
			break;
		}
	}
	/**
	 * 向服务器发送用户注册信息以登陆
	 */
	private void loadData() {
			phone = getIntent().getStringExtra("phone2");
			pwd = getIntent().getStringExtra("pwd");
			user.setText(phone);
			timetemp = System.currentTimeMillis()+AllUtils.getRandom();
			if ("".equals(pwd)) {
				AllUtils.toast(getBaseContext(), "请输入密码");
			}else {
				HttpUtils httpUtils = new HttpUtils(15000);
	            RequestParams params = new RequestParams();
	            params.addBodyParameter("mobilePhone",phone);
	            params.addBodyParameter("loginPwd",pwd);
	            params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("hashCode",AllUtils.Md5(pwd+phone+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
	            httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(RegDone.this, "上传中");
					};
	            	@Override
					public void onFailure(HttpException arg0, String arg1) {
	            		AllUtils.stopProgressDialog();
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						result = AllUtils.getJson(arg0.result,new String[]{"resultCode","myToken"});
						content = result.get(0);
						if (content != null) {
							//登陆成功
							if ("0".equals(content) || "00".equals(content)) {
								AllUtils.toast(getBaseContext(), "注册成功已登录");
								USER.USERPHONE = phone;
								USER.USERTOKEN = result.get(1);
								LocalDOM.getinstance().write(getBaseContext(), "user",USER.USERTOKEN, "1");
								LocalDOM.getinstance().write(getBaseContext(), "user", "phone",USER.USERPHONE);
								LocalDOM.getinstance().write(getBaseContext(), "user", "token",USER.USERTOKEN);
							}
						}
						//请求失败
						else{
							Toast.makeText(getBaseContext(), "请求失败"+content , Toast.LENGTH_SHORT).show();
						}
					}
				} );
			}
	}
	/**
	 * 监听手机back键进行操作
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent(getBaseContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		super.onBackPressed();
	}
}
