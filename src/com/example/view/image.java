package com.example.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import com.example.cz.R;
import com.example.untils.LocalDOM;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class image  extends Activity{
	
	private Bitmap bitmap = null;
	private ImageView im = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.user_need);
	LinearLayout l4=(LinearLayout) findViewById(R.id.user_lin4);
	final LinearLayout l41=(LinearLayout) findViewById(R.id.user_lin41);
	l4.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		l41.setVisibility(View.VISIBLE);
		}
	});
	
	LinearLayout li=(LinearLayout) findViewById(R.id.user_lin7);
	final LinearLayout li1=(LinearLayout) findViewById(R.id.user_lin71);
	li.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			li1.setVisibility(View.VISIBLE);
		}
	});
}
	@Override
	@SuppressLint("SdCardPath")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if(requestCode==11&&resultCode==Activity.RESULT_OK){
	      String sdStatus = Environment.getExternalStorageState();
          if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
              Log.v("TestFile","SD card is not avaiable/writeable right now.");
              return;
          }
          new DateFormat();
          String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg"; 
          Bundle bundle = data.getExtras();
          Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
          FileOutputStream b = null;
          File file = new File("/sdcard/myImage/");
          file.mkdirs();// 创建文件夹
          String fileName = "/sdcard/myImage/"+name;
          try {
              b = new FileOutputStream(fileName);
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          } finally {
              try {
                  b.flush();
                  b.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
	}}
	@Override
	public void startActivityForResult(Intent data, int requestCode) {
		super.startActivityForResult(data, requestCode);
		if(requestCode==1){
       im.setImageBitmap(bitmap);// 将图片显示在ImageView里
		}
		if(requestCode==21){
			ContentResolver resolver = getContentResolver();
			Uri c =  data.getData();
			try {
				Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, c);
				String[] proj = {MediaColumns.DATA};
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(c, proj, null, null, null); 
				cursor.moveToFirst();
				im.setImageBitmap(bm);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}   
		}
}
	@SuppressLint("InflateParams")
	public void SetImageview(){
		getLayoutInflater();
		 View view=LayoutInflater.from(image.this).inflate(R.layout.amendiamge, null);
	  	 final AlertDialog  b = new   AlertDialog.Builder(image.this).create();  
	  	 Window window=b.getWindow();
	  	 b.show(); 
	  	 window.setContentView(view);
	  	 window.setGravity(Gravity.BOTTOM);
	  	Button bt_camera= (Button) view.findViewById(R.id.choose_camer);
	  	bt_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        	startActivityForResult(in, 11);
			b.dismiss();	
			}
		});
	  	Button bt_photo= (Button) view.findViewById(R.id.choose_photo);
	  	bt_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				 intent.addCategory(Intent.CATEGORY_OPENABLE);
				    intent.setType("image/*");
				startActivityForResult(intent, 21);
				b.dismiss();
			}
		});
	  	Button bt_cancle= (Button) view.findViewById(R.id.choose_ca);
	  	bt_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				b.dismiss();
			}
		});
	  	 
	}  
		public void creatFile(){
			   new DateFormat();
		   String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";  
	       FileOutputStream b = null;
	       File file = new File("/DCIM/myImage/");
	       file.mkdirs();// 创建文件夹
	       String fileName = "/DCIM/myImage/"+name;
	         try {
	             b = new FileOutputStream(fileName);
	             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
	         } catch (FileNotFoundException e) {
	             e.printStackTrace();
	         } finally {
	             try {
	                 b.flush();
	                 b.close();
	             } catch (IOException e) {
	                 e.printStackTrace();
	             }
	         }
	      LocalDOM.getinstance().write(image.this, "touxiang", "amendimage", fileName);
	     }
}
