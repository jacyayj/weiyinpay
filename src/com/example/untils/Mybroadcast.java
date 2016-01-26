package com.example.untils;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.example.cz.MainActivity;
import com.example.cz.R;
	/**
	 * �㲥��Ϣ����
	 * @author jacyayj
	 *
	 */
public class Mybroadcast extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		// ֪ͨ����
		Bundle bundle = intent.getExtras();
		Log.d("jjhjh", "onReceive - " + intent.getAction());
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			bundle.getString(JPushInterface.EXTRA_EXTRA);
			bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
			bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
			bundle.getString(JPushInterface.EXTRA_MSG_ID);
			// System.out.println("000000000000000000"+JPushInterface.EXTRA_MESSAGE);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			String title = bundle
					.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			if (content != null && title != null) {
				LocalDOM.getinstance().write(context, "news", "has", "you");
			}
			bundle.getString(JPushInterface.EXTRA_EXTRA);
			bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
			bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			// �����������Щͳ�ƣ�������Щ��������"
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			// �������������������֪ͨ���ݡ�
			bundle.getString(JPushInterface.EXTRA_EXTRA);
			// ����������Լ�д����ȥ�����û���������Ϊ
			Intent i = new Intent(); // �Զ���򿪵Ľ���
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setClass(context, MainActivity.class);
			context.startActivity(i);
		} else {
			Log.d("hhhh", "Unhandled intent - " + intent.getAction());
		}
		// �Զ���֪ͨ����ʽ
		BasicPushNotificationBuilder builder1 = new BasicPushNotificationBuilder(
				context);
		builder1.statusBarDrawable = R.drawable.logo;
		builder1.notificationFlags = Notification.FLAG_AUTO_CANCEL; // ����Ϊ�Զ���ʧ
		builder1.notificationDefaults = Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS; // ����Ϊ�������𶯶�Ҫ
		JPushInterface.setPushNotificationBuilder(1, builder1);
	}
}
