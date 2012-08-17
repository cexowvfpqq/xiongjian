package com.sttm.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;



public class ByteUtil {
	/**
	 * 转换short为byte
	 * 
	 * @param b
	 * @param s
	 *            需要转换的short
	 * @param index
	 */
	public static void putShort(byte b[], short s, int index) {
		b[index + 1] = (byte) (s >> 8);
		b[index + 0] = (byte) (s >> 0);
	}

	/**
	 * 通过byte数组取到short
	 * 
	 * @param b
	 * @param index
	 *            第几位开始取
	 * @return
	 */
	public static short getShort(byte[] b, int index) {
		return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
	}

	/**
	 * 转换int为byte数组
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putInt(byte[] bb, int x, int index) {
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

	/**
	 * 通过byte数组取到int
	 * 
	 * @param bb
	 * @param index
	 *            第几位开始
	 * @return
	 */
	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	/**
	 * 转换long型为byte数组
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putLong(byte[] bb, long x, int index) {
		bb[index + 7] = (byte) (x >> 56);
		bb[index + 6] = (byte) (x >> 48);
		bb[index + 5] = (byte) (x >> 40);
		bb[index + 4] = (byte) (x >> 32);
		bb[index + 3] = (byte) (x >> 24);
	}

	static int bytes2int(byte[] b) {
		if (b.length > 4) {
			return 0;
		}
		int temp = 0;
		for (int i = 0; i < b.length; i++) {
			int i2 = b.length - i - 1;

			temp |= b[i2] << 8 * i;
		}
		if (b.length < 4) {
			temp <<= 8 * (4 - b.length);
		}

		return temp;
	}

	static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	public static int[] getIntBuffer(Blob blob, int[] intBuffer) {
		if (blob == null) {
			return null;
		}
		byte[] table = null;
		try {
			table = blob.getBytes(1, 600 * 800);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream bais = null;
		BufferedInputStream bis = null;
		if (table != null) {
			bais = new ByteArrayInputStream(table);
			bis = new BufferedInputStream(bais);
			byte[] buffer = new byte[4];
			int len = 0;
			int index = 0;
			try {
				while ((len = bis.read(buffer)) != -1) {
					System.out.println(Arrays.toString(buffer));
					intBuffer[index] = bytes2int(buffer, len);
					index++;
				}
				if (bis != null) {
					bis.close();
				}
				if (bais != null) {
					bais.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Arrays.toString(intBuffer));
		return intBuffer;
	}

	public static int bytes2int(byte[] b, int len) {
		if (len > 4) {
			return 0;
		}
		int temp = 0;
		for (int i = 0; i < len; i++) {
			int i2 = len - i - 1;
			temp += b[i2] << 8 * i;
			// temp |= b[i2] << 8 * i
		}
		if (len < 4) {
			temp <<= 8 * (4 - len);
		}
		System.out.println("temp is: " + temp);
		System.out.println("------------------");
		System.out.println("result  is:");
		return temp;
	}

	public byte[] getbyteArray(byte[] b1, byte[] b2) {
		byte[] bytes = new byte[b1.length + b2.length];
		System.arraycopy(b1, 0, bytes, 0, b1.length);
		System.arraycopy(b2, 0, bytes, b1.length, b2.length);
		return bytes;
		// return System.arraycopy(bytes, srcPos, dst, dstPos, length)

	}

	public static byte[] shortToByteArray(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}

	public static final int byteArrayToShort(byte[] b) {
		return (b[0] << 8) + (b[1] & 0xFF);
	}

	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	public static final int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static long byteToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;// 最低位
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// 最低位
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;
		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;

	}

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;

	}

	public static int byteToInt(byte[] b) {
		int s = 0;
		int s0 = b[0] & 0xff;// 最低位
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;

	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {

			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;

	}

	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// 最低位
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;

	}

	public static char[] byte2char(byte[] b) {
		char str[] = new char[b.length];
		for (int i = 0; i < b.length; i++) {
			str[i] = (char) b[i];
		}
		return str;
	}

	public static String byte2str(byte[] b) {
		char str[] = byte2char(b);
		String temp = "";
		for (int i = 0; i < str.length; i++) {
			temp += str[i];
		}
		return temp;
	}

	public static String byte2unicode(byte[] b) {
		String s = "";
		try {
			s = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// char str[] = byte2char(b);
		return s;
	}

	public static int[] byte2intarr(byte[] b) {
		int temp[] = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			temp[i] = b[i] & 0xFF;
		}
		return temp;
	}

	public static byte[] long2byte(long l) {
		String temp = Long.toString(l);
		return str2byte(temp);
	}

	public static byte[] str2byte(String s) {
		byte temp[] = new byte[s.trim().length()];
		for (int i = 0; i < s.trim().length(); i++) {
			temp[i] = (byte) (s.charAt(i) & 0xFF);
		}
		return temp;
	}

	public static byte[] int2byte(int temp, int len) {
		byte b[] = new byte[len];
		for (int i = 0; i < len; i++) {
			b[i] = (byte) (temp & 0xFF);
			temp >>= 8;
		}
		return b;
	}

	public static byte[] intarr2byte(int[] temp) {
		byte b[] = new byte[temp.length];
		for (int i = 0; i < temp.length; i++) {
			b[i] = (byte) (temp[i] & 0xFF);
		}
		return b;
	}

	public static byte[] readByteData2(InputStream is, long length) {

		byte buff[] = new byte[(int) length];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int tag = -1;
		try {
			while ((tag = is.read(buff)) != -1) {
				bos.write(buff, 0, tag);
				bos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return bos.toByteArray();
	}

	public static byte[] readByteData(InputStream is) {

		byte buff[] = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int tag = -1;
		try {
			while ((tag = is.read(buff)) != -1) {
				bos.write(buff, 0, tag);
				bos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return bos.toByteArray();
	}

	public static void writeByteFile(OutputStream os, byte b[]) {
		try {
			os.write(b);
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] reverseByte(byte b[]) {
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (b[i] ^ 0xFF);
		}
		return b;
	}

}
