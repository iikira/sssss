package com.szzjcs.commons.conf;

import java.io.IOException;
import java.nio.file.*;

import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;

/**
 * 这个可以作为对配置文件更新后刷新
 * 
 * @author smartlv
 */
public class ConfigFileWatcher
{
    private static final Logger logger = LogProxy.getLogger(ConfigFileWatcher.class);

    public static class FileWatcherWorker implements Runnable
    {
        private final WatchService watcher;

        public FileWatcherWorker(Path path) throws IOException
        {
            watcher = FileSystems.getDefault().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        }

        @Override
        public void run()
        {
            while (true)
            {
                WatchKey key = null;
                try
                {
                    key = watcher.take();
                }
                catch (Throwable e)
                {

                }
                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                    {// 事件可能lost or discarded
                        continue;
                    }
                    WatchEvent<Path> e = (WatchEvent<Path>) event;
                    Path fileName = e.context();

                    // 通知这些变化的
                    String configFile = String.valueOf(fileName);
                    NotifyFileUpdate fileUpdate = new NotifyFileUpdate();
                    fileUpdate.notifyAllInterface(configFile);
                    // 通知这个配置工具也刷刷
                    ProfileManager.reInit(configFile);

                    System.out.printf("Event %s has happened,which fileName is %s%n", kind.name(), fileName);
                    break;
                }

                if (!key.reset())
                {
                    break;
                }
            }
        }
    }

    /**
     * 容器启动时候告诉哥,文件变化了哥通知你
     */
    public static void start()
    {
        ProfileRoot.reInit();// 只是初始目录

        try
        {
            Thread t = new Thread(new FileWatcherWorker(Paths.get(ProfileRoot.AUTOCONF_PATH)));
            t.setDaemon(true);
            t.start();
        }
        catch (Throwable e)
        {
            logger.error("ConfigFileWatcher start excp", e);
        }
    }
}
