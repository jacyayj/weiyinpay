package com.example.untils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.example.modle.myProgrssDialog;
	/**
	 * ������
	 * @author jacyayj
	 *
	 */
public class AllUtils {
	public static myProgrssDialog progrssDialog;
	/**
	 * @param strResult
	 *            ��Ҫ������json�ַ���
	 * @param key
	 *            Ҫȡ��json���ݵ�keyֵ
	 * @return ������ɳɹ�֮�󷵻ؽ��list��ʧ����ֱ�ӽ���䷵�� ����json�ַ���
	 */
	public static List<String> getJson(String strResult, String key[]) {
		List<String> result = new ArrayList<String>();
		if (strResult != null) {
			try {
				JSONObject object = new JSONObject(strResult);
				for (int j = 0; j < key.length; j++) {
					result.add(object.getString(key[j]));
				}
			} catch (JSONException e) {
				System.out.println("Json parse error1");
				result.add(strResult);
				result.add("����������");
				e.printStackTrace();
				return result;
			}
		} else {
			result.add(strResult);
			result.add("null");
		}
		return result;
	}
	
	/**
	 * ��ȡ�����
	 * @return �õ��������
	 */
	public static String getRandom() {
		return String.valueOf((int) (Math.random() * 900) + 100);
	}
	/**
	 * ��ʾtoast��Ϣ
	 * @param context Ҫ��ʾ�Ĵ���
	 * @param msg	��Ҫ��ʾ����Ϣ
	 */
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * ������ʽ�ж�
	 * @param str	��Ҫ�жϵ��ַ���
	 * @return	����������ַ���
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String str) throws PatternSyntaxException {
		// ֻ������ĸ������
		String regEx = "^[\u4e00-\u9fa5]{1,5}$";
		if (str.matches(regEx)) {
			return str;
		}
		if (str.length() != 0) {
			return str.substring(0, str.length() - 1);
		}
		return str.substring(0, str.length());
	}
	/**
	 * ��ʾ���ȿ�
	 * @param context	��ʾ�Ĵ���
	 * @param msg	��ʾ����Ϣ
	 */
	public static void startProgressDialog(Context context, String msg) {
		if (progrssDialog == null) {
			progrssDialog = myProgrssDialog.createDialog(context);
			progrssDialog.setMessage(msg);
		}
		progrssDialog.show();
	}
	/**
	 * ֹͣdialog
	 */
	public static void stopProgressDialog() {
		if (progrssDialog != null) {
			progrssDialog.dismiss();
			progrssDialog = null;
		}
	}

	/**
	 * ת��ͼƬ��Բ��
	 * 
	 * @param bitmap
	 *            ����Bitmap����
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// ���û����޾��

		canvas.drawARGB(0, 0, 0, 0); // �������Canvas

		// ���������ַ�����Բ,drawRounRect��drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// ��Բ�Ǿ��Σ���һ������Ϊͼ����ʾ���򣬵ڶ��������͵����������ֱ���ˮƽԲ�ǰ뾶�ʹ�ֱԲ�ǰ뾶��
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// ��������ͼƬ�ཻʱ��ģʽ,�ο�http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // ��Mode.SRC_INģʽ�ϲ�bitmap���Ѿ�draw�˵�Circle
		// output.recycle();
		return output;
	}
	/**
	 * ��ȡ����˽Կ
	 * @param context context����
	 * @return	��ȡ��˽Կ
	 */
	public static String readPrivateKeyFile(Context context) {
		// String key = context.getResources().getString(R.string.PUBLIC_KEY);
		StringBuffer sb = new StringBuffer();
		String fileName = "privateKey.txt";
		InputStream is = null;
		BufferedReader bufferedReader = null;
		InputStreamReader reader = null;
		try {
			is = context.getResources().getAssets().open(fileName);
			reader = new InputStreamReader(is);
			bufferedReader = new BufferedReader(reader);
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				reader.close();
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("�ر�������");
			}
		}
		return sb.toString();
	}
	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * @param str
	 * @return
	 */
	public static boolean isNotNull(String str) {
		return null != str && !str.trim().equals("") ? true : false;
	}
	/**
	 * MD5���ܷ�ʽ
	 * @param plainText		��Ҫ���ܵ��ַ���
	 * @return ���ܺ���ַ���
	 */
	public static String Md5(String plainText) {
		String encryptedString = "";
		try {
			byte[] strByte = plainText.getBytes("UTF-8");
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(strByte);
			byte[] md5Byte = algorithm.digest();

			StringBuffer sb = new StringBuffer();
			String hex = "";
			for (int i = 0; i < md5Byte.length; i++) {
				hex = Integer.toHexString(0Xff & md5Byte[i]);
				if (hex.length() == 1) { // ����ô������������md5��С��32λ�������
					sb.append('0');
				}
				sb.append(hex);
				// sb.append(Integer.toHexString(0XFF & md5Byte[i]));
			}
			encryptedString = sb.toString();
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encryptedString;
	}
	/**
	 * ���ܵ绰����
	 * @param str	δ���ܵĵ绰����
	 * @return	�Ѽ��ܵĵ绰����
	 */
	public static String hidePhone(String str) {
		String restr = "";
		char[] strs = str.toCharArray();
		strs[3] = '*';
		strs[4] = '*';
		strs[5] = '*';
		strs[6] = '*';
		for (int i = 0; i < strs.length; i++) {
			restr += strs[i];
		}
		return restr;
	}
	/**
	 * ��������
	 * @param str	δ���ܵ�����
	 * @return	�Ѽ��ܵ�����
	 */
	public static String hideName(String str) {
		String restr = "";
		char[] strs = str.toCharArray();
		strs[0] = '*';
		for (int i = 0; i < strs.length; i++) {
			restr += strs[i];
		}
		return restr;
	}
	/**
	 * ��ʾsharedSDK����
	 * @param context	context����
	 */
	@SuppressLint("SdCardPath") 
	public static void shard(Context context) {
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// �ر�sso��Ȩ
		oks.disableSSOWhenAuthorize();
		// ����ʱNotification��ͼ������� 2.5.9�Ժ�İ汾�����ô˷���
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		oks.setTitle("����");
		// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		oks.setTitleUrl("http://sharesdk.cn");
		// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		oks.setText("���Ƿ����ı�");
		// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		oks.setImagePath("/sdcard/test.jpg");// ȷ��SDcar
												// d������ڴ���ͼƬ
		// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		oks.setUrl("http://sharesdk.cn");
		// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		oks.setComment("���ǲ��������ı�");
		// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		oks.setSite("΢��");
		// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		oks.setSiteUrl("http://sharesdk.cn");
		// ��������GUI
		oks.show(context);
	}
	/**
	 * ͨ��������ʽ�ж��Ƿ�Ϊ�ֻ���
	 * @param mobiles	��Ҫ�жϵ��ֻ���
	 * @return	���Ϲ�����ַ���
	 */
	public static boolean isPhone(String mobiles) {
		Pattern p = Pattern.compile("^((13)|(15)|(18))\\d{9}$");  
		Matcher m = p.matcher(mobiles);  
		return m.matches();  
	}
	/**
	 * ��ȡsdcard·��
	 * @return	sdcard·��
	 */
	public static String getSDCardPath() {
		File sdcardDir = null;
		// �ж�SDCard�Ƿ����
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		String path = sdcardDir.toString() + "/weiyin/ticket/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}
}