package com.example.view;

import com.example.cz.R;
import com.example.modle.USER;
import com.example.untils.AllUtils;
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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
	/**
	 * 申请代理页面、用户可以在这里申请成为代理
	 * @author jacyayj
	 */
public class Applayagent extends Activity {
	@ViewInject(R.id.propser_name)
	private EditText name = null;
	@ViewInject(R.id.propser_number)
	private EditText number = null;
	@ViewInject(R.id.propser_address)
	private EditText address = null;
	@ViewInject(R.id.propser_liuyan)
	private EditText liuyan = null;
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private final String METHOD = "applyAgentAction_applyAgent.shtml";
	private String timetemp = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applayagent);
		ViewUtils.inject(this);
	}
	/**
	 * 页面点击事件
	 * @param v 触发事件的控件
	 */
    @OnClick({R.id.propser_back,R.id.propser_tijiao})
    private void onClick(View v) {
    	switch (v.getId()) {
		case R.id.propser_tijiao : send();
			break;
		case R.id.propser_back : finish();
		default:
			break;
		}
	}
    /**
     * 获取用户输入的信息并发送给服务器
     */
	private void send() {
  		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
  		HttpUtils httpUtils = new HttpUtils(15000);
        RequestParams params = new RequestParams();
  		
        params.addBodyParameter("myToken",USER.USERTOKEN);
        params.addBodyParameter("appUser",name.getText().toString());
        params.addBodyParameter("phone",number.getText().toString());
        params.addBodyParameter("detailAddress",address.getText().toString());
        params.addBodyParameter("leaveWord",liuyan.getText().toString());
        params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+name.getText().toString()+address.getText().toString()+liuyan.getText().toString()+number.getText().toString()+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
        params.addBodyParameter("timestamp",timetemp);
       httpUtils.send(HttpMethod.POST, URL+METHOD, params,new RequestCallBack<String>() {
    	   @Override
    	public void onStart() {
    		AllUtils.startProgressDialog(Applayagent.this,"发送中");
    		super.onStart();
    	}
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			AllUtils.stopProgressDialog();AllUtils.toast(getBaseContext(), arg1);
		}
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			AllUtils.stopProgressDialog();
			String temp = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
			if ("1005".equals(temp)) {
				AllUtils.toast(getBaseContext(), "登录状态出错，请重新登陆");
			}else if ("1000".equals(temp)) {
				AllUtils.toast(getBaseContext(), "发送成功！");
			}else if ("1006".equals(temp)) {
				AllUtils.toast(getBaseContext(), "发送失败！");
			}else if ("1004".equals(temp)) {
				AllUtils.toast(getBaseContext(), "地址不能为空！");
			}else if ("1003".equals(temp)) {
				AllUtils.toast(getBaseContext(), "电话号码错误");
			}else if ("1002".equals(temp)) {
				AllUtils.toast(getBaseContext(), "电话号码不能为空！");
			}else if ("1001".equals(temp)) {
				AllUtils.toast(getBaseContext(), "申请人姓名不能为空！");
			}else {
				AllUtils.toast(getBaseContext(), arg0.result);
			}
		}
       });
	}
}
