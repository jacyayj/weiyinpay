package com.example.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.controll.ActionSheetDialog;
import com.example.controll.ActionSheetDialog.OnSheetItemClickListener;
import com.example.controll.ActionSheetDialog.SheetItemColor;
import com.example.cz.R;
import com.example.modle.TradeModle;
import com.example.modle.USER;
import com.example.untils.AllUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

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
	/**
	 * 信用卡拍照页面
	 * @author jacyayj
	 *
	 */
public class VisaAttest2 extends Activity{
	private ActionSheetDialog dialog = null;
	private File file = null;
	private Bitmap bitmap = null;
	
	@ViewInject(R.id.visa_takephoto)
	private ImageView card;
	
	private TradeModle modle;
	private String METHOD = "promote_addCredit.shtml";
	@SuppressLint("SdCardPath") 
	private String path = "/sdcard/weiyin/img/";
	/**
	 * 页面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visa_attest2);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
	}
	@OnClick({R.id.visa_attest2_next ,R.id.visa_attest2_back,R.id.visa_takephoto})
	public void VisaAttest2Click(View arg0) {
		switch (arg0.getId()) {
		case R.id.visa_attest2_next : 
			if (file == null) {
				AllUtils.toast(getBaseContext(), "请先拍摄信用卡正面照");
			}else {
				addVisa();
			}
			break;
		case R.id.visa_attest2_back : finish();
		break;
		case R.id.visa_takephoto : takePhoto();
		break;
		default:
			break;
		}
	}
	/**
	 * 进行拍照操作
	 */
	private void takePhoto(){
		if (dialog ==null ) {
			dialog = new ActionSheetDialog(VisaAttest2.this);
			dialog.builder()
			.setCancelable(true)
			.setCanceledOnTouchOutside(true)
			.addSheetItem("拍照", SheetItemColor.Red,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				            intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				                            USER.USERPHONE+"visa.jpg")));
				            startActivityForResult(intent2, 2);//采用ForResult打开
						}
					}).show();
		}else {
			dialog.show();
		}
   }
	/**
	 * 通过系统相机返回进行图片裁剪，保存操作
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
                file = new File(Environment.getExternalStorageDirectory()
                        +"/"+USER.USERPHONE+"visa.jpg");
                cropPhoto(Uri.fromFile(file));//裁剪图片
            }
            break;
        case 3:
            if (data != null) {
                Bundle extras = data.getExtras();
                bitmap = extras.getParcelable("data");
                if(bitmap!=null){
                	setPicToView(bitmap);//保存在SD卡中
                }
                card.setImageBitmap(bitmap);
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
        intent.putExtra("aspectX",2.8);
        intent.putExtra("aspectY",1.7);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX",280);
        intent.putExtra("outputY",170 );
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    /**
     * 保存图片到sdcard
     * @param mBitmap	 需要保存的图片
     */
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();  
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
               return;  
           }  
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + USER.USERPHONE +"visa.jpg";//图片名字
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
     * 向服务器发送用户信用卡数据以绑定信用卡
     */
    private void addVisa() {
    	String timetemp = System.currentTimeMillis()+AllUtils.getRandom();
    	RequestParams params = new RequestParams("UTF-8");
    	params.addBodyParameter("myToken", USER.USERTOKEN);
    	params.addBodyParameter("bankNumber ", modle.getId());
    	params.addBodyParameter("effectiveDate ", modle.getDate());
    	params.addBodyParameter("creditFile ", file);
    	System.out.println(modle.getDate()+modle.getId()+USER.USERNAME);
    	params.addBodyParameter("userName ", USER.USERNAME);
    	params.addBodyParameter("timestamp", timetemp);
    	params.addBodyParameter("hashCode", AllUtils.Md5(USER.USERTOKEN+modle.getId()+modle.getDate()+USER.USERNAME+timetemp+AllUtils.readPrivateKeyFile(this)));
    	new  HttpUtils(15000).send(HttpMethod.POST,getResources().getString(R.string.url)+METHOD, params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				AllUtils.startProgressDialog(VisaAttest2.this,"发送中");
				super.onStart();
			}
    		@Override
			public void onFailure(HttpException arg0, String arg1) {
				AllUtils.stopProgressDialog();
				AllUtils.toast(getBaseContext(), arg1);
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				AllUtils.stopProgressDialog();
				String result = AllUtils.getJson(arg0.result, new String[]{"resultCode"}).get(0);
				if ("998".equals(result)) {
					AllUtils.toast(getBaseContext(), "添加失败");
				}else if("999".equals(result)){
					AllUtils.toast(getBaseContext(), "添加成功");
					startActivity(new Intent(VisaAttest2.this,VisaAttest3.class));
				}else if ("1000".equals(result)) {
					AllUtils.toast(getBaseContext(), "信用卡卡号不能为空");
				}else if ("1001".equals(result)) {
					AllUtils.toast(getBaseContext(), "读取信用卡信息失败");
				}else if ("1002".equals(result)) {
					AllUtils.toast(getBaseContext(), "参数不正确");
				}else if ("1003".equals(result)) {
					AllUtils.toast(getBaseContext(), "参数不正确");
				}else if ("1005".equals(result)) {
					AllUtils.toast(getBaseContext(), "登陆状态出错，请重新登录");
				}else if ("1007".equals(result)) {
					AllUtils.toast(getBaseContext(), "信用卡正面照片不能为空");
				}else if ("1008".equals(result)) {
					AllUtils.toast(getBaseContext(), "信用卡有效期不能少于6个月");
				}else if ("1009".equals(result)) {
					AllUtils.toast(getBaseContext(), "已是最高等级，不能再提升等级");
				}else if ("1010".equals(result)) {
					AllUtils.toast(getBaseContext(), "同一家银行的信用卡只能绑定一张");
				}else if ("1011".equals(result)) {
					AllUtils.toast(getBaseContext(), "卡号不错");
				}
				System.out.println(arg0.result);
			}
		});
	}
}	
