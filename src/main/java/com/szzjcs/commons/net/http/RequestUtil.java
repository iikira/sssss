package com.szzjcs.commons.net.http;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.szzjcs.commons.string.StringUtil;

/**
 * 取参数,并自动转成基本数据类型如boolean
 * 
 * @author: smartlv
 * @date 2014年2月14日 下午12:04:35
 */
public class RequestUtil
{
    /**
     * 获取页面输入的String类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @return String型的输入参数
     */
    public static String getStringParameter(ServletRequest request, String name, String defaults)
    {
        return getStringParameter(request, name, defaults, true);
    }

    /**
     * 获取页面输入的String类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @param decode
     *        是否需要解码&#xxx;这种编码
     * @return String型的输入参数
     */
    public static String getStringParameter(ServletRequest request, String name, String defaults, boolean decode)
    {
        String str = request.getParameter(name);
        if (decode)
        {
            // 解码
            str = StringUtil.decodeNetUnicode(str);
        }
        return StringUtil.convertString(str, defaults);
    }

    /**
     * 获取页面输入的int类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @return int型的输入参数
     */
    public static int getIntParameter(ServletRequest request, String name, int defaults)
    {
        return StringUtil.convertInt(request.getParameter(name), defaults);
    }

    /**
     * 获取页面输入的int类型参数，若无该输入参数，则返回0
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @return int型的输入参数
     */
    public static int getIntParameter(ServletRequest request, String name)
    {
        return getIntParameter(request, name, 0);
    }

    /**
     * 获取页面输入的long类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @return long型的输入参数
     */
    public static long getLongParameter(ServletRequest request, String name, long defaults)
    {
        return StringUtil.convertLong(request.getParameter(name), defaults);
    }

    /**
     * 获取页面输入的long类型参数，若无该输入参数，则返回0
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @return long型的输入参数
     */
    public static long getLongParameter(ServletRequest request, String name)
    {
        return getLongParameter(request, name, 0);
    }

    /**
     * 获取页面输入的double类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @return double型的输入参数
     */
    public static double getDoubleParameter(ServletRequest request, String name, double defaults)
    {
        return StringUtil.convertDouble(request.getParameter(name), defaults);
    }

    /**
     * 获取页面输入的double类型参数，若无该参数，则返回0.0
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @return long型的输入参数
     */
    public static double getDoubleParameter(ServletRequest request, String name)
    {
        return getDoubleParameter(request, name, 0.0);
    }

    /**
     * 获取页面输入的short类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @return short型的输入参数
     */
    public static short getShortParameter(ServletRequest request, String name, short defaults)
    {
        return StringUtil.convertShort(request.getParameter(name), defaults);
    }

    /**
     * 获取页面输入的short类型参数，若无该参数，则返回0
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @return short型的输入参数
     */
    public static short getShortParameter(ServletRequest request, String name)
    {
        return getShortParameter(request, name, (short) 0);
    }

    /**
     * 获取页面输入的float类型参数
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @param defaults
     *        设定的默认值
     * @return float型的输入参数
     */
    public static float getFloatParameter(ServletRequest request, String name, float defaults)
    {
        return StringUtil.convertFloat(request.getParameter(name), defaults);
    }

    /**
     * 获取页面输入的float类型参数，若无该参数，则返回0.0
     * 
     * @param request
     *        ServletRequest的实例
     * @param name
     *        参数名字
     * @return long型的输入参数
     */
    public static float getFloatParameter(ServletRequest request, String name)
    {
        return getFloatParameter(request, name, (float) 0.0);
    }

    /**
     * 获取boolean 类型的参数
     * 
     * @param request
     * @param name
     * @param defaults
     * @return boolean
     */
    public static boolean getBooleanParameter(ServletRequest request, String name, boolean defaults)
    {
        return StringUtil.convertBoolean(request.getParameter(name), defaults);
    }

    /**
     * 获取boolean 类型的参数,默认值为false
     * 
     * @param request
     * @param name
     * @return boolean
     */
    public static boolean getBooleanParameter(ServletRequest request, String name)
    {
        return getBooleanParameter(request, name, false);
    }

    public static String getUrlAndQryString(String url, HttpServletRequest request)
    {
        String delimiter = "?";
        if (url.indexOf('?') > 0)
        {
            delimiter = "&";
        }
        String queryString = "";
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements())
        {
            String param = paramNames.nextElement();
            String value = request.getParameter(param);
            queryString += (StringUtil.isEmpty(queryString) ? "" : "&") + param + "=" + value;
        }

        queryString = StringUtil.isEmpty(queryString) ? "" : delimiter + queryString;

        url += queryString;
        return url;
    }
}
