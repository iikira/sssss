package com.szzjcs.commons.thirdapi.push.config;

import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 极光推送
 **/
@Service
@DisconfFile(filename = "push.properties")
public class JpushConfig
{
    private static String appkey = "e6a8f5528739281613d6f117";
    private static String secret = "c8a5c46c8597e3d0ad81851c";
    private static int retry = 3;
    private static boolean apnsProduction = false;// ios apns 是什么环境；false=测试环境；true=正式环境

    @DisconfFileItem(name = "apnsProduction")
    public static boolean isApnsProduction()
    {
        return apnsProduction;
    }

    public static void setApnsProduction(boolean apnsProduction)
    {
        JpushConfig.apnsProduction = apnsProduction;
    }

    @DisconfFileItem(name = "appkey")
    public static String getAppkey()
    {
        return appkey;
    }

    @DisconfFileItem(name = "secret")
    public static String getSecret()
    {
        return secret;
    }

    @DisconfFileItem(name = "retry")
    public static int getRetry()
    {
        return retry;
    }

    @Override
    public String toString()
    {
        return "JpushConfig [appkey=" + appkey + ", secret=" + secret + ", retry=" + retry + "]";
    }
}
