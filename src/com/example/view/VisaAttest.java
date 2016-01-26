package com.example.view;

import java.util.ArrayList;
import java.util.List;

import com.example.cz.R;
import com.example.modle.Bank;
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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import Adapter.VisaAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
	/**
	 * 已绑定信用卡列表页面
	 * @author jacyayj
	 *
	 */
public class VisaAttest extends Activity{
	@ViewInject(R.id.visa_drawer_layout)
	private DrawerLayout drawerLayout = null;
	@ViewInject(R.id.visa_drawer)
	private View drawer = null;
	@ViewInject(R.id.visa_attest_listview)
	private ListView listView = null;
	@ViewInject(R.id.visa_attest_bg)
	private ImageView bg = null;
	
	private String METHOD = "promote_getAllCreditInfo.shtml";
	ArrayList<Bank> data = null;
	VisaAdapter adapter = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visa_attest);
		ViewUtils.inject(this);
		setList();
		loadCard();
	}
	/**
	 * 页面点击事件
	 * @param arg0	触发点击事件的页面
	 */
	@OnClick({R.id.visa_attest_add ,R.id.visa_attest_back ,R.id.visa_konw ,})
	public void visaAttestClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.visa_attest_add : startActivity(new Intent(getBaseContext(), VisaSlot.class));
			break;
		case R.id.visa_attest_back : finish();
		break;
		case R.id.visa_konw : drawerLayout.openDrawer(drawer);;
		break;
		default:
			break;
		}
	}
	/**
	 * 初始化列表
	 */
	private void setList() {
		adapter = new VisaAdapter(this);
		listView.setAdapter(adapter);
	}
	/**
	 * 从服务器加载已绑定的所有信用卡
	 */
	private void loadCard() {
		if (data == null) {
			data = new ArrayList<Bank>();
		}else {
			data.clear();
		}
		String timestamp = System.currentTimeMillis()+AllUtils.getRandom();
		RequestParams params = new RequestParams();
		params.addBodyParameter("myToken", USER.USERTOKEN);
		params.addBodyParameter("timestamp",timestamp );
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timestamp+AllUtils.readPrivateKeyFile(this)));
		new HttpUtils().send(HttpMethod.POST,getResources().getString(R.string.url)+METHOD,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(VisaAttest.this,"加载中");
				super.onStart();
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.toast(getBaseContext(), arg1);
				AllUtils.stopProgressDialog();
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				List<String> result = AllUtils.getJson(arg0.result, new String[]{"resultCode","sb"});
					System.out.println(arg0.result);
					if ("1000".equals(result.get(0))) {
						Gson gson = new Gson();
						JsonParser parser = new JsonParser();
						JsonArray Jarray = parser.parse(result.get(1)).getAsJsonArray();
						for(JsonElement obj : Jarray ){
							Bank cse = gson.fromJson( obj , Bank.class);
							data.add(cse);
						}
						adapter.refresh(data);
					}else if ("1001".equals(result.get(0))) {
						AllUtils.toast(getBaseContext(), "您暂未绑定任何银行卡，请先绑定");
						bg.setVisibility(View.VISIBLE);
					}else if ("1002".equals(result.get(0))) {
						AllUtils.toast(getBaseContext(), "客户端出错");
						bg.setVisibility(View.VISIBLE);
					}else if ("1005".equals(result.get(0))) {
						AllUtils.toast(getBaseContext(), "登陆状态出错，请重新登录");
						bg.setVisibility(View.VISIBLE);
					}else {
						AllUtils.toast(getBaseContext(), arg0.result);
					}
			}
		});
	}
}
