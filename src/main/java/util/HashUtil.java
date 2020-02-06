package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算哈希值的工具类
 * @author fanbin
 * @date 2020/2/6
 */
public class HashUtil {

    private HashUtil() {

    }

    /**
     * 计算给定字符串的 SHA-256 哈希值
     * @param str 原像字符串
     * @return 计算结果
     */
    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = bytes2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 把 byte 数组，转换为十六进制数字的字符串
     * @param bytes byte 数组
     * @return 十六进制数字的字符串
     */
    private static String bytes2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            builder.append(temp);
        }
        return builder.toString();
    }

}
