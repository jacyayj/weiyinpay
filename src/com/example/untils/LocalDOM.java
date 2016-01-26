package com.example.untils;

import android.content.Context;
import android.content.SharedPreferences;
	/**
	 * sharedprefences������
	 * @author jacyayj
	 *
	 */
public class LocalDOM {
	private SharedPreferences preferences;

	private LocalDOM() {
	}
	/**
	 * ��̬
	 * @return ���������
	 */
	public static LocalDOM getinstance() {
		return new LocalDOM();
	}
	/**
	 * д�뱾������
	 * @param context context����
	 * @param name	�ļ�����
	 * @param key	�洢��keyֵ
	 * @param value	��Ҫд���value
	 */
	public void write(Context context, String name, String key, String value) {
		preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
		editor.clear();
	}
	/**
	 * ��ȡ��������
	 * @param context
	 * @param name
	 * @param key
	 * @return
	 */
	public String read(Context context, String name, String key) {
		preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		String temp = preferences.getString(key, "��");
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
