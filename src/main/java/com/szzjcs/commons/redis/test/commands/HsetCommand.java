package com.szzjcs.commons.redis.test.commands;

import com.szzjcs.commons.redis.test.AbstractCommand;

public class HsetCommand extends AbstractCommand
{
    @Override
    public void execute(String[] args)
    {
        this.getJedisProxy().hset(args[1], args[2], args[3]);
    }
}
