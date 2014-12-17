package com.szzjcs.commons.redis.test.commands;

import com.szzjcs.commons.redis.test.AbstractCommand;

import java.util.Arrays;

public class SaddCommand extends AbstractCommand
{
    @Override
    public void execute(String[] args)
    {
        this.getJedisProxy().sadd(args[1], Arrays.copyOfRange(args, 2, args.length));
    }
}
