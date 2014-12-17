package com.szzjcs.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一般用于读取classpath下的配置文件,注意读取的配置文件路径是相对传入参数clazz的load入路径.
 * 
 * @author smartlv
 */

public class CongfigResource
{

    private static final Logger debugLog = LoggerFactory.getLogger(CongfigResource.class);

    /**
     * 读取classpath下的文件
     * 
     * @param resource
     *        资源路径
     * @param clazz
     * @return InputStream 文件的inputstram,如果找不到则返回null
     */
    @SuppressWarnings("rawtypes")
    public static InputStream loadConfigFile(String resource, Class clazz)
    {
        ClassLoader classLoader = null;
        try
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (Exception e)
        {
            CongfigResource.debugLog.error("loadConfigFile error: ", e);
        }
        if (classLoader == null)
        {
            classLoader = clazz.getClassLoader();
        }
        try
        {
            if (classLoader != null)
            {
                URL url = classLoader.getResource(resource);
                if (url == null)
                {
                    System.out.println("Can not find resource:" + resource);
                    return null;
                }
                if (url.toString().startsWith("jar:file:"))
                {
                    System.out.println("Get resource \"" + resource + "\" from jar:\t" + url.toString());
                    return clazz.getResourceAsStream(resource.startsWith("/") ? resource : "/" + resource);
                }
                else
                {
                    System.out.println("Get resource \"" + resource + "\" from:\t" + url.toString());
                    return new FileInputStream(new File(url.toURI()));
                }
            }
        }
        catch (Exception e)
        {
            CongfigResource.debugLog.error("loadConfigFile error: ", e);
        }
        return null;
    }
}
