package com.szzjcs.commons.thirdapi.push;

import java.util.Map;

import com.szzjcs.commons.thirdapi.push.provider.JPushProvider;

public class PushUtil
{
    private static Provider provider = new JPushProvider();

    public static void sendNotification(Platform platform, Audience audience, String alert, int ttl,
            Map<String, String> extras)
    {
        provider.sendNotification(platform, audience, alert, ttl, extras);
    }

    public static void sendMessage(Platform platform, Audience audience, String title, String content, int ttl,
            Map<String, String> extras)
    {
        provider.sendMessage(platform, audience, title, content, 0, extras);
    }
}
