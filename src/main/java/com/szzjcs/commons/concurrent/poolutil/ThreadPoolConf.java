package com.szzjcs.commons.concurrent.poolutil;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * @author smartlv
 */
@DisconfFile(filename = "threadpoolconf.properties")
public class ThreadPoolConf
{
    private static int queueLen = 20000;
    private static int corePoolSize = 20;
    private static int maximumPoolSize = 120;
    private static int keepAliveTime = 120;
    private static int concurrCalQueueLen = 20000;
    private static int concurrCalCorePoolSize = 20;
    private static int concurrCalMaximumPoolSize = 120;
    private static int concurrCalKeepAliveTime = 120;
    private static int concurrCalTaskTimeout = 120;

    @DisconfFileItem(name = "concurrCal_taskQueueLength")
    public static int getConcurrCalQueueLen()
    {
        return concurrCalQueueLen;
    }

    @DisconfFileItem(name = "concurrCal_corePoolSize")
    public static int getConcurrCalCorePoolSize()
    {
        return concurrCalCorePoolSize;
    }

    @DisconfFileItem(name = "concurrCal_maxPoolSize")
    public static int getConcurrCalMaximumPoolSize()
    {
        return concurrCalMaximumPoolSize;
    }

    @DisconfFileItem(name = "concurrCal_keepAliveTime")
    public static int getConcurrCalKeepAliveTime()
    {
        return concurrCalKeepAliveTime;
    }

    @DisconfFileItem(name = "concurrCal_taskTimeout")
    public static int getConcurrCalTaskTimeout()
    {
        return concurrCalTaskTimeout;
    }

    @DisconfFileItem(name = "async_process_queue_length")
    public static int getQueueLen()
    {
        return queueLen;
    }

    @DisconfFileItem(name = "async_process_core_pool_size")
    public static int getCorePoolSize()
    {
        return corePoolSize;
    }

    @DisconfFileItem(name = "async_process_max_pool_size")
    public static int getMaximumPoolSize()
    {
        return maximumPoolSize;
    }

    @DisconfFileItem(name = "async_process_max_pool_size")
    public static int getKeepAliveTime()
    {
        return keepAliveTime;
    }
}
