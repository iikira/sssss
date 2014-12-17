package com.szzjcs.commons.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数字工具类
 * 
 * @author: hualong
 * @date 2014年4月9日 下午4:57:32
 */
public class DigitalUtil
{
    private final static Logger log = LogProxy.getLogger(DigitalUtil.class);

    /**
     * 把货币整数分转换成小数元
     * 
     * @param v
     *        货币分值
     * @return 精确到小数点后两位的元
     * @author: hualong
     * @date: 2014年4月9日下午4:58:54
     */
    public static double toYuan(int v)
    {
        try
        {
            return v / 100D;
        }
        catch (Exception e)
        {
            log.error("activity sku price integer change to double error", e);
            return 0.0;
        }
    }

    /**
     * 把货币小数元转换成整数分
     * 
     * @param v
     *        精确到小数点后两位的元
     * @return 货币整数分
     * @author: hualong
     * @date: 2014年4月9日下午5:00:06
     */
    public static int toFen(double v)
    {
        try
        {
            return (int) (v * 100);
        }
        catch (Exception e)
        {
            log.error("activity sku price integer change to double error", e);
            return 0;
        }
    }

    public static String toAlph(long num, int width){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(width);
        while(num > 25){
            sb.append(alphabet[(int) (num % 26)]);
            num /= 26;
        }
        sb.append(alphabet[(int) num]);
        return StringUtils.leftPad(sb.reverse().toString(), width, 'a');
    }

    /**
     * 把 7 整数转换成 [1, 2, 4]这样的2^n形式因子的数组
     */
    public static List<Integer> toBinaryArray(int i){
        Preconditions.checkArgument(i >= 0);
        List<Integer> array = Lists.newArrayListWithCapacity(8);
        int e = 1;
        while(e <= i && e > 0){
            if((e & i) > 0){
                array.add(e);
            }
            e = e << 1;
        }
        return array;
    }

    public static void main(String[] args){
        long t = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            toBinaryArray(i);
        }

        System.out.println(System.currentTimeMillis() - t);
    }
}
