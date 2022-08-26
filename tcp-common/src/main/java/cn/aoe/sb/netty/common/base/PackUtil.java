package cn.aoe.sb.netty.common.base;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * byte 类型转换工具类
 *
 * @author zhaoruiqing
 */
@Slf4j
@UtilityClass
public class PackUtil {

	private static final long C_DATE_TIME_OFFSET = DateUtil.parseDateTime("0001-01-01 08:00:00").getTime();
	/**
	 * true 小端模式排序，正常理解是大端序
	 */
	private static final boolean LITTLE_ENDIAN = true;

	/**
	 * 合并所有bytes数组并返回
	 *
	 * @param values
	 * @return
	 */
	public static byte[] mergeAllBytes(byte[]... values) {
		int lengthByte = 0;
		for (byte[] value : values) {
			lengthByte += value.length;
		}
		byte[] allByte = new byte[lengthByte];
		int countLength = 0;
		for (byte[] b : values) {
			System.arraycopy(b, 0, allByte, countLength, b.length);
			countLength += b.length;
		}
		return allByte;
	}

	public static byte[] strToBytes(String str) {
		int jsonLength = str.length() * 2;
		byte[] bytes = new byte[jsonLength];
		int j = 0;
		for (char c : str.toCharArray()) {
			byte[] bi = intToBytes((int) c);
			bytes[j++] = bi[2];
			bytes[j++] = bi[3];
		}
		return bytes;
	}


	private static String bytesToStr(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i += 2) {
			byte[] b = new byte[]{0, 0, bytes[i], bytes[i + 1]};
			char c = (char) bytesToInt(b);
			sb.append(c);
		}
		return sb.toString();
	}


	/**
	 * 16进制字符串转byte数组
	 *
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStrToBytes(String hexString) {
		if (StrUtil.isBlank(hexString)) {
			return new byte[]{};
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4
				| charToByte(hexChars[pos + 1]) & 0xff);

		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * bytes转16进制string显示
	 *
	 * @param src
	 * @return
	 */
	public static String bytes2HexStr(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (byte b : src) {
			int v = b & 0xFF;
			String hv = Integer.toHexString(v).toLowerCase();
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}


	public static byte[] charToBytes(char c) {
		return new byte[]{
			(byte) (((int) c >> 8) & 0xFF),
			(byte) ((int) c & 0xFF)
		};
	}

	/**
	 * int类型转成byte数组
	 *
	 * @param n
	 * @return
	 */
	public static byte[] intToBytes(int n) {
		if (LITTLE_ENDIAN) {
			return new byte[]{
				(byte) (n & 0xff),
				(byte) (n >> 8 & 0xff),
				(byte) (n >> 16 & 0xff),
				(byte) (n >> 24 & 0xff)
			};
		}
		//高位在前低位在后
		return new byte[]{
			(byte) ((n >> 24) & 0xFF),
			(byte) ((n >> 16) & 0xFF),
			(byte) ((n >> 8) & 0xFF),
			(byte) (n & 0xFF)
		};
	}

	public static int bytesToInt(byte[] bytes) {
		int offset = 0;
		if (LITTLE_ENDIAN) {
			return (bytes[offset] & 0xFF)
				| ((bytes[offset + 1] & 0xFF) << 8)
				| ((bytes[offset + 2] & 0xFF) << 16)
				| (bytes[offset + 3] & 0xFF) << 24;
		}
		return (((bytes[offset] & 0xFF) << 24)
			| ((bytes[offset + 1] & 0xFF) << 16)
			| ((bytes[offset + 2] & 0xFF) << 8)
			| (bytes[offset + 3] & 0xFF));
	}

	public static short bytesToShort(byte[] bytes) {
		int offset = 0;
		if (LITTLE_ENDIAN) {
			return (short) ((bytes[offset] & 0xFF)
				| ((bytes[offset + 1] & 0xFF) << 8));
		}
		return (short) (((bytes[offset] & 0xFF) << 8)
			| (bytes[offset + 1] & 0xFF));
	}

	public static long bytesToLong(byte[] bytes) {
		if (LITTLE_ENDIAN) {
			return ((long) bytes[0] & 0xff)
				| (((long) bytes[1] & 0xff) << 8)
				| (((long) bytes[2] & 0xff) << 16)
				| (((long) bytes[3] & 0xff) << 24)
				| (((long) bytes[4] & 0xff) << 32)
				| (((long) bytes[5] & 0xff) << 40)
				| (((long) bytes[6] & 0xff) << 48)
				| (((long) bytes[7] & 0xff) << 56);
		}
		long value = 0;
		// 循环读取每个字节通过移位运算完成long的8个字节拼装
		for (int offset = 0; offset < 8; ++offset) {
			int shift = (7 - offset) << 3;
			value |= ((long) 0xff << shift) & ((long) bytes[offset] << shift);
		}
		return value;
	}

	/**
	 * long类型转成byte数组
	 *
	 * @param n
	 * @return
	 */
	public static byte[] longToBytes(long n) {
		if (LITTLE_ENDIAN) {
			return new byte[]{
				(byte) ((n) & 0xFF),
				(byte) ((n >> 8) & 0xFF),
				(byte) ((n >> 16) & 0xFF),
				(byte) ((n >> 24) & 0xFF),
				(byte) ((n >> 32) & 0xFF),
				(byte) ((n >> 40) & 0xFF),
				(byte) ((n >> 48) & 0xFF),
				(byte) ((n >> 56) & 0xFF)
			};
		}
		return new byte[]{
			(byte) ((n >> 56) & 0xFF),
			(byte) ((n >> 48) & 0xFF),
			(byte) ((n >> 40) & 0xFF),
			(byte) ((n >> 32) & 0xFF),
			(byte) ((n >> 24) & 0xFF),
			(byte) ((n >> 16) & 0xFF),
			(byte) ((n >> 8) & 0xFF),
			(byte) (n & 0xFF)
		};
	}

	public static long readToLong(byte[] msgBytes, int index) {
		byte[] b = new byte[8];
		System.arraycopy(msgBytes, index, b, 0, b.length);
		return bytesToLong(b);
	}

	public static int readToInt(byte[] msgBytes, int index) {
		byte[] b = new byte[4];
		System.arraycopy(msgBytes, index, b, 0, b.length);
		if (log.isDebugEnabled()) {
			log.debug("hexStr = {}", bytes2HexStr(b));
		}
		return bytesToInt(b);
	}

	public static short readToShort(byte[] msgBytes, int index) {
		byte[] b = new byte[2];
		System.arraycopy(msgBytes, index, b, 0, b.length);
		return bytesToShort(b);
	}

	public static String readToStr(byte[] msgBytes, int index, int length) {
		byte[] b = new byte[length];
		System.arraycopy(msgBytes, index, b, 0, b.length);
		return bytesToStr(b);
	}

	public static Date readCsDateToJavaDate(byte[] msgBytes, int index) {
		byte[] b = new byte[8];
		System.arraycopy(msgBytes, index, b, 0, b.length);
		long longDate = bytesToLong(b);
		//cDateTimeOffset 本身是负数
		long l = C_DATE_TIME_OFFSET * 10000;
		long date = (longDate + l) / 10000;
		return new Date(date);
	}


	public static byte[] dateToCsDate(Date date) {
		long n = (date.getTime() - C_DATE_TIME_OFFSET) * 10000;
		return longToBytes(n);
	}

}
