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
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
	/**
	 * 登陆注册页面
	 * @author jacyayj
	 */
public class LoginReg extends Activity{
		private Button ok = null;
		private ImageView cencel = null;
		private View find = null;
		private EditText phone = null;
		private EditText pwd = null;
		private ImageView clear1 = null;
		private ImageView clear2 = null;
		
		@ResInject(id = R.string.url,type = ResType.String)
		private String URL = null;
		
		private String PHONE = "";
		private String PWD = "";
		private String content = null;
		private String timetemep= null;
		private final String METHOD = "loginAction_login.shtml";
		
		private List<String> result = null;
		
		private Dialog dialog = null;
		private Window dialogWindow = null;
		/**
		 * 初始化页面
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login_reg);
			ViewUtils.inject(this);
			init();
		}
		/**
		 * 将登陆框dialog中的控件找出
		 */
		private void findview(){
			phone = (EditText) dialogWindow.findViewById(R.id.login_phone);
			pwd = (EditText) dialogWindow.findViewById(R.id.login_pwd);
			ok = (Button) dialogWindow.findViewById(R.id.login_ok);
			cencel = (ImageView) dialogWindow.findViewById(R.id.login_cencel);
			find = dialogWindow.findViewById(R.id.login_find);
			clear1 = (ImageView) dialogWindow.findViewById(R.id.login_clear1);
			clear2 = (ImageView) dialogWindow.findViewById(R.id.login_clear2);
		}
		/**
		 * 登录框上的各种点击事件
		 */
		private void init() {
			login();
			findview();
			clear1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					phone.setText("");
				}
			});
			clear2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pwd.setText("");
				}
			});
			phone.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
						PHONE = s.toString();
					if (!"".equals(pwd.getText())) {
						pwd.setText("");
					}
					if (s.length() == 0) {
						clear1.setVisibility(View.INVISIBLE);
					}else {
						clear1.setVisibility(View.VISIBLE);
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
			pwd.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.length() == 0) {
						clear2.setVisibility(View.INVISIBLE);
					}else {
						clear2.setVisibility(View.VISIBLE);
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
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					timetemep = System.currentTimeMillis()+AllUtils.getRandom();
					dialog.dismiss();
					PWD = pwd.getText().toString();
					if ("".equals(PHONE) && "".equals(PWD)) {
						AllUtils.toast(getBaseContext(), "请输入账号和密码");
					}else {

						if (AllUtils.isPhone(PHONE)) {
							if ("".equals(PWD)) {
								AllUtils.toast(getBaseContext(), "请输入密码");
							}else {
								HttpUtils httpUtils = new HttpUtils(15000);
					            RequestParams params = new RequestParams();
					            params.addBodyParameter("mobilePhone",PHONE);
					            params.addBodyParameter("loginPwd",PWD);
					            params.addBodyParameter("timestamp",timetemep);
								params.addBodyParameter("hashCode",AllUtils.Md5(PWD+PHONE+timetemep+AllUtils.readPrivateKeyFile(LoginReg.this)));
					            httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
									@Override
									public void onStart() {
										AllUtils.startProgressDialog(LoginReg.this, "登录中");
									};
					            	@Override
									public void onFailure(HttpException arg0, String arg1) {
					            		AllUtils.stopProgressDialog();
					            		AllUtils.toast(getBaseContext(), arg1);
									}
									@Override
									public void onSuccess(ResponseInfo<String> arg0) {
										AllUtils.stopProgressDialog();
										result = AllUtils.getJson(arg0.result,new String[]{"resultCode","myToken"});
										content = result.get(0);
										if (content != null) {
											//登陆成功
											if ("0".equals(content) || "00".equals(content)) {
												AllUtils.toast(getBaseContext(),"登陆成功");
												USER.USERPHONE = PHONE;
												USER.USERTOKEN = result.get(1);
												LocalDOM.getinstance().write(getBaseContext(), "user",USER.USERTOKEN, "1");
												LocalDOM.getinstance().write(getBaseContext(), "user", "phone",USER.USERPHONE);
												LocalDOM.getinstance().write(getBaseContext(), "user", "token",USER.USERTOKEN);
												startActivity(new Intent(getBaseContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
												dialog  = null;
												dialogWindow = null;
												finish();
											}
											//登录失败
											else if("1001".equals(content)){
												Toast.makeText(getBaseContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
											}else if ("1002".equals(content)) {
												Toast.makeText(getBaseContext(), "登录密码错误", Toast.LENGTH_SHORT).show();
											}else if ("1004".equals(content)) {
												Toast.makeText(getBaseContext(), "手机号未注册", Toast.LENGTH_SHORT).show();
											}else if ("1005".equals(content)) {
												Toast.makeText(getBaseContext(), "密码错误", Toast.LENGTH_SHORT).show();
											}else if ("1006".equals(content)) {
												Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
											}else {
												Toast.makeText(getBaseContext(), arg0.result, Toast.LENGTH_SHORT).show();
											}
										}
										//请求失败
										else{
											Toast.makeText(getBaseContext(), "请求失败"+content , Toast.LENGTH_SHORT).show();
										}
									}
								} );
							}
						}else {
							AllUtils.toast(getBaseContext(), "请输入正确的手机号");
						}
					}
				}
			});
			cencel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			find.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					startActivity(new Intent(getBaseContext(), Find.class));
					finish();
				}
			});
		}
		@OnClick({R.id.loginreg_back,R.id.reg,R.id.login})
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginreg_back : finish();
				break;
			case R.id.reg : startActivity(new Intent(getBaseContext(), Reg2.class));
				break;
			case R.id.login : dialog.show();;
				break;
			default:
				break;
			}
		}
		/**
		 *创建一个登录框dialog
		 */
	    @SuppressWarnings("deprecation")
		private void login() {
	    	if (dialog == null) {
	    		dialog = new Dialog(LoginReg.this,R.style.mydialog2);
				dialog.setCancelable(false);
				dialog.setContentView(R.layout.login_dialog);
		        dialogWindow = dialog.getWindow();
		        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		        dialogWindow.setGravity(Gravity.CENTER);
		        lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // 宽度
		        lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.42); // 高度
		        dialogWindow.setAttributes(lp);
			}else {
				dialog.show();
			}
		}
}
