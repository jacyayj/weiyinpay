package com.example.view;

import java.util.ArrayList;
import java.util.List;

import com.example.cz.R;
import com.example.modle.NEWS;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.DBOperation;
import com.example.untils.LocalDOM;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.DbUtils;
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

import Adapter.NewsAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
	/**
	 * ��Ϣҳ��һ��ҳ��
	 * @author jacyayj
	 *
	 */
public class News extends Activity {
	
	private final String METHOD = "message_showAll.shtml";
	
	@ViewInject(R.id.news_list)
	private ListView listview = null;
	
	private List<String> titles = null;
	private List<String> contents = null;
	private List<String> times = null;
	private List<String> states = null;
	private ArrayList<NEWS> data = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	@SuppressWarnings("unused")
	private SQLiteDatabase database = null;
	private LocalDOM dom = null;
	private NewsAdapter adapter = null;
	
	DbUtils dbUtils = null;
	private String timetemp = null;
	private final String DB_NAME = "NEWS";
	private int flag;
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		ViewUtils.inject(this);
		dbUtils = DbUtils.create(this);
		dom = LocalDOM.getinstance();
		adapter = new NewsAdapter(this);
		database = new DBOperation(this, DB_NAME, 1).getWritableDatabase();
		setList();
	}
	/**
	 * ҳ�����¼�
	 * @param v ��������¼��Ŀؼ�
	 */
	@OnClick({R.id.news_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.news_back : finish();
			break;
		default:
			break;
		}
	}
	/**
	 * ��дҳ��״̬�����м�����Ϣ����
	 */
	@Override
	protected void onStart() {
		load();
		super.onStart();
	}
	/**
	 * ��ʼ����Ϣ�б�
	 */
	private void setList() {
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
					dom.write(getBaseContext(), "news",times.get(arg2)+titles.get(arg2),"�Ѷ�");
					Intent intent = new Intent(News.this, News2.class);
					intent.putExtra("title",titles.get(arg2));
					intent.putExtra("content",contents.get(arg2));
					intent.putExtra("time",times.get(arg2));
					startActivity(intent);
			}
		});
	}
	/**
	 * �����������������Ϣ
	 */
	private void load() {
		if (titles == null) {
			times = new ArrayList<String>();
			contents = new ArrayList<String>();
			states = new ArrayList<String>();
			titles = new ArrayList<String>();
			data = new ArrayList<NEWS>();
		}else {
			titles.clear();
			states.clear();
			times.clear();
			contents.clear();
			data.clear();
		}
			timetemp = System.currentTimeMillis()+AllUtils.getRandom();
			HttpUtils httpUtils = new HttpUtils(15000);
	        RequestParams params = new RequestParams();
	        params.addBodyParameter("myToken",USER.USERTOKEN);          
	        params.addBodyParameter("timestamp",timetemp);
			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
	        httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>() {
	        	@Override
	        	public void onStart() {
	        		AllUtils.startProgressDialog(News.this, "������");
	        		super.onStart();
	        	}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), arg1);
				}
				@SuppressLint("SimpleDateFormat") @Override
				public void onSuccess(ResponseInfo<String> arg0) {
					AllUtils.stopProgressDialog();
					flag = 0;
					List<String> temp = AllUtils.getJson(arg0.result,new String[]{"resultCode","data"});
					if ("1".equals(temp.get(0))) {
						AllUtils.toast(getBaseContext(), temp.get(0));
					}else if ("2".equals(temp.get(0))) {
						try {
							Gson gson = new Gson();
							JsonParser parser = new JsonParser();
							JsonArray Jarray = parser.parse(temp.get(1)).getAsJsonArray();
							for (JsonElement element : Jarray) {
								NEWS news = gson.fromJson(element, NEWS.class);
								titles.add(news.getFtitle());
								times.add(news.getFdate());
								contents.add(news.getFcontent());
								if ("�Ѷ�".equals(dom.read(getBaseContext(), "news",news.getFdate()+news.getFtitle()))) {
									flag++;
									news.setFstatus("0");
									System.out.println("flag = "+flag);
								}else {
									news.setFstatus("1");
									System.out.println("δ����");
								}
								data.add(news);
							}
							if (flag == titles.size()) {
								System.out.println("��δ��");
								dom.write(getBaseContext(), "news", "has","wu");
							}else {
								dom.write(getBaseContext(), "news", "has","you");
								System.out.println("��δ��");
							}
							adapter.refresh(data);
						} catch (Exception e) {
							System.out.println("JSON ERROR");
						}
					}else {
						AllUtils.toast(getBaseContext(),arg0.result); 
					}
				}});
			}
}
