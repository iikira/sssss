package com.szzjcs.commons.string;

import java.util.Random;

/**
 * 随机工具
 * 
 * @author smartlv
 */
public abstract class RandomUtil
{
    private static final Random random = new Random();
    private static final String DEFAULT_RAND_CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMLOPQRSTUVWXYZ1234567890";

    /**
     * 获取begin和end之间的整数 [begin,end]
     * 
     * @param begin
     *        起始值
     * @param end
     *        终止值
     * @return begin和end之间的整数
     */
    public static int next(int begin, int end)
    {
        if (end <= begin)
        {
            throw new IllegalArgumentException("end must larger than begin");
        }

        int minus = random.nextInt(end - begin + 1);
        return (begin + minus);
    }

    /**
     * 获取begin和end之间的整数 [begin,end]
     * 
     * @param begin
     *        起始值
     * @param end
     *        终止值
     * @return begin和end之间的整数
     */
    public static long next(long begin, long end)
    {
        if (end <= begin)
        {
            throw new IllegalArgumentException("end must larger than begin");
        }

        long minus = random.nextInt((int) (end - begin + 1));
        return (begin + minus);
    }

    /**
     * 获取pSngBegin和pSngEnd之间的数值 [pSngBegin,pSngEnd)
     * 
     * @param pSngBegin
     *        起始值
     * @param pSngEnd
     *        终止值
     * @return pSngBegin和pSngEnd之间的数值
     */
    public static double getRandomNum(double pSngBegin, double pSngEnd)
    {
        if (pSngEnd <= pSngBegin)
        {
            throw new IllegalArgumentException("pSngEnd must larger than pSngBegin");
        }

        return (pSngEnd - pSngBegin) * Math.random() + pSngBegin;
    }

    /**
     * 按照一定概率进行随机<br>
     * 该方法参数太多，不做合法检测<br>
     * FIXME
     * 
     * @param pSngBegin
     *        随机数范围的开始数字
     * @param pSngEnd
     *        随机数范围结束数字
     * @param pSngPB
     *        要随机的数字的开始数字
     * @param pSngPE
     *        要随机的数字的结束数字
     * @param pBytP
     *        要随机的数字随机概率
     * @return 按照一定概率随机的数字
     */
    public static double getRndNumP(double pSngBegin, double pSngEnd, double pSngPB, double pSngPE, double pBytP)
    {
        double sngPLen = pSngPE - pSngPB;
        // total length
        double sngTLen = pSngEnd - pSngBegin;
        // FIXME may throw java.lang.ArithmeticException : / by zero
        if ((sngPLen / sngTLen) * 100 == pBytP)
        {
            return getRandomNum(pSngBegin, pSngEnd);
        }

        // ((sngPLen + sngIncreased) / (sngTLen + sngIncreased)) * 100 =
        // bytP
        double sngIncreased = ((pBytP / 100) * sngTLen - sngPLen) / (1 - (pBytP / 100));
        // 缩放回原来区间
        double sngResult = getRandomNum(pSngBegin, pSngEnd + sngIncreased);
        if (pSngBegin <= sngResult && sngResult <= pSngPB)
        {
            return sngResult;
        }

        if (pSngPB <= sngResult && sngResult <= (pSngPE + sngIncreased))
        {
            return pSngPB + (sngResult - pSngPB) * sngPLen / (sngPLen + sngIncreased);
        }

        if ((pSngPE + sngIncreased) <= sngResult && sngResult <= (pSngEnd + sngIncreased))
        {
            return sngResult - sngIncreased;
        }

        return 0d;
    }

    /**
     * 生成随机字符串
     * 
     * @param char_set
     *        字符串中允许出现的字符
     * @param len
     *        字符串的长度
     * @return 随机字符串
     */
    public static String getString(String char_set, int len)
    {
        char[] chars = new char[len];
        for (int i = 0; i < len; ++i)
        {
            int r = random.nextInt(char_set.length());
            chars[i] = char_set.charAt(r);
        }
        return new String(chars);
    }

    /**
     * 生成随机字符串（可能出现的字符为大小写字母和数字）
     * 
     * @param len
     *        要生成的字符串的长度
     * @return 随机字符串
     */
    public static String getString(int len)
    {
        return getString(RandomUtil.DEFAULT_RAND_CHAR_SET, len);
    }

    /**
     * 生成只包含数字的随机字符串
     * 
     * @param len
     *        长度
     * @return 结果
     */
    public static String getIntString(int len)
    {
        return getString("0123456789", len);
    }

    /**
     * 生成随机字符串（长度为3）
     * 
     * @return 结果
     */
    public static String getIntString()
    {
        return getIntString(3);
    }

    public static void main(String[] args)
    {
        System.out.println(RandomUtil.getString("hello", 123));
    }

    /**
     * 随机获取字符(字母)
     * 
     * @param size
     *        字符位数
     * @return
     */
    public static String randomCharacters(int size)
    {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < size; i++)
        {
            int j = random.nextInt(26) + 1;// 获得随机数
            sb.append(StringUtil.getChar(j));
        }
        return sb.toString();
    }

    /**
     * 随机获取字符(字母+数字)
     * 
     * @param size
     *        字符位数
     * @return
     */
    public static String randomCharactersInt(int size)
    {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < size; i++)
        {
            int j = random.nextInt(35) + 1;// 获得随机数
            if (j > 26)
            {
                sb.append(j);
                continue;
            }
            sb.append(StringUtil.getChar(j));
        }
        return sb.toString();
    }
}
