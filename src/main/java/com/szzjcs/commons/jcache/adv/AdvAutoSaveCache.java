package com.szzjcs.commons.jcache.adv;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szzjcs.commons.concurrent.thread.FixThreadExecutor;
import com.szzjcs.commons.jcache.monitor.Visitable;
import com.szzjcs.commons.jcache.monitor.Visitor;
import com.szzjcs.commons.string.StringUtil;

public abstract class AdvAutoSaveCache<K, V> extends AdvCache<K, V> implements Visitable
{
    private static final Logger logger = LoggerFactory.getLogger("jcache");

    private final FixThreadExecutor tp;

    private volatile long reflushTime = 0;
    private final AtomicLong reflushTime_tmp = new AtomicLong(0);
    private final AtomicInteger loopCount = new AtomicInteger(0);

    // 清楚元素时候做一些处理,前置操作
    private static class ItemSaver<K1, V1> implements ItemHandler<K1, V1>
    {
        final AdvAutoSaveCache<K1, V1> c;

        public ItemSaver(AdvAutoSaveCache<K1, V1> c)
        {
            this.c = c;
        }

        @Override
        public boolean handle(final Node<K1, V1> n, boolean isTimeout)
        {
            c.tp.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    if (c.loopCount.get() >= 10)
                    {
                        c.reflushTime = c.reflushTime_tmp.get();
                        c.reflushTime_tmp.set(0);
                        c.loopCount.set(0);
                    }

                    long now = System.currentTimeMillis();
                    c.saveAndRemove(n.getKey(), n.getValue());
                    c.reflushTime_tmp.addAndGet(System.currentTimeMillis() - now);
                    c.loopCount.addAndGet(1);
                }
            });
            return false;
        }
    }

    public AdvAutoSaveCache(int cleanupStrategy, long timeoutMS, int maxSize, double cleanupRate, int maxMemory,
            int maxCacheObjectSize, int threadCount)
    {
        // init(cleanupStrategy, timeoutMS, maxSize, cleanupRate, maxMemory,
        // maxCacheObjectSize, threadCount);
        // public AdvCache(long timeout, int maxSize, double cleanupRate,
        // ItemHandler<K, V> handler, final String saveFilePath, final long
        // saveInterval, final boolean autoRecover)
        super(timeoutMS, maxSize, cleanupRate, null, null, 3600, false);
        tp = new FixThreadExecutor(threadCount, 1000000);
        setItemHandler(new ItemSaver<K, V>(this));
    }

    /*
     * private void init(int cleanupStrategy, long timeoutMS, int maxSize, double cleanupRate, int maxMemory, int
     * maxCacheObjectSize, int threadCount) { init(cleanupStrategy, timeoutMS, maxSize, cleanupRate, maxMemory,
     * maxCacheObjectSize); tp = new FixThreadExecutor(threadCount, 1000000); //setCleanUpLoopCount(1); }
     */
    public AdvAutoSaveCache(Properties prop)
    {
        super(prop);
        int threadCount = StringUtil.convertInt(prop.getProperty("thread_count"), 1);
        tp = new FixThreadExecutor(threadCount, 1000000);
        /*
         * ItemHandler<K, V> h = getItemHandler(); if(h == null) setItemHandler(new ItemSaver<K, V>(this));
         */
        setItemHandler(new ItemSaver<K, V>(this));
        /*
         * String cleanup_strategy = prop.getProperty("cleanup_strategy"); long timeout =
         * StringUtil.convertLong(prop.getProperty("timeout"), 60 * 60 * 1000); timeout *= 1000; // 转换成毫秒 int maxSize =
         * StringUtil.convertInt(prop.getProperty("max_size"), 10000); int maxMemory =
         * StringUtil.convertInt(prop.getProperty("max_memory"), -1); maxMemory *= 1024 * 1024; int cleanup_rate =
         * StringUtil.convertInt(prop.getProperty("cleanup_rate"), 30); double cleanupRate = ((double) cleanup_rate) /
         * 100; int cleanupStrategy = CommonCache.CLEAN_BY_LASTACCESS_TIME; if
         * ("create_time".equalsIgnoreCase(cleanup_strategy)) { cleanupStrategy = CommonCache.CLEAN_BY_CREATE_TIME; }
         * int maxCacheObjectSize = StringUtil.convertInt(prop.getProperty("max_cache_object_size"), -1); int
         * threadCount = StringUtil.convertInt(prop.getProperty("thread_count"), 1); init(cleanupStrategy, timeout,
         * maxSize, cleanupRate, maxMemory, maxCacheObjectSize, threadCount);
         */
    }

    /*
     * public int cleanUp() { long maxLifeTime = this.timeout; int removeCount = 0; try { int remainSize = maxSize; if
     * (map.size() > maxSize) remainSize = (int) ((1 - cleanupRate) * maxSize); //保留的map的个�? Set<K> inProcess = new
     * HashSet<K>(); for (int i = 0; i < cleanUpLoopCount; i++) //�?多尝�?10�?,使得将缓存数目减至合适的 { long now =
     * System.currentTimeMillis(); Set<Map.Entry<K, CacheValue<V>>> entrySet = map.entrySet(); Iterator<Map.Entry<K,
     * CacheValue<V>>> it = entrySet.iterator(); while (it.hasNext()) { final Map.Entry<K, CacheValue<V>> entry =
     * it.next(); final K key = entry.getKey(); final CacheValue<V> v = entry.getValue(); if (now - v.time >
     * maxLifeTime) //如果生命期超过了限制时间 { try{ if(inProcess.add(key)){ tp.execute(new Runnable(){ public void run() {
     * saveAndRemove(key, v.value); } }); } }catch(Exception e){ logger.error("", e); } if(i > 0 && map.size() -
     * inProcess.size() <= remainSize) // 第二遍时只清到remainSize { break; } } } if (map.size() - inProcess.size() <=
     * remainSize && currentMemoryUsage.get() < maxMemory) //如果map的size已经在保留�?�以�?,直接中断循环 { break; } maxLifeTime =
     * maxLifeTime / 2; //超时时间减为原先的一�?. } } catch(Exception e) { logger.error("cleanUp error: ", e); } return
     * removeCount; }
     */
    protected abstract void saveAndRemove(K key, V value);

    @Override
    public void clear()
    {
        this.hitCount.set(0);
        this.queryCount.set(0);

        try
        {
            Iterator<Entry<K, V>> it = iterator();
            while (it.hasNext())
            {

                final Entry<K, V> entry = it.next();
                final V v = entry.getValue();
                try
                {
                    tp.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            saveAndRemove(entry.getKey(), v);
                        }
                    });
                }
                catch (Exception e)
                {
                    AdvAutoSaveCache.logger.error("", e);
                }
            }
        }
        catch (Exception e)
        {
            AdvAutoSaveCache.logger.error("clear error: ", e);
        }
    }

    @Override
    public long getReflushTime()
    {
        return this.reflushTime;
    }

    @Override
    public void accept(Visitor v, String name)
    {
        // TODO Auto-generated method stub
        v.visitAdvAutoSaveCache(this, name);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
    }

}
