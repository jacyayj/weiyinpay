package com.example.untils;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
	/**
	 * DES加密方法
	 * @author jacyayj
	 *
	 */
public class DesCipher {
	private static Cipher enCipher;
	private static Cipher deCipher;
	private static final String KEY_STRING = "&^我ai亿万商ILoVeViEWAt!@()";				// des加解密用的，不可修改！

	public DesCipher() {
		try {
			byte[] utfKey = KEY_STRING.getBytes("UTF8");

			DESKeySpec keySpec = new DESKeySpec(utfKey);

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			enCipher = Cipher.getInstance("DES");
			deCipher = Cipher.getInstance("DES");
			enCipher.init(Cipher.ENCRYPT_MODE, key);
			deCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (NoSuchPaddingException nspe) {
			nspe.printStackTrace();
		} catch (NoSuchAlgorithmException asae) {
			asae.printStackTrace();
		} catch (InvalidKeyException ike) {
			ike.printStackTrace();
		} catch (InvalidKeySpecException ikse) {
			ikse.printStackTrace();
		}
	}
	/**
	 * 加密方法	
	 * @param data	需要加密的数据
	 * @return	加密完成的数据
	 */
	public String encrypt(Map<String,String> data) {
		String str = data.toString().substring(1,data.toString().length()-1);
		try {
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = enCipher.doFinal(utf8);
			return new sun.misc.BASE64Encoder().encode(enc);
		} catch (BadPaddingException bpe) {
			bpe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IllegalBlockSizeException ibse) {
			ibse.printStackTrace();
		}
		return null;
	}
	/**
	 * 解密方法
	 * @param str	需要解密的数据
	 * @return	解密后的数据
	 */
	public Map<String,String> decrypt(String str) {
		try {
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
			if (dec.length % 8 != 0) {
				return null;
			}
			byte[] utf8 = deCipher.doFinal(dec);
			String data = new String(utf8, "UTF8");
			Map<String,String> map = new HashMap<String, String>();
			String datas[] = data.split(",");
			for (int i = 0; i < datas.length; i++) {
				String string = datas[i].trim();
				try {
					map.put(string.split("=")[0],string.split("=")[1]);
				} catch (Exception e) {
					map.put(string.split("=")[0], null);
				}
			}
			return map;
		} catch (BadPaddingException bpe) {
			bpe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IllegalBlockSizeException ibse) {
			ibse.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("key","value");
		System.out.println(new DesCipher().encrypt(map));
	}
}