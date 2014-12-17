package com.szzjcs.commons.thirdapi.user;

import ch.lambdaj.Lambda;
import com.google.common.collect.Sets;
import junit.framework.Assert;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.szzjcs.commons.thirdapi.user.impl.WeiboUserService;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.isIn;

public class WeiboUserServiceTest
{

    @Test
    public void testGetUid()
    {
        WeiboUserService userService = new WeiboUserService();
        String uid = userService.getUid("2.00G9sVpFv_o6YDe2390167d50KqANi");
        System.out.println("===" + uid);
        Assert.assertNotNull(uid);
    }

    @Test
    public void testGetUser()
    {
        WeiboUserService userService = new WeiboUserService();
        ThirdUser user = userService.getUser("2.00G9sVpFv_o6YDe2390167d50KqANi");
        System.out.println(user);
        Assert.assertNotNull(user);
    }


}
