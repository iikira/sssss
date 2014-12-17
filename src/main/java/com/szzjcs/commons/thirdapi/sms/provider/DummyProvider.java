package com.szzjcs.commons.thirdapi.sms.provider;

import com.szzjcs.commons.redis.JedisProxy;
import com.szzjcs.commons.thirdapi.sms.Provider;
import com.szzjcs.commons.thirdapi.sms.SmsException;

public class DummyProvider implements Provider{

    @Override
    public void send(String mobile, String content) throws SmsException {
        JedisProxy jedisProxy = JedisProxy.getPersistCache();
        jedisProxy.rpush("sms_dummy_provider", mobile + ":" + content);
    }
}
