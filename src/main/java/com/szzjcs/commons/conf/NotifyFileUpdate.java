package com.szzjcs.commons.conf;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifyFileUpdate
{
    private static final Logger logger = LoggerFactory.getLogger("jcache");

    private static final int WARN_LEVEL = 10 * 1000;

    /**
     * 使用并发的LIST防止ConcurrentModificationException
     */
    private static List<FileUpdate> fileUpdaterList = new CopyOnWriteArrayList<FileUpdate>();

    /**
     * 把线程池配置成不能接受任务则抛出异常，理论上把LinkedBlockingQueue装满是不可能的。
     */
    private static Executor executor = new ThreadPoolExecutor(10, 50, 120, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

    private class NotifyTask implements Runnable
    {

        private String confFileName = "";

        private FileUpdate fileUpdate = null;

        private long startTime = 0l;

        public NotifyTask(String confFileName, FileUpdate fileUpdate)
        {
            this.confFileName = confFileName;
            this.fileUpdate = fileUpdate;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void run()
        {
            NotifyFileUpdate.logger.debug("notify " + confFileName + " update to " + fileUpdate.getClass().getName());

            fileUpdate.updateFile(confFileName);
            long costTime = System.currentTimeMillis() - startTime;
            NotifyFileUpdate.logger.debug("notify " + confFileName + " update to " + fileUpdate.getClass().getName()
                    + " cost " + costTime);
            if (costTime > NotifyFileUpdate.WARN_LEVEL)
            {
                // 超过这个等级的回调，打LOG
                NotifyFileUpdate.logger.warn(fileUpdate.getClass().getName() + " update " + confFileName
                        + " too slow, cost " + costTime);
            }
        }
    }

    /*
     * 注册新的接口
     */
    public static void registInterface(FileUpdate fileUpdate)
    {
        NotifyFileUpdate.fileUpdaterList.add(fileUpdate);
    }

    /*
     * 删除已有接口
     */
    public static void rmInterface(FileUpdate fileUpdate)
    {
        NotifyFileUpdate.fileUpdaterList.remove(fileUpdate);
    }

    /*
     * 获取整个接口列表
     */
    public static List<FileUpdate> getInterfaceList()
    {
        return NotifyFileUpdate.fileUpdaterList;
    }

    /*
     * 有文件修改时调用，通知所有接口更新文件
     */
    public void notifyAllInterface(String confFileName)
    {
        Iterator<FileUpdate> iterator = NotifyFileUpdate.fileUpdaterList.iterator();
        while (iterator.hasNext())
        {
            // 逐个通知
            FileUpdate fileUpdate = iterator.next();
            try
            {
                NotifyFileUpdate.executor.execute(new NotifyTask(confFileName, fileUpdate));
            }
            catch (Throwable e)
            {
                NotifyFileUpdate.logger.error("notifyAllInterface Exception", e);
            }
        }
    }

}
