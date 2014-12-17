package com.szzjcs.commons.jcache;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.szzjcs.commons.jcache.adv.AdvCache;
import com.szzjcs.commons.jcache.adv.ItemHandler;
import com.szzjcs.commons.jcache.adv.Node;
import com.szzjcs.commons.jcache.monitor.CacheMonitor;
import com.szzjcs.commons.jcache.monitor.DefaultCacheMonitor;
import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.string.StringUtil;
import com.szzjcs.commons.util.CongfigResource;

/**
 * Cache工厂 如何自定义Cache实现类： 1、新建一个类实现Cache接口，并实现Cache接口的所有方法。 2、提供一个提供带一个Properties参数的构造方法，用于初始化Cache对象
 * 3、在配置文件cacheconfig.xml中配置相应的Cache，如： <config name="example_custom_class"> <class
 * name='com.maowu.jcache.test.TestAuto'> <!-- 指定Cache接口的实现类 --> <property name='initKey' value='abc'/> <!--
 * 构造Cache时的Properties里的property --> <property name='initValue' value='efghi'/> <!-- 同上 --> </class> <check_interval
 * value="5"/> <!-- second !--> </config>
 */
@SuppressWarnings("unchecked")
public class CacheFactory
{
    private static final Logger logger = LogProxy.getLogger(CacheFactory.class);

    private static Map<String, CacheWrapper> caches = new ConcurrentHashMap<String, CacheWrapper>();

    private static CacheMonitor monitor = new DefaultCacheMonitor();

    private static Thread cleanUpThread = new Thread()
    {
        @Override
        public void run()
        {
            cleanUp();
        }
    };

    static
    {
        try
        {
            init("autoconf/cacheconfig.xml");
        }
        catch (Throwable e)
        {
            logger.error("CacheFactory init error: ", e);
        }
        try
        {
            monitor.start();
            cleanUpThread.start();
        }
        catch (Throwable e)
        {
            logger.error("CacheFactory start cleanUpThread error: ", e);
        }
    }

    private static void init(String filename) throws Exception
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        InputStream in = CongfigResource.loadConfigFile(filename, CacheFactory.class);
        Document doc = docBuilder.parse(in);
        in.close();

        Element rootElement = doc.getDocumentElement();
        NodeList servletList = rootElement.getElementsByTagName("config");

        // 先对每一个配置都设置一个CacheProxy，避免cache初始化顺序依赖的问题：
        // 当一个自定义cache里用到另外一个cache（如Cache里定义了private static Cache<int, int> c2 =
        // CacheFactory.getCache("c2");），
        // 如果配置文件中c2的配置项在c1之后，则c1先初始化时，c2还没初始化，因此以上一句getCache取到的是null，由于是static成员，只会初始化一次，即使
        // 等c2初始化完成，该值仍然是null，导致程序后续的逻辑有问题。
        for (int i = 0; i < servletList.getLength(); ++i)
        {
            try
            {
                Element node = (Element) servletList.item(i);
                String name = node.getAttribute("name");
                long checkInterval = StringUtil.convertLong(getConfigValueByName(node, "check_interval"), 60);
                int peakShiftingRange = StringUtil.convertInt(getConfigValueByName(node, "peak_shifting_range"), 1);
                checkInterval *= 1000; // 转换成毫秒
                Cache c = new CacheProxy();
                CacheWrapper old = caches.put(name, new CacheWrapper(c, checkInterval, peakShiftingRange));
                if (old != null)
                {
                    logger.error("duplicate cache name: " + name);
                }
            }
            catch (Exception e)
            {
                logger.error("init error: ", e);
            }
        }

        // 正式初始化cache
        for (int i = 0; i < servletList.getLength(); ++i)
        {
            try
            {
                Element node = (Element) servletList.item(i);
                String name = node.getAttribute("name");
                Cache c = createByElement(name, node);
                logger.info("create cache: " + name);
                CacheWrapper cw = caches.get(name);
                CacheProxy co = (CacheProxy) cw.getCache();
                co.c = c; // 设置CacheProxy的cache，以便于可以通过CacheProxy访问实际的cache
                cw.setCache(c); // 替换cache
            }
            catch (Exception e)
            {
                logger.error("create cache error: ", e);
            }
        }
    }

    private static Cache createByElement(String name, Element node)
    {
        Element e = getElementByName(node, "class");
        if (e != null)
        {
            // custom class cache
            String className = e.getAttribute("name");
            try
            {
                Class c = Class.forName(className);
                Constructor con = c.getConstructor(Properties.class);
                Properties props = new Properties();
                NodeList propList = e.getElementsByTagName("property");
                for (int i = 0; i < propList.getLength(); ++i)
                {
                    try
                    {
                        Element propNode = (Element) propList.item(i);
                        String pn = propNode.getAttribute("name");
                        String pv = propNode.getAttribute("value");
                        props.setProperty(pn, pv);
                    }
                    catch (Exception e1)
                    {
                        logger.error("", e1);
                    }
                }
                props.put("%name%", name);
                return (Cache) con.newInstance(props);
            }
            catch (Exception e2)
            {
                logger.error("", e2);
            }
            return null;
        }
        else
        {
            // adv cache
            String cleanup_strategy = getConfigValueByName(node, "cleanup_strategy");
            ItemHandler handler = null;
            if ("last_access_time".equalsIgnoreCase(cleanup_strategy))
            {
                handler = new ItemHandler()
                {
                    @Override
                    public boolean handle(Node n, boolean isTimeout)
                    {
                        return !isTimeout;
                    }
                };
            }
            long timeout = StringUtil.convertLong(getConfigValueByName(node, "timeout"), 60 * 60 * 1000);
            timeout *= 1000; // 转换成毫秒
            int maxSize = StringUtil.convertInt(getConfigValueByName(node, "max_size"), 10000);
            int cleanup_rate = StringUtil.convertInt(getConfigValueByName(node, "cleanup_rate"), 30);
            double cleanupRate = ((double) cleanup_rate) / 100;
            return new AdvCache(timeout, maxSize, cleanupRate, handler);

        }
    }

    private static String getConfigValueByName(Element node, String name)
    {
        Element e = getElementByName(node, name);
        if (e == null)
        {
            return null;
        }
        return e.getAttribute("value");
    }

    private static Element getElementByName(Element node, String name)
    {
        NodeList paraList = node.getElementsByTagName(name);
        if (paraList.getLength() > 0)
        {
            return (Element) paraList.item(0);
        }
        return null;
    }

    /**
     * 获取指定名字对应的cache
     * 
     * @param <K>
     *        cache的key类型
     * @param <T>
     *        cache的value类型
     * @param name
     *        cache的名字
     * @return Cache<K, T>
     */
    public static <K, T> Cache<K, T> getCache(String name)
    {
        CacheWrapper cw = caches.get(name);
        if (cw == null)
        {
            return null;
        }
        return cw.getCache();

    }

    /**
     * 创建一个Cache
     * 
     * @param <K>
     *        cache的key类型
     * @param <V>
     *        cache的value类型
     * @param name
     *        cache的名字
     * @param cleanupStrategy
     *        清除策略，可选值CommonCache.CLEAN_BY_CREATE_TIME, CommonCache.CLEAN_BY_LASTACCESS_TIME
     * @param timeout
     *        超时时间，单位是毫秒
     * @param maxSize
     *        最大元素个数
     * @param cleanupRate
     *        清除百分比,0-100
     * @param maxMemory
     *        最大占用总内存，-1为不限制
     * @param maxCacheObjectSize
     *        缓存的元素的最大大小，-1为不限制
     * @param checkInterval
     *        检查的时间间隔
     * @return 如果名字对应的cache已经存在，那么直接返回原来的cache，否则，创建一个cache，并返回新的cache
     */
    public static <K, V> Cache<K, V> createCache(String name, int cleanupStrategy, long timeoutMS, int maxSize,
            int cleanupRate, int maxMemory, int maxCacheObjectSize, int checkInterval)
    {
        Cache<K, V> c = getCache(name);
        if (c != null)
        {
            return c;
        }
        checkInterval *= 1000; // 转换成毫秒
        double rate = ((double) cleanupRate) / 100;
        ItemHandler handler = null;
        if (cleanupStrategy == CommonCache.CLEAN_BY_LASTACCESS_TIME)
        {
            handler = new ItemHandler()
            {
                @Override
                public boolean handle(Node n, boolean isTimeout)
                {
                    return !isTimeout;
                }
            };
        }
        c = new AdvCache(timeoutMS, maxSize, rate, handler);
        caches.put(name, new CacheWrapper(c, checkInterval));
        return c;
    }

    public static <K, V> Cache<K, V> createCache(String name, Cache<K, V> cache, int checkInterval)
    {
        caches.put(name, new CacheWrapper(cache, checkInterval));
        return cache;
    }

    private static void cleanUp()
    {
        while (true)
        {
            try
            {
                Collection<CacheWrapper> vs = caches.values();
                for (CacheWrapper o : vs)
                {
                    try
                    {
                        o.cleanUp();
                    }
                    catch (Exception e)
                    {
                        logger.error("cleanUp " + o, e);
                    }
                }
                Thread.sleep(10000);
            }
            catch (Exception e)
            {
                logger.error("cleanUp: ", e);
            }
        }
    }

    /**
     * 获取所有Cache的名字，用于统计
     * 
     * @return String[]
     */
    public static String[] getAllCacheName()
    {
        return caches.keySet().toArray(new String[0]);
    }

    public static void main(String[] args)
    {
        String[] names = getAllCacheName();
        for (int i = 0; i < names.length; ++i)
        {
            System.out.println(names[i]);
        }
        Cache<String, String> c = getCache("example");
        c.put("sbc", "dkenwe,");
        System.out.println(c.get("sbc"));
        c.put("sbc", "dkdkdkdcddsd");
        System.out.println(c.get("aaa"));
        c.remove("sbc");
        System.out.println(c.getMemoryUsage());
    }
}
