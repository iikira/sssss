package com.szzjcs.commons.redis.test;


import org.junit.Test;

public class CacheLoaderTest {

    @Test
    public void loadCache(){
        CacheLoader loader = new CacheLoader("/cache");
        loader.setup();

    }
}
