package com.example.controll;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.modle.TransferListener;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.BatteryInfoResult;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.swiper.Account;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.external.me11.ME11SwipResult;

public interface DeviceController {
	/**
	 * ��ʼ���豸������
	 * @since ver1.0
	 * @param context
	 * @param params
	 */
	public void init(Context context, String driverName, DeviceConnParams params, DeviceEventListener<ConnectionCloseEvent> listener);
	
	/**
	 * ��ȡ�豸���Ӳ���
	 * @return
	 */
	public DeviceConnParams getDeviceConnParams();
	
	/**
	 * �������ӿ��������ͷ������Դ<p>
	 * @since ver1.0
	 */
	public void destroy();

	/**
	 * �����豸<p>
	 * @since ver1.0
	 * @throws Exception
	 */
	public void connect() throws Exception;

	/**
	 * �����ж�<p>
	 * @since ver1.0
	 */
	public void disConnect();
	
	/**
	 * ��õ�ǰ�豸����״̬
	 * @since ver1.0
	 * @return
	 */
	public DeviceConnState getDeviceConnState();
	
	/**
	 * ���ò���
	 * 
	 * @since ver1.0
	 * @param tag ���õı�ǩ��ֵ
	 * @param value ���õ���ֵ
	 */
	public void setParam(int tag, byte[] value);
	/**
	 * ��ö�Ӧ�Ĳ���
	 * 
	 * @since ver1.0
	 * @param tag  ��õĲ�����ֵ
	 * @return
	 */
	public byte[] getParam(int tag);
	/**
	 * װ������Կ
	 * @param kekUsingType����Կ����ʹ�õķ�ʽ
	 * @param mkIndex����Կ����
	 * @param keyData����Կ
	 * @param checkValueУ��ֵ
	 * @param transportKeyIndex������Կ����(����KekUsingType.MAIN_KEY)ʱʹ��
	 */
	public void loadMainKey(KekUsingType kekUsingType, int mkIndex, byte[] keyData, byte[] checkValue,int transportKeyIndex);
	/**
	 * ���¹�����Կ<p>
	 * @since ver1.0
	 * @param workingKeyType ������Կ����
	 * @param encryData ��������
     * @param encryData У������
	 * @return
	 */
	public void updateWorkingKey(WorkingKeyType workingKeyType, byte[] encryData,byte[] checkValue) ;

	/**
	 * ����豸��Ϣ
	 * @since ver1.0
	 * @return
	 */
	public DeviceInfo getDeviceInfo();
	/**
	 * ���ME11�豸��Ϣ
	 * @return
	 */
	public DeviceInfo getDeviceInfo_me11();
	
	/**
	 * ����һ��ˢ������(�������ܷ�ʽ)<p>
	 * 
	 * @since ver1.0
	 * @param msg ���豸������ʾ��Ϣ<p>
	 * @param timeout ˢ����ʱʱ��
	 * @param timeUnit ��ʱʱ�䵥λ
	 * @return
	 */
	public SwipResult swipCard(String msg, long timeout, TimeUnit timeUnit) ;
	/**
	 * ����һ������ˢ������<p>
	 * 
	 * @since ver1.0
	 * @param msg ���豸������ʾ��Ϣ<p>
	 * @param timeout ˢ����ʱʱ��
	 * @param timeUnit ��ʱʱ�䵥λ
	 * @return
	 */
	public SwipResult swipCardForPlain(String msg, long timeout, TimeUnit timeUnit);
	/**
	 * ����һ��ˢ������(�������ܷ�ʽ)<p>
	 * 
	 * @param time ��ǰ����ʱ��
	 * @param timeout ˢ����ʱʱ��
	 * @param timeUnit ��ʱʱ�䵥λ
	 * @return
	 */
	public ME11SwipResult swipCard_me11(byte[] time, long timeout, TimeUnit timeUnit) ;
	/**
	 * ����һ������ˢ������<p>
	 * 
	 * @param time ��ǰ����ʱ��
	 * @param timeout ˢ����ʱʱ��
	 * @param timeUnit ��ʱʱ�䵥λ
	 * @return
	 */
	public ME11SwipResult swipCardForPlain_me11(byte[] time, long timeout, TimeUnit timeUnit);
	
	/**
	 * ��ȡemvģ��
	 * @param filePath
	 * @return
	 */
	public EmvModule getEmvModule();
	/**
	 * 
	 * @param cardReaders
	 * @param amt
	 * @param timeout
	 * @param timeunit
	 */
	public void startEmv(BigDecimal amt, TransferListener transferListener);
	
	/**
	 * ����һ��pin�������<p>
	 * 
	 * @since ver1.0
	 * @param acctHash ˢ��ʱ���ص�<tt>acctHash</tt>
	 * @param inputMaxLen ����������볤�� [0,12]
	 * @param msg ������ʾ����
	 * @return
	 */
	public PinInputEvent startPininput(String acctHash,int inputMaxLen, String msg);
	/**
	 * ����һ��pin��ȡ����,������pin��Ϣ
	 * <p>
	 * �÷�����Ҫ��ˢ����ɺ����,��Ϊpin���������Ҫ�����ϴ�ˢ�����˺���Ϣ
	 * <p>
	 * ͨ��{@link SwipResult#getAccount()}���Ի��ˢ��ʱ���˻���Ϣ����Ҫ��
	 * {@link Account#getIdentityHash()}���빩�豸У�顣
	 * 
	 * @param acctHash�ϴ�ˢ��ʱ���˻�hash
	 * @param inputMaxLen���������������ֵ
	 * @param isEnterEnabled��������ʱ
	 *            ,�Ƿ�ͨ���س����أ����������������ء�
	 * @param msg������Ϣ
	 * @param timeout��ʱʱ��
	 * @return ���ͻ��豸����,�򷵻�nullF
	 */
	public PinInputEvent startPininput(AccountInputType acctInputType, String acctHash, int inputMaxLen, boolean isEnterEnabled, String msg, long timeout) throws InterruptedException;
	/**
	 * ������������
	 * @param acctHash �˺�hash
	 * @param inputMaxLen ������볤��
	 * @param msg ��ʾ��Ϣ
	 * @param listener ��Ӧ����
	 */
	public void startPininput(String acctHash, int inputMaxLen, String msg,
			DeviceEventListener<PinInputEvent> listener);
	/**
	 * ����һ������������������������(pos��ʾ*��)<p>
	 * 
	 * @param title ����
	 * @param content ����
	 * @param minLength ��С����
	 * @param maxLength ��󳤶�
	 * @param timeout ��ʱʱ��
	 * @return
	 * 		���ͻ��豸����,�򷵻�null
	 * @throws InterruptedException ���̱߳��ж�ʱ���׳����쳣
	 */
	public String inputPlainPwd(String title, String content, int minLength, int maxLength, long timeout) throws InterruptedException;
	/**
	 * ���ܽӿ�(ECB��ʽ)
	 * @param wk������Կ
	 * @param input����������
	 * @return
	 */
	public byte[] encrypt(WorkingKey wk,byte[] input);
	
	/**
	 * ����mac����
	 * @param input
	 * @return
	 */
	public byte[] caculateMac(byte[] input);
	
	/**
	 * ������ǰ����
	 */
	public void reset();
	
	public BatteryInfoResult getPowerLevel();
	
	/**
	 * ��ʾ��Ϣ
	 * 
	 * @since ver1.0
	 * @param msg
	 */
	public void showMessage(String msg);
	/**
	 * �ڹ涨��ʱ������ʾ��Ϣ
	 * 
	 * @since ver1.0
	 * @param msg
	 * @param showtime
	 */
	public void showMessageWithinTime(String msg,int showtime);
	/**
	 * pos����
	 * @since ver1.0
	 */
	public void clearScreen();

	/**
	 * ��ӡͼƬ
	 * @param position ƫ����
	 * @param bitmap λͼ
	 */
	public void printBitMap(int position,Bitmap bitmap);
	/**
	 * ��ӡ�ַ�
	 * @param data ����ӡ����
	 */
	public void printString(String data);
	
	public String getCurrentDriverVersion();
}
