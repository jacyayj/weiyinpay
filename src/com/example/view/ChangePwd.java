package com.example.view;

import com.example.cz.R;
import com.example.modle.USER;
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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	 * �޸�����ҳ��
	 * @author jacyayj
	 *
	 */
public class ChangePwd extends Activity {
	@ViewInject(R.id.old_pwd)
	private EditText old_pwd = null;
	@ViewInject(R.id.new_pwd)
	private EditText new_pwd = null;
	@ViewInject(R.id.new_pwd2)
	private EditText new_pwd2 = null;
	@ViewInject(R.id.clear_1)
	private ImageView clear_1 = null;
	@ViewInject(R.id.clear_2)
	private ImageView clear_2 = null;
	@ViewInject(R.id.clear_3)
	private ImageView clear_3 = null;
	@ViewInject(R.id.change_strength)
	private ImageView change_s = null;
	@ViewInject(R.id.changed_codebtn)
	private Button get_code = null;
	@ViewInject(R.id.changed_code)
	private EditText code = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private int cnt = 0;
	private int cnt1 = 0;
	private int cnt2 = 0;
	private int cnt3 = 0;
	private String pwd = "";
	private String pwd1 = "";
	private String pwd2 = "";
	private String CODE = "y";
	private int ct = 60;

	private final String METHOD = "pwdServerAction_changePwd.shtml";
	private final String METHOD1 = "mobileRegister_getVali.shtml";
	private String concent = null;
	private String timetemp = null;
	@SuppressLint("HandlerLeak")
	MyHandler ha=new MyHandler(ChangePwd.this){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==00){
				get_code.setText("���»�ȡ("+ct+"s)");
				ha.sendEmptyMessage(01);
				get_code.setEnabled(false);
			}
			if(msg.what==01){
				ct--;
				get_code.setText("���»�ȡ("+ct+"s)");
				if(ct>-1){
					ha.sendEmptyMessageDelayed(01, 1000);
				}
				if(ct==-1){
					ct=60;
					get_code.setText("��ȡ��֤��");
					get_code.setEnabled(true);
				}
			}
		};
	};
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepwd);
		ViewUtils.inject(this);
		clickEvent();
	}
	/**
	 * ҳ�����¼�
	 * @param v �����¼��Ŀؼ�
	 */
	@OnClick({R.id.changepwd_back,R.id.changed_ok,R.id.changed_codebtn,R.id.clear_1,R.id.clear_2,R.id.clear_3})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.changepwd_back : finish();
			break;
		case R.id.changed_ok : send();
			break;
		case R.id.changed_codebtn : getCode();
			break;
		case R.id.clear_1 : old_pwd.setText("");
			break;
		case R.id.clear_2 : new_pwd.setText("");
			break;
		case R.id.clear_3 : new_pwd2.setText("");
			break;
		default:
			break;
		}
	}
	/**
	 * �ж���������ǿ��
	 */
	private void clickEvent() {
		old_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!s.toString().equals("")) {
					pwd = s.toString();
					clear_1.setVisibility(View.VISIBLE);
				}else {
					clear_1.setVisibility(View.INVISIBLE);
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
		new_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				pwd1 = s.toString();
				if (!s.toString().equals("") && s.length()>=6) {
					pwd1 = s.toString();
					clear_2.setVisibility(View.VISIBLE);
					for (int i = 0; i < pwd1.length(); i++) {
						char temp = pwd1.charAt(i);
						if (temp >= '0' && temp <='9') {
							cnt1 = 1;
							break;
						}else {
							cnt1 = 0;
						}
					}
					for (int i = 0; i < pwd1.length(); i++) {
						char temp = pwd1.charAt(i);
						if (temp >= 33 && temp <= 47 || temp >= 58 && temp <= 64 || temp >= 123 && temp <= 126 || temp >= 91 && temp <= 96) {
							cnt2 = 1;
							break;
						}else {
							cnt2 = 0;
						}
					}
					for (int i = 0; i < pwd1.length(); i++) {
						char temp = pwd1.charAt(i);
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
					clear_2.setVisibility(View.INVISIBLE);
					change_s.setImageResource(R.drawable.wu);
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
		new_pwd2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!s.toString().equals("")) {
					pwd2 = s.toString();
					clear_3.setVisibility(View.VISIBLE);
				}else {
					clear_3.setVisibility(View.INVISIBLE);
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
	 * �����޸�����ָ�������������������Ϣչʾ���û�
	 */
	private void send() {
		if (pwd1!="" && pwd2!="" && pwd1.length()>=6) {
			if (pwd1.equals(pwd2)) {
				if (code.getText().toString().equals(CODE)) {
						timetemp = System.currentTimeMillis()+AllUtils.getRandom();
						HttpUtils httpUtils = new HttpUtils(15000);
			            RequestParams params = new RequestParams();
			            params.addBodyParameter("myToken", USER.USERTOKEN);
			            params.addBodyParameter("loginPwd",pwd);
			            params.addBodyParameter("mobilePhone",USER.USERPHONE);
			            params.addBodyParameter("loginPwd1",pwd2);
			            params.addBodyParameter("timestamp",timetemp);
						params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+pwd+pwd2+USER.USERPHONE+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
			            httpUtils.send(HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
							@Override
							public void onStart() {
								AllUtils.startProgressDialog(ChangePwd.this, "������");
							};
			            	@Override
							public void onFailure(HttpException arg0, String arg1) {
			            		AllUtils.stopProgressDialog();
			            		AllUtils.toast(getBaseContext(), arg1);
							}
							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								AllUtils.stopProgressDialog();
								concent = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
								System.out.println(arg0.result);
								if ("1000".equals(concent)) {
									LocalDOM.getinstance().clear(getBaseContext(), "user");
									AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePwd.this);
									dialog.setTitle("��ʾ");
									dialog.setMessage("�޸ĳɹ��������µ�½");
									dialog.setPositiveButton("ȷ��", new AlertDialog.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
										LocalDOM.getinstance().clear(getBaseContext(), "user");
										startActivity(new Intent().setClass(ChangePwd.this, LoginReg.class));
										finish();
										}
									});
									dialog.create().show();
									}else if ("1001".equals(concent)) {
										AllUtils.toast(getBaseContext(), "�绰���벻��Ϊ��");
									}else if("1002".equals(concent)){
										AllUtils.toast(getBaseContext(), "ԭ���벻��Ϊ��");
									}else if("1003".equals(concent)){
										AllUtils.toast(getBaseContext(), "�����벻��Ϊ��");
									}else if("1004".equals(concent)){
										AllUtils.toast(getBaseContext(), "��������ȷ");
									}else if("1005".equals(concent)){
										AllUtils.toast(getBaseContext(), "��¼״̬���������µ�½");
									}else if("1006".equals(concent)){
										AllUtils.toast(getBaseContext(), "ԭ�������");
									}else if("1007".equals(concent)){
										AllUtils.toast(getBaseContext(), "�޸�ʧ��");
									}else {
										AllUtils.toast(getBaseContext(), arg0.result);
									}
							}
						} );
				}else {
					Toast.makeText(getBaseContext(), "��֤�����", Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(getBaseContext(),"�����������벻��ͬ����˶Ժ����", Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(getBaseContext(), "��������ȷ������", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * ��ȡ��֤��
	 */
	private void getCode() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		HttpUtils httpUtils = new HttpUtils(15000);
        RequestParams params = new RequestParams();
        params.addBodyParameter("mobilePhone",USER.USERPHONE);
        params.addBodyParameter("timestamp",timetemp);
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERPHONE+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
        httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD1,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(ChangePwd.this, "�ϴ���");
			};
        	@Override
			public void onFailure(HttpException arg0, String arg1) {
        		AllUtils.stopProgressDialog();
        		AllUtils.toast(getBaseContext(), arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				Toast.makeText(ChangePwd.this, "��֤���ѷ��͵������ֻ���", Toast.LENGTH_SHORT).show();
				ha.sendEmptyMessage(00);
				CODE = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
			}
		} );
	}
}
