package com.szzjcs.commons.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szzjcs.commons.conf.ProfileManager;
import com.szzjcs.commons.crypto.MD5Coding;
import com.szzjcs.commons.string.StringUtil;

public class FileUtil
{
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static ReentrantReadWriteLock[] locks = new ReentrantReadWriteLock[10];

    static
    {
        try
        {
            FileUtil.initLocks();
        }
        catch (Throwable t)
        {
            FileUtil.logger.error("init locks failed.", t);
        }
    }

    public static ReadLock getReadLock(String pathname)
    {
        int index = FileUtil.name2hash(pathname);
        return FileUtil.locks[index].readLock();
    }

    public static WriteLock getWriteLock(String pathname)
    {
        int index = FileUtil.name2hash(pathname);
        return FileUtil.locks[index].writeLock();
    }

    private static void initLocks()
    {
        for (int i = 0; i < FileUtil.locks.length; i++)
        {
            FileUtil.locks[i] = new ReentrantReadWriteLock();
        }
    }

    public static String md5sum(String pathname)
    {
        String md5sum = "";
        File file = new File(pathname);
        if (!file.exists())
        {
            return "FileNotFound";
        }

        if (file.isDirectory())
        {
            throw new IllegalArgumentException("It is a Directory:" + pathname);
        }

        FileInputStream fis = null;
        FileChannel channel = null;
        FileLock lock = null;
        ReadLock readLock = null;
        try
        {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            lock = channel.lock(0, Long.MAX_VALUE, true);
            readLock = FileUtil.getReadLock(pathname);
            readLock.lock();
            int size = (int) (file.length());
            byte[] bs = new byte[size];
            fis.read(bs);
            md5sum = MD5Coding.encode2HexStr(bs);
            return md5sum;
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed read file content:" + pathname);
        }
        finally
        {
            if (readLock != null)
            {
                readLock.unlock();
            }
            try
            {
                if (lock != null)
                {
                    lock.release();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file lock failed, file=" + pathname, e);
            }
            try
            {
                if (channel != null)
                {
                    channel.close();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file channel failed, file=" + pathname, e);
            }
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file inputstream failed, file=" + pathname, e);
            }
        }
    }

    /**
     * 返回本地所有配置文件MD5SUM值
     * 
     * @return
     */
    public static Map<String, String> md5sums()
    {
        Map<String, String> map = new HashMap<String, String>(512);
        File root = new File(ProfileManager.getpath());
        for (File file : root.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isFile();
            }
        }))
        {
            String name = file.getName();
            String md5sum = FileUtil.md5sum(file.getPath());
            map.put(name, md5sum);
        }
        return map;
    }

    private static int name2hash(String pathname)
    {
        int hash;
        try
        {
            hash = new File(pathname).getCanonicalPath().hashCode();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return Math.abs(hash % FileUtil.locks.length);
    }

    /**
     * @param name
     *        配置文件名称（不含路径）
     * @return
     */
    public static String read(String name)
    {
        String pathname = ProfileManager.getpath() + File.separatorChar + name;
        File file = new File(pathname);
        if (!file.exists())
        {
            return null;
        }
        if (file.isDirectory())
        {
            return null;
        }

        ReadLock readLock = FileUtil.getReadLock(pathname);
        FileLock lock = null;
        FileInputStream fis = null;
        try
        {
            // lock
            readLock.lock();
            fis = new FileInputStream(file);
            fis.getChannel();

            // read
            int length = (int) file.length();
            byte[] buf = new byte[length];
            fis.read(buf);
            return new String(buf);
        }
        catch (IOException e)
        {
            FileUtil.logger.warn("failed to read " + file, e);
            return null;
        }
        finally
        {
            try
            {
                if (lock != null)
                {
                    lock.release();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file lock failed, file=" + pathname, e);
            }

            if (readLock != null)
            {
                readLock.unlock();
            }

            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file inputstream failed, file=" + pathname, e);
            }
        }
    }

    public static void write(String name, String content)
    {
        String pathname = ProfileManager.getpath() + File.separatorChar + name;
        File file = new File(pathname);
        try
        {
            if (!file.getParentFile().exists())
            {
                FileUtil.logger.info("mkdirs for [" + file.getParentFile() + "], status="
                        + file.getParentFile().createNewFile());
            }
            if (!file.exists())
            {
                FileUtil.logger.info("create new file [" + file + "], status=" + file.createNewFile());
            }
        }
        catch (IOException e)
        {
            FileUtil.logger.error("failed to write file[" + pathname + "]", e);
        }

        if (file.isDirectory())
        {
            throw new IllegalArgumentException("It is a Directory:" + pathname);
        }

        RandomAccessFile raf = null;
        FileChannel channel = null;
        FileLock lock = null;
        WriteLock writeLock = null;

        try
        {
            // 1. lock
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            lock = channel.lock();
            writeLock = FileUtil.getWriteLock(pathname);
            writeLock.lock();

            // 2. write file
            byte[] buf = content.getBytes("UTF-8");
            raf.setLength(buf.length);
            raf.seek(0L);
            raf.write(buf);
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed write file:" + pathname, e);
        }
        finally
        {
            if (writeLock != null)
            {
                writeLock.unlock();
            }
            try
            {
                if (lock != null)
                {
                    lock.release();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file lock failed, file=" + pathname, e);
            }
            try
            {
                if (raf != null)
                {
                    raf.close();
                }
            }
            catch (IOException e)
            {
                FileUtil.logger.warn("close file random access file failed, file=" + pathname, e);
            }
        }
    }

    /**
     * 支持变量文件名，例如${logRoot}/abc
     * 
     * @param path
     * @return
     */
    public static String getFilePath(String path)
    {
        if (path == null)
        {
            return "";
        }

        String r = "\\$\\{[^${}]*\\}";
        Pattern p = Pattern.compile(r);
        Matcher m = p.matcher(path);
        while (m.find())
        {
            int startIndex = m.start(); // index of start
            int endIndex = m.end(); // index of end + 1
            String currentMatch = path.substring(startIndex, endIndex);
            String property = System.getProperty(path.substring(startIndex + 2, endIndex - 1).trim());
            System.out.println(currentMatch + "=" + property);
            if (property == null)
            {
                System.err.println("System Property [" + path.substring(startIndex + 2, endIndex - 1).trim()
                        + "] not found.");
                return "";
            }
            path = StringUtil.replaceAll(path, currentMatch, property);
            m = p.matcher(path);
        }

        return path;
    }

    public static void main(String[] args) throws Throwable
    {
        System.out.println(new File("F:/test/xxx/abc.txt").mkdirs());
        System.exit(0);
        File f = new File("F:/lock.txt");
        new FileOutputStream(f).getChannel().lock();
        System.out.println(f.renameTo(new File("F:/lock.new.txt")));
    }

}
