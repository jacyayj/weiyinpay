package com.example.untils;

import android.content.Context;
import android.content.SharedPreferences;
	/**
	 * sharedprefences工具类
	 * @author jacyayj
	 *
	 */
public class LocalDOM {
	private SharedPreferences preferences;

	private LocalDOM() {
	}
	/**
	 * 单态
	 * @return 工具类对象
	 */
	public static LocalDOM getinstance() {
		return new LocalDOM();
	}
	/**
	 * 写入本地数据
	 * @param context context对象
	 * @param name	文件名称
	 * @param key	存储的key值
	 * @param value	需要写入的value
	 */
	public void write(Context context, String name, String key, String value) {
		preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
		editor.clear();
	}
	/**
	 * 读取本地数据
	 * @param context
	 * @param name
	 * @param key
	 * @return
	 */
	public String read(Context context, String name, String key) {
		preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		String temp = preferences.getString(key, "无");
		return temp;
	}

	public void clear(Context context, String name) {
		preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
		editor.clear();
	}
}
