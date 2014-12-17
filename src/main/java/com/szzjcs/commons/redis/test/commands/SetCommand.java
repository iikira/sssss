package com.szzjcs.commons.redis.test.commands;

import com.szzjcs.commons.redis.test.AbstractCommand;

public class SetCommand extends AbstractCommand
{
    @Override
    public void execute(String[] args)
    {
        this.getJedisProxy().set(args[1], args[2]);
    }
}
