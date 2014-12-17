package com.szzjcs.commons.crypto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;

public class SHA1Coding
{
    private static final Logger logger = LogProxy.getLogger(SHA1Coding.class);

    /**
     * 用SHA1算法加密字节数组
     * 
     * @param bytes
     *        要加密的字节
     * @return byte[] 加密后的字节数组，若加密失败，则返回null
     */
    public static byte[] encode(byte[] bytes)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes);
            byte[] digesta = digest.digest();
            return digesta;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 用SHA1算法加密后再转换成hex String
     * 
     * @param bytes
     * @return String
     */
    public static String encode2HexStr(byte[] bytes)
    {
        return HexUtil.bytes2HexStr(SHA1Coding.encode(bytes));
    }

    public static String encode2HexStr(String str, String encoder)
    {
        try {
            return HexUtil.bytes2HexStr(SHA1Coding.encode(str.getBytes(encoder)));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("encoder unsupported:" + encoder);
        }
    }

    /**
     * 用SHA1算法加密后再转换成BASE64编码的字符串
     * 
     * @param bytes
     * @return String
     */
    public static String encode2Base64(byte[] bytes)
    {
        return BASE64Coding.encode(SHA1Coding.encode(bytes));
    }

    /**
     * 计算文件的SHA1
     * 
     * @param filePath
     *        文件路径
     * @return SHA1结果，若加密失败，则返回null
     */
    public static byte[] encodeFile(String filePath)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            byte[] digesta = null;
            int readed = -1;
            try
            {
                while ((readed = fis.read(buffer)) != -1)
                {
                    digest.update(buffer, 0, readed);
                }
                digesta = digest.digest();
            }
            catch (IOException e)
            {
                SHA1Coding.logger.error("IOException:" + filePath, e);
            }
            finally
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    SHA1Coding.logger.error("IOException:" + filePath, e);
                }
            }
            return digesta;
        }
        catch (FileNotFoundException e)
        {
            SHA1Coding.logger.error("file not found:" + filePath, e);
            return null;
        }
        catch (NoSuchAlgorithmException e)
        {
            SHA1Coding.logger.error("NoSuchAlgorithmException:MD5", e);
            return null;
        }
    }

    /**
     * 计算文件的SHA1,转换成hex String
     * 
     * @param filePath
     *        文件路径
     * @return SHA1结果，若加密失败，则返回null
     */
    public static String encodeFile2HexStr(String filePath)
    {
        return HexUtil.bytes2HexStr(SHA1Coding.encodeFile(filePath));
    }

    /**
     * 计算文件的SHA1,转换成Base64 string
     * 
     * @param filePath
     *        文件路径
     * @return SHA1结果，若加密失败，则返回null
     */
    public static String encodeFile2Base64(String filePath)
    {
        byte[] bytes = SHA1Coding.encodeFile(filePath);
        if (bytes == null)
        {
            return null;
        }
        return BASE64Coding.encode(bytes);
    }

    public final static String sha1ForWx(String s)
    {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try
        {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("sha-1");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args)
    {
        String s = "13642121251364813742ziwuliao";
        System.out.println(SHA1Coding.encode2HexStr("ab", "UTF-8").length());
    }
}
