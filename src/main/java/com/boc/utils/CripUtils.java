package com.boc.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CripUtils 
{

	public static void main(String a[])
	{
		try {
			String min = String.valueOf(toNearestWholeMinute());
			//String encryPasswd = encryptStr("boc@123"+min);
			String encryPasswd = encryptStr("boc@123");
			System.out.println("Encrypted password is "+encryPasswd);
			
			String decryptPassword = decryptStr(encryPasswd);
			System.out.println("decryptPassword is "+decryptPassword);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("randomNo is "+randomNo);
	}
	/*public static String generateRquid(int length){
		String alphabet = 
		        //new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"); //9
		 new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"); //9
		int n = alphabet.length(); //10

		String result = new String(); 
		Random r = new Random(); //11

		for (int i=0; i<length; i++) //12
		    result = result + alphabet.charAt(r.nextInt(n)); //13

		return result;
		}*/
	
	private static byte[] encrypt(String message) throws Exception {
		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);

		final byte[] plainTextBytes = message.getBytes("utf-8");
		final byte[] cipherText = cipher.doFinal(plainTextBytes);
		// final String encodedCipherText = new sun.misc.BASE64Encoder()
		// .encode(cipherText);

		return cipherText;
	}
	
	public static String encryptStr(String message) throws Exception {
		byte[] encryptedByte = CripUtils.encrypt(message);
		return new sun.misc.BASE64Encoder().encode(encryptedByte);
	}
	private static String decrypt(byte[] message) throws GeneralSecurityException, IOException {
		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		decipher.init(Cipher.DECRYPT_MODE, key, iv);

		final byte[] plainText = decipher.doFinal(message);
		return new String(plainText, "UTF-8");
	}

	
	public static String decryptStr(String message){
		try {
			byte[] decBtye = new sun.misc.BASE64Decoder().decodeBuffer(message);
			return decrypt(decBtye);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	 public static int toNearestWholeMinute() {
	        Calendar c = new GregorianCalendar();
	        //Date d = c.getTime();
	        Date d = new Date();
	        c.setTime(d);

	        if (c.get(Calendar.SECOND) >= 30)
	            c.add(Calendar.MINUTE, 1);

	        c.set(Calendar.SECOND, 0);

	        return c.get(Calendar.MINUTE);
	    }

	 static Date toNearestWholeHour(Date d) {
	        Calendar c = new GregorianCalendar();
	        c.setTime(d);

	        if (c.get(Calendar.MINUTE) >= 30)
	            c.add(Calendar.HOUR, 1);

	        c.set(Calendar.MINUTE, 0);
	        c.set(Calendar.SECOND, 0);

	        return c.getTime();
	    }
}
