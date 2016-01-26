package com.example.view;

import java.util.regex.PatternSyntaxException;

import com.example.cz.R;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
	/**
	 * 实名认证页面、填写用户姓名和身份证号
	 * @author jacyayj
	 *
	 */
public class Attest extends Activity {
	@ViewInject(R.id.attest_id)
	private EditText id = null;
	@ViewInject(R.id.attest_name)
	private EditText name = null;
	@ViewInject(R.id.attest_clear1)
	private ImageView clear1 = null;
	@ViewInject(R.id.attest_clear2)
	private ImageView clear2 = null;
	@ViewInject(R.id.attest_drawer_layout)
	private DrawerLayout layout = null;
	@ViewInject(R.id.attest_drawer)
	private View draw = null;
	
	private String id_number = "";
	private String user_name = "";
	
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attest);
		ViewUtils.inject(this);
		clickEvent();
	}
	/**
	 * 页面点击事件
	 * @param v 触发事件的控件
	 */
	@OnClick({R.id.attest_back,R.id.attest_next,R.id.attest_konw,R.id.attest_clear1,R.id.attest_clear2})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.attest_back : finish();
			break;
		case R.id.attest_next : 
			id_number = id.getText().toString();
			user_name = name.getText().toString();
			if (id_number.length() == 15 || id_number.length() == 18) {
				if (!user_name.equals("")) {
								USER.USERNAME = user_name;
								USER.USERID = id_number;
								startActivity(new Intent().setClass(Attest.this, CameraActivity.class));
				}else { 
					Toast.makeText(Attest.this, "请输入正确的姓名"+user_name, Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(Attest.this, "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.attest_konw : layout.openDrawer(draw);
			break;
		case R.id.attest_clear1 : id.setText("");
			break;
		case R.id.attest_clear2 : name.setText("");
			break;
		default:
			break;
		}
	}
	/**
	 * 监控两个输入框的内容以控制清除按钮的显示
	 */
	private void clickEvent() {
		id.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				id_number = stringFilter(s.toString()); 
		        if(!s.toString().equals(id_number)){ 
		        	id.setText(id_number); 
		        	id.setSelection(id_number.length()); 
		        } 
		        if(s.length()>0){
		        	clear1.setVisibility(View.VISIBLE);
		        }else {
		        	clear1.setVisibility(View.INVISIBLE);
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
		name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				user_name = AllUtils.stringFilter(s.toString()); 
		        if(!s.toString().equals(user_name)){ 
		        	name.setText(user_name); 
		        	name.setSelection(user_name.length()); 
		        }
		        if(user_name.length()>0){
		        	clear2.setVisibility(View.VISIBLE);
		        }else {
		        	clear2.setVisibility(View.INVISIBLE);
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
	}
	/**
	 * 利用正则控制输入内容
	 * @param str 需要匹配的字符串
	 * @return	符合正则表达式的字符串
	 * @throws PatternSyntaxException 
	 */
    public String stringFilter(String str)throws PatternSyntaxException{      
	     // 只允许字母和数字        
	     String regEx  =  "^[\\d+x+X]{1,18}$";
	     if (str.matches(regEx)) {
			return str;
		}
	     if (str.length()!=0) {
	    	 return str.substring(0, str.length()-1);
		}
	     return str.substring(0, str.length());
	 }
}
