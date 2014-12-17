package com.szzjcs.commons.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.string.StringUtil;

/**
 * 功能：直接取linux服务器ip
 * 
 * @author smartlv
 */
public final class IpUtil
{
    private static final Logger log = LogProxy.getLogger(IpUtil.class);

    /**
     * 按模式匹配,比如你指向匹配内网IP"(172\\..*)|(10\\..*)"
     * 
     * @param pattern
     * @return
     */
    public static String getLocalAddress(String pattern)
    {
        return getLocalAddress(Pattern.compile(pattern));
    }

    // 重载下
    public static String getLocalAddress(Pattern pattern)
    {
        try
        {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements())
            {
                NetworkInterface ni = e.nextElement();
                Enumeration<InetAddress> en = ni.getInetAddresses();
                while (en.hasMoreElements())
                {
                    InetAddress addr = en.nextElement();
                    String ip = addr.getHostAddress();
                    Matcher m = pattern.matcher(ip);
                    if (m.matches())
                        return ip;
                }
            }
        }
        catch (Exception e)
        {
            log.error("", e);
        }
        return null;
    }

    // Linux下只会返回127.0.0.1,windows下的InetAddress.getLocalHost()
    public static String getLocalAddress()
    {
        Enumeration<NetworkInterface> netInterfaces = null;
        try
        {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements())
            {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements())
                {
                    InetAddress ip = ips.nextElement();
                    if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
                            && ip.getHostAddress().indexOf(":") == -1)
                    {
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("", e);
        }
        return "";
    }

    public static long toLong(String ipStr)
    {
        long result = 0;
        String[] ipAddressInArray = ipStr.split("\\.");
        for (int i = 3; i >= 0; i--)
        {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);

        }
        return result;
    }

    /**
     * 获取用户网关ip
     * 
     * @param request
     * @return
     */
    public static String getUserIp(HttpServletRequest request)
    {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtil.isEmpty(ip))
        {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0)
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static void main(String[] args)
    {
        System.out.println(toLong("192.168.1.20"));
    }
}
