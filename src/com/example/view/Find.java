package com.example.view;

import com.example.cz.R;
import com.example.untils.AllUtils;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
	/**
	 * 找回密码界面一，提示用户输入手机号和验证码
	 * @author jacyayj
	 *
	 */
public class Find extends Activity {
	@ViewInject(R.id.find_getcode)
	private Button get_code = null;
	@ViewInject(R.id.find_phone)
	private EditText phone_ed = null;
	@ViewInject(R.id.find_code)
	private EditText code_ed = null;
	@ViewInject(R.id.find_clear1)
	private ImageView clear1 = null;
	@ViewInject(R.id.find_clear2)
	private ImageView clear2 = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private String PHONE;
	private String CODE1 = "y";
	private String CODE2 = "x";
	
	private int ct = 60;
	private String timetemp = null;
	
	private final String METHOD = "mobileRegister_getVali.shtml";
		MyHandler ha=new MyHandler(Find.this){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==00){
				get_code.setText("重新获取("+ct+"s)");
				ha.sendEmptyMessage(01);
				get_code.setEnabled(false);
			}
			if(msg.what==01){
				ct--;
				get_code.setText("重新获取("+ct+"s)");
				if(ct>-1){
					ha.sendEmptyMessageDelayed(01, 1000);
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
	 * 页面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_layout);
		ViewUtils.inject(this);
		clickEvent();
	 };
	 /**
	  * 页面点击事件
	  * @param v 触发点击事件的控件
	  */
	 @OnClick({R.id.find_next,R.id.find_back,R.id.find_getcode,R.id.find_clear1,R.id.find_clear2})
	 private void onClick(View v) {
		 switch (v.getId()) {
		case R.id.find_next : 
			CODE2 = code_ed.getText().toString();
			PHONE = phone_ed.getText().toString();
			if (AllUtils.isPhone(PHONE)) {
				if (CODE1.equals(CODE2)) {
						startActivity(new Intent().putExtra("findphone", PHONE).setClass(Find.this, Find2.class));
						finish();
				}else {
					Toast.makeText(Find.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(Find.this, "请输入手机号", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.find_back : finish();
			break;
		case R.id.find_getcode : getCode();
			break;
		case R.id.find_clear1 : phone_ed.setText("");
			break;
		case R.id.find_clear2 : code_ed.setText("");
			break;
		default:
			break;
		}
	}
	 /**
	  * 判断输入框的内容显示清除按钮；
	  * 
	  */
	private void clickEvent() {
		phone_ed.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0) {
					clear1.setVisibility(View.VISIBLE);
				}else {
					clear1.setVisibility(View.INVISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		code_ed.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0) {
					clear1.setVisibility(View.VISIBLE);
				}else {
					clear1.setVisibility(View.INVISIBLE);
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
	 * 向服务器获取验证码并发送到用户手机
	 */
	private void getCode() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		PHONE = phone_ed.getText().toString();
		if (AllUtils.isPhone(PHONE)) {
				HttpUtils httpUtils = new HttpUtils(15000);
	            RequestParams params = new RequestParams();
	            params.addBodyParameter("mobilePhone",PHONE);
	            params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("hashCode",AllUtils.Md5(PHONE+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
	            httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(Find.this, "提交中");
					};
	            	@Override
					public void onFailure(HttpException arg0, String arg1) {
	            		AllUtils.stopProgressDialog();
	            		AllUtils.toast(getBaseContext(), arg1);
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						CODE1 = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
						ha.sendEmptyMessage(00);
						System.out.println("返回code"+CODE1+"code"+CODE2+"phone"+PHONE);
					}
				} );
		}else {
			Toast.makeText(Find.this,"请输入正确的手机号！", Toast.LENGTH_SHORT).show();
		}
	}
}
