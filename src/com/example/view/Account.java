package com.example.view;
	/**
	 * 我的账户页面;
	 * 我的账户信息处理页面
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
	 * 页面初始化
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
	 * 页面点击事件
	 * @param v 触发点击的控件
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
	 * 从服务器获取账户信息;布置页面内容。
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
						AllUtils.startProgressDialog(Account.this, "加载中");
						super.onStart();
					}
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						AllUtils.stopProgressDialog();
						AllUtils.toast(getBaseContext(), arg1);
						phone.setText(USER.USERPHONE);
						tv3.setText("未认证");
						name.setText("微银用户");
						tv1.setText("￥0.0元");
						tv2.setText("可提现余额￥0.0元");
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
								Bitmap bt = BitmapFactory.decodeFile(path + USER.USERPHONE+"head.jpg");//从Sd中找头像，转换成Bitmap
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
											AllUtils.toast(getBaseContext(), "获取头像失败");
										}
									}else {
										System.out.println("本地无");
										headimg.setImageResource(R.drawable.head);
									}
								}
								phone.setText(AllUtils.hidePhone(result.get(1)));
								if (result.get(4).equals("3")) {
									name.setText(USER.USERNAME);
								}else {
									name.setText("微银用户");
								}
								try {
									tv1.setText("￥"+String.valueOf(Double.parseDouble(result.get(2))/100)+"元");
									tv2.setText("可提现余额￥"+String.valueOf(Double.parseDouble(result.get(3))/100)+"元");
								} catch (Exception e) {
									tv1.setText("￥0.0元");
									tv2.setText("可提现余额￥0.0元");
								}
								if ("1".equals(result.get(4))) {
									tv3.setText("未认证");
								}else if ("2".equals(result.get(4))) {
									tv3.setText("认证中");
								}else if ("3".equals(result.get(4))) {
									tv3.setText("已认证");
								}else if ("4".equals(result.get(4))) {
									tv3.setText("认证失败");
								}else if ("5".equals(result.get(4))) {
									tv3.setText("您已被拉入黑名单");
								}else if ("6".equals(result.get(4))) {
									tv3.setText("该用户被注销");
								}else if ("7".equals(result.get(4))) {
									tv3.setText("该用户被冻结");
								}
							}else if (result.get(7).equals("1005")){
								AllUtils.toast(getBaseContext(), "登陆状态出错请重新登录");
							}
							} catch (Exception e) {
								AllUtils.toast(getBaseContext(),"服务器出错");
								name.setText("微银用户");
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
			.addSheetItem("拍照", SheetItemColor.Red,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				            intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				                            USER.USERPHONE+"head.jpg")));
				            startActivityForResult(intent2, 2);//采用ForResult打开
						}
					})
			.addSheetItem("去相册选择头像", SheetItemColor.Blue,
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
	 * 处理相机返回照片，分别进行裁剪和保存操作
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case 1:
            if (resultCode == RESULT_OK) {
                cropPhoto(data.getData());//裁剪图片
            }
            break;
        case 2:
            if (resultCode == RESULT_OK) {
                file = new File(Environment.getExternalStorageDirectory()+"/"+USER.USERPHONE+"head.jpg");
                cropPhoto(Uri.fromFile(file));//裁剪图片
            }
            break;
        case 3:
            if (data != null) {
                Bundle extras = data.getExtras();
                head =AllUtils.toRoundBitmap( (Bitmap) extras.getParcelable("data"));
                if(head!=null){
                	timetemp = System.currentTimeMillis()+AllUtils.getRandom();
                	setPicToView(head);//保存在SD卡中
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
							AllUtils.startProgressDialog(Account.this, "上传中");
						};
                    	@Override
						public void onFailure(HttpException arg0, String arg1) {
                    		AllUtils.stopProgressDialog();
                    		AllUtils.toast(Account.this, "发送失败！"+arg1);
						}
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							AllUtils.stopProgressDialog();
							String temp = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
							if ("1".equals(temp)) {
								headimg.setImageBitmap(head);//用ImageView显示出来
								AllUtils.toast(Account.this, "上传成功");
							}else if("2".equals(temp)){
								AllUtils.toast(Account.this, "登录状态出错，请重新登陆");
							}else if("3".equals(temp)){
								AllUtils.toast(Account.this, "头像为空");
							}else if("4".equals(temp)){
								AllUtils.toast(Account.this, "上传失败");
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
     * 调用系统的裁剪
     * @param uri 图片路径
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
         // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
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
    	        	System.out.println("下载成功");
    	        	Bitmap bt = BitmapFactory.decodeFile(path + USER.USERPHONE+"head.jpg");//从Sd中找头像，转换成Bitmap
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
						AllUtils.toast(getBaseContext(), "未找到头像");
					}else {
						AllUtils.toast(getBaseContext(),"失败"+msg);
					}
    	        	headimg.setImageDrawable(getResources().getDrawable(R.drawable.head));
    	        }
    	});
    	//调用cancel()方法停止下载
	}
	/**
	 * 保存图片到SDcard
	 * @param mBitmap 需要保存的图片对象
	 */
    private void setPicToView(Bitmap mBitmap) {
       String sdStatus = Environment.getExternalStorageState();  
       if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
              return;  
          }  
       FileOutputStream b = null;
       File file = new File(path);
       file.mkdirs();// 创建文件夹
       String fileName = path + USER.USERPHONE +"head.jpg";//图片名字
       try {
           b = new FileOutputStream(fileName);
           mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } finally {
           try {
               //关闭流
               b.flush();
               b.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }
    /**
     * 登出；注销账户信息
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
				AllUtils.startProgressDialog(Account.this,"发送中");
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
