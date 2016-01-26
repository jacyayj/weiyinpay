package com.example.view;
	/**
	 * �ҵ��˻�ҳ��;
	 * �ҵ��˻���Ϣ����ҳ��
	 * @author jacyayj
	 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.example.controll.ActionSheetDialog.OnSheetItemClickListener;
import com.example.controll.ActionSheetDialog.SheetItemColor;
import com.example.controll.ActionSheetDialog;
import com.example.cz.R;
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

public class Account extends Activity{
	private final String METHOD2 = "loginAction_logout.shtml";
	private final String METHOD = "accountMerchantAction_showAccount.shtml";
	private final String METHOD3 = "fileUpload_updateHead.shtml";
	
	@ViewInject(R.id.account_name)
	private TextView name = null;
	@ViewInject(R.id.account_phone)
	private TextView phone = null;
	@ViewInject(R.id.account_image)
	private ImageView headimg = null;
	@ViewInject(R.id.acc_tv1)
	private TextView tv1= null;
	@ViewInject(R.id.acc_tv2)
	private TextView tv2= null;
	@ViewInject(R.id.acc_tv3)
	private TextView tv3= null;
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private String timetemp = null;
	private Bitmap head = null;
	private String headpath = null;
	private LocalDOM dom = null;
	private File file = null;
	@SuppressLint("SdCardPath")
	private String path = "/sdcard/weiyin/img/";
	/**
	 * ҳ���ʼ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_page);
		ViewUtils.inject(this);
		dom = LocalDOM.getinstance();
		loadData();
	}
	/**
	 * ҳ�����¼�
	 * @param v ��������Ŀؼ�
	 */
	@OnClick({R.id.account_back,R.id.login_out,R.id.account_image,R.id.account2,R.id.account3,R.id.account4,R.id.account5,R.id.account6})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.account_back : finish();
			break;
		case R.id.login_out : loginout();
		break;
		case R.id.account_image : head();
		break;
		case R.id.account2 : startActivity(new Intent(Account.this, Addbank.class));
		break;
		case R.id.account3 : startActivity(new Intent(Account.this, Record.class));
		break;
		case R.id.account4 : startActivity(new Intent(Account.this, ChangePwd.class));
		break;
		case R.id.account5 : 
			if ("3".equals(dom.read(getBaseContext(), "user",USER.USERTOKEN+"rz")) ||
					 "2".equals(dom.read(getBaseContext(), "user",USER.USERTOKEN+"rz")) || 
					 "4".equals(dom.read(getBaseContext(), "user",USER.USERTOKEN+"rz"))) {
					startActivity(new Intent(getBaseContext(), RZ.class));
				}else {
					startActivity(new Intent(getBaseContext(), Fist.class));
				}
		break;
		case R.id.account6 : startActivity(new Intent(Account.this, Terminal.class));
		break;
		default:
			break;
		}
	}
	/**
	 * �ӷ�������ȡ�˻���Ϣ;����ҳ�����ݡ�
	 */
	private void loadData() {
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();
				RequestParams params = new RequestParams();
				params.addBodyParameter("myToken", USER.USERTOKEN);
				params.addBodyParameter("timestamp", timetemp);
				params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(Account.this)));
				HttpUtils httpUtils = new HttpUtils(15000);
				httpUtils.send(HttpMethod.POST,URL+METHOD,params,new RequestCallBack<String>(){
					@Override
					public void onStart() {
						AllUtils.startProgressDialog(Account.this, "������");
						super.onStart();
					}
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						AllUtils.stopProgressDialog();
						AllUtils.toast(getBaseContext(), arg1);
						phone.setText(USER.USERPHONE);
						tv3.setText("δ��֤");
						name.setText("΢���û�");
						tv1.setText("��0.0Ԫ");
						tv2.setText("��������0.0Ԫ");
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						try {
							List<String> result = new ArrayList<String>();
							result = AllUtils.getJson(arg0.result, new String[]{"realName","mobilePhone","amount","famount","isAudit","fidentity","headPath","resultCode"});
							if (result.get(7).equals("1000")) {
								USER.USERNAME = result.get(0);
								USER.USERID = result.get(5);
								headpath = result.get(6);
								Bitmap bt = BitmapFactory.decodeFile(path + USER.USERPHONE+"head.jpg");//��Sd����ͷ��ת����Bitmap
			    	        	if (bt!=null) {
									 headimg.setImageBitmap(AllUtils.toRoundBitmap(bt));
									 bt.recycle();
								}else{
									if (headpath!=null && !headpath.equals("null")) {
										try {
											System.out.println(headpath);
											downLoad();
										} catch (Exception e) {
											headimg.setImageDrawable(getResources().getDrawable(R.drawable.head));
											AllUtils.toast(getBaseContext(), "��ȡͷ��ʧ��");
										}
									}else {
										System.out.println("������");
										headimg.setImageResource(R.drawable.head);
									}
								}
								phone.setText(AllUtils.hidePhone(result.get(1)));
								if (result.get(4).equals("3")) {
									name.setText(USER.USERNAME);
								}else {
									name.setText("΢���û�");
								}
								try {
									tv1.setText("��"+String.valueOf(Double.parseDouble(result.get(2))/100)+"Ԫ");
									tv2.setText("��������"+String.valueOf(Double.parseDouble(result.get(3))/100)+"Ԫ");
								} catch (Exception e) {
									tv1.setText("��0.0Ԫ");
									tv2.setText("��������0.0Ԫ");
								}
								if ("1".equals(result.get(4))) {
									tv3.setText("δ��֤");
								}else if ("2".equals(result.get(4))) {
									tv3.setText("��֤��");
								}else if ("3".equals(result.get(4))) {
									tv3.setText("����֤");
								}else if ("4".equals(result.get(4))) {
									tv3.setText("��֤ʧ��");
								}else if ("5".equals(result.get(4))) {
									tv3.setText("���ѱ����������");
								}else if ("6".equals(result.get(4))) {
									tv3.setText("���û���ע��");
								}else if ("7".equals(result.get(4))) {
									tv3.setText("���û�������");
								}
							}else if (result.get(7).equals("1005")){
								AllUtils.toast(getBaseContext(), "��½״̬���������µ�¼");
							}
							} catch (Exception e) {
								AllUtils.toast(getBaseContext(),"����������");
								name.setText("΢���û�");
								headimg.setImageResource(R.drawable.head);
								System.out.println(arg0.result);
							}
					}
				});
		}
	private void head(){
			new ActionSheetDialog(Account.this)
			.builder()
			.setCancelable(true)
			.setCanceledOnTouchOutside(true)
			.addSheetItem("����", SheetItemColor.Red,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				            intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				                            USER.USERPHONE+"head.jpg")));
				            startActivityForResult(intent2, 2);//����ForResult��
						}
					})
			.addSheetItem("ȥ���ѡ��ͷ��", SheetItemColor.Blue,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent1 = new Intent(Intent.ACTION_PICK, null);
				            intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				            startActivityForResult(intent1, 1);
						}
					}).show();
	   }
	/**
	 * �������������Ƭ���ֱ���вü��ͱ������
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
                file = new File(Environment.getExternalStorageDirectory()+"/"+USER.USERPHONE+"head.jpg");
                cropPhoto(Uri.fromFile(file));//�ü�ͼƬ
            }
            break;
        case 3:
            if (data != null) {
                Bundle extras = data.getExtras();
                head =AllUtils.toRoundBitmap( (Bitmap) extras.getParcelable("data"));
                if(head!=null){
                	timetemp = System.currentTimeMillis()+AllUtils.getRandom();
                	setPicToView(head);//������SD����
                	if (file!=null) {
						file.delete();
					}
                	HttpUtils httpUtils = new HttpUtils(15000);
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("myToken", USER.USERTOKEN);
                    params.addBodyParameter("headFile", new File(path+USER.USERPHONE+"head.jpg"));
                    params.addBodyParameter("timestamp",timetemp);
        			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
                    httpUtils.send(HttpMethod.POST, URL+METHOD3,params,new RequestCallBack<String>() {
						@Override
						public void onStart() {
							AllUtils.startProgressDialog(Account.this, "�ϴ���");
						};
                    	@Override
						public void onFailure(HttpException arg0, String arg1) {
                    		AllUtils.stopProgressDialog();
                    		AllUtils.toast(Account.this, "����ʧ�ܣ�"+arg1);
						}
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							AllUtils.stopProgressDialog();
							String temp = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
							if ("1".equals(temp)) {
								headimg.setImageBitmap(head);//��ImageView��ʾ����
								AllUtils.toast(Account.this, "�ϴ��ɹ�");
							}else if("2".equals(temp)){
								AllUtils.toast(Account.this, "��¼״̬���������µ�½");
							}else if("3".equals(temp)){
								AllUtils.toast(Account.this, "ͷ��Ϊ��");
							}else if("4".equals(temp)){
								AllUtils.toast(Account.this, "�ϴ�ʧ��");
							}else {
								AllUtils.toast(getBaseContext(), arg0.result);
							}
						}
					} );
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
        intent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
	private void downLoad() {
    	HttpUtils http = new HttpUtils();
		 http.download(URL+headpath,path+USER.USERPHONE +"head.jpg",true,false,new RequestCallBack<File>() {
    	        @Override
				public void onStart() {
    	        }
    	        @Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
    	        	System.out.println("���سɹ�");
    	        	Bitmap bt = BitmapFactory.decodeFile(path + USER.USERPHONE+"head.jpg");//��Sd����ͷ��ת����Bitmap
    	        	if (bt!=null) {
						 headimg.setImageBitmap(AllUtils.toRoundBitmap(bt));
						 bt.recycle();
					}
    	        	System.out.println(responseInfo.result);
    	        }
    	        @Override
				public void onFailure(HttpException error, String msg) {
    	        	System.out.println(msg);
    	        	if ("Not Found".equals(msg)) {
						AllUtils.toast(getBaseContext(), "δ�ҵ�ͷ��");
					}else {
						AllUtils.toast(getBaseContext(),"ʧ��"+msg);
					}
    	        	headimg.setImageDrawable(getResources().getDrawable(R.drawable.head));
    	        }
    	});
    	//����cancel()����ֹͣ����
	}
	/**
	 * ����ͼƬ��SDcard
	 * @param mBitmap ��Ҫ�����ͼƬ����
	 */
    private void setPicToView(Bitmap mBitmap) {
       String sdStatus = Environment.getExternalStorageState();  
       if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����  
              return;  
          }  
       FileOutputStream b = null;
       File file = new File(path);
       file.mkdirs();// �����ļ���
       String fileName = path + USER.USERPHONE +"head.jpg";//ͼƬ����
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
    /**
     * �ǳ���ע���˻���Ϣ
     */
    private void loginout() {
		timetemp = System.currentTimeMillis()+AllUtils.getRandom();
		RequestParams params = new RequestParams();
		params.addBodyParameter("myToken", USER.USERTOKEN);
		params.addBodyParameter("mobilePhone", USER.USERPHONE);
		params.addBodyParameter("timestamp", timetemp);
		params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+USER.USERPHONE+timetemp+AllUtils.readPrivateKeyFile(Account.this)));
		HttpUtils httpUtils = new HttpUtils(15000);
		httpUtils.send(HttpMethod.POST,URL+METHOD2,params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(Account.this,"������");
				super.onStart();
			}
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.stopProgressDialog();
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				List<String> temp =  AllUtils.getJson(arg0.result, new String[]{"resultCode"});
				if ("1000".equals(temp.get(0))) {
					LocalDOM.getinstance().clear(Account.this, "user");
					finish();
				}else {
					LocalDOM.getinstance().clear(Account.this, "user");
					finish();
				}
			}
		});
	}
}
