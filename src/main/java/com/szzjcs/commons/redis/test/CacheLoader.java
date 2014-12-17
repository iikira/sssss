package com.szzjcs.commons.redis.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.common.base.Strings;
import com.szzjcs.commons.redis.JedisProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheLoader
{
    private final Logger log = LoggerFactory.getLogger(CacheLoader.class);

    private String fileName;
    public CacheLoader(String fileName)
    {
        this.fileName = fileName;
    }

    public void setup(){
        try
        {
            clearAll();
            load(fileName);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void clearAll()
    {
        log.info("clear all cache............");
        JedisProxy pJedis = JedisProxy.getPersistCache();
        pJedis.flushAll();
        JedisProxy lJedis = JedisProxy.getLRUCache();
        lJedis.flushAll();

    }

    private void load(String fileName) throws IOException
    {
        if(fileName == null){
            return;
        }
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(CacheLoader.class.getResourceAsStream(fileName)));
            String command = null;
            while ((command = reader.readLine()) != null)
            {
                execute(command);
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    private void execute(String command)
    {
        if (Strings.isNullOrEmpty(command))
        {
            return;
        }
        String[] args = command.trim().split("\\s+");
        Command comm = CommandFactory.getCommand(args[0]);
        comm.execute(args);
    }

}
