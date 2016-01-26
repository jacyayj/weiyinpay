package com.example.view;

import java.util.ArrayList;
import java.util.List;


import com.example.cz.R;
import com.example.modle.Raise;
import com.example.modle.RecordModle;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.Recordhistory;
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
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;

import Adapter.RecordAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
	/**
	 * 交易记录页面
	 * @author jacyayj
	 *
	 */
public class Record extends Activity {
	@ViewInject(R.id.record_list)
	private ListView listView = null;
	@ViewInject(R.id.record_back)
	private View back = null;
	@ViewInject(R.id.record_image1)
	private ImageView im = null;
	@ViewInject(R.id.record_btn1)
	private Button bt1 = null;
	@ViewInject(R.id.record_btn2)
	private Button bt2 = null;
	@ViewInject(R.id.record_btn3)
	private Button bt3 = null;
	@ViewInject(R.id.record_btn4)
	private Button bt4 = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private ArrayList<RecordModle> list = null;
	private String type = null;
	private String timetemp = null;
	
	private String content = null;
	private String METHOD = "transAction_transRecord.shtml";
	private JsonArray Jarray = null;
	
	private ArrayList<String> prams = null;
	private List<String> times = null;
	private List<String> types = null;
	private List<String> moneys = null;
	private List<String> states = null;
	private List<String> ordernumbers = null;
	private List<String> cardtypes = null;
	private List<String> cards = null;
	private List<String> paynumbers = null;
	private List<String> shounumbers = null;
	private List<String> tickets = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		ViewUtils.inject(this);
		type = "pay";
		init(); 
	}
	@OnClick({R.id.record_btn1,R.id.record_btn2,R.id.record_btn3,R.id.record_btn4,R.id.record_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.record_btn1 :
			bt1.setBackgroundResource(R.drawable.record_left_a);
			bt2.setBackgroundResource(R.drawable.record_mid_b);
			bt3.setBackgroundResource(R.drawable.record_mid_a2);
			bt4.setBackgroundResource(R.drawable.record_right_b);
		
			bt1.setTextColor(0xffffffff);
			bt2.setTextColor(0xff333333);
			bt3.setTextColor(0xff333333);
			bt4.setTextColor(0xff333333);
			type = "pay";
			listView.setVisibility(View.INVISIBLE);
			im.setVisibility(View.VISIBLE);
			init(); 
			break;
		case R.id.record_btn2 :
			bt1.setBackgroundResource(R.drawable.record_left_b);
			bt2.setBackgroundResource(R.drawable.record_mid_a);
			bt3.setBackgroundResource(R.drawable.record_mid_a2);
			bt4.setBackgroundResource(R.drawable.record_right_b);
			
			bt1.setTextColor(0xff333333);
			bt2.setTextColor(0xffffffff);
			bt3.setTextColor(0xff333333);
			bt4.setTextColor(0xff333333);
			im.setVisibility(View.VISIBLE);
			listView.setVisibility(View.INVISIBLE);
			AllUtils.toast(getBaseContext(), "无任何交易记录");
			break;
		case R.id.record_btn3 :
			bt1.setBackgroundResource(R.drawable.record_left_b);
			bt2.setBackgroundResource(R.drawable.record_mid_a2);
			bt3.setBackgroundResource(R.drawable.record_mid_a);
			bt4.setBackgroundResource(R.drawable.record_right_b);
			
			bt1.setTextColor(0xff333333);
			bt2.setTextColor(0xff333333);
			bt3.setTextColor(0xffffffff);
			bt4.setTextColor(0xff333333);
			type = "cash";
			listView.setVisibility(View.INVISIBLE);
			im.setVisibility(View.VISIBLE);
			init(); 
			break;
		case R.id.record_btn4 :
			bt1.setBackgroundResource(R.drawable.record_left_b);
			bt2.setBackgroundResource(R.drawable.record_mid_b);
			bt3.setBackgroundResource(R.drawable.record_mid_a2);
			bt4.setBackgroundResource(R.drawable.record_right_a);
			
			bt1.setTextColor(0xff333333);
			bt2.setTextColor(0xff333333);
			bt3.setTextColor(0xff333333);
			bt4.setTextColor(0xffffffff);
			im.setVisibility(View.VISIBLE);
			listView.setVisibility(View.INVISIBLE);
			AllUtils.toast(getBaseContext(), "无任何交易记录");
			break;
		case R.id.record_back : finish();
		default:
			break;
		}
	}
	@OnItemSelected({R.id.record_list})
	private void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
		prams = null;
		prams = new ArrayList<String>();
		if ("pay".equals(type)) {
			prams.add(ordernumbers.get(position));
			prams.add("民生服务");
			prams.add(shounumbers.get(position));
			prams.add(times.get(position));
			prams.add(moneys.get(position));
			prams.add(paynumbers.get(position));
			prams.add("刷卡支付");
			prams.add(cards.get(position));
			prams.add(tickets.get(position));
//			System.out.println(tickets.get(position));
			Intent intent = new Intent();
			intent.setClass(getBaseContext(), Detail.class);
			intent.putExtra("temp", "pay");
			intent.putStringArrayListExtra("pram", prams);
			startActivity(intent);
		}else if ("cash".equals(type)) {
				prams.add("手机号普通提款");
				prams.add(paynumbers.get(position));
				prams.add(cards.get(position));
				prams.add(moneys.get(position));
				prams.add(times.get(position));
				prams.add("账户支付");
				prams.add(states.get(position));
				System.out.println(states.get(position));
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), Detail.class);
				intent.putExtra("temp", "cash");
				intent.putStringArrayListExtra("pram", prams);
				startActivity(intent);
		}
	}
	/**
	 * 提现记录
	 */
	private void init (){
		types = new ArrayList<String>();
		moneys = new ArrayList<String>();
		states = new ArrayList<String>();
		times = new ArrayList<String>();
		ordernumbers = new ArrayList<String>();
		cards = new ArrayList<String>();
		cardtypes = new ArrayList<String>();
		paynumbers = new ArrayList<String>();
		shounumbers = new ArrayList<String>();
		tickets = new ArrayList<String>();
		
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		HttpUtils httpUtils = new HttpUtils(30000);
        RequestParams params = new RequestParams();
        params.addBodyParameter("myToken",USER.USERTOKEN);
        params.addBodyParameter("type",type);
        params.addBodyParameter("timestamp",timetemp);
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+type+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
        httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(Record.this, "加载中");
			};
        	@Override
			public void onFailure(HttpException arg0, String arg1) {
        		AllUtils.stopProgressDialog();
        		AllUtils.toast(getBaseContext(), arg1);
        		System.out.println(arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				System.out.println(arg0.result);
				List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","data"});
				if ("1000".equals(temp.get(0))) {
					content = temp.get(1);
						Gson gson = null;
						try {
							gson = new Gson();
							JsonParser parser = new JsonParser();
							Jarray = parser.parse(content).getAsJsonArray();//?????
							if(Jarray.size()==0){
								Toast.makeText(getBaseContext(), "无任何交易记录", Toast.LENGTH_SHORT).show();
								    }else {  
								    	if ("pay".equals(type)) {
								    		for(JsonElement obj : Jarray ){
										    	Recordhistory cse = gson.fromJson( obj , Recordhistory.class);
										    	times.add(cse.getDatetime());
												types.add(cse.getTranstype());
												moneys.add(cse.getAmount());
												states.add(cse.getTransstat());	
												ordernumbers.add(cse.getOrderno());	
												cards.add(cse.getCardno());
												cardtypes.add(cse.getCardtype());
												paynumbers.add(cse.getMfrchantno());
												shounumbers.add(cse.getMerchantno());
												tickets.add(cse.getReceiptFile());
									    	}
										}else if ("cash".equals(type)) {
											for(JsonElement obj : Jarray ){
										    	Raise cse = gson.fromJson( obj , Raise.class);
										    	times.add(cse.getFdate());
												types.add(cse.getFpayType());
												moneys.add(cse.getFamount());
												states.add(cse.getFstatus());	
												cards.add(cse.getfBankNumber());
												paynumbers.add(cse.getTmerchant());
									    	}
										}
									setlist();
								}
						} catch (Exception e) {
							AllUtils.toast(getBaseContext(),arg0.result);
						}
				}else {
					AllUtils.toast(getBaseContext(),"无任何交易记录");
				}
			}
		} );
	}
	/**
	 * 初始化列表
	 */
	private void setlist(){
		if (times.size()>0) {
			listView.setVisibility(View.VISIBLE);
			im.setVisibility(View.INVISIBLE);
		}
		list = new ArrayList<RecordModle>();
		for (int i = 0; i < times.size(); i++) {
			RecordModle item = new RecordModle();
			if ("pay".equals(type)) {
				item.setAmount("+"+moneys.get(i)+"元");
				item.setTranstype(types.get(i));
			}else if ("cash".equals(type)) {
				item.setAmount( "-"+moneys.get(i)+"元");
				item.setTranstype("提现");
			}
			if ("1".equals(states.get(i))) {
				item.setTransstat("交易成功");
			}else {
				item.setTransstat("交易失败");
			}
			item.setDatetime(times.get(i));
			list.add(item);
		}
		RecordAdapter adapter = new RecordAdapter(this);
		adapter.refresh(list);
		listView.setAdapter(adapter);
	}
}