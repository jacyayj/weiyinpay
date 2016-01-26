package com.example.view;

import java.util.List;

import com.example.cz.R;
import com.example.modle.T;
import com.example.modle.TradeModle;
import com.example.modle.USER;
import com.example.untils.AllUtils;
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
	/**
	 * ���������
	 * @author jacyayj
	 *
	 */
public class BlanceActivity  extends Activity{
	
	private final String METHOD1 = "accountMerchantAction_getFuseAmount.shtml";
	private final String METHOD2 = "Cash_appCash.shtml";
	
	@ViewInject(R.id.blance_yue)
	private  TextView blance_yue = null;
	@ViewInject(R.id.blance_money)
	private  EditText blance_money = null;
	@ViewInject(R.id.blance_bank)
	private TextView bank = null;
	@ViewInject(R.id.blance_number)
	private TextView number = null;
	@ViewInject(R.id.type)
	private Spinner spinner = null;
	@ViewInject(R.id.blance_jine)
	private TextView te = null;
	@ViewInject(R.id.blance_image)
	private ImageView bankimage = null;
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	@ResInject(id = R.array.bankarray,type = ResType.StringArray)
	private String banks[] = null;
	
	private String content = null;
	
	private Dialog dialog = null;
	private Window dialogWindow = null;
	private Button ok = null;
	private Button cencle = null;
	
	private EditText pwd = null;
	private TextView money_tv = null;
	
	private String money = "0";
	private String ftype = null;
	
	private double canusemoney = 0;
	private String timetemp = null;
	
	private T t = null;
	private TradeModle modle = null;
	private final int BANKIMG[] = new int[]{R.drawable.chengduban,R.drawable.gongsahng,R.drawable.faxia,R.drawable.guangda,
			R.drawable.guangdingfazhan,R.drawable.jianshe,R.drawable.jiaotong,R.drawable.mingshen,
			R.drawable.nonghang,R.drawable.nongcunxinyongshe,R.drawable.pinan,R.drawable.shanghaipudong,
			R.drawable.shanghaiyh,R.drawable.shenzhenfazhan,R.drawable.zhaoshang,R.drawable.zhongguoyh,
			R.drawable.zhongguoyouzhen,R.drawable.zhongxin,R.drawable.xinye};
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.blance);
    	ViewUtils.inject(this);
    	t = T.getInstance();
    	modle = TradeModle.getInstance();
    	if(blance_money.toString().length()>0){
    		blance_money.setTextColor(0xff000000);
    	}
    	spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.drop_list_ys, new String[]{"���յ���","���յ���"}));
    	for (int i = 0; i < banks.length; i++) {
			if (banks[i].equals(t.getBankname())) {
				bankimage.setImageResource(BANKIMG[i]);
			}
		}
    	loadData();
    }
	/**
	 * ҳ�����¼�
	 * @param v �����¼��Ŀؼ�
	 */
	@OnClick({R.id.blance_back,R.id.blance_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.blance_back : finish();
			break;
		case R.id.blance_ok : 
			money = blance_money.getText().toString();
			if (!("0".equals(money) ||money .equals(""))) {
				if (Double.parseDouble(money) > canusemoney) {
					Toast.makeText(getBaseContext(), "��������", Toast.LENGTH_SHORT).show();
				}else {
					if(!money .equals("") && Double.parseDouble(money) >=100){
						pwd();
					}else{
						Toast.makeText(getBaseContext(), "����С��100", Toast.LENGTH_SHORT).show();
					}
				}
			}else {
				Toast.makeText(getBaseContext(), "����Ϊ0", Toast.LENGTH_SHORT).show();
			}
		default:
			break;
		}
	}
	/**
	 * spinner��item����¼�����
	 * @param arg0
	 * @param arg1	�������item
	 * @param arg2	�����item������
	 * @param arg3	�����item��id
	 */
	@OnItemSelected(R.id.type)
	private void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
		if (arg2 == 0) {
			ftype = "0";
			te.setText("������������2��Ԫ");
		}
		if (arg2 == 1) {
			ftype = "1";
			te.setText("������������20��Ԫ");
		}
	}
	/**
	 * �������������dialog����ʼ����
	 */
	@SuppressWarnings("deprecation")
	private void pwd() {
		dialog = new Dialog(BlanceActivity.this,R.style.mydialog2);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.blance_paypaw);
        dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // ���
        lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.35); // �߶�
        dialogWindow.setAttributes(lp);
        dialog.show();
        ok = (Button)dialogWindow.findViewById(R.id.blance_pwd_ok);
        cencle = (Button)dialogWindow.findViewById(R.id.blance_pwd_cencle);
        pwd = (EditText)dialogWindow.findViewById(R.id.blance_pwd_pwd);
        money_tv = (TextView)dialogWindow.findViewById(R.id.blance_pwd_money);
        money_tv.setText("��"+money+".0Ԫ");
        cencle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();	
				HttpUtils httpUtils = new HttpUtils(15000);
                RequestParams params = new RequestParams();
                params.addBodyParameter("myToken", USER.USERTOKEN);
                params.addBodyParameter("bankId",t.getId());
                params.addBodyParameter("famount",money);
                modle.setPrice(money);
                params.addBodyParameter("ftype",ftype);
                params.addBodyParameter("pwd",pwd.getText().toString());
                params.addBodyParameter("timestamp",timetemp);
    			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+t.getId()+money+ftype+pwd.getText().toString()+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
                httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD2,params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(BlanceActivity.this, "�ύ��");
					};
                	@Override
					public void onFailure(HttpException arg0, String arg1) {
                		AllUtils.stopProgressDialog();
                		AllUtils.toast(getBaseContext(), arg1);
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						content = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
						if ("1001".equals(content)) {
							Toast.makeText(getBaseContext(), "���ֽ���Ϊ��", Toast.LENGTH_SHORT).show();
						}else if ("1002".equals(content)) {
							Toast.makeText(getBaseContext(), "�������Ͳ���Ϊ��", Toast.LENGTH_SHORT).show();
						}else if ("1003".equals(content)) {
							Toast.makeText(getBaseContext(), "�����˺Ų���Ϊ��", Toast.LENGTH_SHORT).show();
						}else if ("1004".equals(content)) {
							Toast.makeText(getBaseContext(), "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
						}else if ("1005".equals(content)) {
							Toast.makeText(getBaseContext(), "��������ȷ", Toast.LENGTH_SHORT).show();
						}else if ("1006".equals(content)) {
							Toast.makeText(getBaseContext(), "��¼״̬���������µ�¼", Toast.LENGTH_SHORT).show();
						}else if ("1007".equals(content)) {
							Toast.makeText(getBaseContext(), "�����˺����δͨ��", Toast.LENGTH_SHORT).show();
						}else if ("1008".equals(content)) {
							Toast.makeText(getBaseContext(), "������", Toast.LENGTH_SHORT).show();
						}else if ("1009".equals(content)) {
							Toast.makeText(getBaseContext(), "�������ֲ��ܴ�������", Toast.LENGTH_SHORT).show();
						}else if ("1010".equals(content)) {
							Toast.makeText(getBaseContext(), "�������", Toast.LENGTH_SHORT).show();
						}else if ("1011".equals(content)) {
							Toast.makeText(getBaseContext(), "���ֽ��ܴ����˻������ֽ��", Toast.LENGTH_SHORT).show();
						}else if ("1012".equals(content)) {
							Toast.makeText(getBaseContext(), "T+1���������", Toast.LENGTH_SHORT).show();
							startActivity(new Intent(getApplicationContext(), DrawMoneyDone.class));
							dialogWindow = null;
							dialog = null;
							finish();
						}else if ("1013".equals(content)) {
							Toast.makeText(getBaseContext(), "���ֽ��ܴ����˻������ֽ��", Toast.LENGTH_SHORT).show();
						}else if ("1014".equals(content)) {
							Toast.makeText(getBaseContext(), "T+0ʧ��", Toast.LENGTH_SHORT).show();
							startActivity(new Intent(getApplicationContext(), DrawMoneyDone.class));
							dialogWindow = null;
							dialog = null;
							finish();
						}else if ("1015".equals(content)) {
							Toast.makeText(getBaseContext(), "T+0�ɹ�", Toast.LENGTH_SHORT).show();
							startActivity(new Intent(getApplicationContext(), DrawMoneyDone.class));
							dialogWindow = null;
							dialog = null;
							finish();
						}else if ("1016".equals(content)) {
							Toast.makeText(getBaseContext(), "���ֽ����ڵ�����ý��", Toast.LENGTH_SHORT).show();
						}else if ("1017".equals(content)) {
							Toast.makeText(getBaseContext(), "�����ۼ������ܶ�ﵽ��������ֵ�ˣ�������������", Toast.LENGTH_SHORT).show();
						}else if ("1018".equals(content)) {
							Toast.makeText(getBaseContext(), "�ۼ���������ֵ������ˣ���ʱ��������", Toast.LENGTH_SHORT).show();
						}else if ("1019".equals(content)) {
							Toast.makeText(getBaseContext(), "����ϵͳ�ۼ������ܺʹﵽ���ͣ�������������", Toast.LENGTH_SHORT).show();
						}else if ("1020".equals(content)) {
							Toast.makeText(getBaseContext(), "���п�����ȷ", Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(getBaseContext(), arg0.result, Toast.LENGTH_SHORT).show();
						}
					}
				} );
			}
		});
	}
	/**
	 * �ӷ��������ص�ǰ�˻���չʾ���û�
	 */
	private void loadData() {
			timetemp = System.currentTimeMillis()+AllUtils.getRandom();
	    	bank.setText(t.getBankname());
	    	number.setText("β��"+t.getCard().substring(t.getCard().length()-4)+"���");
	    	HttpUtils httpUtils = new HttpUtils(15000);
            RequestParams params = new RequestParams();
            params.addBodyParameter("myToken", USER.USERTOKEN);
            params.addBodyParameter("timestamp",timetemp);
			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
            httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD1,params,new RequestCallBack<String>() {
				@Override
				public void onStart() {
					AllUtils.startProgressDialog(BlanceActivity.this, "������");
				};
            	@Override
				public void onFailure(HttpException arg0, String arg1) {
            		AllUtils.stopProgressDialog();
            		AllUtils.toast(getBaseContext(), arg1);
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					AllUtils.stopProgressDialog();
					List<String> temp = AllUtils.getJson(arg0.result, new String[]{"resultCode","fuseAmount"});
					System.out.println(arg0.result);
					if ("1000".equals(temp.get(0))) {
						try {
							canusemoney = Double.parseDouble(temp.get(1))/100;
							blance_yue.setText(String.valueOf(canusemoney));	
						} catch (Exception e) {
						}
					}else if ("1001".equals(temp.get(0))) {
						AllUtils.toast(getBaseContext(), "δ��֤");
					}else if ("1002".equals(temp.get(0))) {
						AllUtils.toast(getBaseContext(), "��������ȷ");
					}else if ("1005".equals(temp.get(0))) {
						AllUtils.toast(getBaseContext(), "��½״̬���������µ�½");
					}else {
						AllUtils.toast(getBaseContext(),arg0.result);
					}
				}
			} );
		}
}
