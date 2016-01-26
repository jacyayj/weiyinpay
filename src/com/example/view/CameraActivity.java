package com.example.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.controll.ActionSheetDialog;
import com.example.controll.ActionSheetDialog.OnSheetItemClickListener;
import com.example.controll.ActionSheetDialog.SheetItemColor;
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
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * ʵ����֤���ս���
 * @author jacyayj
 *
 */
@SuppressLint({ "SdCardPath"})
public class CameraActivity  extends   Activity{
	private final String METHOD = "fileUpload_Authentic.shtml";
	private String path = "/sdcard/weiyin/img/";
	private int id;
	private String timetemp = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;

	@ViewInject(R.id.attest_idcard_a)
 	private ImageView take_photo1 = null;
	@ViewInject(R.id.attest_idcard_b)
 	private ImageView take_photo2 = null;
	@ViewInject(R.id.attest_idcard_p)
 	private ImageView take_photo3 = null;
 	
 	private Bitmap img1 = null;
 	private Bitmap img2 = null;
 	private Bitmap img3 = null;
 	private File file = null;
 	/***
 	 * ��ʼ��ҳ��
 	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.attest2);
    	ViewUtils.inject(this);
    }
	/**
	 * ҳ�����¼�
	 * @param v �����¼��Ŀؼ�
	 */
	@OnClick({R.id.attest2_next,R.id.attest2_back,R.id.attest2_photo,R.id.attest_idcard_a,R.id.attest_idcard_b,R.id.attest_idcard_p})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.attest2_next : send();
			break;
		case R.id.attest2_back : finish();
			break;
		case R.id.attest2_photo : 
			startActivity(new Intent().setClass(CameraActivity.this, Deal.class));
			LocalDOM.getinstance().write(getBaseContext(), "user", "from", "photo");
			break;
		case R.id.attest_idcard_a : 
			id = 1;
			takephoto();
			break;
		case R.id.attest_idcard_b : 
			id = 3;
			takephoto();
			break;
		case R.id.attest_idcard_p : 
			id = 3;
			takephoto();
			break;
		default:
			break;
		}
	}
	/**
	 * �������������Ƭ���û���Ϣ������������Ϣ�����û���˵
	 */
	private void send() {
		if (img1 !=null && img2!=null && img3!=null) {
		  img1.recycle();img2.recycle();img3.recycle();
		  timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		  HttpUtils httpUtils = new HttpUtils(15000);
          RequestParams params = new RequestParams("UTF-8");
          params.addBodyParameter("myToken", USER.USERTOKEN);
          params.addBodyParameter("username", USER.USERNAME);
          params.addBodyParameter("identity", USER.USERID);
          params.addBodyParameter("identityForntFile", new File(path+"img1.jpg"));
          params.addBodyParameter("identityBackFile", new File(path+"img2.jpg"));
          params.addBodyParameter("personFile", new File(path+"img3.jpg"));
          params.addBodyParameter("timestamp",timetemp);
          params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+USER.USERID+USER.USERNAME+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
          httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
				@Override
			public void onStart() {
					AllUtils.startProgressDialog(CameraActivity.this, "������");
				};
          	@Override
			public void onFailure(HttpException arg0, String arg1) {
              		AllUtils.stopProgressDialog();
              		AllUtils.toast(CameraActivity.this, "����ʧ�ܣ�"+arg1);
              		System.out.println(arg1);
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					System.out.println(arg0.result);
					AllUtils.stopProgressDialog();
					String temp = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
					if ("1".equals(temp)) {
						startActivity(new Intent().setClass(CameraActivity.this, Autonym.class));
					}if ("2".equals(temp)) {
						AllUtils.toast(CameraActivity.this,"��֤ʧ�ܣ�");
					}if ("3".equals(temp)) {
						AllUtils.toast(CameraActivity.this,"�����ֳ����֤��Ƭ����Ϊ��");
					}if ("4".equals(temp)) {
						AllUtils.toast(CameraActivity.this,"���֤���治��Ϊ��");
					}if ("5".equals(temp)) {
						AllUtils.toast(CameraActivity.this,"���֤�Ŵ���");
					}if ("3".equals(temp)) {
						AllUtils.toast(CameraActivity.this,"���ֲ���Ϊ��");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
					}
				} 
			} );
		}else {
			AllUtils.toast(getBaseContext(), "�������գ�");
		}
	
	}
	/**
	 * ����ϵͳ�����������
	 */
	private void takephoto() {
		new ActionSheetDialog(CameraActivity.this)
		.builder()
		.setCancelable(true)
		.setCanceledOnTouchOutside(true)
		.addSheetItem("����", SheetItemColor.Red,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						 switch (id) {
			        		case 1 : intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
		                            "img1.jpg")));
			        			break;
			        		case 2 : intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
		                            "img2.jpg")));
			        			break;
			        		case 3 : intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
		                            "img3.jpg")));
			        			    break;
			        			default : break;
			        		}
			            startActivityForResult(intent2, 2);//����ForResult��
					}
				}).show();
	}
	/**
	 * ����ϵͳ���������Ϣ��ͼƬ���вü����������
	 */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case 1:
            if (resultCode == RESULT_OK) {
                cropPhoto(data.getData());//�ü�ͼƬ
            }
            break;
        case 2:
            if (resultCode == RESULT_OK) {
                switch (id) {
        		case 1 : file = new File(Environment.getExternalStorageDirectory()+ "/img1.jpg");
        			break;
        		case 2 : file = new File(Environment.getExternalStorageDirectory()+ "/img2.jpg");
        			break;
        		case 3 : file = new File(Environment.getExternalStorageDirectory()+ "/img3.jpg");
        			    break;
        			default : break;
        		}
                cropPhoto(Uri.fromFile(file));//�ü�ͼƬ
            }
            break;
        case 3:
            if (data != null) {
                Bundle extras = data.getExtras();
                System.out.println(id);
                	switch (id) {
    				case 1 : img1 = extras.getParcelable("data");
    						 if(img1!=null ){
    						 setPicToView(img1);//������SD����
                			 take_photo1.setImageBitmap(img1);//��ImageView��ʾ����
//                			 img1.recycle();
    						}
    					break;
    				case 2 : img2 = extras.getParcelable("data");
    						 if(img2!=null ){
							 setPicToView(img2);//������SD����
		        			 take_photo2.setImageBitmap(img2);//��ImageView��ʾ����
//		        			 img2.recycle();
    						}
    					break;
    				case 3 : img3 = extras.getParcelable("data");
    						 if(img3!=null ){
							 setPicToView(img3);//������SD����
			       			 take_photo3.setImageBitmap(img3);//��ImageView��ʾ����
//			       			 img3.recycle();
    						}
	       			    break;
	       			default : break;
    				}
                    if (file!=null) {
                   	 file.delete();
           		}
            }
            break;
        default:
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    };
    /**
     * ����ϵͳ�Ĳü�
     * @param uri ͼƬ·��
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
         // aspectX aspectY �ǿ�ߵı���
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1.75);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 400);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    /**
     * �����������Ƭ��SDCARD
     * @param mBitmap	��Ҫ�����ͼƬ
     */
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();  
       if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����  
              return;  
          }  
       FileOutputStream b = null;
       File file = new File(path);
       file.mkdirs();// �����ļ���
       String fileName = null;
       switch (id) {
		case 1 : fileName = path + "img1.jpg";//ͼƬ����
			break;
		case 2 : fileName = path + "img2.jpg";//ͼƬ����
			break;
		case 3 : fileName = path + "img3.jpg";//ͼƬ����
			    break;
			default : break;
		}
       try {
           b = new FileOutputStream(fileName);
           mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// ������д���ļ�
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } finally {
           try {
               //�ر���
               b.flush();
               b.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }
}
