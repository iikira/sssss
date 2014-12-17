package com.szzjcs.commons.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 其实是org.apache.tomcat.util.threads里的
 * 
 * @author smartlv
 */
public class TaskThreadFactory implements ThreadFactory
{
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public TaskThreadFactory(String namePrefix)
    {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r)
    {
        TaskThread t = new TaskThread(group, r, namePrefix + threadNumber.getAndIncrement());
        if (t.isDaemon())
        {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY)
        {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
