package com.szzjcs.commons.util;

import java.util.Random;

public class GameAlgoUtil
{
    private static final Random RANDOM = new Random();

    /**
     * 在指定的概论参数内尝试是否命中
     * 
     * @param probability
     *        如果值为1, 则表示 1% 概率.
     * @return 如果命中了,就返回true
     */
    public static boolean tryHit(double probability)
    {
        if (probability < 0.0 || probability >= 100.0)
        {
            throw new IllegalArgumentException("probability's range is [0,100), but is" + probability);
        }
        return RANDOM.nextDouble() < probability / 100;
    }
}
