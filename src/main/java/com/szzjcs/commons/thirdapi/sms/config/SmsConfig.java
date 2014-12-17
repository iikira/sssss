package com.szzjcs.commons.thirdapi.sms.config;

import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 短信网关
 **/
@Service
@DisconfFile(filename = "sms.properties")
public class SmsConfig
{
    private static String provider;
    private static int expire = 100;

    private static String yktUrl = "http://220.181.190.42/sendMultiSmsIface";
    private static String yktUsrname = "ykttest";
    private static String yktMdrSalt = "8cd94f156c8c4f8483d74bb4";
    private static String yktSign = "一卡通";

    private static String iemsUrl = "http://GATEWAY.IEMS.NET.CN/GsmsHttp";
    private static String iemsUsrname = "68570:admin";
    private static String iemsPassword = "80149796";

    @DisconfFileItem(name = "sms.iems.Password")
    public static String getIemsPassword()
    {
        return iemsPassword;
    }

    @DisconfFileItem(name = "sms.iems.usrname")
    public static String getIemsUsrname()
    {
        return iemsUsrname;
    }

    @DisconfFileItem(name = "sms.iems.url")
    public static String getIemsUrl()
    {
        return iemsUrl;
    }

    @DisconfFileItem(name = "sms.provider")
    public static String getProvider()
    {
        return provider;
    }

    public static void setProvider(String provider)
    {
        SmsConfig.provider = provider;
    }

    @DisconfFileItem(name = "sms.ykt.url")
    public static String getYktUrl()
    {
        return yktUrl;
    }

    @DisconfFileItem(name = "sms.ykt.usrname")
    public static String getYktUsrname()
    {
        return yktUsrname;
    }

    @DisconfFileItem(name = "sms.ykt.mdrSalt")
    public static String getYktMdrSalt()
    {
        return yktMdrSalt;
    }

    @DisconfFileItem(name = "sms.ykt.sign")
    public static String getYktSign()
    {
        return yktSign;
    }

    @DisconfFileItem(name = "expire")
    public static int getExpire()
    {
        return expire;
    }

    @Override
    public String toString()
    {
        return "SmsConfig [yktUrl=" + yktUrl + ", yktUsrname=" + yktUsrname + ", yktMdrSalt=" + yktMdrSalt
                + ", yktSign=" + yktSign + "]";
    }

}
