package com.szzjcs.commons.logutil;

import org.slf4j.Logger;

/**
 * 输出debug及业务调试日志
 * 
 * @author: smartlv
 * @date 2014年2月12日 下午7:24:26
 */
public class LogUtil
{

    private static String CLASS_NAME = LogUtil.class.getName();

    /**
     * 方便调试,直接打印日志堆栈
     * 
     * @param log
     *        日志对象
     * @param str
     *        格式化字符串 或者 日志消息
     * @param obj
     *        消息
     */
    public static void bizDebug(Logger log, String str, Object... obj)
    {
        if (log.isDebugEnabled())
        {
            String prefix = "";
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if (stackTraceElements != null && stackTraceElements.length > 2)
            {
                int deep = 2;
                String c = stackTraceElements[deep].getClassName();
                if (c != null && c.contains(CLASS_NAME))
                {
                    deep = 3;
                    c = stackTraceElements[deep].getClassName();
                }
                if (c != null && c.contains("."))
                {
                    c = c.substring(c.lastIndexOf('.') + 1);
                }
                prefix = c + "." + stackTraceElements[deep].getMethodName() + " line:"
                        + stackTraceElements[deep].getLineNumber();
            }
            try
            {
                if (obj != null)
                {
                    log.debug(prefix + " " + String.format(str, obj));
                }
                else
                {
                    log.debug(prefix + " " + str);
                }
            }
            catch (Exception ex)
            {
                log.debug(prefix + " format err. " + str);
            }
        }
    }
}
