package com.szzjcs.commons.redis.test.commands;

import com.szzjcs.commons.redis.test.AbstractCommand;

public class ZaddCommand extends AbstractCommand
{
    @Override
    public void execute(String[] args)
    {
        this.getJedisProxy().zadd(args[1], Double.valueOf(args[2]), args[3]);
    }
}
