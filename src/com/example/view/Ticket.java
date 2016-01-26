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
	 * ����СƱҳ��
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
	 * ��ʼ��ҳ��
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
		name.setText("��Ʒ��");
		number.setText("��Ʒ���");
		zdnumber.setText(modle.getPosid());
		sq.setText("007");
		czy.setText("007");
		money.setText(modle.getPrice()+".00Ԫ");
		ordernumber.setText(modle.getOrdernumber());
		type.setText(modle.getType());
		date.setText(modle.getDate());
		cardnumber.setText(modle.getId());
		}
		/**
		 * ��ȡ��Ļ
		 * @param activity ��Ҫ��ȡ�Ĵ���
		 * @return	��ȡ��ͼƬ
		 */
		private Bitmap takeScreenShot(Activity activity){
			//View������Ҫ��ͼ��View
			View view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);

			view.buildDrawingCache();

			Bitmap b1 = view.getDrawingCache();

			//��ȡ״̬���߶�
			Rect frame = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			@SuppressWarnings("deprecation")
			int width = activity.getWindowManager().getDefaultDisplay().getWidth();
			@SuppressWarnings("deprecation")
			int height = activity.getWindowManager().getDefaultDisplay().getHeight();//ȥ��������
			Bitmap b = Bitmap.createBitmap(b1, 35,(int)(statusBarHeight+height*0.103), (int)(width*0.935), height - (int)(statusBarHeight+height*0.11));
			view.destroyDrawingCache();
			b1.recycle();
			return b;
			}
			//���浽sdcard
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
			AllUtils.startProgressDialog(Ticket.this, "������");
			}
			} catch (FileNotFoundException e) {
				AllUtils.toast(getBaseContext(), "СƱ����ʧ��");
			e.printStackTrace();
			} catch (IOException e) {
			e.printStackTrace();
			}
			}
			/**
			 * ����������͵���СƱ
			 * @param object ��Ҫ��ת��ҳ��
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
							AllUtils.toast(getBaseContext(), "�ϴ�ʧ��");
						}else if("1".equals(temp)){
							AllUtils.toast(getBaseContext(), "�ϴ��ɹ�");
							save(object);
						}else if("2".equals(temp)){
							AllUtils.toast(getBaseContext(), "��¼״̬���������µ�½");
						}else if("4".equals(temp)){
							AllUtils.toast(getBaseContext(), "�벻Ҫ�ظ��ύ");
						}else if("3".equals(temp)){
							AllUtils.toast(getBaseContext(), "СƱ��ȡʧ��");
						}
					}
				});
			}
			/**
			 * ����СƱ��sdcard
			 * @param object ��Ҫ��ת��ҳ��
			 */
		private void save(final Class<?> object) {
			if (dialog == null) {
				dialog = new AlertDialog.Builder(Ticket.this);
				dialog.setTitle("��ʾ");
				dialog.setMessage("�Ƿ񱣴�СƱ��");
				dialog.setNegativeButton("��", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface mdialog, int which) {
						startActivity(new Intent(getApplicationContext(),object).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						finish();
					 }
				});
				dialog.setPositiveButton("��",new  AlertDialog.OnClickListener() {
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
		 * �����ֻ���back�����в���
		 */
		@Override
		public void onBackPressed() {
			savePic(takeScreenShot(Ticket.this),AllUtils.getSDCardPath()+modle.getOrdernumber()+".jpg");
			send(MainActivity.class);
		}
}
