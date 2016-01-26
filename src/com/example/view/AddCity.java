package com.example.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.cz.R;
import com.example.modle.T;
import com.example.modle.USER;
import com.example.modle.ZH;
import com.example.untils.City;
import com.example.untils.AllUtils;
import com.example.untils.Provice;
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
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import Adapter.CityAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
/**
 * ������п��ģ�������Ϣ��
 * �����С�ʡ���С�֧��
 * @author jacyayj
 *
 */
public class AddCity extends Activity implements SearchView.OnQueryTextListener{
	
	private final String METHOD = "provinceMarket_getProvince.shtml";
	private final String METHOD2 = "provinceMarket_getMarket.shtml";
	private final String METHOD3 = "querybranchinfos_getbranchinfos.shtml";
	private final String METHOD4 = "querybankinfos_getbankinfos.shtml";
	
	@ViewInject(R.id.city_c1)
	private TextView c1= null;
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	@ViewInject(R.id.add_city_bankimage)
	private ImageView bankimage= null;
	@ViewInject(R.id.city_pw)
	private TextView city= null;
	@ViewInject(R.id.city_zh_name)
	private TextView zhname= null;
	
	private SearchView searchView = null;
	private ListView searchList = null;
	private Dialog dialog = null;
	private AlertDialog.Builder builder = null;
	
	private  String bankname= null;
	private String timetemp = null;
	
	private T t = null;
	private CityAdapter adapter = null;
	
	private Map<String,String> bankid = null;
	private Map<String,String> proviceid = null;
	private Map<String,String> marketid = null;
	private Map<String,String> zhid = null;
	private ArrayList<String> zhs= null;
	private ArrayList<String> provicename = null;
	private ArrayList<String> marketname = null;
	private ArrayList<String> banks= null;
	
	private final int BANKIMG[] = new int[]{R.drawable.chengduban,R.drawable.gongsahng,R.drawable.faxia,R.drawable.guangda,
			R.drawable.guangdingfazhan,R.drawable.jianshe,R.drawable.jiaotong,R.drawable.mingshen,
			R.drawable.nonghang,R.drawable.nongcunxinyongshe,R.drawable.pinan,R.drawable.shanghaipudong,
			R.drawable.shanghaiyh,R.drawable.shenzhenfazhan,R.drawable.zhaoshang,R.drawable.zhongguoyh,
			R.drawable.zhongguoyouzhen,R.drawable.zhongxin,R.drawable.xinye};
	 
	/**
	 * ҳ���ʼ�������߳�ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_city);
		ViewUtils.inject(this);
	    t = T.getInstance();
		}
	/**
	 * ҳ���еĵ���¼���
	 * @param v ��������¼��Ŀؼ�
	 */
	@OnClick({R.id.city_back,R.id.city_ok,R.id.city_bank})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.city_back : finish();
			break;
		case R.id.city_ok : 
		    if (c1.getText().equals("��ѡ�񿪻�����")) {
				AllUtils.toast(getBaseContext(), "����ѡ�񿪻�����");
			}else {
				if (city.getText().equals("��ѡ�񿪻�ʡ��")) {
					AllUtils.toast(getBaseContext(), "����ѡ�񿪻�ʡ��");
				}else {
				    	startActivity(new Intent().setClass(AddCity.this, AffirmBind.class));
				}
			}
			break;	
		case R.id.city_bank : getBank();
			break;
		case R.id.city_add : getP();
			break;
		case R.id.city_zh : getZ();
			break;
		default :
			break;
		}
	}
	/**
	 * ��ȡ���п����������ƽ�������ʾ�ڽ����ϡ�
	 */
	private  void Bank(){
    	 setNull();
 		 builder = new AlertDialog.Builder(AddCity.this);
    	 builder.setTitle("��ѡ�񿪻�����");
    	 View view=getLayoutInflater().inflate(R.layout.addcity_list_bank, null);
    	 builder.setView(view);
    	 dialog = builder.create();
    	 ListView listView=(ListView) view.findViewById(R.id.addcity_list_bank1);
    	 ArrayList<Map<String, Object>>arrayList=new ArrayList<Map<String,Object>>();
    	 for(int i=0;i<banks.size();i++){
    		 Map<String, Object>map=new HashMap<String, Object>();
    		 map.put("bankname",banks.get(i));
    		 map.put("image",BANKIMG[i]);
    		 arrayList.add(map);
    	 }
    	 SimpleAdapter adapter=new SimpleAdapter(this, arrayList, R.layout.addcity_list_bank_item, new String[]{"bankname","image"}, new int[]{R.id.addcity_list_bank_text,R.id.addcity_list_bank_image});
    	 listView.setAdapter(adapter);
    	 listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				bankname = banks.get(position);
				c1.setText(bankname);
				t.setBankname(bankname);
				t.setId(bankid.get(bankname));
				System.out.println("id?"+bankid.get(bankname)+t.getId());
				bankimage.setVisibility(View.VISIBLE);
				bankimage.setImageResource(BANKIMG[position]);
				dialog.dismiss();
			}
		});
    	 dialog.show();
     }
	/**
	 * �������п�ʡ��dialog���������¼�
	 * @param objects ���п���������
	 */
	private void creatP(final String[] objects) {
		setNull();
		builder = new AlertDialog.Builder(AddCity.this);
		builder.setTitle("��ѡ�����п�����ʡ��");
		builder.setItems(objects, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				city.setText(objects[arg1]+" - ");
				t.setP(proviceid.get(objects[arg1]));
				getW();
				System.out.println(t.getP());
			}
		});
		builder.create().show();
	}
	/**
	 * �������п�������dialog���������¼�
	 * @param types ���п�������������������
	 */
	private void creatW(final String[] types) {
		setNull();
		builder = new AlertDialog.Builder(AddCity.this);
		builder.setTitle("��ѡ�����п����ڳ���");
		builder.setItems(types, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				city.append(types[arg1]);
				t.setW(marketid.get(types[arg1]));
				System.out.println(t.getW());
			}
		});
		builder.create().show();
	}
	/**
	 * ��������dialog���������¼�
	 */
	private void creatS() {
		setNull();
		builder = new AlertDialog.Builder(AddCity.this);
		builder.setTitle("��ѡ�����п�����");
		View view = LayoutInflater.from(this).inflate(R.layout.dialoglist,null);
		builder.setView(view);
		dialog = builder.create();
		searchList = (ListView) view.findViewById(R.id.dialog_list);
		searchView = (SearchView) view.findViewById(R.id.search);
		searchView.setOnQueryTextListener(this);  
        searchView.setSubmitButtonEnabled(false); 
		adapter = new CityAdapter(this,zhs);
		searchList.setAdapter(adapter);
		searchList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				zhname.setText(zhs.get(arg2));
				t.setZ(zhid.get(zhs.get(arg2)));
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	/**
	 * ��ȡ������������ʡ��Ϣ������
	 */
	private void getP() {
		if (proviceid==null && provicename==null) {
			proviceid = new HashMap<String, String>();
			provicename = new ArrayList<String>();
		}else {
			proviceid.clear();
			provicename.clear();
		}
		new HttpUtils(15000).send(HttpMethod.POST,URL+METHOD, new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(AddCity.this,"������"); 
				super.onStart();
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.stopProgressDialog();
				System.out.println(arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				String temp = AllUtils.getJson(arg0.result, new String []{"Province"}).get(0);
				try {
					Gson gson = new Gson();
					JsonParser parser = new JsonParser();
					JsonArray array = parser.parse(temp).getAsJsonArray();
					for (JsonElement obj : array) {
						Provice cse = gson.fromJson( obj , Provice.class);
						proviceid.put(cse.getPname(),cse.getPid());
						provicename.add(cse.getPname());
					}
					creatP( provicename.toArray(new String[provicename.size()]));
					System.out.println(arg0.result);
				} catch (Exception e) {
					AllUtils.toast(getBaseContext(), arg0.result);				
					}
			}
		});
	}
	/**
	 * ��ȡ����������������Ϣ������
	 */
	private void getW() {
		if (marketid==null && marketname==null) {
			marketid = new HashMap<String, String>();
		    marketname = new ArrayList<String>();
		}else {
			marketid.clear();
			marketname.clear();
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("provineid",t.getP());
		new HttpUtils(15000).send(HttpMethod.POST,URL+METHOD2, params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(AddCity.this,"������");
				super.onStart();
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.stopProgressDialog();
				System.out.println(arg1);
				AllUtils.toast(getBaseContext(), arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				String temp = AllUtils.getJson(arg0.result, new String []{"Market"}).get(0);
				try {
					Gson gson = new Gson();
					JsonParser parser = new JsonParser();
					JsonArray array = parser.parse(temp).getAsJsonArray();
					for (JsonElement obj : array) {
						City cse = gson.fromJson( obj , City.class);
						marketid.put(cse.getCname(),cse.getCid());
						marketname.add(cse.getCname());
					}
					creatW(marketname.toArray(new String[marketname.size()]));
					System.out.println(arg0.result);
					System.out.println("��ID��"+marketid.get("������"));
				} catch (Exception e) {
					AllUtils.toast(getBaseContext(), arg0.result);	
					System.out.println(e.getMessage()+arg0.result);
				}
			}
		});
	}
	/**
	 * ��ȡ���п�����֧����Ϣ������
	 */
	private void getZ(){
		if (banks != null && bankid != null) {
			if (proviceid != null && provicename != null || marketid != null && marketname !=null) {
				if (zhs == null && zhid ==null) {
					zhs = new ArrayList<String>();
					zhid = new HashMap<String, String>();
				}else {
					zhs.clear();
					zhid.clear();
				}
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();
				RequestParams params = new RequestParams();
				params.addBodyParameter("myToken",USER.USERTOKEN);
				params.addBodyParameter("bankcardid",t.getId());
				params.addBodyParameter("cityid",t.getW());
				params.addBodyParameter("banknumber",t.getCard());
				params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+t.getId()+t.getCard()+t.getW()+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
				new HttpUtils(15000).send(HttpMethod.POST,URL+METHOD3, params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(AddCity.this,"������");
						super.onStart();
					}
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						AllUtils.stopProgressDialog();
						System.out.println(arg1);
						AllUtils.toast(getBaseContext(), arg1);
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						List<String> result = AllUtils.getJson(arg0.result, new String []{"resultCode","sb"});
						if ("1007".equals(result.get(0))) {
							try {
								Gson gson = new Gson();
								JsonParser parser = new JsonParser();
								JsonArray array = parser.parse(result.get(1)).getAsJsonArray();
								for (JsonElement obj : array) {
									ZH cse = gson.fromJson( obj ,ZH.class);
									zhid.put(cse.getFbranchName(),cse.getPid());
									zhs.add(cse.getFbranchName());
								}
								creatS();
							} catch (Exception e) {
								AllUtils.toast(getBaseContext(), arg0.result);	
								System.out.println(e.getMessage()+arg0.result);
							}
						}else if ("1001".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "���п�id����Ϊ��");
						}else if ("1002".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "���п��Ų���ȷ");
						}else if ("1003".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "��id����Ϊ��");
						}else if ("1004".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "token����");
						}else if ("1005".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "û�и�������Ϣ");
						}else if ("1006".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "���п���ƥ�䣬������ѡ��");
						}else if ("1008".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "û�����֧����Ϣ");
						}else{
							AllUtils.toast(getBaseContext(), "����ʧ��");
						}
					}
				});
			}else {
				AllUtils.toast(getBaseContext(), "����ѡ�񿪻���ʡ��");
			}
		}else {
			AllUtils.toast(getBaseContext(), "����ѡ������");
		}
	}
	/**
	 * ��ȡ����������Ϣ
	 */
	private void getBank(){
		if (banks == null && bankid == null) {
			banks = new ArrayList<String>();
			bankid = new HashMap<String, String>();
		}else {
				banks.clear();
				bankid.clear();
			}
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		RequestParams params = new RequestParams();
		params.addBodyParameter("myToken",USER.USERTOKEN);
		params.addBodyParameter("timestamp",timetemp);
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
		new HttpUtils(15000).send(HttpMethod.POST,URL+METHOD4, params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(AddCity.this,"������");
				super.onStart();
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.stopProgressDialog();
				System.out.println(arg1);
				AllUtils.toast(getBaseContext(), arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				List<String> result = AllUtils.getJson(arg0.result, new String []{"resultCode","sb"});
				if ("1000".equals(result.get(0))) {
					try {
						Gson gson = new Gson();
						JsonParser parser = new JsonParser();
						JsonArray array = parser.parse(result.get(1)).getAsJsonArray();
						for (JsonElement obj : array) {
							com.example.modle.Bank cse = gson.fromJson( obj ,com.example.modle.Bank.class);
							bankid.put(cse.getPname(),cse.getPid());
							banks.add(cse.getPname());
						}
						Bank();
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(), arg0.result);	
						System.out.println(e.getMessage()+arg0.result);
					}
				}else {
					AllUtils.toast(getBaseContext(), "����ʧ��");
				}
			}
		});
	}
	private void setNull() {
		if (builder != null) {
			builder = null;
		}
	}
	@Override
	public boolean onQueryTextChange(String arg0) {
		adapter.refresh(searchItem(arg0));
		return false;
	}
	 public ArrayList<String> searchItem(String name) {  
	        ArrayList<String> mSearchList = new ArrayList<String>();  
	        for (int i = 0; i < zhs.size(); i++) {  
	            int index = zhs.get(i).indexOf(name);  
	            // ����ƥ�������  
	            if (index != -1) {  
	                mSearchList.add(zhs.get(i));  
	                System.out.println(zhs.get(i));
	            }  
	        }  
	        return mSearchList;  
	    }  
	@Override
	public boolean onQueryTextSubmit(String arg0) {
		return false;
	}
}
