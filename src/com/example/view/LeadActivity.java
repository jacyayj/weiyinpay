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
	 * 启动页，每次启动APP时进入该页面
	 * @author jacyayj
	 *
	 */
public class LeadActivity  extends Activity{
	 	boolean isFirstIn = false;

	    private static final int GO_HOME = 1000;
	    private static final int GO_GUIDE = 1001;
	    // 延迟3秒
	    private static final long SPLASH_DELAY_MILLIS = 3000;
	    private static final String SHAREDPREFERENCES_NAME = "first_pref";
	    /**
	     * Handler:跳转到不同界面
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
	     * 初始化页面
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
	        // 读取SharedPreferences中需要的数据
	        // 使用SharedPreferences来记录程序的使用次数
	        SharedPreferences preferences = getSharedPreferences(
	                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

	        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
	        isFirstIn = preferences.getBoolean("isFirstIn", true);

	        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
	        if (!isFirstIn) {
	            // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
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
			//  极光推送	
			JPushInterface.setDebugMode(true);
			JPushInterface.init(LeadActivity.this);
		}
	    @Override
		protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    }
	} 
       

	

	
		
	
	
	

