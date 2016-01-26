package com.example.cz;

import com.newland.mtype.module.common.pin.WorkingKeyType;

public class Const {
	
	/**
	 * ����Կ����<p>
	 * ����������ͬ���ʾʹ��ͬһ������Կ����
	 * @author lance
	 */
	public static class MKIndexConst{
		/**
		 * ����Կ����
		 */
		public static final int DEFAULT_MK_INDEX = 01;
	}
	
	/**
	 * ������Կ����:{@link WorkingKeyType#PININPUT}
	 * @author lance
	 */
	public static class PinWKIndexConst{
		/**
		 * Ĭ��PIN���ܹ�����Կ����
		 */
		public static final int DEFAULT_PIN_WK_INDEX = 2;
	}
	/**
	 * ������Կ����:{@link WorkingKeyType#MAC}
	 * @author lance
	 */
	public static class MacWKIndexConst{
		/**
		 * Ĭ��MAC���ܹ�����Կ����
		 */
		public static final int DEFAULT_MAC_WK_INDEX = 3;
	}
	/**
	 * ������Կ����:{@link WorkingKeyType#DATAENCRYPT}
	 * @author lance
	 */
	public static class DataEncryptWKIndexConst{
		/**
		 * Ĭ�ϴŵ����ܹ�����Կ����
		 */
		public static final int DEFAULT_TRACK_WK_INDEX = 4;
		
		public static final int DEFAULT_MUTUALAUTH_WK_INDEX = 5;
	}
	
	/**
	 * �豸���������ع��
	 * 
	 * @author lance
	 *
	 */
	public static class DeviceParamsPattern{
		/**
		 * Ĭ�ϴ�ű��뼯<p>
		 */
		public static final String DEFAULT_STORENCODING = "utf-8";
		/**
		 * ���ڸ�ʽ�����<p>
		 */
		public static final String DEFAULT_DATEPATTERN = "yyyyMMddHHmmss"; 
	}
	
	/**
	 * �豸����<tt>tag</tt>
	 * @author lance
	 */
	public static class DeviceParamsTag{
		/**
		 * �̻��Ŵ��<tt>tag</tt>
		 */
		public static final int MRCH_NO = 0xFF9F11;
		/**
		 * �ն˺Ŵ��<tt>tag</tt>
		 */
		public static final int TRMNL_NO = 0xFF9F12;
		/**
		 * ������Կ���<tt>tag</tt>
		 */
		public static final int WK_UPDATEDATE = 0xFF9F13;
		/**
		 * pos��ʾ���<tt>tag</tt>
		 */
		public static final int DEVICE_TYPE = 0xFF9F14;
		/**
		 * �ն˺Ŵ��<tt>tag</tt>
		 */
		public static final int MRCH_NAME = 0xFF9F15;
	}

	public static class KeyIndexConst {
		/**
		 * KSN ��ʼ������
		 */
		public static final int KSN_INITKEY_INDEX = 1;
	}
}
