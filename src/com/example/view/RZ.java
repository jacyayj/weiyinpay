package com.example.view;

import com.example.cz.R;
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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
	/**
	 * 认证状态页面
	 * @author jacyayj
	 *
	 */
public class RZ extends Activity {
	@ViewInject(R.id.rz_name)
	private TextView name = null;
	@ViewInject(R.id.rz_id)
	private TextView id = null;
	@ViewInject(R.id.rz_state)
	private TextView state = null;
	@ViewInject(R.id.rz_ok)
	private Button ok = null;
	@ViewInject(R.id.rz_state_lible)
	private TextView statelable = null;
	@ViewInject(R.id.rz_state_img)
	private ImageView stateimg = null;
	@ViewInject(R.id.rz_fail_reason)
	private TextView fail = null;
	@ViewInject(R.id.rz_done_on)
	private View normal = null;
	@ViewInject(R.id.vip_grade)
	private TextView vip_grade = null;
	
	private Intent intent = null;
	private String METHOD = "promote_getLevel.shtml";
	/**
	 * 页面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rz);
		ViewUtils.inject(this);
		init();
		isVIP();
	}
	/**
	 * 根据参数展示用户等级，认证状态
	 * 
	 */
	private void init() {
		name.setText(USER.USERNAME);
		id.setText(USER.USERID);
		if ("3".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			ok.setVisibility(View.INVISIBLE);
			statelable.setText("已认证");
			statelable.setTextColor(Color.rgb(48, 208,87));
			stateimg.setImageResource(R.drawable.rz_done);
			normal.setVisibility(View.VISIBLE);
			fail.setVisibility(View.INVISIBLE);
		}else if ("2".equals(LocalDOM.getinstance().read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
			statelable.setText("认证中");
			statelable.setTextColor(Color.rgb(255, 128,0));
			stateimg.setImageResource(R.drawable.rz_on);
			normal.setVisibility(View.INVISIBLE);
			fail.setVisibility(View.INVISIBLE);
		}else {
			statelable.setText("认证失败");
			statelable.setTextColor(Color.rgb(255,0,0));
			stateimg.setImageResource(R.drawable.rz_fail);
			normal.setVisibility(View.INVISIBLE);
			fail.setVisibility(View.VISIBLE);
			fail.setText("认证失败");
		}
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.to_upgrade,R.id.rz_ok,R.id.rz_back,R.id.rz_plan,R.id.grade_rule})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.to_upgrade : startActivity(intent);
			break;
		case R.id.rz_ok : startActivity(new Intent().setClass(RZ.this,Attest.class));
		break;
		case R.id.rz_back : finish();
		break;
		case R.id.rz_plan : startActivity(new Intent(RZ.this,AttestDone.class));
		break;
		case R.id.grade_rule : startActivity(new Intent(RZ.this,GradeRule.class));
		break;
		default:
			break;
		}
	}
	/**
	 * 获取用户等级
	 */
	private void isVIP() {
		intent = new Intent(getBaseContext(),UpGrade.class);
		String timestamp = System.currentTimeMillis()+AllUtils.getRandom();
		RequestParams params = new RequestParams();
		params.addBodyParameter("myToken", USER.USERTOKEN);
		params.addBodyParameter("timestamp",timestamp );
		params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timestamp+AllUtils.readPrivateKeyFile(this)));
		new HttpUtils().send(HttpMethod.POST,getResources().getString(R.string.url)+METHOD,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(RZ.this,"加载中");
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
				String result = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
				if ("0".equals(result)) {
					intent.putExtra("grade","0");
				}else if ("1001".equals(result)) {
					AllUtils.toast(getBaseContext(), "登录状态出错请重新登录");
				}else if ("1002".equals(result)) {
					AllUtils.toast(getBaseContext(), "客户端错误");
				}else if("1".equals(result)){
					vip_grade.setText("");
					vip_grade.setBackgroundResource(R.drawable.level1);
					intent.putExtra("grade","1");
				}else if("2".equals(result)){
					vip_grade.setText("");
					vip_grade.setBackgroundResource(R.drawable.level2);
					intent.putExtra("grade","1");
				}else if("3".equals(result)){
					vip_grade.setText("");
					vip_grade.setBackgroundResource(R.drawable.level3);
					intent.putExtra("grade","1");
				}else if("4".equals(result)){
					vip_grade.setText("");
					vip_grade.setBackgroundResource(R.drawable.level4);
					intent.putExtra("grade","1");
				}else if("5".equals(result)){
					vip_grade.setText("");
					vip_grade.setBackgroundResource(R.drawable.level5);
					intent.putExtra("grade","1");
				}else {
					AllUtils.toast(getBaseContext(), arg0.result);
					System.out.println(arg0.result);
				}
			}
		});
	}
}
