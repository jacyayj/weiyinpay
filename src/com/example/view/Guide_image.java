package com.example.view;

import com.example.cz.MainActivity;
import com.example.cz.R;
import com.example.untils.LocalDOM;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 引导页，用户第一次使用本APP是进入该页面
 * @author jacyayj
 *
 */
public class Guide_image extends Activity implements OnPageChangeListener{
	
	final View[]view=new View[3];
	private int[]im=new int[]{R.drawable.dian,R.drawable.dian_2,R.drawable.dian_3};
	private ImageView imageView;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	/**
	 * 初始化页面，控制页面进行自动滚动和滑动
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.yidao_viewpager);
	ViewPager pager=(ViewPager) findViewById(R.id.yindao_viewpager);
	imageView=(ImageView) findViewById(R.id.yidao_viewpager_image);
	view[0]=LayoutInflater.from(this).inflate(R.layout.yidao1, null);
	view[1]=LayoutInflater.from(this).inflate(R.layout.yindao2, null);
	view[2]=LayoutInflater.from(this).inflate(R.layout.yindao3, null);
	ImageView ig=(ImageView) view[2].findViewById(R.id.yindao_last);
	ig.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			LocalDOM.getinstance().write(Guide_image.this, "cnt", "ct1", "3");
			Intent in=new Intent(Guide_image.this,MainActivity.class);
			startActivity(in);
		}
	});
	pager.setAdapter(new PagerAdapter() {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public int getCount() {
			return view.length;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(view[position]);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(view[position]);
			return view[position];
		}
	});
	pager.setOnPageChangeListener(this);
	setGuided();
}
@Override
public void onPageScrollStateChanged(int arg0) {
	imageView.setBackgroundResource(im[arg0]);
}
@Override
public void onPageScrolled(int arg0, float arg1, int arg2) {
}
@Override
public void onPageSelected(int arg0) {
	//imageView.setBackgroundDrawable(getResources().getDrawable(im[arg0]));
	//imageView.setBackground(getResources().getDrawable(im[arg0]));
//	imageView.setBackgroundResource(im[arg0]);
//	imageView.setBackgroundColor(im[arg0]);
//	((ViewPager) imageView).setCurrentItem(im[arg0]);
	if(arg0==0){
		imageView.setImageResource(im[0]);
	}
	if(arg0==1){
		imageView.setImageResource(im[1]);
	}
	if(arg0==2){
		imageView.setImageResource(im[2]);
	}
	
}	
	
private void setGuided() {
    SharedPreferences preferences = getSharedPreferences(
            SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
    Editor editor = preferences.edit();
    // 存入数据
    editor.putBoolean("isFirstIn", false);
    // 提交修改
    editor.commit();
}
	
}
