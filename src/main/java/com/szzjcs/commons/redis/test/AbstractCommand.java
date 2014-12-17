package com.szzjcs.commons.redis.test;

import com.szzjcs.commons.redis.JedisProxy;

public abstract class AbstractCommand implements Command
{
    private JedisProxy jedisProxy;

    public AbstractCommand(){
        jedisProxy = JedisProxy.getLRUCache();
    }

    public JedisProxy getJedisProxy() {
        return jedisProxy;
    }

    public void setJedisProxy(JedisProxy jedisProxy) {
        this.jedisProxy = jedisProxy;
    }
}
