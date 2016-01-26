package com.example.view;

import cn.jpush.android.api.JPushInterface;

import com.example.cz.MainActivity;
import com.example.cz.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
	/**
	 * ����ҳ��ÿ������APPʱ�����ҳ��
	 * @author jacyayj
	 *
	 */
public class LeadActivity  extends Activity{
	 	boolean isFirstIn = false;

	    private static final int GO_HOME = 1000;
	    private static final int GO_GUIDE = 1001;
	    // �ӳ�3��
	    private static final long SPLASH_DELAY_MILLIS = 3000;
	    private static final String SHAREDPREFERENCES_NAME = "first_pref";
	    /**
	     * Handler:��ת����ͬ����
	     */
	 @SuppressLint("HandlerLeak")
	/*   private Handler mHandler = new Handler() {

	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case GO_HOME:
	                goHome();
	                break;
	            case GO_GUIDE:
	                //goGuide();
	                break;
	            }
	            super.handleMessage(msg);
	        }
	    };*/
	    Handler mHandler=new Handler(){
	    	@Override
			public void handleMessage(Message msg) {
	    		if(msg.what==122){
	    			goHome() ;
	    		}
	    	};
	    };
	    /**
	     * ��ʼ��ҳ��
	     */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.lead_photo);
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
			    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
			    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  
				}
	        mHandler.sendEmptyMessageDelayed(122, 3000);
	       // init();
			push();
	    }
	    @SuppressWarnings("unused")
		private void init() {
	        // ��ȡSharedPreferences����Ҫ������
	        // ʹ��SharedPreferences����¼�����ʹ�ô���
	        SharedPreferences preferences = getSharedPreferences(
	                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

	        // ȡ����Ӧ��ֵ�����û�и�ֵ��˵����δд�룬��true��ΪĬ��ֵ
	        isFirstIn = preferences.getBoolean("isFirstIn", true);

	        // �жϳ�����ڼ������У�����ǵ�һ����������ת���������棬������ת��������
	        if (!isFirstIn) {
	            // ʹ��Handler��postDelayed������3���ִ����ת��MainActivity
	            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
	        } else {
	            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
	        }

	    }

	    private void goHome() {
	        Intent intent = new Intent(LeadActivity.this, MainActivity.class);
	        startActivity(intent);
	        LeadActivity.this.finish();
	    }
	    @SuppressWarnings("unused")
		private void goGuide() {
	        Intent intent = new Intent(LeadActivity.this, Guide_image.class);
	        startActivity(intent);
	        LeadActivity.this.finish();
	    }
	    @Override
		protected void onResume() {
			super.onResume();
			JPushInterface.onResume(this);
		}
		@Override
		protected void onPause() {
			super.onPause();
			JPushInterface.onPause(this);
		}
		public void push(){
			//  ��������	
			JPushInterface.setDebugMode(true);
			JPushInterface.init(LeadActivity.this);
		}
	    @Override
		protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    }
	} 
       

	

	
		
	
	
	

