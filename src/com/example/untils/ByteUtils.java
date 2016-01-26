package com.example.untils;

public class ByteUtils {

	/**
	 * ʮ������ ת�� byte[]
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexString2ByteArray(String hexStr) {
		if (hexStr == null)
			return null;
		if (hexStr.length() % 2 != 0) {
			return null;
		}
		byte[] data = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			char hc = hexStr.charAt(2 * i);
			char lc = hexStr.charAt(2 * i + 1);
			byte hb = hexChar2Byte(hc);
			byte lb = hexChar2Byte(lc);
			if (hb < 0 || lb < 0) {
				return null;
			}
			int n = hb << 4;
			data[i] = (byte) (n + lb);
		}
		return data;
	}

	public static byte hexChar2Byte(char c) {
		if (c >= '0' && c <= '9')
			return (byte) (c - '0');
		if (c >= 'a' && c <= 'f')
			return (byte) (c - 'a' + 10);
		if (c >= 'A' && c <= 'F')
			return (byte) (c - 'A' + 10);
		return -1;
	}

	/**
	 * byte[] ת 16�����ַ���
	 * 
	 * @param arr
	 * @return
	 */
	public static String byteArray2HexString(byte[] arr) {
		StringBuilder sbd = new StringBuilder();
		for (byte b : arr) {
			String tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() < 2)
				tmp = "0" + tmp;
			sbd.append(tmp);
		}
		return sbd.toString();
	}

	public static String byteArray2HexStringWithSpace(byte[] arr) {
		StringBuilder sbd = new StringBuilder();
		for (byte b : arr) {
			String tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() < 2)
				tmp = "0" + tmp;
			sbd.append(tmp);
			sbd.append(" ");
		}
		return sbd.toString();
	}

	static public String getBCDString(byte[] data, int start, int end) {
		byte[] t = new byte[end - start + 1];
		System.arraycopy(data, start, t, 0, t.length);
		return ByteUtils.byteArray2HexString(t);
	}

	static public String getHexString(byte[] data, int start, int end) {
		byte[] t = new byte[end - start + 1];
		System.arraycopy(data, start, t, 0, t.length);
		return ByteUtils.byteArray2HexStringWithSpace(t);
	}
}
