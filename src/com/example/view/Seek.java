package com.example.view;

import java.util.List;

import com.example.controll.DeviceController;
import com.example.cz.DeviceControllerImpl;
import com.example.cz.Const.MKIndexConst;
import com.example.cz.R;
import com.example.modle.TerminalModle;
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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.audioport.AudioPortV100ConnParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
	/**
	 * ��Ƶ�豸����ҳ��
	 * @author jacyayj
	 *
	 */
public class Seek extends Activity {
	@ViewInject(R.id.seek_card)
	private ImageView card = null;
	@ResInject(id = R.anim.card_anim,type = ResType.Animation)
	private Animation animation = null;
	
	private DeviceController controller = null;
	private String timetemp = null;
	private TradeModle modle = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;

	private final String ME11_DRIVER_NAME = "com.newland.me.ME11Driver";
	private final String METHOD = "appTermNo_binding.shtml";
	private final String METHOD2 = "appPosAction_showAll.shtml";
	private String deviceInteraction = "";
	private final String MAIN_KEY = "11111111111111111111111111111111";
	protected  final String WORKINGKEY_DATA_PIN = "FA0CEE21C2D2A93347930E6EDD3A2D5C";// ����pin��Կ����
	protected  final String WORKINGKEY_DATA_TRACK = "245BD98D85ACF0465B05CF1483A004AF";// ���Դŵ���Կ����
	protected  final String WORKINGKEY_DATA_MAC = "998742F7BC2D7005998742F7BC2D7005";// ����mac��Կ����
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x01) {
				startActivity(new Intent(Seek.this, SlotCard.class));
				finish();
			}else if(msg.what == 0x02){
				initMe11Controller();
				try {
					connectDevice();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AllUtils.stopProgressDialog();
						}
					});
				} catch (Exception e) {
					AllUtils.toast(getBaseContext(), "������豸");
				}
			}
		};
	};
	protected boolean processing = false;
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seek);
		ViewUtils.inject(this);
		card.startAnimation(animation);
		controller = DeviceControllerImpl.getInstance();
		modle = TradeModle.getInstance();
		AllUtils.startProgressDialog(Seek.this, "�豸������");
		handler.sendEmptyMessageDelayed(0x02,1000);
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.seek_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.seek_back : finish();
			break;

		default:
			break;
		}
	}
	/**
	 * ���óɵȴ���ʼ������״̬
	 * @since ver1.0
	 */
	private void initMe11Controller() {
		btnStateToWaitingInitFinished();
		Seek.this.initMe11DeviceController(new AudioPortV100ConnParams());
		btnStateInitFinished();
	}
	/**
	 * ��ʼ��ME11�豸
	 * @since ver1.0
	 * @param params �豸���Ӳ���
	 */
	private void initMe11DeviceController(DeviceConnParams params) {
		controller.init(Seek.this, ME11_DRIVER_NAME, params, new DeviceEventListener<ConnectionCloseEvent>() {
			@Override
			public void onEvent(ConnectionCloseEvent event, Handler handler) {
				if (event.isSuccess()) {
					appendInteractiveInfoAndShow("�豸���ͻ������Ͽ���");
				}
				if (event.isFailed()) {
					appendInteractiveInfoAndShow("�豸�����쳣�Ͽ���" + event.getException().getMessage());
				}
			}
			@Override
			public Handler getUIHandler() {
				return null;
			}
		});
	}
	/**
	 * �ӷ�������ȡ�󶨵�POS���ն˺�
	 */
	private void getPos() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		HttpUtils httpUtils = new HttpUtils(15000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("myToken", USER.USERTOKEN);
		params.addBodyParameter("timestamp",timetemp);
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getApplicationContext())));
		httpUtils.send(HttpMethod.POST,URL+METHOD2, params,new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0,
					String arg1) {
				AllUtils.toast(getBaseContext(), arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				System.out.println(arg0.result);
				List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","data"});
				if ("1001".equals(temp.get(0))) {
					Toast.makeText(getBaseContext(),"����ʧ��", Toast.LENGTH_SHORT).show();
				}else if ("1002".equals(temp.get(0))) {
					Toast.makeText(getBaseContext(), "��½״̬���������µ�¼", Toast.LENGTH_SHORT).show();
				}else if ("1000".equals(temp.get(0))) {
					try {
						Gson gson = new Gson();
						JsonParser parser = new JsonParser();
						JsonArray Jarray = parser.parse(temp.get(1)).getAsJsonArray();
						for(JsonElement obj : Jarray ){
							TerminalModle cse = gson.fromJson( obj , TerminalModle.class);
							if (cse.getFsn()!=null) {
								System.out.println("���ն˺�"+cse.getFsn());
								modle.setPosid(cse.getFsn());
								LocalDOM.getinstance().write(getBaseContext(), "POS",USER.USERPHONE,modle.getPosid());
								handler.sendEmptyMessage(0x01);
							}else {
								System.out.println("���ն˺�22"+cse.getFsn());
							}
						}
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(), arg0.result);
					}
				}else {
					AllUtils.toast(getBaseContext(), "�ն˺���֤����������");
				}
			}
		});
	}
	/**
	 * ����POS��
	 */
	private void BindPos() {
		if (processing) {
			appendInteractiveInfoAndShow("�豸��ǰ����ִ�г�������...");
			return;
		}
		String temp = LocalDOM.getinstance().read(getBaseContext(), "POS",USER.USERPHONE);
		if ("��".equals(temp)) {
			btnStateToProcessing();
			try {
				DeviceInfo deviceInfo = null;
				deviceInfo = controller.getDeviceInfo_me11();
				if (deviceInfo!=null) {
					modle.setPosid(deviceInfo.getCSN());
					//										modle.setPosid("wyw000000000001");
					System.out.println("��ȡ���ն˺�"+deviceInfo.getCSN());
				}
				//									appendInteractiveInfoAndShow("�豸�����Ϣ:" + deviceInfo);
			} catch (Exception e) {
				appendInteractiveInfoAndShow("��ȡ�豸��Ϣʧ��!ԭ��:" + e.getMessage());
			}
			btnStateInitFinished();
			timetemp = System.currentTimeMillis()+AllUtils.getRandom();
			HttpUtils httpUtils = new HttpUtils(15000);
			RequestParams params = new RequestParams();
			params.addBodyParameter("myToken", USER.USERTOKEN);
			params.addBodyParameter("APPfsn",modle.getPosid());
			//						TradeModle.getInstance().setPosid("qwer22");
			params.addBodyParameter("timestamp",timetemp);
			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+modle.getPosid()+timetemp+AllUtils.readPrivateKeyFile(getApplicationContext())));
			httpUtils.send(HttpMethod.POST, URL+METHOD, params,new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0,String arg1) {
					System.out.println("shibai:"+arg1);
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String temp = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
					System.out.println("code:"+temp);
					if ("0".equals(temp)) {
						AllUtils.toast(getApplicationContext(), "�ն˰󶨳ɹ�");
						LocalDOM.getinstance().write(getBaseContext(), "POS",USER.USERPHONE,modle.getPosid());
						handler.sendEmptyMessage(0x01);
					}else if ("1".equals(temp)) {
						getPos();
					}else if ("2".equals(temp)) {
						AllUtils.toast(getApplicationContext(), "�ն˰�ʧ��");
					}else if ("3".equals(temp)) {
						AllUtils.toast(getApplicationContext(), "���ն��Ѿ�������");
					}else if ("4".equals(temp)) {
						AllUtils.toast(getApplicationContext(), "������ն˺Ų�����");
					}else if ("5".equals(temp)) {
						AllUtils.toast(getApplicationContext(), "��½״̬���������µ�¼");
					}else if ("6".equals(temp)) {
						AllUtils.toast(getApplicationContext(), "������ն˺Ų���Ϊ��");
					}else {
						AllUtils.toast(getApplicationContext(), arg0.result);
					}
				}
			});
		}else {
			modle.setPosid(temp);
			handler.sendEmptyMessage(0x01);
			System.out.println("�����ն˺�"+modle.getPosid());
		}
	}
	/**
	 * �����豸
	 */
	public void connectDevice() {
		//			appendInteractiveInfoAndShow("�豸������..."); 
		try {
			controller.connect();
			appendInteractiveInfoAndShow("�豸���ӳɹ�");
			if ("pay".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
				BindPos();
			}else {
				handler.sendEmptyMessage(0x01);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			appendInteractiveInfoAndShow("������豸...");
		}
	}
	/**
	 * ��������Կ
	 */
	@SuppressWarnings("unused")
	private void loadMainKey() {
		try {
			controller.loadMainKey(KekUsingType.TR31_BLOCK, MKIndexConst.DEFAULT_MK_INDEX, ISOUtils.hex2byte(MAIN_KEY), ISOUtils.hex2byte("95EF0AD7"), 0x00);
			System.out.println("����Կװ�سɹ�!");
			loadWorkKey();
		} catch (Exception ex) {
			appendInteractiveInfoAndShow("����Կװ��ʧ��!" + ex.getMessage());
			AllUtils.stopProgressDialog();
		}
	}
	/**
	 * ���¹�����Կ
	 */
	private void loadWorkKey() {
		try {
			controller.updateWorkingKey(WorkingKeyType.PININPUT, ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), ISOUtils.hex2byte("CD468BFA"));
			controller.updateWorkingKey(WorkingKeyType.DATAENCRYPT, ISOUtils.hex2byte(WORKINGKEY_DATA_TRACK), ISOUtils.hex2byte("72400D5A"));
			controller.updateWorkingKey(WorkingKeyType.MAC, ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), ISOUtils.hex2byte("F13DC4A8"));
			System.out.println("������Կװ�سɹ�!");
		} catch (Exception ex) {
			appendInteractiveInfoAndShow("������Կװ��ʧ��!" + ex);
			AllUtils.stopProgressDialog();
		}
	}
	/**
	 * չʾ��ʾ��Ϣ
	 * @param string
	 */
	private void appendInteractiveInfoAndShow(String string) {
		deviceInteraction = string;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AllUtils.toast(getBaseContext(), deviceInteraction);
			}
		});
	}
	private void btnStateToProcessing() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				processing = true;
			}
		});
	}
	private void btnStateInitFinished() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				processing  = false;
			}
		});
	}
	private void btnStateToWaitingInitFinished() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				processing = true;
			}
		});
	}
}
