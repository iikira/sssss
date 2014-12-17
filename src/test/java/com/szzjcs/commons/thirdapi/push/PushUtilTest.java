package com.szzjcs.commons.thirdapi.push;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PushUtilTest
{
    @Test
    public void sendAllNotification()
    {
        Audience.Builder builder = new Audience.Builder();
        Map<String, String> extras = new HashMap<>();
        extras.put("Model", "traffic");
        extras.put("CODE", "1");
        PushUtil.sendMessage(Platform.ALL, builder.addOrTag("actived_device").build(), "",
                "{\"playtime\":\"2014-07-17 15:00:00\", \"nextTime\":\" 2014-07-17 15:15:00\"}", 0, extras);
    }

    @Test
    public void sendAlias()
    {
        Audience.Builder audience = new Audience.Builder();
        audience.addAlias("5465548513900457984");
        Map<String, String> extras = new HashMap<>();
        extras.put("Model", "ticket");
        extras.put("CODE", "1");
        extras.put("activityId", "8778");
        PushUtil.sendNotification(Platform.ALL, audience.build(), "testtest!", 0, extras);
    }
}
