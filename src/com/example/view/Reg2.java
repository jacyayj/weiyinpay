package com.example.view;

import com.example.cz.R;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.example.untils.MyHandler;
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
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 注册页面
 * @author jacyayj
 *	注册第二部发送接收验证码
 */
public class Reg2 extends Activity {
	@ViewInject(R.id.reg_phone)
	private EditText reg_phone = null;
	@ViewInject(R.id.reg_code)
	private EditText reg_code = null;
	@ViewInject(R.id.reg_pwd)
	private EditText reg_pwd = null;
	@ViewInject(R.id.reg_pwd2)
	private EditText reg_pwd2 = null;
	@ViewInject(R.id.reg_getcode)
	private Button get_code = null;
	@ViewInject(R.id.regclear_1)
	private ImageView clear1 = null;
	@ViewInject(R.id.regclear_2)
	private ImageView clear2 = null;
	@ViewInject(R.id.regclear_3)
	private ImageView clear3 = null;
	@ViewInject(R.id.reg_strength)
	private ImageView change_s = null;
	@ViewInject(R.id.reg_box)
	private CheckBox box = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private final String METHOD = "mobileRegister_getVali.shtml";
	private final String METHOD2 = "mobileRegister_register.shtml";
	
	private int cnt = 0;
	private int cnt1 = 0;
	private int cnt2 = 0;
	private int cnt3 = 0;
	private int ct=60;
	
	private String code = "x";
	private String PHONE = "";
	private String pwd = "";
	private String pwd2 = "";
	private String content = null;
	private String CODE = "y";
	private String timetemp = null;
	
	private Intent intent = null;
	
	//设置验证码获取时间
	MyHandler handler=new MyHandler(Reg2.this){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==00){
				get_code.setText("重新获取("+ct+"s)");
				handler.sendEmptyMessage(01);
				get_code.setEnabled(false);
			}
			if(msg.what==01){
				ct--;
				get_code.setText("重新获取("+ct+"s)");
				if(ct>-1){
					handler.sendEmptyMessageDelayed(01, 1000);
				}
				if(ct==-1){
					ct=60;
					get_code.setText("获取验证码");
					get_code.setEnabled(true);
				}
			}
		};
	};
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_2);
		ViewUtils.inject(this);
		clickEvent();
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.reg2_back,R.id.reg2_next,R.id.reg_deal,R.id.regclear_1,R.id.regclear_2,R.id.regclear_3,R.id.reg_getcode})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg2_back : finish();
			break;
		case R.id.reg2_next : reg();
			break;
		case R.id.reg_deal :
			startActivity(new Intent().setClass(Reg2.this, Deal.class));
			LocalDOM.getinstance().write(getBaseContext(), "user", "from", "reg");
			break;
		case R.id.regclear_1 : reg_phone.setText("");
			break;
		case R.id.regclear_2 : reg_pwd.setText("");
			break;
		case R.id.regclear_3 : reg_pwd2.setText("");
			break;
		case R.id.reg_getcode : getCode();
			break;
		default:
			break;
		}
	}
	/**
	 *根据输入框内容进行强度判断和清除按钮的显示
	 */
	private void clickEvent() {
		//设置下一步监听器
		reg_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().equals("")) {
					clear1.setVisibility(View.INVISIBLE);
				}else {
					clear1.setVisibility(View.VISIBLE);
				}
				PHONE = s.toString();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		reg_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().equals("")) {
					clear2.setVisibility(View.INVISIBLE);
				}else {
					clear2.setVisibility(View.VISIBLE);
				}
				if (s.length()>=6 && s!="") {
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
					change_s.setImageResource(R.drawable.wu);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		reg_pwd2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().equals("")) {
					clear3.setVisibility(View.INVISIBLE);
				}else {
					clear3.setVisibility(View.VISIBLE);
				}				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	/**
	 * 获取验证码
	 */
	private void getCode(){
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		if (AllUtils.isPhone(PHONE)){
			HttpUtils httpUtils = new HttpUtils(15000);
            RequestParams params = new RequestParams();
            params.addBodyParameter("mobilePhone",PHONE);
            params.addBodyParameter("timestamp",timetemp);
			params.addBodyParameter("hashCode",AllUtils.Md5(PHONE+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
            httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
				@Override
				public void onStart() {
					AllUtils.startProgressDialog(Reg2.this, "请求中");
				};
            	@Override
				public void onFailure(HttpException arg0, String arg1) {
            		AllUtils.stopProgressDialog();
            		AllUtils.toast(getBaseContext(), arg1);
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					AllUtils.stopProgressDialog();
					handler.sendEmptyMessage(00);
					CODE = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
					Toast.makeText(Reg2.this,CODE, Toast.LENGTH_SHORT).show();
				}
			} );
		}else {
			AllUtils.toast(getBaseContext(), "请输入正确的手机号");
		}
	}
	/**
	 * 向服务器发送用户信息，并根据服务器返回数据给予用户提示
	 */
	private void reg() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		code = reg_code.getText().toString();
		//验证码输入正确
		if (code.equals(CODE)) {
			pwd = reg_pwd.getText().toString();
			pwd2 = reg_pwd2.getText().toString();
			//密码设置正确
			if (pwd.length()>=6 && pwd2.length()>=6) {
				intent = new Intent();
				intent.putExtra("phone2", PHONE);
				intent.putExtra("pwd", pwd2);
				intent.setClass(Reg2.this, RegDone.class);
				//两次密码相同
				if (pwd.equals(pwd2)) {
					if (box.isChecked()) {
						HttpUtils httpUtils = new HttpUtils(15000);
			            RequestParams params = new RequestParams();
			            params.addBodyParameter("mobilePhone",PHONE);
			            params.addBodyParameter("loginPwd",pwd);
			            params.addBodyParameter("timestamp",timetemp);
			            params.addBodyParameter("vali",CODE);
						params.addBodyParameter("hashCode",AllUtils.Md5(pwd+PHONE+CODE+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
			            httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD2,params,new RequestCallBack<String>() {
							@Override
							public void onStart() {
								AllUtils.startProgressDialog(Reg2.this, "上传中");
							};
			            	@Override
							public void onFailure(HttpException arg0, String arg1) {
			            		AllUtils.stopProgressDialog();
							}
							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								AllUtils.stopProgressDialog();
								System.out.println(arg0.result);
								content = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
								if (content!=null) {
									//注册失败
									if ("-1".equals(content)) {
										Toast.makeText(Reg2.this, "注册失败！账户已存在！", Toast.LENGTH_SHORT).show();
									}
									//注册成功
									else if("2".equals(content)){
										startActivity(intent);
									}else if ("0".equals(content)) {
										Toast.makeText(Reg2.this, "请输入正确的手机号和密码", Toast.LENGTH_SHORT).show();
									}else {
										Toast.makeText(Reg2.this, content, Toast.LENGTH_SHORT).show();
									}
								}
							}
						} );
					//请求成功（返回不为空）
					}else {
						AllUtils.toast(Reg2.this, "请仔细阅读协议后继续。");
					}
				}
				//两次密码不同
				else {
						Toast.makeText(Reg2.this, "两次输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
				}
			}
			//密码输入错误
			else {
				Toast.makeText(Reg2.this, "密码长度不少于6位", Toast.LENGTH_SHORT).show();
			}
		}
		//验证码输入错误
		else {
			Toast.makeText(Reg2.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
		}
	}
}
