  package com.example.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.cz.R;
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
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
	
	/**
	 * 我要付款界面，想当前账户充值金额
	 * @author jacyayj
	 *
	 */
public class Pay extends Activity {
	@ViewInject(R.id.pay_phone)
	private EditText phone = null;
	@ViewInject(R.id.pay_money)
	private EditText money = null;
	@ViewInject(R.id.pay_name)
	private TextView name = null;
	@ViewInject(R.id.pay_shtype)
	private TextView type = null;
	@ViewInject(R.id.drawer_layout)
	private DrawerLayout drawerLayout = null;
	@ViewInject(R.id.drawer)
	private View drawer = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private String content = null;
	private String user = null;
	private String timetemp = null;
	
	private final String METHOD = "payAction_getUNameByPhone.shtml";
	private final String METHOD2 = "payAction_createOrderform.shtml";
	
	private boolean CANGO = false;
	private TradeModle modle= null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
		loadDate();
		onclickEvent();
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.pay_back,R.id.pay_next,R.id.pay_shtype,R.id.pay_fast,R.id.pay_konw})
	private void onClick(View v){
		switch (v.getId()) {
		case R.id.pay_back : finish();
			break;
		case R.id.pay_next : pay();
			break;
		case R.id.pay_shtype : creatP();
			break;
		case R.id.pay_fast : loadDate();
			break;
		case R.id.pay_konw : drawerLayout.openDrawer(drawer);
			break;
		default:
			break;
		}
	}
	/**
	 * 判断输入的手机号是否为正确的手机号并通过手机号向服务器请求真实姓名
	 */
	private void onclickEvent() {
		phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
						user = s.toString();
				//手机号输入正确
					if(AllUtils.isPhone(user)){
								timetemp = System.currentTimeMillis()+AllUtils.getRandom();
								user = phone.getText().toString();
								RequestParams params = new RequestParams();
								params.addBodyParameter("mobilePhone",user);
								params.addBodyParameter("myToken",USER.USERTOKEN);
								params.addBodyParameter("timestamp",timetemp);
								params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+user+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
								HttpUtils httpUtils = new HttpUtils(15000);
								httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
									@Override
									public void onStart() {
										AllUtils.startProgressDialog(Pay.this, "加载中");
										super.onStart();
									}
									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										AllUtils.stopProgressDialog();
									}
									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										AllUtils.stopProgressDialog();
										System.out.println(arg0.result);
										List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","result"});
										content = temp.get(0);
										if ("1000".equals(content)) {
											content = temp.get(1);
											USER.USERNAME = content;
											name.setText(AllUtils.hideName(content));
											Selection.setSelection(phone.getText(),phone.getText().length());
											CANGO = true;
										}else {
											name.setText("微银用户");
										}
									}});
								CANGO = true;
							}else {
								name.setText("微银用户");
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
	 * 选择商户类型一级dialog
	 */
	private void creatP() {
		final String types[] = new String []{"民生服务","团购商品","日用百货","生活娱乐"};
		AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
		builder.setTitle("请选择商户类型");
		builder.setItems(types, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				type.setText(types[which]+"-");
				if (which == 0) {
					creatW(new String[]{"公共缴费","交通费","期刊订阅"});
				}if (which == 1) {
					creatW(new String[]{"服饰团购","五金建材","文具团购"});
				}if (which == 2) {
					creatW(new String[]{"贷款","代购","服务"});
				}if (which == 3) {
					creatW(new String[]{"餐饮","住宿","娱乐"});
				}
			}
		}).create().show();
	}
	/**
	 * 选择商户类型二级dialog
	 * @param types
	 */
	private void creatW(final String types[]) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
		builder.setTitle("请选择商户名称");
		builder.setItems(types, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				type.append(types[which]);
			}
		}).create().show();
	}
	/**
	 * 通过当前账户手机号获取用户真实姓名
	 */
	private void loadDate() {
		if ("3".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			timetemp = System.currentTimeMillis()+AllUtils.getRandom();
			user = USER.USERPHONE;
			phone.setText(user);
			RequestParams params = new RequestParams();
			params.addBodyParameter("mobilePhone",user);
			params.addBodyParameter("myToken",USER.USERTOKEN);
			params.addBodyParameter("timestamp",timetemp);
			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+user+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
			HttpUtils httpUtils = new HttpUtils(15000);
			httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
				@Override
				public void onStart() {
					AllUtils.startProgressDialog(Pay.this, "加载中");
					super.onStart();
				}
				@Override
				public void onFailure(HttpException arg0,
						String arg1) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), arg1);
				}
				@Override
				public void onSuccess(
						ResponseInfo<String> arg0) {
					AllUtils.stopProgressDialog();
					System.out.println(arg0.result);
					List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","result"});
					content = temp.get(0);
					if ("1000".equals(content)) {
						content = temp.get(1);
						USER.USERNAME = content;
						name.setText(AllUtils.hideName(content));
						Selection.setSelection(phone.getText(),phone.getText().length());
						CANGO = true;
					}else {
						name.setText("微银用户");
					}
				}});
		}
	}
	/**
	 * 发送用户输入信息，并根据服务器反馈给予用户提示
	 */
	private void pay() {
		if (money.getText().toString().equals("")) {
			Toast.makeText(getBaseContext(), "请输入金额", Toast.LENGTH_SHORT).show();
		}else {
			if (type.getText().toString().equals("请选择商户及类型")) {
				Toast.makeText(getBaseContext(), "请选择商户及类型", Toast.LENGTH_SHORT).show();
			}else {
			if (CANGO) {
				modle.setPhone(phone.getText().toString());
				modle.setPrice(money.getText().toString());
				modle.setName(name.getText().toString());
				modle.setType(type.getText().toString());
				modle.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();
				RequestParams params = new RequestParams();
				params.addBodyParameter("mobilePhone",phone.getText().toString());
				params.addBodyParameter("price",money.getText().toString());
				params.addBodyParameter("applied",type.getText().toString());
				params.addBodyParameter("myToken",USER.USERTOKEN);
				params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+type.getText().toString()+phone.getText().toString()+money.getText().toString()+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
				HttpUtils httpUtils = new HttpUtils(15000);
				httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD2,params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(Pay.this, "加载中");
					}
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						AllUtils.stopProgressDialog();
						AllUtils.toast(getBaseContext(), arg1);
					}  
					public void onSuccess(
							ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","orderformNumber"});
						content = temp.get(0);
						try {
							if ("0".equals(content)) {
								content = temp.get(1);
								modle.setOrdernumber(content);
								System.out.println(modle.getOrdernumber());
								startActivity(new Intent(getBaseContext(), Trade.class));
								LocalDOM.getinstance().write(getBaseContext(), "user", "from", "pay");
							}else {
								AllUtils.toast(getBaseContext(), arg0.result);
							}
						} catch (Exception e) {
							AllUtils.toast(getBaseContext(), arg0.result);
						}
						System.out.println(arg0.result);
					}});
			}else {
				Toast.makeText(getBaseContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				}
			}
		 }
	}
}
