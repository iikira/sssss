package com.szzjcs.commons.concurrent.poolutil;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.szzjcs.commons.concurrent.TaskQueue;
import com.szzjcs.commons.concurrent.TaskThreadFactory;
import com.szzjcs.commons.concurrent.TaskThreadPoolExecutor;
import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.logutil.LogUtil;
import com.szzjcs.commons.util.TimeCostUtil;

/**
 * 异步处理管理器。上层仍一个异步处理的对象进来，这里进行处理。适合不需要返回结果的旁路数据的处理。 提交处理的数据并不进行持久化，适合不太重要信息的处理，例如写日志到数据库等这些次要逻辑
 * 
 * @author smartlv
 */
public class AsyncProcessManager
{
    private static final Logger log = LogProxy.getLogger(AsyncProcessManager.class);

    private static AsyncProcessManager process = new AsyncProcessManager();

    private final TaskThreadPoolExecutor exec;

    private AsyncProcessManager()
    {
        TaskQueue taskqueue = new TaskQueue(ThreadPoolConf.getQueueLen());

        exec = new TaskThreadPoolExecutor(ThreadPoolConf.getCorePoolSize(), ThreadPoolConf.getMaximumPoolSize(),
                ThreadPoolConf.getKeepAliveTime(), TimeUnit.SECONDS, taskqueue, new TaskThreadFactory("async-exec-"));
        taskqueue.setParent(exec);

        exec.setRejectedExecutionHandler(new RejectedExecutionHandler()
        {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
            {
                log.error("AsyncProcessManager rejectedExecution r:" + r + ",task count:" + executor.getTaskCount());
            }
        });
    }

    public static AsyncProcessManager getIntance()
    {
        return process;
    }

    public static void addTask(final Runnable runnable)
    {
        getIntance().exec.submit(new Runnable()// 再包一层是为了做些日志咯
                {
                    @Override
                    public void run()
                    {
                        TimeCostUtil timeCostUtil = new TimeCostUtil();
                        try
                        {
                            runnable.run();
                        }
                        catch (Exception ex)
                        {
                            log.error(ex.getMessage(), ex);
                        }
                        LogUtil.bizDebug(log, "[async process] %s runnable:%s", timeCostUtil, runnable);
                    }
                });
    }

    public static String getQueueInfo()
    {
        return "completed task:" + getIntance().exec.getCompletedTaskCount() + "queue size:"
                + getIntance().exec.getQueue().size() + ",task count:" + getIntance().exec.getTaskCount();
    }

}
