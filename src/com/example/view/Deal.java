package com.example.view;

import com.example.cz.R;
import com.example.untils.LocalDOM;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Э��ҳ��
 * @author jacyayj
 *
 */
public class Deal extends Activity {
	@ViewInject(R.id.deal_content)
	private WebView deal = null;
	@ViewInject(R.id.deal_img)
	private ImageView dealimg = null;
	@ViewInject(R.id.deal_name)
	private TextView title = null;
	/**
	 * ��ʼ��ҳ��;
	 * ���ݲ����ж�չʾ���û�����Э��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deal);
		ViewUtils.inject(this);
		if ("fist".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
			deal.setVisibility(View.VISIBLE);
			deal.loadUrl("file:///android_asset/payAgreement.html"); 
		}else if ("reg".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
			title.setText("΢��ע��Э��");
			deal.setVisibility(View.VISIBLE);
			deal.loadUrl("file:///android_asset/agreement.html");
		}else if ("photo".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
			title.setText("����ָ��");
			deal.setVisibility(View.VISIBLE);
			deal.loadUrl("file:///android_asset/photo/photograph.html");
		}else if ("upgrade".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
			dealimg.setVisibility(View.VISIBLE);
			title.setText("���ÿ���֤����������");
			dealimg.setBackgroundResource(R.drawable.upgrade_deal);
		}
	}
	/**
	 * ҳ�����¼�
	 * @param v	�����¼��Ŀؼ�
	 */
	@OnClick({R.id.deal_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.deal_back : finish();
			
			break;

		default:
			break;
		}
	}
}
