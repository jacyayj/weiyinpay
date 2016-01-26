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
	 * 工具类
	 * @author jacyayj
	 *
	 */
public class AllUtils {
	public static myProgrssDialog progrssDialog;
	/**
	 * @param strResult
	 *            需要解析的json字符串
	 * @param key
	 *            要取的json数据的key值
	 * @return 解析完成成功之后返回结果list，失败则直接将语句返回 解析json字符串
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
				result.add("服务器错误！");
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
	 * 获取随机数
	 * @return 得到的随机数
	 */
	public static String getRandom() {
		return String.valueOf((int) (Math.random() * 900) + 100);
	}
	/**
	 * 显示toast信息
	 * @param context 要显示的窗口
	 * @param msg	需要显示的消息
	 */
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 正则表达式判断
	 * @param str	需要判断的字符串
	 * @return	符合正则的字符串
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
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
	 * 显示进度框
	 * @param context	显示的窗口
	 * @param msg	显示的消息
	 */
	public static void startProgressDialog(Context context, String msg) {
		if (progrssDialog == null) {
			progrssDialog = myProgrssDialog.createDialog(context);
			progrssDialog.setMessage(msg);
		}
		progrssDialog.show();
	}
	/**
	 * 停止dialog
	 */
	public static void stopProgressDialog() {
		if (progrssDialog != null) {
			progrssDialog.dismiss();
			progrssDialog = null;
		}
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
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

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
		// output.recycle();
		return output;
	}
	/**
	 * 读取加密私钥
	 * @param context context对象
	 * @return	读取的私钥
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
				System.out.println("关闭流错误");
			}
		}
		return sb.toString();
	}
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNotNull(String str) {
		return null != str && !str.trim().equals("") ? true : false;
	}
	/**
	 * MD5加密方式
	 * @param plainText		需要加密的字符串
	 * @return 加密后的字符串
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
				if (hex.length() == 1) { // 不这么处理会出现最后的md5码小于32位的情况。
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
	 * 加密电话号码
	 * @param str	未加密的电话号码
	 * @return	已加密的电话号码
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
	 * 加密姓名
	 * @param str	未加密的姓名
	 * @return	已加密的姓名
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
	 * 显示sharedSDK分享
	 * @param context	context对象
	 */
	@SuppressLint("SdCardPath") 
	public static void shard(Context context) {
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("分享");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");// 确保SDcar
												// d下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("微银");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		// 启动分享GUI
		oks.show(context);
	}
	/**
	 * 通过正则表达式判断是否为手机号
	 * @param mobiles	需要判断的手机号
	 * @return	符合规则的字符串
	 */
	public static boolean isPhone(String mobiles) {
		Pattern p = Pattern.compile("^((13)|(15)|(18))\\d{9}$");  
		Matcher m = p.matcher(mobiles);  
		return m.matches();  
	}
	/**
	 * 获取sdcard路径
	 * @return	sdcard路径
	 */
	public static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
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