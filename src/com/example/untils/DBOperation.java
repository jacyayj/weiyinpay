package com.example.untils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOperation extends SQLiteOpenHelper {

	public DBOperation(Context context, String name,
			int version) {
		super(context, name, null, version);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO 自动生成的方法存根
		String sqstrt = "create table news(_id integer primarykey,ftitle varchar, fcontent varchar,fdate varchar,fstatus varchar)";
		arg0.execSQL(sqstrt);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO 自动生成的方法存根

	}

}
