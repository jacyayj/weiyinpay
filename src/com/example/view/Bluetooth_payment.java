package com.example.view;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.wisepad.WisePadController;
import com.bbpos.wisepad.WisePadController.BatteryStatus;
import com.bbpos.wisepad.WisePadController.CheckCardMode;
import com.bbpos.wisepad.WisePadController.CheckCardResult;
import com.bbpos.wisepad.WisePadController.ConnectionMode;
import com.bbpos.wisepad.WisePadController.CurrencyCharacter;
import com.bbpos.wisepad.WisePadController.DisplayText;
import com.bbpos.wisepad.WisePadController.EmvOption;
import com.bbpos.wisepad.WisePadController.Error;
import com.bbpos.wisepad.WisePadController.PhoneEntryResult;
import com.bbpos.wisepad.WisePadController.PinEntryResult;
import com.bbpos.wisepad.WisePadController.PinEntrySource;
import com.bbpos.wisepad.WisePadController.PrintResult;
import com.bbpos.wisepad.WisePadController.StartEmvResult;
import com.bbpos.wisepad.WisePadController.TerminalSettingStatus;
import com.bbpos.wisepad.WisePadController.TransactionResult;
import com.bbpos.wisepad.WisePadController.TransactionType;
import com.bbpos.wisepad.WisePadController.WisePadControllerListener;
import com.example.cz.R;
import com.example.modle.TradeModle;
import com.example.untils.AllUtils;
import com.example.untils.DesCipher;
import com.example.untils.EmvData;
import com.example.untils.LocalDOM;
	/**
	 * 蓝牙POS刷卡页面
	 * @author jacyayj
	 *
	 */
@SuppressWarnings("deprecation")
public class Bluetooth_payment extends Activity {
	
	private Window dialogWindow = null;
	private TextView money = null;
	private EditText pwd = null;
	private Button cencle = null;
	private Button ok = null;
	private String pass = null;
	private ImageView ds[] = null;
	private LocalDOM dom = null;
	
	private boolean isIC = false;
	
	private final String[] DEVICE_NAMES = new String[] { "WisePad", "WP", "MPOS", "M360", "M368", "M188" };
	private Dialog dialog;
	private Dialog pindialog = null;
	private View back = null;
	private WisePadController wisePadController;
	private MyWisePadControllerListener listener;
	
	private boolean isPinCanceled = false;
	
	private ArrayAdapter<String> arrayAdapter;
	private List<BluetoothDevice> foundDevices;
	
	private Map<String,String> emvdata = null;
	
	private TradeModle modle = null;
	private DesCipher cipher =null;
	
	private String masterKey = "11223344556677889900AABBCCDDEEFF";
	
	private String pinSessionKey = "A1223344556677889900AABBCCDDEEFF";
	private String encryptedPinSessionKey = "";
	private String pinKcv = "";
	
	private String dataSessionKey = "A2223344556677889900AABBCCDDEEFF";
	private String encryptedDataSessionKey = "";
	private String dataKcv = "";
	
	private String trackSessionKey = "A4223344556677889900AABBCCDDEEFF";
	private String encryptedTrackSessionKey = "";
	private String trackKcv = "";
	
	private String macSessionKey = "A6223344556677889900AABBCCDDEEFF";
	private String encryptedMacSessionKey = "";
	private String macKcv = "";
	
	/**
	 * 初始化页面及所需工具
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.bluetooth_payment_activity);
        modle = TradeModle.getInstance();
        ((TextView)findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
    	back = findViewById(R.id.pay_back);
        listener = new MyWisePadControllerListener();
        wisePadController = WisePadController.getInstance(this, listener);
        dom = LocalDOM.getinstance();
		startConnection();
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
    }
    /**
     * 监控页面状态，当页面销毁时断开设备
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	wisePadController.stopBTv2();
    }
    /**
     * 加密方法
     * @param data
     * @param key
     * @return
     */
    @SuppressLint("TrulyRandom") 
    public String encrypt(String data, String key) {
    	if(key.length() == 16) {
    		key += key.substring(0, 8);
    	}
    	byte[] d = hexToByteArray(data);
    	byte[] k = hexToByteArray(key);
    	
    	SecretKey sk = new SecretKeySpec(k, "DESede");
    	try {
    		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
    		cipher.init(Cipher.ENCRYPT_MODE, sk);
			byte[] enc = cipher.doFinal(d);
			return toHexString(enc);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	return null;
    }
    
    public void injectNextSessionKey() {
		if(!encryptedPinSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "1");
			data.put("encSK", encryptedPinSessionKey);
			data.put("kcv", pinKcv);
			System.out.println("正在传输秘钥");
			encryptedPinSessionKey = "";  
			wisePadController.injectSessionKey(data);
			return;
		}
		
		if(!encryptedDataSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "2");
			data.put("encSK", encryptedDataSessionKey);
			data.put("kcv", dataKcv);
			System.out.println("正在传输秘钥");
			encryptedDataSessionKey = "";
			wisePadController.injectSessionKey(data);
			return;
		}
		
		if(!encryptedTrackSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "3");
			data.put("encSK", encryptedTrackSessionKey);
			data.put("kcv", trackKcv);
			System.out.println("正在传输秘钥");
			encryptedTrackSessionKey = "";
			wisePadController.injectSessionKey(data);
			return;
		}
		
		if(!encryptedMacSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "4");
			data.put("encSK", encryptedMacSessionKey);
			data.put("kcv", macKcv);
			System.out.println("正在传输秘钥");
			encryptedMacSessionKey = "";
			wisePadController.injectSessionKey(data);
			return;
		}
	}
    /**
     * 断开设备连接方法
     */
	public void stopConnection() {
		ConnectionMode connectionMode = wisePadController.getConnectionMode();
		if(connectionMode == ConnectionMode.BLUETOOTH_2) {
			wisePadController.stopBTv2();
		} else if(connectionMode == ConnectionMode.BLUETOOTH_4) {
			wisePadController.disconnectBTv4();
		} else if(connectionMode == ConnectionMode.AUDIO) {
			wisePadController.stopAudio();
		}
	}
	/**
	 * 开启设备连接
	 */
    private void startConnection() {
    	AllUtils.startProgressDialog(Bluetooth_payment.this,"设备连接中");
    	Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
		final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
		for(int i = 0; i < pairedObjects.length; ++i) {
			pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
		}

		final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(Bluetooth_payment.this, android.R.layout.simple_list_item_1);
		for (int i = 0; i < pairedDevices.length; ++i) {
			mArrayAdapter.add(pairedDevices[i].getName());
		}
		
		dismissDialog();
		
		dialog = new Dialog(Bluetooth_payment.this);
		dialog.setContentView(R.layout.bluetooth_2_device_list_dialog);
		dialog.setTitle(R.string.bluetooth_devices);
		
		ListView listView1 = (ListView)dialog.findViewById(R.id.pairedDeviceList);
		listView1.setAdapter(mArrayAdapter);
		listView1.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AllUtils.toast(getBaseContext(), "设备连接中");
				wisePadController.startBTv2(pairedDevices[position]);
				dismissDialog();
			}
		});
		arrayAdapter = new ArrayAdapter<String>(Bluetooth_payment.this, android.R.layout.simple_list_item_1);
		ListView listView2 = (ListView)dialog.findViewById(R.id.discoveredDeviceList);
		listView2.setAdapter(arrayAdapter);
		listView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AllUtils.toast(getBaseContext(), "设备连接中");
				wisePadController.startBTv2(foundDevices.get(position));
				dismissDialog();
			}
		});
		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
			
			@Override
		          	public void onClick(View v) {
				wisePadController.stopScanBTv2();
				dismissDialog();
			}
		});
		dialog.setCancelable(false);
		dialog.show();
		wisePadController.scanBTv2(DEVICE_NAMES, 120);
	}
    
    private static byte[] hexToByteArray(String s) {
		if(s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}
    
    private static String toHexString(byte[] b) {
		if(b == null) {
			return "null";
		}
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xFF ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
    
    public void dismissDialog() {
    	if(dialog != null) {
    		dialog.dismiss();
    		dialog = null;
    	}
    }
    /**
     * BPOS数卡流程
     * @author jacyayj
     *
     */
    class MyWisePadControllerListener implements WisePadControllerListener {
		/**
		 * 等待用户刷卡
		 */
		@Override
		public void onWaitingForCard(CheckCardMode checkCardMode) {
				AllUtils.toast(getBaseContext(), "请刷卡或插卡");
		}
		/**
		 * 蓝牙设备连接成功（2.0）
		 */
		@Override
		public void onBTv2Connected(BluetoothDevice bluetoothDevice) {
			AllUtils.toast(getBaseContext(), "设备已连接（蓝牙2）");
			AllUtils.stopProgressDialog();
			start();
		}
		/**
		 * 搜索蓝牙（2.0）
		 */
		@Override
		public void onBTv2Detected() {
			AllUtils.toast(getBaseContext(), "蓝牙搜索中");
		}
		/**
		 * 搜索到新蓝牙，更新界面（2.0）
		 */
		@Override
		public void onBTv2DeviceListRefresh(List<BluetoothDevice> foundDevices) {
			Bluetooth_payment.this.foundDevices = foundDevices;
			if(arrayAdapter != null) {
				arrayAdapter.clear();
				for(int i = 0; i < foundDevices.size(); ++i) {
					arrayAdapter.add(foundDevices.get(i).getName());
				}
				arrayAdapter.notifyDataSetChanged();
			}
		}
		/**
		 * 断开连接（2.0）
		 */
		@Override
		public void onBTv2Disconnected() {
			AllUtils.toast(getBaseContext(), "设备已断开");	
		}
		/**
		 * 停止搜索（2.0）
		 */
		@Override
		public void onBTv2ScanStopped() {
			AllUtils.toast(getBaseContext(), "停止搜索");	
		}
		/**
		 * 搜索超时（2.0）
		 */
		@Override
		public void onBTv2ScanTimeout() {
			AllUtils.toast(getBaseContext(), "搜索超时");
		}
		/**
		 * 判断刷卡状态与卡片类型
		 */
		@Override
		public void onReturnMagStripeCardNumber(CheckCardResult checkCardResult, String cardNumber) {
			System.out.println("onReturnMagStripeCardNumber卡号:"+cardNumber);
    		if(checkCardResult == CheckCardResult.NONE) {
    			AllUtils.toast(getBaseContext(), "刷卡或插卡超时");
			} else if(checkCardResult == CheckCardResult.ICC) {
				AllUtils.toast(getBaseContext(), "IC卡已插入");
			} else if(checkCardResult == CheckCardResult.NOT_ICC) {
				AllUtils.toast(getBaseContext(), "不是IC卡，请插卡");
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				AllUtils.toast(getBaseContext(), "失败请重新刷卡");
				start();
			} else if(checkCardResult == CheckCardResult.MCR) {
				System.out.println("卡号"+cardNumber);
			} else if(checkCardResult == CheckCardResult.NO_RESPONSE) {
				AllUtils.toast(getBaseContext(), "刷卡或插卡不正常，请重试");
			} else if(checkCardResult == CheckCardResult.TRACK2_ONLY) {
				System.out.println("卡号"+cardNumber);
			}
		}
		/**
		 * 蓝牙连接成功（4.0）
		 */
		@Override
		public void onBTv4Connected() {
			AllUtils.toast(getBaseContext(), "设备已连接（蓝牙4）");
		}
		/**
		 * 找到新的蓝牙并更新界面（4.0）
		 */
		@Override
		public void onBTv4DeviceListRefresh(List<BluetoothDevice> foundDevices) {
			Bluetooth_payment.this.foundDevices = foundDevices;
			if(arrayAdapter != null) {
				arrayAdapter.clear();
				for(int i = 0; i < foundDevices.size(); ++i) {
					arrayAdapter.add(foundDevices.get(i).getName());
				}
				arrayAdapter.notifyDataSetChanged();
			}
		}
		/**
		 * 断开连接（4.0）
		 */
		@Override
		public void onBTv4Disconnected() {
			AllUtils.toast(getBaseContext(), "设备已断开");
		}
		/**
		 * 停止搜索（4.0）
		 */
		@Override
		public void onBTv4ScanStopped() {
			AllUtils.toast(getBaseContext(), "停止搜索");		
		}
		/**
		 * 搜索超时（4.0）
		 */
		@Override
		public void onBTv4ScanTimeout() {
			AllUtils.toast(getBaseContext(), "搜索超时");
		}
		/**
		 * 刷卡结果返回数据
		 */
		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
    		if(checkCardResult == CheckCardResult.NONE) {
    			AllUtils.toast(getBaseContext(), "刷卡或插卡超时");
			} else if(checkCardResult == CheckCardResult.ICC) {
				isIC = true;
				AllUtils.toast(getBaseContext(), "IC卡刷卡中");
				System.out.println("IC卡");
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("emvOption", EmvOption.START);
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_INSERT);
			    if ("addacc".equals(dom.read(getBaseContext(), "user","from"))) {
					wisePadController.getEmvCardData();
				}else if ("visa".equals(dom.read(getBaseContext(), "user","from"))) {
					wisePadController.getEmvCardData();
				}else {
					emvdata = new HashMap<String, String>();
					cipher = new DesCipher();
					wisePadController.startEmv(data);
				}
			} else if(checkCardResult == CheckCardResult.NOT_ICC) {
				AllUtils.toast(getBaseContext(), "不是IC卡，请刷卡");
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				AllUtils.toast(getBaseContext(), "刷卡不良好");
			} else if(checkCardResult == CheckCardResult.MCR) {
				isIC = false;
				AllUtils.toast(getBaseContext(), "磁条卡刷卡中");
				System.out.println("磁条卡");
				emvdata = new HashMap<String, String>();
				cipher = new DesCipher();
				String content = "卡信息：" + "\n";
				Object[] keys = decodeData.keySet().toArray();
				Arrays.sort(keys);
				for(Object key : keys) {
					String value = decodeData.get(key);
					content += key + ": " + value + "\n";
					emvdata.put((String) key, value);
				}
				System.out.println(content);
				String serviceCode = decodeData.get("serviceCode");
				modle.setId(decodeData.get("PAN"));
				modle.setSencondno(decodeData.get("encTrack2"));
				modle.setThirdno(decodeData.get("encTrack3"));
				modle.setPosid(decodeData.get("ksn"));
				modle.setDate(decodeData.get("expiryDate"));
				if(serviceCode.endsWith("0") || serviceCode.endsWith("6")) {
					input();
				}
			} else if(checkCardResult == CheckCardResult.NO_RESPONSE) {
				AllUtils.toast(getBaseContext(), "刷卡或插卡不正常，请重试");
			} else if(checkCardResult == CheckCardResult.TRACK2_ONLY) {
				System.out.println("只有2磁道");
				AllUtils.toast(getBaseContext(), "磁条卡刷卡中");
				System.out.println("磁条卡");
				emvdata = new HashMap<String, String>();
				cipher = new DesCipher();
				String content = "卡信息：" + "\n";
				Object[] keys = decodeData.keySet().toArray();
				Arrays.sort(keys);
				for(Object key : keys) {
					String value = decodeData.get(key);
					content += key + ": " + value + "\n";
					emvdata.put((String) key, value);
				}
				System.out.println(content);
				String serviceCode = decodeData.get("serviceCode");
				modle.setId(decodeData.get("PAN"));
				modle.setSencondno(decodeData.get("encTrack2"));
				modle.setThirdno(decodeData.get("encTrack3"));
				modle.setPosid(decodeData.get("ksn"));
				modle.setDate(decodeData.get("expiryDate"));
				if(serviceCode.endsWith("0") || serviceCode.endsWith("6")) {
							input();
				}
			} else if(checkCardResult == CheckCardResult.USE_ICC_CARD) {
				AllUtils.toast(getBaseContext(), "请用IC卡");
			} else if(checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
				AllUtils.toast(getBaseContext(), "监测到拍卡");
			}
		}
    	/**
    	 * 取消刷卡操作
    	 */
    	@Override
    	public void onReturnCancelCheckCardResult(boolean isSuccess) {
    		if(isSuccess) {
    			AllUtils.toast(getBaseContext(), "取消读卡成功");
    		} else {
    			AllUtils.toast(getBaseContext(), "取消读卡失败");
    		}
    	}
    	/**
    	 * 开启IC卡emv交易状态
    	 */
		@Override
    	public void onReturnStartEmvResult(StartEmvResult startEmvResult, String ksn) {
    		if(startEmvResult == StartEmvResult.SUCCESS) {
    			AllUtils.toast(getBaseContext(), "EMV启动成功");
    		} else {
    			AllUtils.toast(getBaseContext(), "EMV启动失败");
    		}
    	}
		/**
		 * 返回设备信息
		 */
    	@Override
		public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
    		String isSupportedTrack1 = deviceInfoData.get("isSupportedTrack1");
			String isSupportedTrack2 = deviceInfoData.get("isSupportedTrack2");
			String isSupportedTrack3 = deviceInfoData.get("isSupportedTrack3");
			String bootloaderVersion = deviceInfoData.get("bootloaderVersion");
			String firmwareVersion = deviceInfoData.get("firmwareVersion");
			String mainProcessorVersion = deviceInfoData.get("mainProcessorVersion");
			String coProcessorVersion = deviceInfoData.get("coprocessorVersion");
			String isUsbConnected = deviceInfoData.get("isUsbConnected");
			String isCharging = deviceInfoData.get("isCharging");
			String batteryLevel = deviceInfoData.get("batteryLevel");
			String batteryPercentage = deviceInfoData.get("batteryPercentage");
			String hardwareVersion = deviceInfoData.get("hardwareVersion");
			String productId = deviceInfoData.get("productId");
			String pinKsn = deviceInfoData.get("pinKsn");
			String emvKsn = deviceInfoData.get("emvKsn");
			String trackKsn = deviceInfoData.get("trackKsn");
			String csn = deviceInfoData.get("csn");
			String vendorID = deviceInfoData.get("vendorID");
			String terminalSettingVersion = deviceInfoData.get("terminalSettingVersion");
			String deviceSettingVersion = deviceInfoData.get("deviceSettingVersion");
			String formatID = deviceInfoData.get("formatID");
			
			String content = "";
			content += getString(R.string.format_id) + formatID + "\n";
			content += getString(R.string.bootloader_version) + bootloaderVersion + "\n";
			content += getString(R.string.firmware_version) + firmwareVersion + "\n";
			content += getString(R.string.main_processor_version) + mainProcessorVersion + "\n";
			content += getString(R.string.coprocessor_version) + coProcessorVersion + "\n";
			content += getString(R.string.usb) + isUsbConnected + "\n";
			content += getString(R.string.charge) + isCharging + "\n";
			content += getString(R.string.battery_level) + batteryLevel + "\n";
			content += getString(R.string.battery_percentage) + batteryPercentage + "\n";
			content += getString(R.string.hardware_version) + hardwareVersion + "\n";
			content += getString(R.string.track_1_supported) + isSupportedTrack1 + "\n";
			content += getString(R.string.track_2_supported) + isSupportedTrack2 + "\n";
			content += getString(R.string.track_3_supported) + isSupportedTrack3 + "\n";
			content += getString(R.string.product_id) + productId + "\n";
			content += getString(R.string.pin_ksn) + pinKsn + "\n";
			content += getString(R.string.emv_ksn) + emvKsn + "\n";
			content += getString(R.string.track_ksn) + trackKsn + "\n";
			content += getString(R.string.csn) + csn + "\n";
			content += getString(R.string.vendor_id) + vendorID + "\n";
			content += getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
			content += getString(R.string.device_setting_version) + deviceSettingVersion + "\n";
			System.out.println("具体数据2"+content);
		}
		
    	@Override
		public void onReturnTransactionResult(TransactionResult transactionResult) {
		}
    	/**
    	 * 交易返回数据
    	 */
		@Override
		public void onReturnTransactionResult(TransactionResult transactionResult, Hashtable<String, String> data) {
			if(transactionResult == TransactionResult.APPROVED) {
				AllUtils.toast(getBaseContext(), "刷卡成功");
				System.out.println("成功");
//				if (modle.getPwd()!=null) {
				System.out.println("二磁道"+modle.getSencondno()+"\n三磁道"+modle.getThirdno()+"\n卡号"+modle.getId());
				if ("addacc".equals(dom.read(getBaseContext(), "user","from"))) {
					AllUtils.stopProgressDialog();
					dom.write(getBaseContext(), "user","from","slot");
					finish();
				}else if ("visa".equals(dom.read(getBaseContext(), "user","from"))) {
					AllUtils.stopProgressDialog();
					startActivity(new Intent(Bluetooth_payment.this,VisaAttest1.class));
					finish();
				}else {
					AllUtils.stopProgressDialog();
					startActivity(new Intent(Bluetooth_payment.this, Sign.class));
					Map<String,String> map = new HashMap<String, String>();
					map = cipher.decrypt(cipher.encrypt(emvdata));
					System.out.println(cipher.encrypt(emvdata)+"\n"+map);
					System.out.println("卡号："+map.get("maskedPAN"));
					finish();
				}
//				}else {
//					System.out.println("二磁道"+modle.getSencondno()+"\n三磁道"+modle.getThirdno()+"\n卡号"+modle.getId());
//					AllUtils.toast(getBaseContext(), "空");
//				}
			} else if(transactionResult == TransactionResult.TERMINATED) {
				AllUtils.toast(getBaseContext(), "操作终止");
			} else if(transactionResult == TransactionResult.DECLINED) {
				AllUtils.toast(getBaseContext(), "操作不成功");
				System.out.println("进来了?");
			} else if(transactionResult == TransactionResult.CANCEL) {
				AllUtils.toast(getBaseContext(), "操作取消");
			} else if(transactionResult == TransactionResult.CAPK_FAIL) {
				AllUtils.toast(getBaseContext(), "操作不成功（CAPK失败）");
			} else if(transactionResult == TransactionResult.NOT_ICC) {
				AllUtils.toast(getBaseContext(), "操作不成功（不是IC卡）");
			} else if(transactionResult == TransactionResult.SELECT_APP_FAIL) {
				AllUtils.toast(getBaseContext(), "操作不成功（APP失败）");
			} else if(transactionResult == TransactionResult.DEVICE_ERROR) {
				AllUtils.toast(getBaseContext(), "操作不成功，设备出错");
			} else if(transactionResult == TransactionResult.APPLICATION_BLOCKED) {
				AllUtils.toast(getBaseContext(), "操作不成功，APP一杯停用");
			} else if(transactionResult == TransactionResult.ICC_CARD_REMOVED) {
				AllUtils.toast(getBaseContext(), "操作不成功，IC卡已被移除");
			} else if(transactionResult == TransactionResult.CARD_BLOCKED) {
				AllUtils.toast(getBaseContext(), "操作不成功，该卡已被锁");
			} else if(transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
				AllUtils.toast(getBaseContext(), "操作不成功，暂不支持该卡");
			} else if(transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
				AllUtils.toast(getBaseContext(), "操作不成功，条件不符合");
			} else if(transactionResult == TransactionResult.INVALID_ICC_DATA) {
				AllUtils.toast(getBaseContext(), "操作不成功，无效的IC卡数据");
			} else if(transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
				AllUtils.toast(getBaseContext(), "操作不成功，缺少必要的数据");
			} else if(transactionResult == TransactionResult.NO_EMV_APPS) {
				AllUtils.toast(getBaseContext(), "操作不成功，无EMV应用程序");
			}else {
				AllUtils.toast(getBaseContext(), transactionResult.name());
			}
			
			if(data.get("receiptData") != null) {
				System.out.println("单据详情"+data.get("receiptData"));
//				message += "\n" + getString(R.string.receipt_data) + "" + data.get("receiptData");
			}
		}
		/**
		 * 设备返回数据
		 */
		@Override
		public void onReturnBatchData(String tlv) {
			dismissDialog();
			String content = getString(R.string.batch_data) + "\n";
			Hashtable<String, String> decodeData = WisePadController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			for(Object key : keys) {
				String value = decodeData.get(key);
				content += key + ": " + value + "\n";
			}
			Log.d("=====批量数据（ReturnBatchData）======", content);
		}
		
		@Override
		public void onReturnTransactionLog(String tlv) {
			dismissDialog();
			String content = getString(R.string.transaction_log);
			content += tlv;
			Log.d("=====事务日志（ ReturnTransactionLog）======", content);
		}
		
		@Override
		public void onReturnReversalData(String tlv) {
			dismissDialog();
			String content = getString(R.string.reversal_data);
			content += tlv;
			Log.d("=====反转数据（ ReturnReversalData）======", content);
		}
		/**
		 * 确认金额
		 */
		@Override
		public void onReturnAmountConfirmResult(boolean isSuccess) {
			if(isSuccess) {
				System.out.println("金额已确认");
			} else {
				System.out.println("金额已取消");
			}
		}
		/**
		 * IC卡返回加密密码
		 */
		@Override
		public void onReturnPinEntryResult(PinEntryResult pinEntryResult, Hashtable<String, String> data) {
			System.out.println("这是返回密码？");
			if(pinEntryResult == PinEntryResult.ENTERED) {
				String content = getString(R.string.pin_entered);
				Object[] keys = data.keySet().toArray();
				Arrays.sort(keys);
				for(Object key : keys) {
					String value = data.get(key);
					emvdata.put((String) key, value);
					System.out.println("密码："+key + ": " + value + "\n");
				}
				if(data.containsKey("epb")) {
					content += "\n" + getString(R.string.epb) + data.get("epb");	
					modle.setPwd(data.get("epb"));
					System.out.println("密码"+data.get("epb"));
				}
				if(data.containsKey("ksn")) {
					content += "\n" + getString(R.string.ksn) + data.get("ksn");					
				}
				if(data.containsKey("randomNumber")) {
					content += "\n" + getString(R.string.random_number) + data.get("randomNumber");
					System.out.println("随机数"+data.get("randomNumber"));
				}
				if(data.containsKey("encWorkingKey")) {
					content += "\n" + getString(R.string.encrypted_working_key) + data.get("encWorkingKey");	
				}
				System.out.println("芯片卡pin："+content);
			} else if(pinEntryResult == PinEntryResult.BYPASS) {
				AllUtils.toast(getBaseContext(), "密码已略过");
			} else if(pinEntryResult == PinEntryResult.CANCEL) {
				AllUtils.toast(getBaseContext(), "密码已取消");
			} else if(pinEntryResult == PinEntryResult.TIMEOUT) {
				AllUtils.toast(getBaseContext(), "密码输入超时");
				AllUtils.stopProgressDialog();
			} else if(pinEntryResult == PinEntryResult.KEY_ERROR) {
				AllUtils.toast(getBaseContext(), "密码错误");
			} else if(pinEntryResult == PinEntryResult.NO_PIN) {
				AllUtils.toast(getBaseContext(), "没有密码");
			}else {
				AllUtils.toast(getBaseContext(), "密码错误");
			}
		}

		
		@Override
		public void onReturnAmount(Hashtable<String, String> data) {
			String amount = data.get("amount");
			String cashbackAmount = data.get("cashbackAmount");
			String currencyCode = data.get("currencyCode");
			
			String text = "";
			text += getString(R.string.amount_with_colon) + amount + "\n";
			text += getString(R.string.cashback_with_colon) + cashbackAmount + "\n";
			text += getString(R.string.currency_with_colon) + currencyCode + "\n";
			System.out.println(text);
		}
		
		@Override
		public void onReturnUpdateTerminalSettingResult(TerminalSettingStatus terminalSettingStatus) {
			dismissDialog();
			if(terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
				AllUtils.toast(getBaseContext(), "更新终端设置成功");
			} else if(terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
				AllUtils.toast(getBaseContext(), "更新终端设置失败，找不到标签");
			} else if(terminalSettingStatus == TerminalSettingStatus.LENGTH_INCORRECT) {
				AllUtils.toast(getBaseContext(), "更新终端设置失败，长度错误");
			} else if(terminalSettingStatus == TerminalSettingStatus.TLV_INCORRECT) {
				AllUtils.toast(getBaseContext(), "更新终端设置失败，TLV错误");
			} else if(terminalSettingStatus == TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
				AllUtils.toast(getBaseContext(), "更新终端设置失败，引导程序不支持");
			}
		}
		
		@Override
		public void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value) {
			dismissDialog();
			if(terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
				AllUtils.toast(getBaseContext(), "获取终端设置成功"+ "\n" + getString(R.string.value) + " " + value);
			} else if(terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
				AllUtils.toast(getBaseContext(), "获取终端设置失败，找不到标签");
			} else if(terminalSettingStatus == TerminalSettingStatus.TAG_INCORRECT) {
				AllUtils.toast(getBaseContext(), "获取终端设置失败，标签错误");
			}
		}
		@Override
		public void onReturnEnableInputAmountResult(boolean isSuccess) {
		}
		@Override
		public void onReturnDisableInputAmountResult(boolean isSuccess) {
		}
		@Override
		public void onReturnPhoneNumber(PhoneEntryResult phoneEntryResult, String phoneNumber) {
		}
		@Override
		public void onReturnEmvCardBalance(boolean isSuccess, String tlv) {
			if(isSuccess) {
				System.out.println("余额"+tlv);
			} else {
				System.out.println("查询余额失败");
			}			
		}
		/**
		 * IC卡跳过流程直接获取的卡片信息
		 */
		@Override
		public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
			if(isSuccess) {
				String content = getString(R.string.batch_data) + "\n";
				Hashtable<String, String> decodeData = WisePadController.decodeTlv(tlv);
				Object[] keys = decodeData.keySet().toArray();
				Arrays.sort(keys);
				for(Object key : keys) {
					String value = decodeData.get(key);
					content += key + ": " + value + "\n";
				}
				System.out.println("卡信息"+content);
				String date = decodeData.get("5f24");
				modle.setDate(date.substring(0,4));
				modle.setId(decodeData.get("maskedPAN"));
				if ("addacc".equals(dom.read(getBaseContext(), "user","from"))) {
					AllUtils.stopProgressDialog();
					dom.write(getBaseContext(), "user","from","slot");
					finish();
				}else if ("visa".equals(dom.read(getBaseContext(), "user","from"))) {
					AllUtils.stopProgressDialog();
					startActivity(new Intent(Bluetooth_payment.this,VisaAttest1.class));
					finish();
				}else {
					AllUtils.stopProgressDialog();
					startActivity(new Intent(Bluetooth_payment.this,Sign.class));
					finish();
				}
			} else {
				System.out.println("获取卡信息失败");
			}			
		}
		
		@Override
		public void onReturnEmvCardNumber(String cardNumber) {
			System.out.println("卡号"+cardNumber);
		}
		@Override
		public void onReturnEmvTransactionLog(String[] transactionLogs) {
			String content = "交易日志"+ "\n";
			for(int i = 0; i < transactionLogs.length; ++i) {
				content += (i + 1) + ": " + transactionLogs[i] + "\n";
			}
			System.out.println(content);
		}

		@Override
		public void onReturnEmvLoadLog(String[] loadLogs) {
			String content = "日志" + "\n";
			for(int i = 0; i < loadLogs.length; ++i) {
				content += (i + 1) + ": " + loadLogs[i] + "\n";
			}
			System.out.println(content);
		}
		
		@Override
		public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
			if(isSuccess) {
    			String content = "";
        		if(data.containsKey("ksn")) {
        			content += "终端号" + data.get("ksn") + "\n";
        		}
        		if(data.containsKey("randomNumber")) {
        			content += "随机数" + data.get("randomNumber") + "\n";
        		}
        		if(data.containsKey("encData")) {
        			content += "加密数据" + data.get("encData") + "\n";
        		}
        		if(data.containsKey("mac")) {
        			content += "MAC" + data.get("mac") + "\n";
        		}
        		System.out.println("onReturnEncryptDataResult"+content);
    		} else {
    			System.out.println("加密数据失败");
    		}
		}

		@Override
		public void onReturnInjectSessionKeyResult(boolean isSuccess) {
			if(isSuccess) {
				System.out.println("灌入秘钥成功");
				injectNextSessionKey();
			} else {
				System.out.println("灌入秘钥失败");
			}
		}
		/**
		 * 加密数据
		 */
		@Override
		public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data) {
			Object[] keys = data.keySet().toArray();
			Arrays.sort(keys);
			String content = "APDU结果：" + "\n";
			for(int i = 0; i < keys.length; ++i) {
				content += keys[i] + ": " + data.get(keys[i]) + "\n";
			}
			System.out.println(content);
		}

		@Override
		public void onReturnViposExchangeApduResult(String apdu) {
			System.out.println("APDU结果："+apdu);
		}
		private void setAmount() {
			System.out.println("设置金额");
			String amount = modle.getPrice();
			String cashbackAmount = "";
			TransactionType transactionType = TransactionType.GOODS;
			CurrencyCharacter[] currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.DOLLAR};
			String currencyCode;
			if(Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
				currencyCode = "156";
				currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.YUAN};
			} else {
				currencyCode = "840";
			}
				if(wisePadController.setAmount(amount, cashbackAmount, currencyCode, transactionType, currencyCharacters)) {
					System.out.println("currencyCode"+currencyCode);
					AllUtils.toast(getBaseContext(), "请确认金额");
				} else {
					setAmount();
				}
		}
		@Override
		public void onRequestSelectApplication(ArrayList<String> appList) {
		}
		@Override
		public void onRequestSetAmount() {
				setAmount();
		}
		/**
		 * 输入密码
		 */
		@Override
		public void onRequestPinEntry(PinEntrySource pinEntrySource) {
			System.out.println("输入密码");
			if(pinEntrySource == PinEntrySource.KEYPAD) {
				AllUtils.toast(getBaseContext(), "请在设备上输入密码");
			} else {
				input();
			}
		}
		@Override
		public void onRequestVerifyID(String tlv) {
		}
		@Override
		public void onRequestCheckServerConnectivity() {
		}
		/**
		 * 向服务器发送的数据
		 */
		@Override
		public void onRequestOnlineProcess(String tlv) {
			String content = "全部数据" + "\n";
			Hashtable<String, String> decodeData = WisePadController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			for(Object key : keys) {
				String value = decodeData.get(key);
				content += key + ": " + value + "\n";
				emvdata.put((String) key, value);
			}
			System.out.println("密码块99"+decodeData.get("99"));
			System.out.println(content);
			System.out.println("这是服务器回复结果");
			if(isPinCanceled) {
				AllUtils.toast(getBaseContext(), "服务器回复失败");
				wisePadController.sendOnlineProcessResult(null);
			} else {
				System.out.println("服务器回复成功");
				wisePadController.sendOnlineProcessResult("8A023030");
			}
		}
		/**
		 * 交易时间
		 */
		@SuppressLint("SimpleDateFormat") 
		@Override
		public void onRequestTerminalTime() {
			String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
			wisePadController.sendTerminalTime(terminalTime);
			System.out.println("请求时间"+terminalTime);
		}
		/**
		 * 向用户展示的类容
		 */
		@Override
		public void onRequestDisplayText(DisplayText displayText) {
			if(displayText == DisplayText.AMOUNT_OK_OR_NOT) {
				AllUtils.toast(getBaseContext(), "金额OK？");
			} else if(displayText == DisplayText.APPROVED) {
				AllUtils.toast(getBaseContext(), "已批准");
			} else if(displayText == DisplayText.CALL_YOUR_BANK) {
				AllUtils.toast(getBaseContext(), "请致电您的银行");
			} else if(displayText == DisplayText.CANCEL_OR_ENTER) {
				AllUtils.toast(getBaseContext(), "输入或取消");
			} else if(displayText == DisplayText.CARD_ERROR) {
				AllUtils.toast(getBaseContext(), "卡错误");
			} else if(displayText == DisplayText.DECLINED) {
				AllUtils.toast(getBaseContext(), "拒绝");
			} else if(displayText == DisplayText.ENTER_PIN) {
			} else if(displayText == DisplayText.INCORRECT_PIN) {
				AllUtils.toast(getBaseContext(), "密码不正确");
			} else if(displayText == DisplayText.INSERT_CARD) {
				AllUtils.toast(getBaseContext(), "请插卡");
			} else if(displayText == DisplayText.NOT_ACCEPTED) {
				AllUtils.toast(getBaseContext(), "不接受");
			} else if(displayText == DisplayText.PIN_OK) {
				AllUtils.toast(getBaseContext(), "密码正确");
			} else if(displayText == DisplayText.PLEASE_WAIT) {
			} else if(displayText == DisplayText.PROCESSING_ERROR) {
				AllUtils.toast(getBaseContext(), "处理错误");
			} else if(displayText == DisplayText.REMOVE_CARD) {
				AllUtils.toast(getBaseContext(), "刷卡结束，请移除卡");
			} else if(displayText == DisplayText.USE_CHIP_READER) {
			} else if(displayText == DisplayText.USE_MAG_STRIPE) {
				AllUtils.toast(getBaseContext(), "请使用磁条卡");
			} else if(displayText == DisplayText.TRY_AGAIN) {
				AllUtils.toast(getBaseContext(), "请再试一次");
			} else if(displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {
				AllUtils.toast(getBaseContext(), "请参阅您的付款装置");
			} else if(displayText == DisplayText.TRANSACTION_TERMINATED) {
				AllUtils.toast(getBaseContext(), "终止");
			} else if(displayText == DisplayText.TRY_ANOTHER_INTERFACE) {
				AllUtils.toast(getBaseContext(), "请尝试另一个接口");
			} else if(displayText == DisplayText.ONLINE_REQUIRED) {
				AllUtils.toast(getBaseContext(), "需要在线");
			} else if(displayText == DisplayText.PROCESSING) {
				AllUtils.toast(getBaseContext(), "处理中");
			} else if(displayText == DisplayText.WELCOME) {
				AllUtils.toast(getBaseContext(), "欢迎");
			} else if(displayText == DisplayText.PRESENT_ONLY_ONE_CARD) {
				AllUtils.toast(getBaseContext(), "请只出示一张卡");
			} else if(displayText == DisplayText.CAPK_LOADING_FAILED) {
				AllUtils.toast(getBaseContext(), "CAPK加载失败");
			} else if(displayText == DisplayText.LAST_PIN_TRY) {
				AllUtils.toast(getBaseContext(), "最后密码尝试");
			} else if(displayText == DisplayText.SELECT_ACCOUNT) {
				AllUtils.toast(getBaseContext(), "请选择户口");
			} else if(displayText == DisplayText.ENTER_AMOUNT) {
				AllUtils.toast(getBaseContext(), "请输入金额");
			} else if(displayText == DisplayText.INSERT_OR_TAP_CARD) {
				AllUtils.toast(getBaseContext(), "请插卡或拍卡");
			} else if(displayText == DisplayText.APPROVED_PLEASE_SIGN) {
				AllUtils.toast(getBaseContext(), "处理成功，请签字");
			} else if(displayText == DisplayText.TAP_CARD_AGAIN) {
				AllUtils.toast(getBaseContext(), "请再拍卡");
			} else if(displayText == DisplayText.AUTHORISING) {
				AllUtils.toast(getBaseContext(), "正在授权");
			} else if(displayText == DisplayText.INSERT_OR_SWIPE_CARD_OR_TAP_ANOTHER_CARD) {
				AllUtils.toast(getBaseContext(), "请插卡、拍卡或拍其他卡");
			} else if(displayText == DisplayText.INSERT_OR_SWIPE_CARD) {
				AllUtils.toast(getBaseContext(), "请插卡或刷卡");
			} else if(displayText == DisplayText.MULTIPLE_CARDS_DETECTED) {
				AllUtils.toast(getBaseContext(), "监测到多张卡");
			}
		}
		@Override
		public void onRequestClearDisplay() {
		}
		@Override
		public void onRequestReferProcess(String pan) {
			System.out.println(2);
		}
		@Override
		public void onRequestAdviceProcess(String tlv) {
			System.out.println(1);
		}
		/**
		 * 确认交易
		 */
		@Override
		public void onRequestFinalConfirm() {
			System.out.println("确认金额"+isPinCanceled);
			if(!isPinCanceled) {
				wisePadController.sendFinalConfirmResult(true);
			} else {
				wisePadController.sendFinalConfirmResult(false);
			}
		}
		@Override
		public void onRequestInsertCard() {
			AllUtils.toast(getBaseContext(), "请插卡");
		}
		/**
		 * 设备电量不足
		 */
		@Override
		public void onBatteryLow(BatteryStatus batteryStatus) {
			if(batteryStatus == BatteryStatus.LOW) {
				AllUtils.toast(getBaseContext(), "POS机电量不足");
			} else if(batteryStatus == BatteryStatus.CRITICALLY_LOW) {
				AllUtils.toast(getBaseContext(), "电量严重不足已断开连接");
			}
		}
		@Override
		public void onBTv2DeviceNotFound() {
			AllUtils.toast(getBaseContext(), "设备没插上");
		}
		@Override
		public void onAudioDeviceNotFound() {
			AllUtils.toast(getBaseContext(), "设备没插上");
		}
		@Override
		public void onDevicePlugged() {
			AllUtils.toast(getBaseContext(), "检测到设备");
		}
		@Override
		public void onDeviceUnplugged() {
			AllUtils.toast(getBaseContext(), "请插入设备");
		}
		/**
		 * 错误信息
		 */
		@Override
		public void onError(Error arg0, String arg1) {
			if(arg0 == Error.CMD_NOT_AVAILABLE) {
				AllUtils.toast(getBaseContext(), "命令不可用");
			} else if(arg0 == Error.TIMEOUT) {
				AllUtils.toast(getBaseContext(), "设备没回复");
			} else if(arg0 == Error.DEVICE_RESET) {
				AllUtils.toast(getBaseContext(), "设备已重置");
			} else if(arg0 == Error.UNKNOWN) {
				AllUtils.toast(getBaseContext(), "位置错误");
			} else if(arg0 == Error.DEVICE_BUSY) {
				AllUtils.toast(getBaseContext(), "设备正常执行操作");
			} else if(arg0 == Error.INPUT_OUT_OF_RANGE) {
				AllUtils.toast(getBaseContext(), "输入错误");
			} else if(arg0 == Error.INPUT_INVALID_FORMAT) {
				AllUtils.toast(getBaseContext(), "输入格式不正确");
			} else if(arg0 == Error.INPUT_ZERO_VALUES) {
				AllUtils.toast(getBaseContext(), "输入值为零");
			} else if(arg0 == Error.INPUT_INVALID) {
				AllUtils.toast(getBaseContext(), "输入无效");
				start();
			} else if(arg0 == Error.CASHBACK_NOT_SUPPORTED) {
				Toast.makeText(Bluetooth_payment.this, getString(R.string.cashback_not_supported), Toast.LENGTH_LONG).show();
			} else if(arg0 == Error.CRC_ERROR) {
				AllUtils.toast(getBaseContext(), "ERC错误");
			} else if(arg0 == Error.COMM_ERROR) {
				AllUtils.toast(getBaseContext(), "通讯错误");
			} else if(arg0 == Error.FAIL_TO_START_BTV2) {
				AllUtils.toast(getBaseContext(), "开启蓝牙2失败");
				AllUtils.stopProgressDialog();
			} else if(arg0 == Error.FAIL_TO_START_BTV4) {
				AllUtils.toast(getBaseContext(), "开启蓝牙4失败");
			} else if(arg0 == Error.FAIL_TO_START_AUDIO) {
				AllUtils.toast(getBaseContext(), "开启音频失败");
			} else if(arg0 == Error.INVALID_FUNCTION_IN_CURRENT_MODE) {
				AllUtils.toast(getBaseContext(), "无效的指令");
			} else if(arg0 == Error.COMM_LINK_UNINITIALIZED) {
				AllUtils.toast(getBaseContext(), "设备未连接");
			} else if(arg0 == Error.BTV2_ALREADY_STARTED) {
				AllUtils.toast(getBaseContext(), "设备已连接");
			} else if(arg0 == Error.BTV4_ALREADY_STARTED) {
				AllUtils.toast(getBaseContext(), "设备已连接");
			} else if(arg0 == Error.BTV4_NOT_SUPPORTED) {
				Toast.makeText(Bluetooth_payment.this, getString(R.string.bluetooth_4_not_supported), Toast.LENGTH_LONG).show();
			}
		
			
		}
		@Override
		public void onPrintDataCancelled() {
			// TODO 自动生成的方法存根
			
		}
		@Override
		public void onPrintDataEnd() {
			// TODO 自动生成的方法存根
			
		}
		@Override
		public void onRequestPrintData(int arg0, boolean arg1) {
			// TODO 自动生成的方法存根
			
		}
		@Override
		public void onReturnApduResult(boolean arg0, String arg1, int arg2) {
			// TODO 自动生成的方法存根
			
		}
		@Override
		public void onReturnApduResultWithPkcs7Padding(boolean arg0, String arg1) {
			// TODO 自动生成的方法存根
			
		}
		/**
		 * 磁条卡密码加密
		 */
		@Override
		public void onReturnEncryptPinResult(Hashtable<String, String> data) {
			if(data.containsKey("epb")) {
				modle.setPwd(data.get("epb"));
				emvdata.put("epb",data.get("epb"));
				System.out.println("密码"+data.get("epb"));
			}
			if(data.containsKey("ksn")) {
				emvdata.put("ksn",data.get("ksn"));
			}
			if(data.containsKey("randomNumber")) {
				emvdata.put("randomNumber",data.get("randomNumber"));
				System.out.println("随机数"+data.get("randomNumber"));
			}
			if(data.containsKey("encWorkingKey")) {
				emvdata.put("encWorkingKey",data.get("encWorkingKey"));
			}
			if ("addacc".equals(dom.read(getBaseContext(), "user","from"))) {
				AllUtils.stopProgressDialog();
				dom.write(getBaseContext(), "user","from","slot");
				finish();
			}else if ("visa".equals(dom.read(getBaseContext(), "user","from"))) {
				AllUtils.stopProgressDialog();
				startActivity(new Intent(Bluetooth_payment.this,VisaAttest1.class));
				finish();
			}else {
				AllUtils.stopProgressDialog();
				Map<String,String> map = new HashMap<String, String>();
				map = cipher.decrypt(cipher.encrypt(emvdata));
				System.out.println(cipher.encrypt(emvdata)+"\n"+map);
				System.out.println("卡号："+map.get("PAN"));
				startActivity(new Intent(Bluetooth_payment.this, Sign.class));
				finish();
			}
		}
		@Override
		public void onReturnPowerOffIccResult(boolean arg0) {
			// TODO 自动生成的方法存根
			
		}
		@Override
		public void onReturnPowerOnIccResult(boolean arg0, String arg1,
				String arg2, int arg3) {
			// TODO 自动生成的方法存根
			
		}
		@Override
		public void onReturnPrintResult(PrintResult arg0) {
			System.out.println("onReturnPrintResult");
		}
		@Override
		public void onWaitingReprintOrPrintNext() {
			System.out.println("onWaitingReprintOrPrintNext");
		}
    }
    @SuppressWarnings("unused")
	private void encinput() {
		encryptedPinSessionKey = encrypt(pinSessionKey, masterKey);
		encryptedDataSessionKey = encrypt(dataSessionKey, masterKey);
		encryptedTrackSessionKey = encrypt(trackSessionKey, masterKey);
		encryptedMacSessionKey = encrypt(macSessionKey, masterKey);
		
		pinKcv = "0000000000000000";
		pinKcv = encrypt(pinKcv, pinSessionKey);
		pinKcv = pinKcv.substring(0, 6);
		
		dataKcv = "0000000000000000";
		dataKcv = encrypt(dataKcv, dataSessionKey);
		dataKcv = dataKcv.substring(0, 6);
		
		trackKcv = "0000000000000000";
		trackKcv = encrypt(trackKcv, trackSessionKey);
		trackKcv = trackKcv.substring(0, 6);
		
		macKcv = "0000000000000000";
		macKcv = encrypt(macKcv, macSessionKey);
		macKcv = macKcv.substring(0, 6);
		injectNextSessionKey();
	}
    /**
     * 开启刷卡
     */
    private void start() {
    	AllUtils.startProgressDialog(Bluetooth_payment.this,"刷卡中");
//    	String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
//    	String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);
    	System.out.println("start");
		isPinCanceled = false;
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("checkCardMode", CheckCardMode.SWIPE_OR_INSERT);
//		data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
//		data.put("randomNumber", "987654321");
//	    data.put("encPinKey", encWorkingKey + workingKeyKcv);
//	    data.put("encDataKey", encWorkingKey + workingKeyKcv);
//	    data.put("encMacKey", encWorkingKey + workingKeyKcv);
//		data.put("amount", modle.getPrice());
		wisePadController.checkCard(data);
	}
    /**
     * 自定义密码输入
     */
	private void input() {
		AllUtils.toast(getBaseContext(), "请输入密码");
		pindialog = new Dialog(Bluetooth_payment.this,R.style.mydialog2);
		pindialog.setCancelable(false);
		pindialog.setContentView(R.layout.input_pwd);
		dialogWindow = pindialog.getWindow();
		cencle = (Button) dialogWindow.findViewById(R.id.pwd_cencle);
		ok = (Button) dialogWindow.findViewById(R.id.pwd_ok);
		pwd = (EditText) dialogWindow.findViewById(R.id.pwd_pwd);
		money = (TextView) dialogWindow.findViewById(R.id.pwd_money);
		money.setText("￥"+modle.getPrice()+"元");
		ds = new ImageView[6];
		for (int i = 0; i < ds.length; i++) {
			ds[i] = (ImageView) dialogWindow.findViewById(R.id.pwd_1+i);
		}
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // 宽度
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.35); // 高度
		dialogWindow.setAttributes(lp);
		pindialog.show();
		cencle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wisePadController.cancelPinEntry();
				pindialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pindialog.dismiss();
				if (pass.length() == 6) {
					modle.setPwd(pass);
					emvdata.put(EmvData.PIN,pass);
					System.out.println("明文密码"+pass);
					if (isIC) {
						System.out.println("IC卡密码"+emvdata.get(EmvData.PIN));
						wisePadController.sendPinEntryResult(pass);
						System.out.println("IC卡密码"+emvdata.get(EmvData.PIN));
					}else {
						Hashtable<String,Object> data = new Hashtable<String,Object>();
						data.put("pinEntryTimeout", 120);
						data.put("pin", pass);
						data.put("pan", modle.getId());
						wisePadController.encryptPin(data);
					}
				}else {
					Toast.makeText(getBaseContext(), "请输入6位数的密码", Toast.LENGTH_SHORT).show();
				}
			}
		});
		pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				pass = s.toString();
				switch (s.length()) {
				case 0:	ds[0].setVisibility(View.INVISIBLE);
				ds[1].setVisibility(View.INVISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 1:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.INVISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);		
				break;
				case 2:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 3:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 4:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 5:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.VISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 6:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.VISIBLE);
				ds[5].setVisibility(View.VISIBLE);
				break;
				default:
					break;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
	}
	@Override
	public void onBackPressed() {
		AllUtils.stopProgressDialog();
	}
}