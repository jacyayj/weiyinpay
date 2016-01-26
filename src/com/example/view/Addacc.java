package com.example.view;
	/**
	 * 添加银行卡账户基本信息
	 * @author jacyayj
	 */
import java.util.List;

import com.example.cz.R;
import com.example.modle.T;
import com.example.modle.TradeModle;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Addacc extends Activity {
	@ViewInject(R.id.add_name)
	private TextView name_ed = null;
	@ViewInject(R.id.add_number)
	private EditText number_ed = null;
	@ViewInject(R.id.add_clear)
	private ImageView clear = null;
	@ResInject(id=R.string.url,type=ResType.String)
	private String URL = null;
	
	private String name = "";
	private String id = "";
	private String timetemp = null;
	private T t = null ;
	private TradeModle modle = null;
	
	private final String METHOD = "payAction_getUNameByPhone.shtml";
	/**
	 * 初始化页面，页面所需工具
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_account);
		ViewUtils.inject(this);
		t = T.getInstance();
		modle = TradeModle.getInstance();
		//控制清除按钮的可见性
		number_ed.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.length()>0){
						clear.setVisibility(View.VISIBLE);
						}else {
							clear.setVisibility(View.INVISIBLE);
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
		load();
	}
	/**
	 * 页面点击事件
	 * @param v 触发点击事件的按钮
	 */
	@OnClick({R.id.add_back,R.id.add_clear,R.id.slot_card,R.id.add_next})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_back : finish();
			break;
		case R.id.add_clear : number_ed.setText("");
		break;
		case R.id.slot_card : 
			LocalDOM.getinstance().write(getBaseContext(), "user", "from", "addacc");
			toSlot();
		break;
		case R.id.add_next : 
			name = name_ed.getText().toString();
			id = number_ed.getText().toString();
			if (!name.equals("")&& id.length()>=13) {
					t. setName(name);
					t.setCard(id);
					startActivity(new Intent().setClass(Addacc.this, AddCity.class));
			}else{
				Toast.makeText(getBaseContext(), "请输入正确的姓名和卡号", Toast.LENGTH_SHORT).show();	
			}
		break;
		default:
			break;
		}
	}
	/***
	 * 加载当前账户实名信息
	 */
	private void load() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		if ("3".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("myToken", USER.USERTOKEN);
			params.addBodyParameter("mobilePhone", USER.USERPHONE);
			params.addBodyParameter("timestamp", timetemp);
			params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+USER.USERPHONE+timetemp+AllUtils.readPrivateKeyFile(Addacc.this)));
			HttpUtils httpUtils = new HttpUtils(15000);
			httpUtils.send(HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
				@Override
				public void onStart() {
					AllUtils.startProgressDialog(Addacc.this, "加载中");
					super.onStart();
				}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), arg1);
					name_ed.setText("微银用户");
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					AllUtils.stopProgressDialog();
					System.out.println(arg0.result);
					try {
						List<String> result = AllUtils.getJson(arg0.result, new String[]{"resultCode","result"});
						if ("1000".equals(result.get(0))) {
							USER.USERNAME = result.get(1);
							name_ed.setText(USER.USERNAME);
						}else if ("1001".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "电话号码不能为空");
						}else if ("1002".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "该手机号未进行实名认证或在认证中");
						}else if ("1005".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "登陆状态出错请重新登录");
						}else{
							AllUtils.toast(getBaseContext(), arg0.result);
							System.out.println(result.get(0));
						}
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(), arg0.result);
					}
				}
			});
		}else {
			name_ed.setText("微银用户");
		}
	}
	/**
	 * 选择刷卡方式（蓝牙(BPOSM188)。音频(新大陆ME11)）
	 */
	private void toSlot() {
		final String types[] = new String []{"蓝牙设备","音频设备"};
		AlertDialog.Builder builder = new AlertDialog.Builder(Addacc.this);
		builder.setTitle("请选择您的设备类型");
		builder.setItems(types, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					startActivity(new Intent().setClass(Addacc.this, Bluetooth_payment.class));
				}if (which == 1) {
					startActivity(new Intent().setClass(Addacc.this, Seek.class));
				}
			}
		}).create().show();
	}
	/**
	 * 将刷卡获取的卡号显示于页面上
	 */
	@Override
	protected void onRestart() {
		if ("slot".equals(LocalDOM.getinstance().read(getBaseContext(), "user", "from"))) {
			number_ed.setText(modle.getId());
		}
		super.onRestart();
	}
}
