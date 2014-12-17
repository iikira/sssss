package com.szzjcs.commons.redis;

import com.szzjcs.commons.json.JsonConverter;
import org.junit.Test;

public class JedisProxyTest
{
    @Test
    public void testJedisFactory()
    {
        A a = null;
        a = JedisProxy.getLRUCache().get("aaaa", A.class);
        System.out.println(a);

    }

    public static class A{
        private int a =1;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }
}
