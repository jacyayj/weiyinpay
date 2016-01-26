package com.example.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.cz.R;
import com.example.modle.TradeModle;
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
	 * ǩ��ҳ��
	 * @author jacyayj
	 *
	 */
public class Sign extends Activity{
	private LayoutParams p = null;
	private PaintView mView = null;
	@ViewInject(R.id.sign_money)
	private TextView money = null;
	@ViewInject(R.id.sign_agreen)
	private TextView agreen = null;
	@ViewInject(R.id.sign_time)
	private TextView time = null;
	
	@ResInject(id = R.string.url,type  = ResType.String)
	private String URL = null;
	
	private String METHOD = "fileUpload_signatoryPicture.shtml";
	private String METHOD2 = "appInsertSync_insertPay.shtml";
	private String timetemp = null;
	private TradeModle modle = null;
	private boolean CANGO = false;
	@SuppressLint("SdCardPath")
	private String path = "/sdcard/weiyin/ticket/";
	/**
	 * ��ʼ��ҳ��
	 */
	@Override
	@SuppressWarnings("deprecation")
	@SuppressLint({"SimpleDateFormat" })
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign);
		ViewUtils.inject(this);
		time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		modle = TradeModle.getInstance();
		if ("deal".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
			money.setVisibility(View.INVISIBLE);
			agreen.setText("����ͬ������Э�鲢ǩ��");
		}else {
			money.setText("��"+modle.getPrice()+"Ԫ");
			agreen.setText("����ͬ��΢��֧��Э�鲢ȷ�Ͻ��");
		}
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.sign_body);
		
		p = getWindow().getAttributes();
		p.width = getWindowManager().getDefaultDisplay().getWidth();
		p.height = getWindowManager().getDefaultDisplay().getHeight();
		getWindow().setAttributes(p);
		
		mView = new PaintView(Sign.this);
		frameLayout.addView(mView);
		mView.requestFocus();
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
	 */
	@OnClick({R.id.sign_back,R.id.sign_clear,R.id.sign_ok})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_back : finish();
			break;
		case R.id.sign_clear :
			CANGO = false;
			mView.clear();
			break;
		case R.id.sign_ok : 
			if ("deal".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
				if (CANGO) {
					setPicToView(mView.getCachebBitmap());
					send();
				}else {
					AllUtils.toast(getBaseContext(), "����ǩ��");
				}
			}else {
				if (CANGO) {
					modle.setSign(mView.getCachebBitmap());
					Pay();
				}else {
					AllUtils.toast(getBaseContext(), "����ǩ��");
				}
			}
			break;
		default:
			break;
		}

	}
//	protected void onResume() {
//		 /**
//		  * ����Ϊ����
//		  */
//		 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		 }
//		 super.onResume();
//		}
	/**
	 * ǩ������
	 * @author jacyayj
	 *
	 */
	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Bitmap cachebBitmap;
		private Path path;

		public Bitmap getCachebBitmap() {
			return cachebBitmap;
		}
		public PaintView(Context context) {
			super(context);					
			init();			
		}
		private void init(){
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);					
			path = new Path();
			cachebBitmap = Bitmap.createBitmap(p.width,(int)(p.height*0.8),Config.ARGB_8888);			
			cacheCanvas = new Canvas(cachebBitmap);
			cacheCanvas.drawColor(Color.WHITE);
		}
		public void clear() {
			if (cacheCanvas != null) {
				paint.setColor(0xff000);
				cacheCanvas.drawPaint(paint);
				paint.setColor(Color.BLACK);
				cacheCanvas.drawColor(Color.WHITE);
				invalidate();			
			}
		}
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(cachebBitmap, 0, 0, null);
			canvas.drawPath(path, paint);
		}
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
			int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}
			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;
			Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_4444);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (cachebBitmap != null) {
				newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
			}
			cachebBitmap = newBitmap;
			cacheCanvas = newCanvas;
		}
		private float cur_x, cur_y;
		@Override
		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouchEvent(MotionEvent event) {
			CANGO = true;
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				cur_x = x;
				cur_y = y;
				path.moveTo(cur_x, cur_y);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				path.quadTo(cur_x, cur_y, x, y);
				cur_x = x;
				cur_y = y;
				break;
			}
			case MotionEvent.ACTION_UP: {
				cacheCanvas.drawPath(path, paint);
				path.reset();
				break;
					}
				}
			invalidate();
			return true;
			}
		}
	/**
	 * �����������ǩ����ͼ
	 */
		private void send() {
		 final File file = new File(path + USER.USERTOKEN +"sign.jpg");
		 HttpUtils httpUtils = new HttpUtils(15000);
         RequestParams params = new RequestParams();
         timetemp = System.currentTimeMillis()+AllUtils.getRandom();
         params.addBodyParameter("myToken", USER.USERTOKEN);
         params.addBodyParameter("signatoryFile",file);
         params.addBodyParameter("timestamp",timetemp);
		 params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
         httpUtils.send(HttpRequest.HttpMethod.POST, URL+METHOD,params,new RequestCallBack<String>() {
				@Override
				public void onStart() {
					AllUtils.startProgressDialog(Sign.this, "�ϴ�ǩ����");
				};
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					AllUtils.stopProgressDialog();
					AllUtils.toast(getBaseContext(), "����ʧ�ܣ�"+arg1);
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					AllUtils.stopProgressDialog();
					System.out.println(arg0.result);
					String temp = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
					if ("1".equals(temp)) {
						AllUtils.toast(getBaseContext(), "�ϴ��ɹ�");
						file.delete();
						startActivity(new Intent().setClass(Sign.this, Attest.class));
						finish();
					}else if("2".equals(temp)){
						AllUtils.toast(getBaseContext(), "��¼״̬���������µ�½");
					}else if("3".equals(temp)){
						AllUtils.toast(getBaseContext(), "����ǩ��");
					}else if("4".equals(temp)){
						AllUtils.toast(getBaseContext(), "�ϴ�ʧ��");
					}else {
						AllUtils.toast(getBaseContext(), arg0.result);
					}
				}
			} );
		}
		/**
		 * ����������͸�������
		 */
		private void Pay() {
			timetemp = AllUtils.getRandom()+System.currentTimeMillis();
			HttpUtils httpUtils = new HttpUtils(15000);
            RequestParams params = new RequestParams();
            timetemp = System.currentTimeMillis()+AllUtils.getRandom();
            params.addBodyParameter("myToken", USER.USERTOKEN);
            params.addBodyParameter("logno", modle.getOrdernumber());
            params.addBodyParameter("terminalno", modle.getPosid());
            System.out.println("�����ն˺�"+modle.getPosid());
            params.addBodyParameter("cardno", modle.getId());
            params.addBodyParameter("amount",modle.getPrice());
            params.addBodyParameter("transtype", modle.getType());
            params.addBodyParameter("acctNoT2",modle.getSencondno());
//        params.addBodyParameter("acctNoT3","");
            params.addBodyParameter("cardtype","���ÿ�");
            params.addBodyParameter("enterclose","mpos");
            params.addBodyParameter("password", modle.getPwd());
            params.addBodyParameter("timestamp",timetemp);
            String temp = modle.getSencondno()+modle.getPrice()+modle.getId()+"���ÿ�"+"mpos"+modle.getOrdernumber()+modle.getPwd()+modle.getPosid()+modle.getType();
			params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+temp+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
            httpUtils.send(HttpRequest.HttpMethod.POST,URL+METHOD2,params,new RequestCallBack<String>() {
					@Override
					public void onStart() {
					AllUtils.startProgressDialog(Sign.this, "������");
					};
            	@Override
				public void onFailure(HttpException arg0, String arg1) {
					AllUtils.stopProgressDialog();
            		AllUtils.toast(getBaseContext(), arg1);
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						String temp = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
						if ("1".equals(temp)) {
						AllUtils.toast(getBaseContext(), "���׳ɹ�");
						startActivity(new Intent(getApplicationContext(), Ticket.class));
						finish();
						}else if ("0".equals(temp)) {
							AllUtils.toast(getBaseContext(), "����ʧ��");
						}else if ("2".equals(temp)) {
							AllUtils.toast(getBaseContext(), "�ն˺Ų���Ϊ��");
						}else if ("3".equals(temp)) {
							AllUtils.toast(getBaseContext(), "���Ų���Ϊ��");
						}else if ("4".equals(temp)) {
							AllUtils.toast(getBaseContext(), "����Ϊ��");
						}else if ("5".equals(temp)) {
							AllUtils.toast(getBaseContext(), "�������Ͳ���Ϊ��");
						}else if ("6".equals(temp)) {
							AllUtils.toast(getBaseContext(), "2�ŵ����ݲ���Ϊ��");
						}else if ("7".equals(temp)) {
							AllUtils.toast(getBaseContext(), "3�ŵ����ݲ���Ϊ��");
						}else if ("8".equals(temp)) {
							AllUtils.toast(getBaseContext(), "�������Ϊ��");
						}else if ("9".equals(temp)) {
							AllUtils.toast(getBaseContext(), "ͨ����ʶ������Ϊ��");
						}else if ("10".equals(temp)) {
							AllUtils.toast(getBaseContext(), "�����Ų���Ϊ��");
						}else if ("11".equals(temp)) {
							AllUtils.toast(getBaseContext(), "���벻��Ϊ��");
						}else if ("12".equals(temp)) {
							AllUtils.toast(getBaseContext(), "��½״̬���������µ�¼");
						}else if ("13".equals(temp)) {
							AllUtils.toast(getBaseContext(), "��ͨ��������");
						}else if ("14".equals(temp)) {
							AllUtils.toast(getBaseContext(), "���̻�û���ն˺�");
						}else if ("15".equals(temp)) {
							AllUtils.toast(getBaseContext(), "�����������ύ");
						}else if ("16".equals(temp)) {
							AllUtils.toast(getBaseContext(), "��������");
						}else if ("17".equals(temp)) {
							AllUtils.toast(getBaseContext(), "���ʲ�����");
						}else{
							AllUtils.toast(getBaseContext(), arg0.result);
						}
					}
				} );
		}
		/**
		 * ����ͼƬ��sdcard
		 * @param mBitmap ��Ҫ�����ͼƬ
		 */
		private void setPicToView(Bitmap mBitmap) {
	       String sdStatus = Environment.getExternalStorageState();  
	       if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����  
	              return;  
	          }  
	       FileOutputStream b = null;
	       File file = new File(path);
	       file.mkdirs();// �����ļ���
	       String fileName = path + USER.USERTOKEN +"sign.jpg";//ͼƬ����
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
	               mBitmap.recycle();
	           } catch (IOException e) {
	               e.printStackTrace();
	           }
	       }
	    }
}
