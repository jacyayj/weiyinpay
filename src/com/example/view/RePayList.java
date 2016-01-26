package com.example.view;

import com.example.cz.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
	/**
	 * 新用卡还款二级页面
	 * @author jacyayj
	 */
public class RePayList extends Activity {
	@ViewInject(R.id.repay_list1_bank)
	private TextView bank = null;
	@ViewInject(R.id.repay_list1_number)
	private TextView number = null;
	@ViewInject(R.id.repay_list1_image)
	private ImageView bankimage = null;
	@ViewInject(R.id.repay_list1_ts)
	private TextView ts = null;
	@ViewInject(R.id.repay_time1)
	private CheckBox box1 = null;
	@ViewInject(R.id.repay_time2)
	private CheckBox box2 = null;
	@ViewInject(R.id.repay_list1_money)
	private EditText money = null;

	private Dialog dialog = null;
	private Window dialogWindow = null;
	private Button ok = null;
	private Button cencle = null;
//	private EditText pwd = null;
	private TextView money_tv = null;
	private TextView title = null;

	private Dialog dialog2 = null;
	private Window dialogWindow2 = null;
	private EditText card = null;
	private Button card_ok = null;
	private Button card_cencle = null;
	private ImageView ds[] = null;
	/**
	 * 初始化页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repay_list1);
		ViewUtils.inject(this);
		init();
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.repay_list1_back,R.id.repay_list1_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.repay_list1_back : finish();
			break;
		case R.id.repay_list1_ok : inPutCard();
			break;
		default:
			break;
		}
	}
	private void init(){
		box1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					box2.setChecked(false);
					ts.setText("2小时内到账，22:00后次日到账");
				}
			}
		});
		box2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					box1.setChecked(false);
					ts.setText("1-3个工作日内到账，22:00后次日到账");
				}
			}
		});
	};
	@SuppressWarnings("deprecation")
	private void inPut() {
		dialog = new Dialog(RePayList.this,R.style.mydialog2);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.blance_paypaw);
		dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // 宽度
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.35); // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
		ok = (Button)dialogWindow.findViewById(R.id.blance_pwd_ok);
		cencle = (Button)dialogWindow.findViewById(R.id.blance_pwd_cencle);
//		pwd = (EditText)dialogWindow.findViewById(R.id.blance_pwd_pwd);
		title = (TextView) dialogWindow.findViewById(R.id.pwd_title);
		money_tv = (TextView)dialogWindow.findViewById(R.id.blance_pwd_money);
		money_tv.setText("￥"+money.getText()+"元");
		title.setText("还款金额");
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				dialog.cancel();
			}
		});
		cencle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				dialog.cancel();
			}
		});
	}
	@SuppressWarnings("deprecation")
	private void inPutCard() {
		dialog2 = new Dialog(RePayList.this,R.style.mydialog2);
		dialog2.setCancelable(false);
		dialog2.setContentView(R.layout.inputcard);
		dialogWindow2 = dialog2.getWindow();
		WindowManager.LayoutParams lp = dialogWindow2.getAttributes();
		dialogWindow2.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow2.getWindowManager().getDefaultDisplay().getWidth()*0.85); // 宽度
		lp.height =  (int) (dialogWindow2.getWindowManager().getDefaultDisplay().getHeight()*0.35); // 高度
		dialogWindow2.setAttributes(lp);
		dialog2.show();
		card = (EditText) dialogWindow2.findViewById(R.id.card_card);
		card_cencle = (Button) dialogWindow2.findViewById(R.id.card_cencle);
		card_ok = (Button) dialogWindow2.findViewById(R.id.card_ok);
		ds = new ImageView[6];
		for (int i = 0; i < ds.length; i++) {
			ds[i] = (ImageView) dialogWindow2.findViewById(R.id.card_1+i);
		}
		card_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog2.dismiss();
				dialog2.cancel();
				inPut();
			}
		});
		card_cencle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog2.dismiss();
				dialog2.cancel();
			}
		});
		card.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				switch (s.length()) {
				case 0:	
				ds[0].setVisibility(View.INVISIBLE);
				ds[1].setVisibility(View.INVISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 1:	
				ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.INVISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);		
				break;
				case 2:	
				ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 3:	
				ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 4:	
				ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 5:	
				ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.VISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 6:	
				ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.VISIBLE);
				ds[5].setVisibility(View.VISIBLE);
				break;
				default:
					break;
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
}
