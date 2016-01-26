package com.example.view;

import java.util.List;

import com.example.cz.R;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
	/**
	 * ���ÿ���������ҳ��
	 * @author jacyayj
	 *
	 */
public class VisaAttest3 extends Activity {
	@ViewInject(R.id.visa_amount)
	private TextView amount = null;
	
	private Button cencle = null;
	private Button ok = null;
	
	private Dialog dialog = null;
	
	private String money = null;
	private String METHOD = "promote_promote.shtml";
	private final String METHOD1 = "accountMerchantAction_getFuseAmount.shtml";
	/**
	 * ҳ���ʼ��
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visa_attest3);
		ViewUtils.inject(this);
		load();
	}
	/**
	 * ����������ɫ
	 */
	private void setTextColor() {
		money = amount.getText().toString();
		SpannableStringBuilder builder = new SpannableStringBuilder(money);  
		//ForegroundColorSpan Ϊ����ǰ��ɫ��BackgroundColorSpanΪ���ֱ���ɫ  
		ForegroundColorSpan span = new ForegroundColorSpan(Color.GRAY);
		ForegroundColorSpan gspan = new ForegroundColorSpan(Color.GRAY); 
		builder.setSpan(gspan, 0,7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(span, money.length()-1,money.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		amount.setText(builder);
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.visa_attest3_back,R.id.visa_pay,R.id.amount_not})
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.visa_attest3_back : onBackPressed();;
			break;
		case R.id.visa_pay : pay();
		break;
		case R.id.amount_not : finish();
		break;
		default:
			break;
		}
	}
	/**
	 * �˻�����
	 */
	@SuppressWarnings("deprecation")
	private void amountNot() {
		if (dialog ==null) {
			dialog = new Dialog(VisaAttest3.this,R.style.mydialog2);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.amount_not_dialog);
			Window  dialogWindow = dialog.getWindow();
			cencle = (Button) dialogWindow.findViewById(R.id.visa_amount_cnecle);
			ok = (Button) dialogWindow.findViewById(R.id.visa_amount_ok);
			ok.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					startActivity(new Intent(VisaAttest3.this,Pay.class));
					finish();
				}
			});
			cencle.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
						dialog.dismiss();
				}
			});
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.CENTER);
			lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // ���
			lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.3); // �߶�
			dialogWindow.setAttributes(lp);
			dialog.show();
		}else {
			dialog.show();
		}
	}
	private void pay() {
    	String timetemp = System.currentTimeMillis()+AllUtils.getRandom();
    	RequestParams params = new RequestParams("UTF-8");
    	params.addBodyParameter("myToken", USER.USERTOKEN);
    	params.addBodyParameter("timestamp", timetemp);
    	params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(this)));
    	new  HttpUtils(15000).send(HttpMethod.POST,getResources().getString(R.string.url)+METHOD, params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(VisaAttest3.this,"������");
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
				String result = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
				if ("998".equals(result)) {
					AllUtils.toast(getBaseContext(), "����ʧ��");
				}else if("999".equals(result)){
					AllUtils.toast(getBaseContext(), "����ɹ�");
					onBackPressed();
				}else if ("1002".equals(result)) {
					AllUtils.toast(getBaseContext(), "��������ȷ");
				}else if ("1006".equals(result)) {
					amountNot();
				}else if ("1005".equals(result)) {
					AllUtils.toast(getBaseContext(), "��½״̬���������µ�¼");
				}else if ("1009".equals(result)) {
					AllUtils.toast(getBaseContext(), "������ߵȼ��������������ȼ�");
				}else {
					AllUtils.toast(getBaseContext(), arg0.result);
				}
				System.out.println(arg0.result);
			}
		});
	}
	private void load() {
		String timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		HttpUtils httpUtils = new HttpUtils(15000);
        RequestParams params = new RequestParams();
        params.addBodyParameter("myToken", USER.USERTOKEN);
        params.addBodyParameter("timestamp",timetemp);
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
        httpUtils.send(HttpRequest.HttpMethod.POST, getResources().getString(R.string.url)+METHOD1,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(VisaAttest3.this, "������");
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
						amount.setText("��ǰ�˻����Ϊ"+String.valueOf(Double.parseDouble(temp.get(1))/100)+"Ԫ");
						setTextColor();
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(),"������Ч����");
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
	@Override
	public void onBackPressed() {
		startActivity(new Intent(VisaAttest3.this,VisaAttest.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	}
}
