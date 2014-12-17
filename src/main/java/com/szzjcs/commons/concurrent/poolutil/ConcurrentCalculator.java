package com.szzjcs.commons.concurrent.poolutil;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

import com.szzjcs.commons.concurrent.TaskQueue;
import com.szzjcs.commons.concurrent.TaskThreadFactory;
import com.szzjcs.commons.concurrent.TaskThreadPoolExecutor;
import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.util.Pair;

/**
 * 可以并发提交任务，每次提交一个任务后会返回一个独一无二的编号，可以通过这个编号来索引到任务
 * 
 * @author smartlv
 * @date:20121029
 */
public class ConcurrentCalculator<T>
{
    private static final Logger BUSI_LOG = LogProxy.getLogger(ConcurrentCalculator.class);

    private final TaskThreadPoolExecutor exec;
    private final Map<Long, Pair<Long, FutureTask<T>>> tasks = new ConcurrentHashMap<>();// 放置时间戳和任务。时间戳用于超时扫描检查这个任务是不是没有被去掉，以免map膨胀
    private final AtomicLong currentSeqNo = new AtomicLong(0);
    private int resultTimeout = 4000;// 超时时间4秒
    private boolean isClose = false;// 是否被关闭了

    private static final int EXPIRE_INTERVAL = 20 * 1000;// 一个任务超时时间，这里设置20秒，超过这个时间就有可能被从map中清理掉
    private final Thread removeThread;// 一个对象要多一个线程用于定时删除过时记录

    private static ConcurrentCalculator<Object> concurrentCalculatorV2 = new ConcurrentCalculator<Object>();

    public static ConcurrentCalculator<Object> getIntance()
    {
        return concurrentCalculatorV2;
    }

    /**
     * 初始化函数
     */
    public ConcurrentCalculator()
    {
        TaskQueue taskqueue = new TaskQueue(ThreadPoolConf.getConcurrCalQueueLen());
        exec = new TaskThreadPoolExecutor(ThreadPoolConf.getConcurrCalCorePoolSize(),
                ThreadPoolConf.getConcurrCalMaximumPoolSize(), ThreadPoolConf.getConcurrCalKeepAliveTime(),
                TimeUnit.SECONDS, taskqueue, new TaskThreadFactory("cal-exec-"));
        taskqueue.setParent(exec);
        resultTimeout = ThreadPoolConf.getConcurrCalTaskTimeout();

        removeThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!isClose)
                {
                    try
                    {
                        Thread.sleep(1000 * 60);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    removeTimeoutTasks();
                }
            }
        });
        removeThread.start();
    }

    /**
     * 真正的并行工作方法 把一个一个任务分配到每一个线程中 设置后立即提交任务并处理 任务以key，value方式来索引最终的处理结果
     */
    public long submitTask(Callable<T> callable)
    {
        long seqno = currentSeqNo.incrementAndGet();
        // 默认根据CPU核心个数拆分任务，创建FutureTask并提交到Executor
        FutureTask<T> task = new FutureTask<T>(callable);
        // 任务放入到hashtable，把每一个callalbe放入到线程池中执行
        tasks.put(seqno, Pair.makePair(System.currentTimeMillis(), task));
        exec.submit(task);
        return seqno;
    }

    /**
     * 返回任务合集 key是你提交时的key 任务未完成时会阻塞在此
     * 
     * @return
     */
    public T getResult(long seqNo)
    {
        T result = null;
        Pair<Long, FutureTask<T>> task = tasks.get(seqNo);
        if (task != null)
        {
            try
            {
                tasks.remove(seqNo);// 不管是否成功，查了一次就要删除
                result = task.second.get(resultTimeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException | TimeoutException | ExecutionException e)
            {
                BUSI_LOG.error("", e);
            }
        }

        if (BUSI_LOG.isDebugEnabled())
        {
            BUSI_LOG.debug("concurr getresult:" + seqNo + "\t" + result);
        }
        return result;
    }

    /**
     * 删除超时的记录
     */
    public void removeTimeoutTasks()
    {
        BUSI_LOG.info("task node remove,start....");
        Iterator<Map.Entry<Long, Pair<Long, FutureTask<T>>>> iterator = tasks.entrySet().iterator();
        Long timeoutTime = System.currentTimeMillis() - ConcurrentCalculator.EXPIRE_INTERVAL;
        while (iterator.hasNext())
        {
            Map.Entry<Long, Pair<Long, FutureTask<T>>> node = iterator.next();
            if (node.getValue().first < timeoutTime)
            {
                BUSI_LOG.info("task node remove,id:" + node.getKey());
                iterator.remove();
            }
        }
        BUSI_LOG.info("task node remove,finish....");
    }

    private void close()
    {
        tasks.clear();
        exec.shutdown();
        isClose = true;
    }

    /**
     * main 测试方法
     * 
     * @param args
     */
    public static void main(String[] args) throws InterruptedException
    {
        ConcurrentCalculator<Object> concurrentCalculatorV2 = ConcurrentCalculator.getIntance();
        for (int i = 0; i < 100; i++)
        {
            final int ii = i;
            concurrentCalculatorV2.submitTask(new Callable<Object>()
            {
                @Override
                public Object call() throws Exception
                {
                    for (int j = 0; j < 100; j++)
                    {
                        System.out.print("hello" + ii + "," + j + "   ");
                    }
                    System.out.println("");
                    return ii;
                }
            });

        }
        Thread.sleep(20000);
        concurrentCalculatorV2.close();
    }

}
