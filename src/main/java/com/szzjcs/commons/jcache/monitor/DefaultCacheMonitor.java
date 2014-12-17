package com.szzjcs.commons.jcache.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szzjcs.commons.jcache.Cache;
import com.szzjcs.commons.jcache.CacheFactory;

public class DefaultCacheMonitor implements CacheMonitor
{

    private MonitorVisitor monitorVisitor = new MonitorVisitor();

    private static final Logger logger = LoggerFactory.getLogger("jcacheMonitor");

    private Thread monitorThread;

    private static final int monitorInterval = 5 * 60 * 1000;// 5min

    /*
     * (non-Javadoc)
     * @see com.maowu.jcache.monitor.CacheMonitor#log()
     */
    public void log()
    {
        String[] names = CacheFactory.getAllCacheName();
        for (String name : names)
        {
            Cache<?, ?> c = CacheFactory.getCache(name);
            if (c instanceof Visitable)
            {
                ((Visitable) c).accept(monitorVisitor, name);
            }
            else if (c instanceof Cache<?, ?>)
            {
                monitorVisitor.visitCustomCache(c, name);
            }
        }
    }

    public void start()
    {
        monitorThread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(monitorInterval);
                        log();
                    }
                    catch (Exception e)
                    {
                        logger.error("DefaultCacheMonitor run error: ", e);
                    }
                }
            }
        };
        try
        {
            monitorThread.start();
        }
        catch (Exception e)
        {
            logger.error("DefaultCacheMonitor monitor thread start error: ", e);
        }
    }

}
