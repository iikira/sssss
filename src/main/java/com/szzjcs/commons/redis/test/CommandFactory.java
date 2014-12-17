package com.szzjcs.commons.redis.test;

import com.szzjcs.commons.redis.JedisProxy;
import com.szzjcs.commons.redis.test.commands.HsetCommand;
import com.szzjcs.commons.redis.test.commands.SaddCommand;
import com.szzjcs.commons.redis.test.commands.SetCommand;
import com.szzjcs.commons.redis.test.commands.ZaddCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private final static Map<String, AbstractCommand> commands = new HashMap<>();
    static {
        commands.put("set", new SetCommand());
        commands.put("zadd", new ZaddCommand());
        commands.put("hset", new HsetCommand());
        commands.put("sadd", new SaddCommand());
    }

    public static Command getCommand(String commondStr){
        String[] cs = commondStr.split("_");
        AbstractCommand command = commands.get(cs[0].toLowerCase());
        if(cs.length > 1){
            if(cs[1].equalsIgnoreCase("p")){
                command.setJedisProxy(JedisProxy.getPersistCache());
            }
        }
        return command;
    }
}
