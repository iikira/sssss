package com.szzjcs.commons.concurrent.poolutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 轻量级任务执行框架fork/join详解,使用该框架计算文件目录大小 把任务划分成很多小任务，互不依赖，各自并发跑起来,然后再合并汇总
 * 
 * @author smartlv
 */
public class ForkJoinDemo
{
    private static class CalculateTask extends RecursiveTask<Long>
    {
        private static final long serialVersionUID = -33877484347317696L;
        final File file;

        public CalculateTask(final File theFile)
        {
            file = theFile;
        }

        @Override
        public Long compute()
        {
            long size = 0;
            if (file.isFile())
            {
                size = file.length();
            }
            else
            {
                final File[] children = file.listFiles();
                if (children != null)
                {
                    List<ForkJoinTask<Long>> taskList = new ArrayList<ForkJoinTask<Long>>();
                    for (final File child : children)
                    {
                        if (child.isFile())
                        {
                            size += child.length();
                        }
                        else
                        {
                            taskList.add(new CalculateTask(child));
                        }
                    }

                    // invokeAll方法循环调用task.fork()执行子任务.
                    for (final ForkJoinTask<Long> task : ForkJoinTask.invokeAll(taskList))
                    {
                        // task.join()方法等待任务完成并返回执行的结果
                        size += task.join();
                    }
                }
            }
            return size;
        }
    }

    public static void main(String[] args)
    {
        long start = System.nanoTime();

        long total = new ForkJoinPool().invoke(new CalculateTask(new File(System.getProperty("user.dir"))));
        long end = System.nanoTime();
        System.out.println("Total Size: " + total + "B");
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }
}
