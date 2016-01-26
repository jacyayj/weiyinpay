package com.example.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.cz.MainActivity;
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
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
	/**
	 * 电子小票页面
	 * @author jacyayj
	 *
	 */
public class Ticket extends Activity {
	@ViewInject(R.id.t_spname)
	private ImageView iv1 = null;
	@ViewInject(R.id.t_spname)
	private TextView name = null;
	@ViewInject(R.id.t_spnumber)
	private TextView number = null;
	@ViewInject(R.id.t_zhongduan)
	private TextView zdnumber = null;
	@ViewInject(R.id.t_card)
	private TextView cardnumber = null;
	@ViewInject(R.id.t_ordernumber)
	private TextView ordernumber = null;
	@ViewInject(R.id.t_spname)
	private AlertDialog.Builder dialog = null;
	@ViewInject(R.id.t_sptype)
	private TextView type = null;
	@ViewInject(R.id.t_sq)
	private TextView sq = null;
	@ViewInject(R.id.t_czy)
	private TextView czy = null;
	@ViewInject(R.id.t_spdate)
	private TextView date = null;
	@ViewInject(R.id.t_money)
	private TextView money = null;
	
	@ResInject(id = R.string.url,type = ResType.String)
	private String URL = null;
	
	private File file = null;
	private final String METHOD = "smallticket_uploadTicket.shtml";
	private TradeModle modle = null;
	private String timetemp = null;
	/**
	 * 初始化页面
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
		init();
		iv1 = (ImageView) findViewById(R.id.ticket_sign);
		iv1.setBackgroundDrawable(new BitmapDrawable(modle.getSign()));
		}
		@OnClick({R.id.ticket_back,R.id.ticket_next})
		private void onClick(View v) {
			switch (v.getId()) {
			case R.id.ticket_back : onBackPressed();
				break;
			case R.id.ticket_next :
				System.out.println("??");
				savePic(takeScreenShot(Ticket.this),AllUtils.getSDCardPath()+modle.getOrdernumber()+".jpg");
				send(PayDown.class);
				break;
			default:
				break;
			}
		}
		private void init() {
		name.setText("商品名");
		number.setText("商品编号");
		zdnumber.setText(modle.getPosid());
		sq.setText("007");
		czy.setText("007");
		money.setText(modle.getPrice()+".00元");
		ordernumber.setText(modle.getOrdernumber());
		type.setText(modle.getType());
		date.setText(modle.getDate());
		cardnumber.setText(modle.getId());
		}
		/**
		 * 截取屏幕
		 * @param activity 需要截取的窗口
		 * @return	截取的图片
		 */
		private Bitmap takeScreenShot(Activity activity){
			//View是你需要截图的View
			View view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);

			view.buildDrawingCache();

			Bitmap b1 = view.getDrawingCache();

			//获取状态栏高度
			Rect frame = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			@SuppressWarnings("deprecation")
			int width = activity.getWindowManager().getDefaultDisplay().getWidth();
			@SuppressWarnings("deprecation")
			int height = activity.getWindowManager().getDefaultDisplay().getHeight();//去掉标题栏
			Bitmap b = Bitmap.createBitmap(b1, 35,(int)(statusBarHeight+height*0.103), (int)(width*0.935), height - (int)(statusBarHeight+height*0.11));
			view.destroyDrawingCache();
			b1.recycle();
			return b;
			}
			//保存到sdcard
			private void savePic(Bitmap b,String strFileName){
			FileOutputStream fos = null;
			try {
			fos = new FileOutputStream(strFileName);
			if (null != fos)
			{
			b.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.flush();
			fos.close();
			b.recycle();
			AllUtils.startProgressDialog(Ticket.this, "发送中");
			}
			} catch (FileNotFoundException e) {
				AllUtils.toast(getBaseContext(), "小票保存失败");
			e.printStackTrace();
			} catch (IOException e) {
			e.printStackTrace();
			}
			}
			/**
			 * 向服务器发送电子小票
			 * @param object 需要跳转的页面
			 */
			private void send(final Class<?> object) {
				file = new File(AllUtils.getSDCardPath()+modle.getOrdernumber()+".jpg");
				timetemp = System.currentTimeMillis()+AllUtils.getRandom();
				RequestParams params = new RequestParams();
				params.addBodyParameter("myToken", USER.USERTOKEN);
				params.addBodyParameter("timestamp",timetemp);
				params.addBodyParameter("receiptFile ",file);
				System.out.println(AllUtils.getSDCardPath()+modle.getOrdernumber()+".jpg");
				params.addBodyParameter("hashCode",AllUtils.Md5(USER.USERTOKEN+timetemp+AllUtils.readPrivateKeyFile(getBaseContext())));
				HttpUtils utils = new HttpUtils(15000);
				utils.send(HttpMethod.POST,URL+METHOD, params,new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						AllUtils.stopProgressDialog();
						AllUtils.toast(getBaseContext(), arg1);
						System.out.println(arg1);
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						AllUtils.stopProgressDialog();
						System.out.println(arg0.result);
						String temp = AllUtils.getJson(arg0.result,new String[]{"resultCode"}).get(0);
						if ("0".equals(temp)) {
							AllUtils.toast(getBaseContext(), "上传失败");
						}else if("1".equals(temp)){
							AllUtils.toast(getBaseContext(), "上传成功");
							save(object);
						}else if("2".equals(temp)){
							AllUtils.toast(getBaseContext(), "登录状态出错请重新登陆");
						}else if("4".equals(temp)){
							AllUtils.toast(getBaseContext(), "请不要重复提交");
						}else if("3".equals(temp)){
							AllUtils.toast(getBaseContext(), "小票截取失败");
						}
					}
				});
			}
			/**
			 * 保存小票到sdcard
			 * @param object 需要跳转的页面
			 */
		private void save(final Class<?> object) {
			if (dialog == null) {
				dialog = new AlertDialog.Builder(Ticket.this);
				dialog.setTitle("提示");
				dialog.setMessage("是否保存小票？");
				dialog.setNegativeButton("是", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface mdialog, int which) {
						startActivity(new Intent(getApplicationContext(),object).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						finish();
					 }
				});
				dialog.setPositiveButton("否",new  AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (file!=null) {
							file.delete();
						}
						startActivity(new Intent(getApplicationContext(),object).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						finish();
					}
				});
				dialog.create().show();
			}else {
				dialog.show();
			}
		}
		/**
		 * 监听手机的back键进行操作
		 */
		@Override
		public void onBackPressed() {
			savePic(takeScreenShot(Ticket.this),AllUtils.getSDCardPath()+modle.getOrdernumber()+".jpg");
			send(MainActivity.class);
		}
}
