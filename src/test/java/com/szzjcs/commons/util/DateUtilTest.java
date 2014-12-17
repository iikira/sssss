package com.szzjcs.commons.util;

import java.util.Date;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateUtilTest
{
    @Test
    public void between()
    {
        DateTime start = DateTime.parse("2012-01-01");
        DateTime end = DateTime.parse("2012-01-15");
        DateTime now = DateTime.parse("2012-01-13");
        Assert.assertTrue(DateUtil.between(start.toDate(), end.toDate(), now.toDate()));

        start = DateTime.parse("2012-01-01");
        end = DateTime.parse("2012-01-15");
        now = DateTime.parse("2012-01-16");
        Assert.assertTrue(!DateUtil.between(start.toDate(), end.toDate(), now.toDate()));

        end = DateTime.parse("2012-01-15");
        now = DateTime.parse("2010-01-16");
        Assert.assertTrue(DateUtil.between(null, end.toDate(), now.toDate()));

        start = DateTime.parse("2012-01-15");
        now = DateTime.parse("2013-01-16");
        Assert.assertTrue(DateUtil.between(start.toDate(), null, now.toDate()));

    }

    @Test
    public void compareTo()
    {
        Assert.assertTrue(DateUtil.parseTime("2014-01-15 00:00:00").compareTo(new Date()) < 0);
    }
}
