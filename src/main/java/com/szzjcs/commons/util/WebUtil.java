package com.szzjcs.commons.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * 放一些web系统用到工具方法
 * 
 * @author smartlv
 */
public class WebUtil
{
    public static String COOKIEID_KEY = "sd_userid";
    /**
     * 获取cookieid,若无则生成
     * 
     * @return
     */
    public static String getCookieId(HttpServletRequest request, HttpServletResponse response)
    {

        String cookieValue = getCookieValue(request, COOKIEID_KEY);
        if (StringUtils.isEmpty(cookieValue))
        {
            int randnumber = (new java.util.Random()).nextInt(9999);
            String sdUserid = randnumber + "" + System.currentTimeMillis();
            addCookie(response, COOKIEID_KEY, sdUserid);
            cookieValue = sdUserid;
        }
        return cookieValue;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName)
    {
        if (cookieName != null)
        {
            Cookie cookie = getCookie(request, cookieName);
            if (cookie != null)
            {
                return cookie.getValue();
            }
        }
        return "";
    }

    private static Cookie getCookie(HttpServletRequest request, String cookieName)
    {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        try
        {
            if (cookies != null && cookies.length > 0)
            {
                for (int i = 0; i < cookies.length; i++)
                {
                    cookie = cookies[i];
                    if (cookie.getName().equals(cookieName))
                    {
                        return cookie;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static void addCookie(HttpServletResponse response, String name, String value)
    {
        Cookie cookies = new Cookie(name, value);
        cookies.setPath("/");
        cookies.setMaxAge(60 * 60 * 24 * 365);// 设置cookie经过多长秒后被删除。如果0，就说明立即删除。如果是负数就表明当浏览器关闭时自动删除。
        // cookies.setDomain(".runlife.com.cn");
        response.addCookie(cookies);
    }

}
