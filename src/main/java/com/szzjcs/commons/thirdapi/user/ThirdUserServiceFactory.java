package com.szzjcs.commons.thirdapi.user;

import com.szzjcs.commons.thirdapi.user.impl.QQUserService;
import com.szzjcs.commons.thirdapi.user.impl.WeiboUserService;

/**
 * 第三方服务工厂类
 */
public class ThirdUserServiceFactory
{
    private static QQUserService qqUserService = new QQUserService();
    private static WeiboUserService weiboUserService = new WeiboUserService();

    public static ThirdUserService getService(PlatformEnum platform)
    {
        switch (platform)
        {
        case QQ:
            return qqUserService;
        case WEIBO:
            return weiboUserService;
        default:
            throw new IllegalArgumentException("current platform not supported: " + platform);
        }
    }
}
