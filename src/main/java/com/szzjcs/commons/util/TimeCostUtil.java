package com.szzjcs.commons.util;

/**
 * 输出打印花费时间
 * 
 * @author: smartlv
 */
public class TimeCostUtil
{
    private long lastTime;
    private String tips = "";

    public TimeCostUtil()
    {
        lastTime = System.currentTimeMillis();
    }

    public TimeCostUtil(String tips)
    {
        this();
        this.tips = tips;
    }

    public String getCostTimeString()
    {
        return getCostTimeString(null);
    }

    public String getCostTimeString(String t)
    {
        long n = System.currentTimeMillis();
        long cost = (n - lastTime);
        String s = null;
        if (t != null)
        {
            this.tips = t;
        }
        if (cost > 10000)
        {
            s = tips + " cost:" + (cost / 1000l) + "s";
        }
        else
        {
            s = tips + " cost:" + (cost) + "ms";
        }
        lastTime = n;

        return s;
    }

    @Override
    public String toString()
    {
        return getCostTimeString();
    }
}
