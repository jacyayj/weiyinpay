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
	 * ����POSˢ��ҳ��
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
	 * ��ʼ��ҳ�漰���蹤��
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
     * ���ҳ��״̬����ҳ������ʱ�Ͽ��豸
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	wisePadController.stopBTv2();
    }
    /**
     * ���ܷ���
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
			System.out.println("���ڴ�����Կ");
			encryptedPinSessionKey = "";  
			wisePadController.injectSessionKey(data);
			return;
		}
		
		if(!encryptedDataSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "2");
			data.put("encSK", encryptedDataSessionKey);
			data.put("kcv", dataKcv);
			System.out.println("���ڴ�����Կ");
			encryptedDataSessionKey = "";
			wisePadController.injectSessionKey(data);
			return;
		}
		
		if(!encryptedTrackSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "3");
			data.put("encSK", encryptedTrackSessionKey);
			data.put("kcv", trackKcv);
			System.out.println("���ڴ�����Կ");
			encryptedTrackSessionKey = "";
			wisePadController.injectSessionKey(data);
			return;
		}
		
		if(!encryptedMacSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "4");
			data.put("encSK", encryptedMacSessionKey);
			data.put("kcv", macKcv);
			System.out.println("���ڴ�����Կ");
			encryptedMacSessionKey = "";
			wisePadController.injectSessionKey(data);
			return;
		}
	}
    /**
     * �Ͽ��豸���ӷ���
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
	 * �����豸����
	 */
    private void startConnection() {
    	AllUtils.startProgressDialog(Bluetooth_payment.this,"�豸������");
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
				AllUtils.toast(getBaseContext(), "�豸������");
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
				AllUtils.toast(getBaseContext(), "�豸������");
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
     * BPOS��������
     * @author jacyayj
     *
     */
    class MyWisePadControllerListener implements WisePadControllerListener {
		/**
		 * �ȴ��û�ˢ��
		 */
		@Override
		public void onWaitingForCard(CheckCardMode checkCardMode) {
				AllUtils.toast(getBaseContext(), "��ˢ����忨");
		}
		/**
		 * �����豸���ӳɹ���2.0��
		 */
		@Override
		public void onBTv2Connected(BluetoothDevice bluetoothDevice) {
			AllUtils.toast(getBaseContext(), "�豸�����ӣ�����2��");
			AllUtils.stopProgressDialog();
			start();
		}
		/**
		 * ����������2.0��
		 */
		@Override
		public void onBTv2Detected() {
			AllUtils.toast(getBaseContext(), "����������");
		}
		/**
		 * �����������������½��棨2.0��
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
		 * �Ͽ����ӣ�2.0��
		 */
		@Override
		public void onBTv2Disconnected() {
			AllUtils.toast(getBaseContext(), "�豸�ѶϿ�");	
		}
		/**
		 * ֹͣ������2.0��
		 */
		@Override
		public void onBTv2ScanStopped() {
			AllUtils.toast(getBaseContext(), "ֹͣ����");	
		}
		/**
		 * ������ʱ��2.0��
		 */
		@Override
		public void onBTv2ScanTimeout() {
			AllUtils.toast(getBaseContext(), "������ʱ");
		}
		/**
		 * �ж�ˢ��״̬�뿨Ƭ����
		 */
		@Override
		public void onReturnMagStripeCardNumber(CheckCardResult checkCardResult, String cardNumber) {
			System.out.println("onReturnMagStripeCardNumber����:"+cardNumber);
    		if(checkCardResult == CheckCardResult.NONE) {
    			AllUtils.toast(getBaseContext(), "ˢ����忨��ʱ");
			} else if(checkCardResult == CheckCardResult.ICC) {
				AllUtils.toast(getBaseContext(), "IC���Ѳ���");
			} else if(checkCardResult == CheckCardResult.NOT_ICC) {
				AllUtils.toast(getBaseContext(), "����IC������忨");
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				AllUtils.toast(getBaseContext(), "ʧ��������ˢ��");
				start();
			} else if(checkCardResult == CheckCardResult.MCR) {
				System.out.println("����"+cardNumber);
			} else if(checkCardResult == CheckCardResult.NO_RESPONSE) {
				AllUtils.toast(getBaseContext(), "ˢ����忨��������������");
			} else if(checkCardResult == CheckCardResult.TRACK2_ONLY) {
				System.out.println("����"+cardNumber);
			}
		}
		/**
		 * �������ӳɹ���4.0��
		 */
		@Override
		public void onBTv4Connected() {
			AllUtils.toast(getBaseContext(), "�豸�����ӣ�����4��");
		}
		/**
		 * �ҵ��µ����������½��棨4.0��
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
		 * �Ͽ����ӣ�4.0��
		 */
		@Override
		public void onBTv4Disconnected() {
			AllUtils.toast(getBaseContext(), "�豸�ѶϿ�");
		}
		/**
		 * ֹͣ������4.0��
		 */
		@Override
		public void onBTv4ScanStopped() {
			AllUtils.toast(getBaseContext(), "ֹͣ����");		
		}
		/**
		 * ������ʱ��4.0��
		 */
		@Override
		public void onBTv4ScanTimeout() {
			AllUtils.toast(getBaseContext(), "������ʱ");
		}
		/**
		 * ˢ�������������
		 */
		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
    		if(checkCardResult == CheckCardResult.NONE) {
    			AllUtils.toast(getBaseContext(), "ˢ����忨��ʱ");
			} else if(checkCardResult == CheckCardResult.ICC) {
				isIC = true;
				AllUtils.toast(getBaseContext(), "IC��ˢ����");
				System.out.println("IC��");
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
				AllUtils.toast(getBaseContext(), "����IC������ˢ��");
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				AllUtils.toast(getBaseContext(), "ˢ��������");
			} else if(checkCardResult == CheckCardResult.MCR) {
				isIC = false;
				AllUtils.toast(getBaseContext(), "������ˢ����");
				System.out.println("������");
				emvdata = new HashMap<String, String>();
				cipher = new DesCipher();
				String content = "����Ϣ��" + "\n";
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
				AllUtils.toast(getBaseContext(), "ˢ����忨��������������");
			} else if(checkCardResult == CheckCardResult.TRACK2_ONLY) {
				System.out.println("ֻ��2�ŵ�");
				AllUtils.toast(getBaseContext(), "������ˢ����");
				System.out.println("������");
				emvdata = new HashMap<String, String>();
				cipher = new DesCipher();
				String content = "����Ϣ��" + "\n";
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
				AllUtils.toast(getBaseContext(), "����IC��");
			} else if(checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
				AllUtils.toast(getBaseContext(), "��⵽�Ŀ�");
			}
		}
    	/**
    	 * ȡ��ˢ������
    	 */
    	@Override
    	public void onReturnCancelCheckCardResult(boolean isSuccess) {
    		if(isSuccess) {
    			AllUtils.toast(getBaseContext(), "ȡ�������ɹ�");
    		} else {
    			AllUtils.toast(getBaseContext(), "ȡ������ʧ��");
    		}
    	}
    	/**
    	 * ����IC��emv����״̬
    	 */
		@Override
    	public void onReturnStartEmvResult(StartEmvResult startEmvResult, String ksn) {
    		if(startEmvResult == StartEmvResult.SUCCESS) {
    			AllUtils.toast(getBaseContext(), "EMV�����ɹ�");
    		} else {
    			AllUtils.toast(getBaseContext(), "EMV����ʧ��");
    		}
    	}
		/**
		 * �����豸��Ϣ
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
			System.out.println("��������2"+content);
		}
		
    	@Override
		public void onReturnTransactionResult(TransactionResult transactionResult) {
		}
    	/**
    	 * ���׷�������
    	 */
		@Override
		public void onReturnTransactionResult(TransactionResult transactionResult, Hashtable<String, String> data) {
			if(transactionResult == TransactionResult.APPROVED) {
				AllUtils.toast(getBaseContext(), "ˢ���ɹ�");
				System.out.println("�ɹ�");
//				if (modle.getPwd()!=null) {
				System.out.println("���ŵ�"+modle.getSencondno()+"\n���ŵ�"+modle.getThirdno()+"\n����"+modle.getId());
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
					System.out.println("���ţ�"+map.get("maskedPAN"));
					finish();
				}
//				}else {
//					System.out.println("���ŵ�"+modle.getSencondno()+"\n���ŵ�"+modle.getThirdno()+"\n����"+modle.getId());
//					AllUtils.toast(getBaseContext(), "��");
//				}
			} else if(transactionResult == TransactionResult.TERMINATED) {
				AllUtils.toast(getBaseContext(), "������ֹ");
			} else if(transactionResult == TransactionResult.DECLINED) {
				AllUtils.toast(getBaseContext(), "�������ɹ�");
				System.out.println("������?");
			} else if(transactionResult == TransactionResult.CANCEL) {
				AllUtils.toast(getBaseContext(), "����ȡ��");
			} else if(transactionResult == TransactionResult.CAPK_FAIL) {
				AllUtils.toast(getBaseContext(), "�������ɹ���CAPKʧ�ܣ�");
			} else if(transactionResult == TransactionResult.NOT_ICC) {
				AllUtils.toast(getBaseContext(), "�������ɹ�������IC����");
			} else if(transactionResult == TransactionResult.SELECT_APP_FAIL) {
				AllUtils.toast(getBaseContext(), "�������ɹ���APPʧ�ܣ�");
			} else if(transactionResult == TransactionResult.DEVICE_ERROR) {
				AllUtils.toast(getBaseContext(), "�������ɹ����豸����");
			} else if(transactionResult == TransactionResult.APPLICATION_BLOCKED) {
				AllUtils.toast(getBaseContext(), "�������ɹ���APPһ��ͣ��");
			} else if(transactionResult == TransactionResult.ICC_CARD_REMOVED) {
				AllUtils.toast(getBaseContext(), "�������ɹ���IC���ѱ��Ƴ�");
			} else if(transactionResult == TransactionResult.CARD_BLOCKED) {
				AllUtils.toast(getBaseContext(), "�������ɹ����ÿ��ѱ���");
			} else if(transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
				AllUtils.toast(getBaseContext(), "�������ɹ����ݲ�֧�ָÿ�");
			} else if(transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
				AllUtils.toast(getBaseContext(), "�������ɹ�������������");
			} else if(transactionResult == TransactionResult.INVALID_ICC_DATA) {
				AllUtils.toast(getBaseContext(), "�������ɹ�����Ч��IC������");
			} else if(transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
				AllUtils.toast(getBaseContext(), "�������ɹ���ȱ�ٱ�Ҫ������");
			} else if(transactionResult == TransactionResult.NO_EMV_APPS) {
				AllUtils.toast(getBaseContext(), "�������ɹ�����EMVӦ�ó���");
			}else {
				AllUtils.toast(getBaseContext(), transactionResult.name());
			}
			
			if(data.get("receiptData") != null) {
				System.out.println("��������"+data.get("receiptData"));
//				message += "\n" + getString(R.string.receipt_data) + "" + data.get("receiptData");
			}
		}
		/**
		 * �豸��������
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
			Log.d("=====�������ݣ�ReturnBatchData��======", content);
		}
		
		@Override
		public void onReturnTransactionLog(String tlv) {
			dismissDialog();
			String content = getString(R.string.transaction_log);
			content += tlv;
			Log.d("=====������־�� ReturnTransactionLog��======", content);
		}
		
		@Override
		public void onReturnReversalData(String tlv) {
			dismissDialog();
			String content = getString(R.string.reversal_data);
			content += tlv;
			Log.d("=====��ת���ݣ� ReturnReversalData��======", content);
		}
		/**
		 * ȷ�Ͻ��
		 */
		@Override
		public void onReturnAmountConfirmResult(boolean isSuccess) {
			if(isSuccess) {
				System.out.println("�����ȷ��");
			} else {
				System.out.println("�����ȡ��");
			}
		}
		/**
		 * IC�����ؼ�������
		 */
		@Override
		public void onReturnPinEntryResult(PinEntryResult pinEntryResult, Hashtable<String, String> data) {
			System.out.println("���Ƿ������룿");
			if(pinEntryResult == PinEntryResult.ENTERED) {
				String content = getString(R.string.pin_entered);
				Object[] keys = data.keySet().toArray();
				Arrays.sort(keys);
				for(Object key : keys) {
					String value = data.get(key);
					emvdata.put((String) key, value);
					System.out.println("���룺"+key + ": " + value + "\n");
				}
				if(data.containsKey("epb")) {
					content += "\n" + getString(R.string.epb) + data.get("epb");	
					modle.setPwd(data.get("epb"));
					System.out.println("����"+data.get("epb"));
				}
				if(data.containsKey("ksn")) {
					content += "\n" + getString(R.string.ksn) + data.get("ksn");					
				}
				if(data.containsKey("randomNumber")) {
					content += "\n" + getString(R.string.random_number) + data.get("randomNumber");
					System.out.println("�����"+data.get("randomNumber"));
				}
				if(data.containsKey("encWorkingKey")) {
					content += "\n" + getString(R.string.encrypted_working_key) + data.get("encWorkingKey");	
				}
				System.out.println("оƬ��pin��"+content);
			} else if(pinEntryResult == PinEntryResult.BYPASS) {
				AllUtils.toast(getBaseContext(), "�������Թ�");
			} else if(pinEntryResult == PinEntryResult.CANCEL) {
				AllUtils.toast(getBaseContext(), "������ȡ��");
			} else if(pinEntryResult == PinEntryResult.TIMEOUT) {
				AllUtils.toast(getBaseContext(), "�������볬ʱ");
				AllUtils.stopProgressDialog();
			} else if(pinEntryResult == PinEntryResult.KEY_ERROR) {
				AllUtils.toast(getBaseContext(), "�������");
			} else if(pinEntryResult == PinEntryResult.NO_PIN) {
				AllUtils.toast(getBaseContext(), "û������");
			}else {
				AllUtils.toast(getBaseContext(), "�������");
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
				AllUtils.toast(getBaseContext(), "�����ն����óɹ�");
			} else if(terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
				AllUtils.toast(getBaseContext(), "�����ն�����ʧ�ܣ��Ҳ�����ǩ");
			} else if(terminalSettingStatus == TerminalSettingStatus.LENGTH_INCORRECT) {
				AllUtils.toast(getBaseContext(), "�����ն�����ʧ�ܣ����ȴ���");
			} else if(terminalSettingStatus == TerminalSettingStatus.TLV_INCORRECT) {
				AllUtils.toast(getBaseContext(), "�����ն�����ʧ�ܣ�TLV����");
			} else if(terminalSettingStatus == TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
				AllUtils.toast(getBaseContext(), "�����ն�����ʧ�ܣ���������֧��");
			}
		}
		
		@Override
		public void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value) {
			dismissDialog();
			if(terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
				AllUtils.toast(getBaseContext(), "��ȡ�ն����óɹ�"+ "\n" + getString(R.string.value) + " " + value);
			} else if(terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
				AllUtils.toast(getBaseContext(), "��ȡ�ն�����ʧ�ܣ��Ҳ�����ǩ");
			} else if(terminalSettingStatus == TerminalSettingStatus.TAG_INCORRECT) {
				AllUtils.toast(getBaseContext(), "��ȡ�ն�����ʧ�ܣ���ǩ����");
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
				System.out.println("���"+tlv);
			} else {
				System.out.println("��ѯ���ʧ��");
			}			
		}
		/**
		 * IC����������ֱ�ӻ�ȡ�Ŀ�Ƭ��Ϣ
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
				System.out.println("����Ϣ"+content);
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
				System.out.println("��ȡ����Ϣʧ��");
			}			
		}
		
		@Override
		public void onReturnEmvCardNumber(String cardNumber) {
			System.out.println("����"+cardNumber);
		}
		@Override
		public void onReturnEmvTransactionLog(String[] transactionLogs) {
			String content = "������־"+ "\n";
			for(int i = 0; i < transactionLogs.length; ++i) {
				content += (i + 1) + ": " + transactionLogs[i] + "\n";
			}
			System.out.println(content);
		}

		@Override
		public void onReturnEmvLoadLog(String[] loadLogs) {
			String content = "��־" + "\n";
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
        			content += "�ն˺�" + data.get("ksn") + "\n";
        		}
        		if(data.containsKey("randomNumber")) {
        			content += "�����" + data.get("randomNumber") + "\n";
        		}
        		if(data.containsKey("encData")) {
        			content += "��������" + data.get("encData") + "\n";
        		}
        		if(data.containsKey("mac")) {
        			content += "MAC" + data.get("mac") + "\n";
        		}
        		System.out.println("onReturnEncryptDataResult"+content);
    		} else {
    			System.out.println("��������ʧ��");
    		}
		}

		@Override
		public void onReturnInjectSessionKeyResult(boolean isSuccess) {
			if(isSuccess) {
				System.out.println("������Կ�ɹ�");
				injectNextSessionKey();
			} else {
				System.out.println("������Կʧ��");
			}
		}
		/**
		 * ��������
		 */
		@Override
		public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data) {
			Object[] keys = data.keySet().toArray();
			Arrays.sort(keys);
			String content = "APDU�����" + "\n";
			for(int i = 0; i < keys.length; ++i) {
				content += keys[i] + ": " + data.get(keys[i]) + "\n";
			}
			System.out.println(content);
		}

		@Override
		public void onReturnViposExchangeApduResult(String apdu) {
			System.out.println("APDU�����"+apdu);
		}
		private void setAmount() {
			System.out.println("���ý��");
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
					AllUtils.toast(getBaseContext(), "��ȷ�Ͻ��");
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
		 * ��������
		 */
		@Override
		public void onRequestPinEntry(PinEntrySource pinEntrySource) {
			System.out.println("��������");
			if(pinEntrySource == PinEntrySource.KEYPAD) {
				AllUtils.toast(getBaseContext(), "�����豸����������");
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
		 * ����������͵�����
		 */
		@Override
		public void onRequestOnlineProcess(String tlv) {
			String content = "ȫ������" + "\n";
			Hashtable<String, String> decodeData = WisePadController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			for(Object key : keys) {
				String value = decodeData.get(key);
				content += key + ": " + value + "\n";
				emvdata.put((String) key, value);
			}
			System.out.println("�����99"+decodeData.get("99"));
			System.out.println(content);
			System.out.println("���Ƿ������ظ����");
			if(isPinCanceled) {
				AllUtils.toast(getBaseContext(), "�������ظ�ʧ��");
				wisePadController.sendOnlineProcessResult(null);
			} else {
				System.out.println("�������ظ��ɹ�");
				wisePadController.sendOnlineProcessResult("8A023030");
			}
		}
		/**
		 * ����ʱ��
		 */
		@SuppressLint("SimpleDateFormat") 
		@Override
		public void onRequestTerminalTime() {
			String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
			wisePadController.sendTerminalTime(terminalTime);
			System.out.println("����ʱ��"+terminalTime);
		}
		/**
		 * ���û�չʾ������
		 */
		@Override
		public void onRequestDisplayText(DisplayText displayText) {
			if(displayText == DisplayText.AMOUNT_OK_OR_NOT) {
				AllUtils.toast(getBaseContext(), "���OK��");
			} else if(displayText == DisplayText.APPROVED) {
				AllUtils.toast(getBaseContext(), "����׼");
			} else if(displayText == DisplayText.CALL_YOUR_BANK) {
				AllUtils.toast(getBaseContext(), "���µ���������");
			} else if(displayText == DisplayText.CANCEL_OR_ENTER) {
				AllUtils.toast(getBaseContext(), "�����ȡ��");
			} else if(displayText == DisplayText.CARD_ERROR) {
				AllUtils.toast(getBaseContext(), "������");
			} else if(displayText == DisplayText.DECLINED) {
				AllUtils.toast(getBaseContext(), "�ܾ�");
			} else if(displayText == DisplayText.ENTER_PIN) {
			} else if(displayText == DisplayText.INCORRECT_PIN) {
				AllUtils.toast(getBaseContext(), "���벻��ȷ");
			} else if(displayText == DisplayText.INSERT_CARD) {
				AllUtils.toast(getBaseContext(), "��忨");
			} else if(displayText == DisplayText.NOT_ACCEPTED) {
				AllUtils.toast(getBaseContext(), "������");
			} else if(displayText == DisplayText.PIN_OK) {
				AllUtils.toast(getBaseContext(), "������ȷ");
			} else if(displayText == DisplayText.PLEASE_WAIT) {
			} else if(displayText == DisplayText.PROCESSING_ERROR) {
				AllUtils.toast(getBaseContext(), "�������");
			} else if(displayText == DisplayText.REMOVE_CARD) {
				AllUtils.toast(getBaseContext(), "ˢ�����������Ƴ���");
			} else if(displayText == DisplayText.USE_CHIP_READER) {
			} else if(displayText == DisplayText.USE_MAG_STRIPE) {
				AllUtils.toast(getBaseContext(), "��ʹ�ô�����");
			} else if(displayText == DisplayText.TRY_AGAIN) {
				AllUtils.toast(getBaseContext(), "������һ��");
			} else if(displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {
				AllUtils.toast(getBaseContext(), "��������ĸ���װ��");
			} else if(displayText == DisplayText.TRANSACTION_TERMINATED) {
				AllUtils.toast(getBaseContext(), "��ֹ");
			} else if(displayText == DisplayText.TRY_ANOTHER_INTERFACE) {
				AllUtils.toast(getBaseContext(), "�볢����һ���ӿ�");
			} else if(displayText == DisplayText.ONLINE_REQUIRED) {
				AllUtils.toast(getBaseContext(), "��Ҫ����");
			} else if(displayText == DisplayText.PROCESSING) {
				AllUtils.toast(getBaseContext(), "������");
			} else if(displayText == DisplayText.WELCOME) {
				AllUtils.toast(getBaseContext(), "��ӭ");
			} else if(displayText == DisplayText.PRESENT_ONLY_ONE_CARD) {
				AllUtils.toast(getBaseContext(), "��ֻ��ʾһ�ſ�");
			} else if(displayText == DisplayText.CAPK_LOADING_FAILED) {
				AllUtils.toast(getBaseContext(), "CAPK����ʧ��");
			} else if(displayText == DisplayText.LAST_PIN_TRY) {
				AllUtils.toast(getBaseContext(), "������볢��");
			} else if(displayText == DisplayText.SELECT_ACCOUNT) {
				AllUtils.toast(getBaseContext(), "��ѡ�񻧿�");
			} else if(displayText == DisplayText.ENTER_AMOUNT) {
				AllUtils.toast(getBaseContext(), "��������");
			} else if(displayText == DisplayText.INSERT_OR_TAP_CARD) {
				AllUtils.toast(getBaseContext(), "��忨���Ŀ�");
			} else if(displayText == DisplayText.APPROVED_PLEASE_SIGN) {
				AllUtils.toast(getBaseContext(), "����ɹ�����ǩ��");
			} else if(displayText == DisplayText.TAP_CARD_AGAIN) {
				AllUtils.toast(getBaseContext(), "�����Ŀ�");
			} else if(displayText == DisplayText.AUTHORISING) {
				AllUtils.toast(getBaseContext(), "������Ȩ");
			} else if(displayText == DisplayText.INSERT_OR_SWIPE_CARD_OR_TAP_ANOTHER_CARD) {
				AllUtils.toast(getBaseContext(), "��忨���Ŀ�����������");
			} else if(displayText == DisplayText.INSERT_OR_SWIPE_CARD) {
				AllUtils.toast(getBaseContext(), "��忨��ˢ��");
			} else if(displayText == DisplayText.MULTIPLE_CARDS_DETECTED) {
				AllUtils.toast(getBaseContext(), "��⵽���ſ�");
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
		 * ȷ�Ͻ���
		 */
		@Override
		public void onRequestFinalConfirm() {
			System.out.println("ȷ�Ͻ��"+isPinCanceled);
			if(!isPinCanceled) {
				wisePadController.sendFinalConfirmResult(true);
			} else {
				wisePadController.sendFinalConfirmResult(false);
			}
		}
		@Override
		public void onRequestInsertCard() {
			AllUtils.toast(getBaseContext(), "��忨");
		}
		/**
		 * �豸��������
		 */
		@Override
		public void onBatteryLow(BatteryStatus batteryStatus) {
			if(batteryStatus == BatteryStatus.LOW) {
				AllUtils.toast(getBaseContext(), "POS����������");
			} else if(batteryStatus == BatteryStatus.CRITICALLY_LOW) {
				AllUtils.toast(getBaseContext(), "�������ز����ѶϿ�����");
			}
		}
		@Override
		public void onBTv2DeviceNotFound() {
			AllUtils.toast(getBaseContext(), "�豸û����");
		}
		@Override
		public void onAudioDeviceNotFound() {
			AllUtils.toast(getBaseContext(), "�豸û����");
		}
		@Override
		public void onDevicePlugged() {
			AllUtils.toast(getBaseContext(), "��⵽�豸");
		}
		@Override
		public void onDeviceUnplugged() {
			AllUtils.toast(getBaseContext(), "������豸");
		}
		/**
		 * ������Ϣ
		 */
		@Override
		public void onError(Error arg0, String arg1) {
			if(arg0 == Error.CMD_NOT_AVAILABLE) {
				AllUtils.toast(getBaseContext(), "�������");
			} else if(arg0 == Error.TIMEOUT) {
				AllUtils.toast(getBaseContext(), "�豸û�ظ�");
			} else if(arg0 == Error.DEVICE_RESET) {
				AllUtils.toast(getBaseContext(), "�豸������");
			} else if(arg0 == Error.UNKNOWN) {
				AllUtils.toast(getBaseContext(), "λ�ô���");
			} else if(arg0 == Error.DEVICE_BUSY) {
				AllUtils.toast(getBaseContext(), "�豸����ִ�в���");
			} else if(arg0 == Error.INPUT_OUT_OF_RANGE) {
				AllUtils.toast(getBaseContext(), "�������");
			} else if(arg0 == Error.INPUT_INVALID_FORMAT) {
				AllUtils.toast(getBaseContext(), "�����ʽ����ȷ");
			} else if(arg0 == Error.INPUT_ZERO_VALUES) {
				AllUtils.toast(getBaseContext(), "����ֵΪ��");
			} else if(arg0 == Error.INPUT_INVALID) {
				AllUtils.toast(getBaseContext(), "������Ч");
				start();
			} else if(arg0 == Error.CASHBACK_NOT_SUPPORTED) {
				Toast.makeText(Bluetooth_payment.this, getString(R.string.cashback_not_supported), Toast.LENGTH_LONG).show();
			} else if(arg0 == Error.CRC_ERROR) {
				AllUtils.toast(getBaseContext(), "ERC����");
			} else if(arg0 == Error.COMM_ERROR) {
				AllUtils.toast(getBaseContext(), "ͨѶ����");
			} else if(arg0 == Error.FAIL_TO_START_BTV2) {
				AllUtils.toast(getBaseContext(), "��������2ʧ��");
				AllUtils.stopProgressDialog();
			} else if(arg0 == Error.FAIL_TO_START_BTV4) {
				AllUtils.toast(getBaseContext(), "��������4ʧ��");
			} else if(arg0 == Error.FAIL_TO_START_AUDIO) {
				AllUtils.toast(getBaseContext(), "������Ƶʧ��");
			} else if(arg0 == Error.INVALID_FUNCTION_IN_CURRENT_MODE) {
				AllUtils.toast(getBaseContext(), "��Ч��ָ��");
			} else if(arg0 == Error.COMM_LINK_UNINITIALIZED) {
				AllUtils.toast(getBaseContext(), "�豸δ����");
			} else if(arg0 == Error.BTV2_ALREADY_STARTED) {
				AllUtils.toast(getBaseContext(), "�豸������");
			} else if(arg0 == Error.BTV4_ALREADY_STARTED) {
				AllUtils.toast(getBaseContext(), "�豸������");
			} else if(arg0 == Error.BTV4_NOT_SUPPORTED) {
				Toast.makeText(Bluetooth_payment.this, getString(R.string.bluetooth_4_not_supported), Toast.LENGTH_LONG).show();
			}
		
			
		}
		@Override
		public void onPrintDataCancelled() {
			// TODO �Զ����ɵķ������
			
		}
		@Override
		public void onPrintDataEnd() {
			// TODO �Զ����ɵķ������
			
		}
		@Override
		public void onRequestPrintData(int arg0, boolean arg1) {
			// TODO �Զ����ɵķ������
			
		}
		@Override
		public void onReturnApduResult(boolean arg0, String arg1, int arg2) {
			// TODO �Զ����ɵķ������
			
		}
		@Override
		public void onReturnApduResultWithPkcs7Padding(boolean arg0, String arg1) {
			// TODO �Զ����ɵķ������
			
		}
		/**
		 * �������������
		 */
		@Override
		public void onReturnEncryptPinResult(Hashtable<String, String> data) {
			if(data.containsKey("epb")) {
				modle.setPwd(data.get("epb"));
				emvdata.put("epb",data.get("epb"));
				System.out.println("����"+data.get("epb"));
			}
			if(data.containsKey("ksn")) {
				emvdata.put("ksn",data.get("ksn"));
			}
			if(data.containsKey("randomNumber")) {
				emvdata.put("randomNumber",data.get("randomNumber"));
				System.out.println("�����"+data.get("randomNumber"));
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
				System.out.println("���ţ�"+map.get("PAN"));
				startActivity(new Intent(Bluetooth_payment.this, Sign.class));
				finish();
			}
		}
		@Override
		public void onReturnPowerOffIccResult(boolean arg0) {
			// TODO �Զ����ɵķ������
			
		}
		@Override
		public void onReturnPowerOnIccResult(boolean arg0, String arg1,
				String arg2, int arg3) {
			// TODO �Զ����ɵķ������
			
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
     * ����ˢ��
     */
    private void start() {
    	AllUtils.startProgressDialog(Bluetooth_payment.this,"ˢ����");
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
     * �Զ�����������
     */
	private void input() {
		AllUtils.toast(getBaseContext(), "����������");
		pindialog = new Dialog(Bluetooth_payment.this,R.style.mydialog2);
		pindialog.setCancelable(false);
		pindialog.setContentView(R.layout.input_pwd);
		dialogWindow = pindialog.getWindow();
		cencle = (Button) dialogWindow.findViewById(R.id.pwd_cencle);
		ok = (Button) dialogWindow.findViewById(R.id.pwd_ok);
		pwd = (EditText) dialogWindow.findViewById(R.id.pwd_pwd);
		money = (TextView) dialogWindow.findViewById(R.id.pwd_money);
		money.setText("��"+modle.getPrice()+"Ԫ");
		ds = new ImageView[6];
		for (int i = 0; i < ds.length; i++) {
			ds[i] = (ImageView) dialogWindow.findViewById(R.id.pwd_1+i);
		}
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // ���
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.35); // �߶�
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
					System.out.println("��������"+pass);
					if (isIC) {
						System.out.println("IC������"+emvdata.get(EmvData.PIN));
						wisePadController.sendPinEntryResult(pass);
						System.out.println("IC������"+emvdata.get(EmvData.PIN));
					}else {
						Hashtable<String,Object> data = new Hashtable<String,Object>();
						data.put("pinEntryTimeout", 120);
						data.put("pin", pass);
						data.put("pan", modle.getId());
						wisePadController.encryptPin(data);
					}
				}else {
					Toast.makeText(getBaseContext(), "������6λ��������", Toast.LENGTH_SHORT).show();
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