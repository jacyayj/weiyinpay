package com.example.view;

import java.util.ArrayList;
import java.util.List;

import Adapter.BankAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.cz.R;
import com.example.modle.Bank;
import com.example.modle.T;
import com.example.modle.TradeModle;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
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
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
	/**
	 * 提现入口，显示当前账户绑定的所有储蓄卡
	 * @author jacyayj
	 *
	 */
public class Addbank  extends Activity{
	private final String METHOD  = "accountMerchantAction_getMoney.shtml";
	private final String METHOD4 = "bankAcount_delBank.shtml";
	
	@ViewInject(R.id.addlistview)
	private SwipeMenuListView list = null;
	@ViewInject(R.id.nocard)
	private ImageView body = null;
	@ViewInject(R.id.t_drawer_layout)
	private DrawerLayout layout = null;
	@ViewInject(R.id.t_drawer)
	private View drawer = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	
	private BankAdapter adapter = null;
	private List<String> bank = null;
	private List<String> number = null;
	private List<String> name = null;
	private List<String> bankid = null;
	private ArrayList<Bank> data = null;
	
	private String content = null;
	private String timetemp = null;
	
	private T t = null;
	private TradeModle modle = null;
	
	/***
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbank);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
		setList();
		loadDate();
	}
	/**
	 * 页面中的点击事件
	 * @param v 触发点击事件的控件
	 */
	@OnClick({R.id.addbank_back,R.id.addbank_add,R.id.t_konw})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.addbank_back : finish();
			break;
		case R.id.addbank_add : 
			if ("3".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			startActivity(new Intent(Addbank.this, Addacc.class));
			}else {
				AllUtils.toast(getBaseContext(), "请先实名认证");
				body.setVisibility(View.VISIBLE);
			}
			break; 
		case R.id.t_konw : layout.openDrawer(drawer);
			break;
		default:
			break;
		}
	}
	/**
	 * 初始化银行卡列表的listview
	 */
	private void setList(){
		adapter = new BankAdapter(this);
		list.setAdapter(adapter);
		//创建滑动删除的item
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
//				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				// set item title
				deleteItem.setTitle("删除");
				// set item title fontsize
				deleteItem.setTitleSize(18);
				// set item title font color
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		//为listview添加滑动删除效果
		list.setMenuCreator(creator);
		//l点击删除按钮进行删除操作
		list.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();
				RequestParams params = new RequestParams();
				params.addBodyParameter("myToken", USER.USERTOKEN);
				params.addBodyParameter("bankId", bankid.get(position));
				params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+bankid.get(position)+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
				HttpUtils http = new HttpUtils(15000);
				http.send(HttpRequest.HttpMethod.POST,URL+METHOD4,
						params,
						new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(Addbank.this, "删除中");
					}
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						AllUtils.stopProgressDialog();
						System.out.println(responseInfo.result);
						String temp = AllUtils.getJson(responseInfo.result, new String[]{"resultCode"}).get(0);
						if ("删除成功".equals(temp)) {
							data.remove(position);
							adapter.refresh(data);
							AllUtils.toast(getBaseContext(), "删除成功");
						}else {
							AllUtils.toast(getBaseContext(), "删除失败\n"+temp);
						}
					}
					@Override
					public void onFailure(HttpException error, String msg) {
						AllUtils.stopProgressDialog();
						AllUtils.toast(getBaseContext(), "删除失败\n"+msg);
					}
				});
				return false;
			}
		});
		//listview item的点击事件
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				t = T.getInstance();
				t.setId(bankid.get(position)); 
				t.setBankname(bank.get(position)); 
				t.setCard(number.get(position)); 
				startActivity(new Intent().setClass(Addbank.this,BlanceActivity.class));
			}
		});
	}
	/**
	 * 获得当前账户绑定的储蓄卡信息，并进行解析
	 */
	private void loadDate() {
		if (bank == null) {
			bank = new ArrayList<String>();
			bankid = new ArrayList<String>();
			number = new ArrayList<String>();
			name = new ArrayList<String>();
			data = new ArrayList<Bank>();
		}else {
			bank.clear();bankid.clear();number.clear();name.clear();data.clear();
		}
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		RequestParams params = new RequestParams();
		params.addBodyParameter("myToken", USER.USERTOKEN);
		params.addBodyParameter("timestamp", timetemp);
		params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(Addbank.this)));
		HttpUtils httpUtils = new HttpUtils(15000);
		httpUtils.send(HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(Addbank.this, "加载中");
				super.onStart();
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.stopProgressDialog();
				AllUtils.toast(getBaseContext(), arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				System.out.println(arg0.result);
				List<String> temp = AllUtils.getJson(arg0.result ,new String[]{"resultCode","sb"});
				if ("1000".equals(temp.get(0))) {
					content = temp.get(1);
					System.out.println("content:"+content);
					try {
						Gson gson = new Gson();
						JsonParser parser = new JsonParser();
						JsonArray Jarray = parser.parse(content).getAsJsonArray();
						for(JsonElement obj : Jarray ){
							Bank cse = gson.fromJson( obj , Bank.class);
							bank.add(cse.getPname());
							number.add(cse.getPnumber());
							modle.setId( cse.getPnumber());
							name.add(cse.getUsername());
							bankid.add(cse.getPid());
							data.add(cse);
						}
						//更新列表
						adapter.refresh(data);
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(), content);
						System.out.println(arg0.result);
						System.out.println(content);
					}
				}else if("1001".equals(temp.get(0))){
					body.setVisibility(View.VISIBLE);
					AllUtils.toast(getBaseContext(), "您尚未绑定任何银行卡请先绑定");
				}else if("1002".equals(temp.get(0))){
					AllUtils.toast(getBaseContext(), "参数出错");
				}else if("1005".equals(temp.get(0))){
					AllUtils.toast(getBaseContext(), "登陆状态出错请重新登录");
				}else {
					AllUtils.toast(getBaseContext(), "未知错误");
					System.out.println(arg0.result);
				}
			}
		});
	}
	/**
	 * 转换为DP单位
	 * @param dp listview的item高度
	 * @return 转换为dp的值
	 */
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
