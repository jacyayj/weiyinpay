package com.example.cz;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Adapter.MainAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.example.untils.UpdateVersion;
import com.example.view.Account;
import com.example.view.Addbank;
import com.example.view.Applayagent;
import com.example.view.ChangePwd;
import com.example.view.Fist;
import com.example.view.LoginReg;
import com.example.view.More;
import com.example.view.News;
import com.example.view.Pay;
import com.example.view.RZ;
import com.example.view.RePay;
import com.example.view.Record;
import com.example.view.Telphone;
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
import com.lidroid.xutils.view.annotation.event.OnItemClick;

/**
 * 程序入口，主页面
 * @author jacyayj
 *
 */
public class MainActivity extends Activity {
	
	private final String METHOD2 = "accountMerchantAction_isAudit.shtml";
	private final String METHOD = "loginAction_logout.shtml";
	
	private UpdateVersion mUpdateManager = null;
	private double vers;
	
	@ViewInject(R.id.main_gridview)
	private GridView gridView = null;
	
	@ResInject(id = R.array.body_array,type = ResType.StringArray)
	private String items[] = null;
	
	@ViewInject(R.id.news)
	private ImageView news = null;
	
	@ResInject(id = R.string.url ,type = ResType.String)
	private String URL = null;
	
	private String timetemp = null;
	private String islogin = null;
	private List<String> result = null;

	private String apkUrl = "http://d2.apk8.com:8020/game/diandiandian.apk";
	private LocalDOM dom = null;
	

	/**
	 * 页面中点点击事件
	 * @param v 传入的控件
	 */
	@OnClick({R.id.more,R.id.news,R.id.main_lable1,R.id.main_lable2})
	public void mainClick(View v) {
		if ("1".equals(islogin)) {
		switch (v.getId()) {
		case R.id.more : startActivity(new Intent(MainActivity.this,More.class));
			break;
		case R.id.news : startActivity(new Intent(MainActivity.this,News.class));
			break;
		case R.id.main_lable1 : startActivity(new Intent(MainActivity.this, Addbank.class));
			break;
		case R.id.main_lable2 : startActivity(new Intent(MainActivity.this, Account.class));
			break;	
		default:
			break;
			}
		}else {
			startActivity(new Intent(MainActivity.this,LoginReg.class));
		}
	}
	/**
	 * 页面主题GRIDVIEW的点击事件处理
	 * @param parent
	 * @param view 点击的item
	 * @param position	item索引
	 * @param id itemid
	 */
	@OnItemClick({R.id.main_gridview})
	private void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			if ("1".equals(islogin)) {
				if (position == 0) {
					startActivity(new Intent(getBaseContext(),
							Record.class));
				} else if (position == 1) {
					startActivity(new Intent(getBaseContext(),
							Pay.class));
				} else if (position == 2) {
					startActivity(new Intent(getBaseContext(),Addbank.class));
				} else if (position == 3) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 4) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 5) {
					if ("3".equals(dom.read(getBaseContext(), "user",
							USER.USERTOKEN + "rz"))
							|| "2".equals(dom.read(getBaseContext(),
									"user", USER.USERTOKEN + "rz"))
							|| "4".equals(dom.read(getBaseContext(),
									"user", USER.USERTOKEN + "rz"))) {
						startActivity(new Intent().setClass(
								getBaseContext(), RZ.class));
					} else {
						startActivity(new Intent(getBaseContext(), Fist.class));
					}
				} else if (position == 6) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 7) {
					startActivity(new Intent(getBaseContext(),ChangePwd.class));
				} else if (position == 9) {
					startActivity(new Intent(getBaseContext(),Telphone.class));
				} else if (position == 8) {
					startActivityForResult(new Intent(getBaseContext(), Applayagent.class), 0);
				} else if (position == 10) {
					AllUtils.shard(MainActivity.this);
				} else if (position == 11) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 12) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 13) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");  
				} else if (position == 14) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 15) {
					AllUtils.toast(MainActivity.this, "亲，此功能正在开发中...");
				} else if (position == 16) {
					startActivity(new Intent(MainActivity.this, RePay.class).putExtra("from","repay"));
				} else if (position == 17) {

				}
			} else {
				startActivity(new Intent().setClass(getBaseContext(),
						LoginReg.class));
			}
	}
	/**
	 * 与服务器通讯活动初始信息，确保账户处于安全环境
	 * 
	 * 	 */
	private void load() {
		if ("1".equals(islogin)
				&& !"3".equals(dom.read(getBaseContext(), "user",
						USER.USERTOKEN + "rz"))) {
			timetemp = System.currentTimeMillis() + AllUtils.getRandom();
			HttpUtils httpUtils = new HttpUtils(15000);
			RequestParams params = new RequestParams();
			params.addBodyParameter("myToken", USER.USERTOKEN);
			params.addBodyParameter("timestamp", timetemp);
			params.addBodyParameter(
					"hashCode",
					AllUtils.Md5(USER.USERTOKEN + timetemp
							+ AllUtils.readPrivateKeyFile(getBaseContext())));
			httpUtils.send(HttpMethod.POST, URL + METHOD2, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							AllUtils.stopProgressDialog();
							AllUtils.toast(getBaseContext(), arg1);
						};

						@Override
						public void onStart() {
							AllUtils.startProgressDialog(MainActivity.this,
									"加载中");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							AllUtils.stopProgressDialog();
							result = AllUtils.getJson(arg0.result,
									new String[] { "resultCode", "isAudit",
											"Fname", "Fidentity" });
							System.out.println(arg0.result);
							if ("1000".equals(result.get(0))) {
								if ("1".equals(result.get(1))) {
									dom.write(getBaseContext(), "user",
											USER.USERTOKEN + "rz", "1");
								} else if ("2".equals(result.get(1))) {
									dom.write(getBaseContext(), "user",
											USER.USERTOKEN + "rz", "2");
									USER.USERNAME = result.get(2);
									USER.USERID = result.get(3);
								} else if ("3".equals(result.get(1))) {
									dom.write(getBaseContext(), "user",
											USER.USERTOKEN + "rz", "3");
									USER.USERNAME = result.get(2);
									USER.USERID = result.get(3);
								} else if ("4".equals(result.get(1))) {
									dom.write(getBaseContext(), "user",
											USER.USERTOKEN + "rz", "4");
									USER.USERNAME = result.get(2);
									USER.USERID = result.get(3);
								} else {
									dom.write(getBaseContext(), "user",
											USER.USERTOKEN + "rz", "-1");
								}
							} else if ("1005".equals(result.get(0))) {
								AllUtils.toast(getBaseContext(), "登录状态出错请重新登录");
							} else {
								AllUtils.toast(getBaseContext(), result.get(0));
							}
						}
					});
		}
	}
	/**
	 * 消息的处理
	 */
	private void LOADNOTIFI() {
		String temp = dom.read(getBaseContext(), "news", "has");
		if ("you".equals(temp)) {
			news.setBackgroundResource(R.drawable.news_a);
			System.out.println(temp);
		} else if ("wu".equals(temp)) {
			news.setBackgroundResource(R.drawable.news_b);
			System.out.println(temp);
		} else {
			System.out.println(temp);
		}
	}
	/**
	 * back键的监听，通知服务器退出登录注销当前账户信息。退出程序
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("确认退出");
		builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timetemp = System.currentTimeMillis() + AllUtils.getRandom();
				HttpUtils httpUtils = new HttpUtils(15000);
				RequestParams params = new RequestParams();
				params.addBodyParameter("myToken", USER.USERTOKEN);
				params.addBodyParameter("mobilePhone", USER.USERPHONE);
				params.addBodyParameter("timestamp", timetemp);
				params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+ USER.USERPHONE + timetemp+ AllUtils.readPrivateKeyFile(getBaseContext())));
				httpUtils.send(HttpRequest.HttpMethod.POST, URL + METHOD,
						params, new RequestCallBack<String>() {
							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								AllUtils.stopProgressDialog();
								dom.clear(MainActivity.this, "user");
								System.exit(0);
							};
							@Override
							public void onStart() {
								AllUtils.startProgressDialog(MainActivity.this,"发送中");
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								AllUtils.stopProgressDialog();
								List<String> temp = AllUtils.getJson(
										arg0.result,
										new String[] { "resultCode" });
								if ("1000".equals(temp.get(0))) {
									dom.clear(MainActivity.this, "user");
									System.exit(0);
								} else {
									dom.clear(MainActivity.this, "user");
									System.exit(0);
								}
							}
						});
			}
		});
		builder.setNegativeButton("取消", null).create().show();
	}
	/**
	 * 初始化页面，页面中所需的工具
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		MainAdapter adapter = new MainAdapter(this,items);
		gridView.setAdapter(adapter);
		dom = LocalDOM.getinstance();
		updateversion();
	}
	/**
	 * 重写onstart方法，在其中处理消息、与服务器通讯
	 */
	@Override
	protected void onStart() {
		islogin = dom.read(getBaseContext(), "user", USER.USERTOKEN);
		LOADNOTIFI();
		load();
		super.onStart();
	}
	/**
	 * 版本更新
	 */
	public void updateversion() {
		double a = version();
		Intent in = getIntent();
		String v1 = in.getStringExtra("content");
		if (v1 != null) {
			Pattern p = Pattern.compile("[0-9]*");
			Matcher m = p.matcher(v1);
			if (m.matches()) {
				double ve = Double.parseDouble(v1);
				if (a < ve) {
					// 更新软件版本
					mUpdateManager = new UpdateVersion(this, apkUrl);
					mUpdateManager.checkUpdateInfo();
				}
			}
		}
	}
	public double version() {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = packInfo.versionName;
			vers = Double.parseDouble(version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return vers;
	}
}
