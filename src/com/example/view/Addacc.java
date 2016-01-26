package com.example.view;
	/**
	 * ������п��˻�������Ϣ
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
	 * ��ʼ��ҳ�棬ҳ�����蹤��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_account);
		ViewUtils.inject(this);
		t = T.getInstance();
		modle = TradeModle.getInstance();
		//���������ť�Ŀɼ���
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
	 * ҳ�����¼�
	 * @param v ��������¼��İ�ť
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
				Toast.makeText(getBaseContext(), "��������ȷ�������Ϳ���", Toast.LENGTH_SHORT).show();	
			}
		break;
		default:
			break;
		}
	}
	/***
	 * ���ص�ǰ�˻�ʵ����Ϣ
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
					AllUtils.startProgressDialog(Addacc.this, "������");
					super.onStart();
				}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), arg1);
					name_ed.setText("΢���û�");
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
							AllUtils.toast(getBaseContext(), "�绰���벻��Ϊ��");
						}else if ("1002".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "���ֻ���δ����ʵ����֤������֤��");
						}else if ("1005".equals(result.get(0))) {
							AllUtils.toast(getBaseContext(), "��½״̬���������µ�¼");
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
			name_ed.setText("΢���û�");
		}
	}
	/**
	 * ѡ��ˢ����ʽ������(BPOSM188)����Ƶ(�´�½ME11)��
	 */
	private void toSlot() {
		final String types[] = new String []{"�����豸","��Ƶ�豸"};
		AlertDialog.Builder builder = new AlertDialog.Builder(Addacc.this);
		builder.setTitle("��ѡ�������豸����");
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
	 * ��ˢ����ȡ�Ŀ�����ʾ��ҳ����
	 */
	@Override
	protected void onRestart() {
		if ("slot".equals(LocalDOM.getinstance().read(getBaseContext(), "user", "from"))) {
			number_ed.setText(modle.getId());
		}
		super.onRestart();
	}
}
