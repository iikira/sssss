package com.szzjcs.commons.util;

import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.net.IpUtil;
import com.szzjcs.commons.string.StringUtil;

public class IdWorkerCreator
{
    private static final Logger log = LogProxy.getLogger(IdWorkerCreator.class);

    private static IdWorker idWorker = null;
    static
    {
        try
        {
            String serverip = IpUtil.getLocalAddress();

            long longip = ipTail2Long(serverip);
            idWorker = new IdWorker(longip);
            log.info("[IdWorkerCreator.static]serverip: " + serverip + ", longip: " + longip + ", idWorker: "
                    + idWorker);
        }
        catch (Throwable e)
        {
            log.error("", e);
        }
    }

    // 把IP最后几位转成long型,只适合润生活初期自己的IdWorker用
    private static long ipTail2Long(String paramString)
    {
        String[] arrayOfString = StringUtil.split(paramString, ".");

        return StringUtil.convertLong(arrayOfString[3], 1);
    }

    public static long nextId()
    {
        return -idWorker.nextId();
    }

    public static void main(String[] args)
    {
        for (int i = 0; i < 100; i++) {
            System.out.println(i + " " + nextId());
        }


    }

}
