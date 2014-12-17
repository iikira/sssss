package com.szzjcs.commons.redis;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.JedisPool;

import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfUpdate;
import com.szzjcs.commons.logutil.LogProxy;

/**
 * redis操作客户端工具类
 * 
 * @author: hualong
 * @date 2014年2月17日 上午9:58:35
 */
public class JedisPoolFactory
{
    private static Logger log = LogProxy.getLogger(JedisPoolFactory.class);

    private static String configFile = "jedisconf";

    private static JedisPool jedisPool = null;

    private static Map<String, JedisPool> JEDISPOOL_MAP = new HashMap<>();

    static
    {
        loadXmlConfig();
    }

    /**
     * @author: smartlv
     * @date: 2014年2月17日下午3:36:30
     */
    private static void loadXmlConfig()
    {
        DefaultListableBeanFactory context = new DefaultListableBeanFactory();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions("classpath:jedisconf.xml");
        initJedisPool(context);
    }

    private static void initJedisPool(DefaultListableBeanFactory context)
    {
        JEDISPOOL_MAP = (Map) context.getBean("jedisPoolMap");

        if (CollectionUtils.isEmpty(JEDISPOOL_MAP))
        {
            log.warn("no any jedis instance");
        }
    }

    public static JedisPool getJedisPool(String name)
    {
        return JEDISPOOL_MAP.get(name);
    }

    @Service
    @DisconfUpdateService(classes = { JedisConfig.class })
    public static class JedisConfigCallback implements IDisconfUpdate
    {
        public void reload() throws Exception
        {
            // 要从disconf得到回调通知,不然更新了业务也不知道刷新,等下个disconf版本
            loadXmlConfig();
        }
    }
}
