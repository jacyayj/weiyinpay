package com.example.view;

import com.example.cz.R;
import com.example.modle.T;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
	/**
	 * ȷ�ϰ�ҳ�桢���û�չʾ��ǰ�󶨵��˻���Ϣ
	 * @author jacyayj
	 *
	 */
public class AffirmBind extends Activity {
	
	private String METHOD="bankAcount_addBankAcount.shtml";
	
	@ViewInject(R.id.bind_phone)
	private TextView phone = null;
	@ViewInject(R.id.bind_bank)
	private TextView bank = null;
	@ViewInject(R.id.bind_name)
	private TextView name = null;
	@ViewInject(R.id.bind_number)
	private TextView number = null;
	@ViewInject(R.id.bindok_img)
	private ImageView bankimage = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	@ResInject(id = R.array.bankarray,type = ResType.StringArray)
	private String banks[] = null;
	
	private String content = null;
	private String timetemp = null;
	
	private T t = null;
	private final int BANKIMG[] = new int[]{R.drawable.chengduban,R.drawable.gongsahng,R.drawable.faxia,R.drawable.guangda,
			R.drawable.guangdingfazhan,R.drawable.jianshe,R.drawable.jiaotong,R.drawable.mingshen,
			R.drawable.nonghang,R.drawable.nongcunxinyongshe,R.drawable.pinan,R.drawable.shanghaipudong,
			R.drawable.shanghaiyh,R.drawable.shenzhenfazhan,R.drawable.zhaoshang,R.drawable.zhongguoyh,
			R.drawable.zhongguoyouzhen,R.drawable.zhongxin,R.drawable.xinye};
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.affirm_bind);
		ViewUtils.inject(this);
		t = T.getInstance();
		bank.setText(t.getBankname());
		for (int i = 0; i < banks.length; i++) {
			if (bank.getText().toString().equals(banks[i])) {
				bankimage.setImageResource(BANKIMG[i]);
			}
		}
		name.setText(t.getName());
		number.setText(t.getCard());
		phone.setText(USER.USERPHONE);
	}
	/**
	 * �������¼�
	 * @param v �����¼��Ŀؼ�
	 */
	@OnClick({R.id.bind_back,R.id.bind_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.bind_back : finish();
			break;
		case R.id.bind_ok : send();
			break;
		default:
			break;
		}
	}
	/**
	 * ������������û���Ϣ
	 */
	private void send() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		RequestParams params = new RequestParams();
		   params.addBodyParameter("myToken", USER.USERTOKEN);
		   params.addBodyParameter("bankName", t.getId());
		   params.addBodyParameter("bankBranchName", t.getZ());
		   params.addBodyParameter("province",t.getP());
		   params.addBodyParameter("market",t.getW());
		   params.addBodyParameter("username", t.getName());
		   params.addBodyParameter("fbankNo", "1");
		   params.addBodyParameter("bankNumber",t.getCard());
		   params.addBodyParameter("timestamp",timetemp);
		   String temp =t.getZ()+t.getId()+t.getCard()+"1"+t.getW()+t.getP()+t.getName();
		   System.out.println(temp);
		   params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+temp+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
		   HttpUtils http = new HttpUtils(15000);
		   http.send(HttpRequest.HttpMethod.POST,
		       URL+METHOD,
		       params,
		       new RequestCallBack<String>() {
				@Override
				public void onStart() {
					AllUtils.startProgressDialog(AffirmBind.this, "������");
		           }
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					AllUtils.stopProgressDialog();
					System.out.println("���"+responseInfo.result);
					content = AllUtils.getJson(responseInfo.result, new String[]{"resultCode"}).get(0);
					if ("2".equals(content)) {
							Toast.makeText(getBaseContext(), "��ӳɹ�", Toast.LENGTH_SHORT).show();
							startActivity(new Intent(AffirmBind.this, Addbank.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
							finish();
					}else if ("1".equals(content)) {
						Toast.makeText(getBaseContext(), "��½״̬���������µ�¼", Toast.LENGTH_SHORT).show();
					}else if ("3".equals(content)){
						Toast.makeText(getBaseContext(), "����ʧ��", Toast.LENGTH_SHORT).show();
					}else if ("4".equals(content)){
						Toast.makeText(getBaseContext(), "���ֲ���Ϊ��", Toast.LENGTH_SHORT).show();
					}else if ("5".equals(content)){
						Toast.makeText(getBaseContext(), "��ѡ�����", Toast.LENGTH_SHORT).show();
					}else if ("6".equals(content)){
						Toast.makeText(getBaseContext(), "ʡ��ѡ�����", Toast.LENGTH_SHORT).show();
					}else if ("7".equals(content)){
						Toast.makeText(getBaseContext(), "֧�в���Ϊ��", Toast.LENGTH_SHORT).show();
					}else if ("8".equals(content)){
						Toast.makeText(getBaseContext(), "�����в���Ϊ��", Toast.LENGTH_SHORT).show();
					}else if ("9".equals(content)) {
						Toast.makeText(getBaseContext(), "���п��ѱ����", Toast.LENGTH_SHORT).show();
					}else if ("10".equals(content)) {
						Toast.makeText(getBaseContext(), "���Ų�����", Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getBaseContext(), "����"+content, Toast.LENGTH_SHORT).show();
					}
		           }
				@Override
				public void onFailure(HttpException error, String msg) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), msg);
		           }
		   });
	}
}
