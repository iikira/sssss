package com.szzjcs.commons.util;

import org.junit.Assert;
import org.junit.Test;

public class GameAlgoUtilTest
{
    @Test
    public void testTryHit()
    {

        double prob = 0.0;
        int deviation = 20;
        int hits = 0;
        int counts = 10000;
        for (int i = 0; i < counts; i++)
        {
            if (GameAlgoUtil.tryHit(prob))
                hits++;
        }
        System.out.println(hits);
        double expected = counts * prob / 100;
        Assert.assertTrue((hits < expected + deviation) && (hits > expected - deviation));
    }
}
