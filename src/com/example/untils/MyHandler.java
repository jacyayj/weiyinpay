package com.example.untils;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class MyHandler extends Handler {

	WeakReference<Activity> reference;

	public MyHandler(Activity activity) {
		reference = new WeakReference<Activity>(activity);
	}

	@Override
	public void handleMessage(Message msg) {
		// final Activity activity = reference.get();
	}
}
