package com.szzjcs.commons.util;

import javax.servlet.http.HttpServletRequest;

import com.szzjcs.commons.string.StringUtil;

/**
 * Ua工具类
 * 
 * @author: smartlv
 * @date 2014年3月6日 下午8:02:49
 */
public abstract class BrowserFeatureUtil
{
    /** ua的http header 名字 */
    public static final String UAHEADER = "User-Agent";
    private static final String WEXINUA = "MicroMessenger/";

    // 是否微信浏览器访问
    public static boolean isWx(HttpServletRequest req)
    {
        String ua = StringUtil.convertString(req.getHeader(UAHEADER), "");
        int pos = ua.indexOf(WEXINUA);
        if (pos != -1)
        {
            return true;
        }

        return false;
    }

    // Mobile/9B206 MicroMessenger/5.0" 5.0为用户安装的微信版本号
    public static boolean isCanWxPay(HttpServletRequest req)
    {
        String ua = StringUtil.convertString(req.getHeader(UAHEADER), "");
        int pos = ua.indexOf(WEXINUA);
        String ver = "";
        if (pos != -1)
        {
            ver = ua.substring(pos + WEXINUA.length(), ua.length());
        }
        float version = StringUtil.convertFloat(ver, 5.0f);
        if (version >= 5.0f)
        {
            return true;
        }
        return false;
    }

    static final String[] MOBILE_SPECIFIC_SUBSTRING = { "iPad", "iPhone", "Android", "MIDP", "Opera Mobi",
            "Opera Mini", "BlackBerry", "HP iPAQ", "IEMobile", "MSIEMobile", "Windows Phone", "HTC", "LG", "MOT",
            "Nokia", "Symbian", "Fennec", "Maemo", "Tear", "Midori", "armv", "Windows CE", "WindowsCE", "Smartphone",
            "240x320", "176x220", "320x320", "160x160", "webOS", "Palm", "Sagem", "Samsung", "SGH", "SIE",
            "SonyEricsson", "MMP", "UCWEB" };

    static final String[] MOBILE_IOS = { "iPhone", "iPod", "iPad" };

    public static boolean checkMobile(HttpServletRequest req)
    {
        String userAgent = req.getHeader("user-agent");
        for (String mobile : MOBILE_SPECIFIC_SUBSTRING)
        {
            if (userAgent.contains(mobile) || userAgent.contains(mobile.toUpperCase())
                    || userAgent.contains(mobile.toLowerCase()))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean checkIos(HttpServletRequest req)
    {
        String userAgent = req.getHeader("user-agent");
        for (String mobile : MOBILE_IOS)
        {
            if (userAgent.contains(mobile) || userAgent.contains(mobile.toUpperCase())
                    || userAgent.contains(mobile.toLowerCase()))
            {
                return true;
            }
        }

        return false;
    }
}
