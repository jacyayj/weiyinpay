package cn.sharesdk.onekeyshare;

import static com.mob.tools.utils.R.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import com.mob.tools.FakeActivity;

/** 摇一摇启动分享的例子 */
public class Shake2Share extends FakeActivity implements SensorEventListener {
	// 检测的时间间隔
	private static final int UPDATE_INTERVAL = 100;
	// 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感
	private static final int SHAKE_THRESHOLD = 1500;

	private OnShakeListener listener;
	private SensorManager mSensorManager;
	private long mLastUpdateTime;
	private float mLastX;
	private float mLastY;
	private float mLastZ;
	private boolean shaken;

	public void setOnShakeListener(OnShakeListener listener) {
		this.listener = listener;
	}

	@Override
	public void setActivity(Activity activity) {
		super.setActivity(activity);
		int resId = getBitmapRes(activity, "ssdk_oks_shake_to_share_back");
		if (resId > 0) {
			activity.setTheme(android.R.style.Theme_Dialog);
			activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			Window win = activity.getWindow();
			win.setBackgroundDrawableResource(resId);
		}
	}

	@Override
	public void onCreate() {
		startSensor();

		int resId = getBitmapRes(activity, "ssdk_oks_yaoyiyao");
		if (resId > 0) {
			ImageView iv = new ImageView(activity);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			iv.setImageResource(resId);
			activity.setContentView(iv);
		}

		resId = getStringRes(activity, "shake2share");
		if (resId > 0) {
			Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
		}
	}

	private void startSensor() {
		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager == null) {
			throw new UnsupportedOperationException();
		}
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (sensor == null) {
			throw new UnsupportedOperationException();
		}
		boolean success = mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		if (!success) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void onDestroy() {
		stopSensor();
	}

	private void stopSensor() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
			mSensorManager = null;
		}
	}

	@Override
	@SuppressLint("FloatMath")
	public void onSensorChanged(SensorEvent event) {
		long currentTime = System.currentTimeMillis();
		long diffTime = currentTime - mLastUpdateTime;
		if (diffTime > UPDATE_INTERVAL) {
			if(mLastUpdateTime != 0) {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
				float deltaX = x - mLastX;
				float deltaY = y - mLastY;
				float deltaZ = z - mLastZ;
				float delta = FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000;
				if (delta > SHAKE_THRESHOLD) {
					if (!shaken) {
						shaken = true;
						finish();
					}

					if (listener != null) {
						listener.onShake();
					}
				}
				mLastX = x;
				mLastY = y;
				mLastZ = z;
			}
			mLastUpdateTime = currentTime;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public static interface OnShakeListener {
		public void onShake();
	}

}
